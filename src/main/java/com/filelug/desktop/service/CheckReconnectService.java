package com.filelug.desktop.service;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelug.desktop.Constants;
import com.filelug.desktop.Utility;
import com.filelug.desktop.db.DatabaseAccess;
import com.filelug.desktop.db.HyperSQLDatabaseAccess;
import com.filelug.desktop.model.*;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <code>CheckReconnectService</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class CheckReconnectService {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(CheckReconnectService.class.getSimpleName());

    private static CheckReconnectService theInstance;

    private final UserService userService;

//    private final ComputerService computerService;

    private ScheduledExecutorService scheduledExecutorService;

    public static CheckReconnectService getInstance() {
        if (theInstance == null) {
            theInstance = new CheckReconnectService();
        }

        return theInstance;
    }

    private CheckReconnectService() {
        DatabaseAccess dbAccess = new HyperSQLDatabaseAccess();

        userService = new DefaultUserService(dbAccess);

//        computerService = new DefaultComputerService(dbAccess);

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        // use only one check service to check all ConnectSockets

        // when scheduledExecutorService will be set to null after stop() invoked
        // and we need to create a new one again.
        if (scheduledExecutorService == null  || scheduledExecutorService.isShutdown() || scheduledExecutorService.isTerminated()) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }

        FutureCallback<HttpResponse> callback = new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                int statusCode = result.getStatusLine().getStatusCode();

                if (statusCode == HttpServletResponse.SC_OK) {
                    List<UserComputerConnectionStatus> userComputerConnectionStatuses;

                    try {
                        String responseString = EntityUtils.toString(result.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));

                        ObjectMapper mapper = Utility.createObjectMapper();

                        userComputerConnectionStatuses = mapper.readValue(responseString, new TypeReference<List<UserComputerConnectionStatus>>() {
                        });
                    } catch (Exception e) {
                        userComputerConnectionStatuses = null;

                        LOGGER.error("Error to parsing response message.", e);
                    }

                    if (userComputerConnectionStatuses != null && userComputerConnectionStatuses.size() > 0) {
                        List<String> localUserIds = userService.findAllUserIds();

                        for (UserComputerConnectionStatus connectionStatus : userComputerConnectionStatuses) {
                            String receivedUserId = connectionStatus.getUserId();

                            User user = userService.findLocalUserById(receivedUserId);

                            if (user != null) {
                                localUserIds.remove(receivedUserId);

                                // User found, update status

                                Boolean connected = connectionStatus.getSocketConnected();
                                Boolean needReconnect = connectionStatus.getNeedReconnect();

                                AccountState.State newAccountState = (connected != null && connected) ? AccountState.State.ACTIVATED : AccountState.State.INACTIVATED;

                                User oldUser = user.copy();

                                // Do not check if it is the same with the value in local db to enforce to update the connection status in menu item.
                                // The connection status in menu item could not be updated for awhile until the server can be connected.

                                user.setState(newAccountState);

                                userService.updateLocalUser(user);

                                User newUser = userService.findLocalUserById(receivedUserId);

                                UserChangedService.getInstance().userUpdated(oldUser, newUser);

                                // DEBUG
//                                LOGGER.info(String.format("Updated status of user '%s'", user.getNickname()));

                                if (needReconnect) {
                                    // request to reconnect

                                    // DEBUG
//                                    LOGGER.info(String.format("User '%s' needs to reconnect to server.", user.getNickname()));

                                    try {
                                        String lugServerId = null;

                                        String sessionId = user.getSessionId();

                                        if (sessionId == null || sessionId.trim().length() < 1) {
                                            // It can't go here for the admin user
                                            // because it needs the session id of the administrator to invoke this service to check reconnect.

                                            // invoke loginApplyUser to get session id for the applied user.
                                            // loginApplyUser will get new lugServerId, so don't need to get again

                                            userService.loginApplyUserWithUserId(receivedUserId);

                                            user = userService.findLocalUserById(receivedUserId);

                                            lugServerId = user.getLugServerId();

                                            sessionId = user.getSessionId();

                                            // DEBUG
//                                            LOGGER.info(String.format("Got session for user '%s'", user.getNickname()));
                                        } else {
                                            // Get new lug server id and save to User before connecting

                                            try {
                                                lugServerId = userService.dispatchConnection(receivedUserId);

                                                // DEBUG
//                                                LOGGER.info(String.format("Disconnect user '%s' before connecting.", user.getNickname()));
                                            } catch (Exception e) {
                                                LOGGER.error("Failed to dispatch connection for user: '" + receivedUserId + "'", e);
                                            }

                                            if (lugServerId == null || lugServerId.trim().length() < 1) {
                                                // use the current lug server id

                                                lugServerId = user.getLugServerId();
                                            } else if (!lugServerId.equals(user.getLugServerId())) {
                                                // update to db only if changed

                                                user.setLugServerId(lugServerId);

                                                userService.updateLocalUser(user);
                                            }
                                        }

                                        connectFromComputer(receivedUserId, sessionId, lugServerId);
                                    } catch (Exception e) {
                                        LOGGER.error("Error on connecting socket to server.", e);
                                    }
                                }
                            }
                            // This service sends all the users in local db to server to ask for the connection status,
                            // and it returned only the updted status of these users,
                            // so it will not be the case that the user returned is not found!
//                            else {
//                                // User not found -> invoke loginApplyUser to login user and create
//
//                                try {
//                                    userService.loginApplyUserWithUserId(receivedUserId);
//
//                                    user = userService.findLocalUserById(receivedUserId);
//
//                                    if (user != null) {
//                                        // DEBUG
//                                        LOGGER.info(String.format("User '%s' signed in successfully and tries to connect to server.", user.getNickname()));
//
//                                        connectFromComputer(receivedUserId, user.getSessionId(), user.getLugServerId());
//                                    } else {
//                                        LOGGER.error(String.format("Failed to connect for user: '%s' because the user not exists.", receivedUserId));
//                                    }
//                                } catch (Exception e) {
//                                    LOGGER.error("Failed to sign in or connect for applied user: " + receivedUserId, e);
//                                }
//                            }
                        }

                        // the remain of localUserIds is not found in server and should be deleted from local db

                        if (localUserIds != null && localUserIds.size() > 0) {
                            for (String localUserId : localUserIds) {
                                User user = userService.findLocalUserById(localUserId);

                                if (user != null && (user.getAdmin() != null && !user.getAdmin() )) {
                                    try {
                                        userService.disconnectFromComputer(localUserId);
                                    } catch (Exception e) {
                                        LOGGER.error(String.format("Failed disconnect (remote) connection when deleting non-approved user '%s'", user.getNickname()), e);
                                    }

                                    User oldUser = user.copy();

                                    userService.deleteLocalUserById(localUserId);

                                    // notify user deleted
                                    UserChangedService.getInstance().userDeleted(oldUser);

                                    LOGGER.info(String.format("User '%s' deleted in local because the user has no permission to access this computer anymore.", user.getNickname()));
                                }
                            }
                        }
                    }
                } else {
                    if (statusCode != 0) {
                        LOGGER.debug("Set the connection states of all users to inactive for response status code to check if it needs reconnect : " + statusCode);

                        List<User> allUsers = userService.findAllLocalUsers();

                        if (allUsers != null) {
                            for (User user : allUsers) {
                                User oldUser = user.copy();

                                String userId = user.getAccount();

                                user.setState(AccountState.State.INACTIVATED);

                                userService.updateLocalUser(user);

                                User newUser = userService.findLocalUserById(userId);

                                UserChangedService.getInstance().userUpdated(oldUser, newUser);
                            }
                        }
                    }

                    // if statusCode is 403, meaning that the session of the admin user not found --> reset the application

                    if (statusCode == HttpServletResponse.SC_FORBIDDEN) {
                        ResetApplicationNotificationService.getInstance().applicationShouldReset(ResetApplicationNotificationService.REASON.SESSION_OR_USER_NOT_FOUND);
                    }
                }
            }

            @Override
            public void failed(Exception ex) {
                if (HttpHostConnectException.class.isInstance(ex) || ConnectTimeoutException.class.isInstance(ex) || UnknownHostException.class.isInstance(ex) || ConnectException.class.isInstance(ex)) {
                    // DEBUG
//                    LOGGER.error("Network Failure.\nClass: " + ex.getClass().getName() + "\nMessage: " + ex.getMessage());
                } else {
                    // DEBUG
//                    LOGGER.error("Failed to check reconnect!\nClass: " + ex.getClass().getName() + "\nMessage: " + ex.getMessage());
                }
            }

            @Override
            public void cancelled() {
                // DEBUG
//                LOGGER.warn("User cancelled check-reconnect service.");
            }
        };

        userService.checkReconnectRecursively(scheduledExecutorService, callback, Constants.INTERVAL_IN_MILLIS_TO_CHECK_RECONNECT, TimeUnit.MILLISECONDS);
    }

    private void connectFromComputer(String userId, String sessionId, String lugServerId) throws Exception {
        ConnectModel connectModel = new ConnectModel();

        // computerId is not used, but userId IS USED when connecting from computer because we need use it to disconnect current connection, if any.

        connectModel.setSid(Sid.CONNECT_V2);
        connectModel.setAccount(userId);
        connectModel.setSessionId(sessionId);
        connectModel.setLugServerId(lugServerId);
        connectModel.setLocale(Utility.getApplicationLocale());

        // system properties
        Properties properties = Utility.prepareSystemProperties();

        connectModel.setProperties(properties);

        ConnectResponseState connectResponseState = new ConnectResponseState(connectModel);

        userService.connectFromComputer(connectResponseState);
    }

    public void stop() {
        if (scheduledExecutorService != null) {
            try {
                scheduledExecutorService.shutdown();
                scheduledExecutorService.awaitTermination(Constants.AWAIT_TERMINATION_IN_SECONDS_TO_CHECK_RECONNECT_SCHEDULER, TimeUnit.SECONDS);

                LOGGER.debug("Successfully shutdown and terminate executor service: " + scheduledExecutorService.toString());
            } catch (Exception e) {
                // ignored
                LOGGER.debug("Failed to shutdown and terminate executor service: " + scheduledExecutorService.toString() + "\n" + e.getMessage());
            } finally {
                // set to null so the next time start() invoked, it will create a new one.
                scheduledExecutorService = null;
            }
        }
    }
}
