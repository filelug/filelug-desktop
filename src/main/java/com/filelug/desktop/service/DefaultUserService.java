package com.filelug.desktop.service;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.filelug.desktop.*;
import com.filelug.desktop.dao.UserDao;
import com.filelug.desktop.db.DatabaseAccess;
import com.filelug.desktop.db.HyperSQLDatabaseAccess;
import com.filelug.desktop.exception.UserNotFoundException;
import com.filelug.desktop.model.*;
import com.filelug.desktop.service.websocket.ConnectSocket;
import com.filelug.desktop.service.websocket.LoginWithQRCodeEndpoint;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//import org.eclipse.jetty.websocket.api.Session;

/**
 * <code>DefaultUserService</code> implements UserService
 *
 * @author masonhsieh
 * @version 1.0
 */
public class DefaultUserService implements UserService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("SERVICE_USER");

    private final ServiceUtilities serviceUtilities;

    private final UserDao userDao;

    public DefaultUserService() {
        this(null);
    }

    public DefaultUserService(DatabaseAccess dbAccess) {
        DatabaseAccess localDbAccess;

        if (dbAccess == null) {
            localDbAccess = new HyperSQLDatabaseAccess();
        } else {
            localDbAccess = dbAccess;
        }

        serviceUtilities = new ServiceUtilities();

        userDao = new UserDao(localDbAccess);

//        computerService = new DefaultComputerService(localDbAccess);
    }

    public Session loginUserOrGetQRCode() throws Exception {
        // send message to server to login admin or ask for qr code

        Session session = serviceUtilities.connectWebSocket(LoginWithQRCodeEndpoint.class, new URI(serviceUtilities.composeInitialConnectFullWebSocketAddress("ws/user/qrcode-login")));

        String message = prepareMessageToGetQRCodeFromServer();

        session.getBasicRemote().sendText(message);

        return session;
    }

    private String prepareMessageToGetQRCodeFromServer() throws Exception {
        ObjectMapper objectMapper = Utility.createObjectMapper();

        ObjectNode rootNode = objectMapper.createObjectNode();

        // sid
        rootNode.put(PropertyConstants.PROPERTY_NAME_SID, Sid.GET_QR_CODE_V2);

        String computerGroup;
        String computerName;

        // Check if any existing computer id for V2, if any, sent the existing computer id and recovery key;
        // if not, check if any existing computer id for V1, if any, sent the existing computer id and recovery key of V1;
        // if not, do't sent computer id and recovery key

        long NEGATIVE_COMPUTER_ID = -1L;

        long computerId = Utility.getPreferenceLong(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, NEGATIVE_COMPUTER_ID);

        if (computerId != NEGATIVE_COMPUTER_ID) {
            // Found existing computer id with version 2.x -> use it

            // computer id

            rootNode.put(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, computerId);

            // recovery key

            String recoveryKey = Utility.getPreference(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, "");

            if (recoveryKey.trim().length() > 0) {
                rootNode.put(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, recoveryKey);
            }

            // computer group

            computerGroup = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, Constants.DEFAULT_COMUPTER_GROUP);

            rootNode.put(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, computerGroup);

            // computer name

            computerName = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, Utility.createNewComputerName());

            rootNode.put(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, computerName);
        } else {
            // no computer id found -> don't send computer id and recovery key

            // computer-group

            computerGroup = Constants.DEFAULT_COMUPTER_GROUP;

            rootNode.put(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, computerGroup);

            // computer-name (default)

            computerName = Utility.createNewComputerName();

            rootNode.put(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, computerName);
        }

        // locale

        String locale = System.getProperty(PropertyConstants.PROPERTY_NAME_LOCALE);

        rootNode.put(PropertyConstants.PROPERTY_NAME_LOCALE, locale);

        // verification
        rootNode.put(PropertyConstants.PROPERTY_NAME_VERIFICATION, Utility.generateVerificationToLogin(computerGroup, computerName, locale));

        // device-token
        DeviceToken deviceTokenObject = Utility.prepareDeviceTokenObject();

        JsonNode deviceTokenNode = objectMapper.valueToTree(deviceTokenObject);

        rootNode.set(PropertyConstants.PROPERTY_NAME_DEVICE_TOKEN, deviceTokenNode);

        // system properties

        JsonNode syspropsNode = Utility.prepareJsonNodeFromSystemProperties(objectMapper);

        rootNode.set(PropertyConstants.PROPERTY_NAME_SYSTEM_PROPERTIES, syspropsNode);

        return objectMapper.writeValueAsString(rootNode);
    }

    @Override
    public String dispatchConnection(String userId) throws Exception {
        String lugServerId = null;

        try {
            ObjectMapper mapper = Utility.createObjectMapper();

            String path = "computer/dispatch2";

            String sessionId = userDao.findSessionIdById(userId);

            if (sessionId == null) {
                throw new Exception(ClopuccinoMessages.getMessage("session.not.exists"));
            } else {
                // save or update truststore before invoking the dispatch service

                Set<Header> headers = new HashSet<>();

                headers.add(new BasicHeader(Constants.HTTP_HEADER_FILELUG_SESSION_ID_NAME, sessionId));

                HttpResponse response = serviceUtilities.doPostJson(null, path, headers);

                int status = response.getStatusLine().getStatusCode();

                String responseString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));

                if (status == HttpServletResponse.SC_OK) {
                    JsonNode responseNode = mapper.readTree(responseString);

                    if (responseNode != null && responseNode.get(PropertyConstants.PROPERTY_NAME_LUG_SERVER_ID) != null) {
                        lugServerId = responseNode.get(PropertyConstants.PROPERTY_NAME_LUG_SERVER_ID).textValue();
                    }
                } else {
                    String message = ClopuccinoMessages.getMessage("status.and.reason", String.valueOf(status), responseString);

                    throw new Exception(message);
                }
            }
        } catch (UnknownHostException | HttpHostConnectException | ConnectTimeoutException e) {
            String message = ClopuccinoMessages.getMessage("status.and.reason", String.valueOf(HttpServletResponse.SC_REQUEST_TIMEOUT), ClopuccinoMessages.getMessage("no.network"));

            String responseMessage = Utility.localizedString("error.check.computer.name", message);

            LOGGER.error(responseMessage, e);

            throw new Exception(message);
        } catch (Exception e) {
            String message = Utility.localizedString("error.prepare.connection", e.getMessage());

            LOGGER.error(message, e);

            throw new Exception(e.getMessage());
        }

        return lugServerId;
    }

    @Override
    public void connectFromComputer(ConnectResponseState connectResponseState) throws IllegalArgumentException, NotRegisteredException, IOException, Exception {
        ConnectModel connectModel = connectResponseState.getConnectModel();

        if (connectModel == null) {
            throw new IllegalArgumentException("Null connect model to connect");
        } else {
            String userId = connectModel.getAccount();
            String sessionId = connectModel.getSessionId();
            String lugServerId = connectModel.getLugServerId();
            String locale = connectModel.getLocale();

            if (userId == null || userId.trim().length() < 1
                || lugServerId  == null || lugServerId.trim().length() < 1
                || sessionId  == null || sessionId.trim().length() < 1
                || locale  == null || locale.trim().length() < 1) {
                String errorMessage = ClopuccinoMessages.getMessage("at.least.one.empty.account.property");

                throw new IllegalArgumentException(errorMessage);
            } else {
                // DON'T disconnect existing here to prevent the UserComputer.socketConnected of new socket connected later be set to false in server
                // Let the server handle the disconnect of the old one when new socket with the same UserComputer is connected.

                // disconnect current socket and remove instance from ConnectSocket

//                try {
//                    disconnectFromComputer(userId);
//                } catch (Exception e) {
//                    LOGGER.error("Error on disconnecting old connection before setting up a new connection for user: " + userId + ".\n" + e.getMessage(), e);
//                }

                ConnectSocket socket;
                Session session = null;
                try {
                    CountDownLatch waitLatch = new CountDownLatch(1);

                    socket = new ConnectSocket(userId, connectResponseState, waitLatch, lugServerId);

                    URI serverURI = new URI(serviceUtilities.composeFullWebSocketAddress(lugServerId, "connect"));

                    session = serviceUtilities.connectWebSocket(socket, serverURI);

                    ObjectMapper mapper = Utility.createObjectMapper();

                    PropertiesSerializer propertiesSerializer = new PropertiesSerializer();
                    propertiesSerializer.addToObjectMapper(mapper);

                    String requestJson = mapper.writeValueAsString(connectModel);

                    Future<Void> future = session.getAsyncRemote().sendText(requestJson);

                    // current thread blocked until ConnectSocket using resp to response to the client
                    future.get((long) (Constants.FUTURE_WAIT_TIMEOUT_IN_SECONDS_DEFAULT * 1.1), TimeUnit.SECONDS);
                } catch (Exception e) {
                    String errorMessage = e.getMessage();

                    LOGGER.error(String.format("Error on connectin to server for user: '%s', error message: '%s'", userId, errorMessage), e);

                    if (session != null && session.isOpen()) {
                        try {
                            String reason = (errorMessage != null && errorMessage.trim().length() > 0) ? errorMessage : "ERROR";

                            session.close(new CloseReason(CloseReason.CloseCodes.CLOSED_ABNORMALLY, reason));
                        } catch (Exception e1) {
                            // ignored
                        }
                    }
                }
            }
        }
    }

    @Override
    public void connectUser(User user, boolean needUpdateLugServerId) {
        String userId = user.getAccount();
        String sessionId = user.getSessionId();

        try {
            String lugServerId = user.getLugServerId();

            if (lugServerId == null || lugServerId.trim().length() < 1 || needUpdateLugServerId) {
                // Get new lug server id and save to User before connecting

                String newLugServerId = dispatchConnection(userId);

                if (newLugServerId != null && newLugServerId.trim().length() > 0 && !newLugServerId.equals(lugServerId)) {
                    // use the current lug server id

                    lugServerId = newLugServerId;

                    user.setLugServerId(lugServerId);

                    updateLocalUser(user);
                }
            }

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

            connectFromComputer(connectResponseState);
        } catch (Throwable t) {
            User oldUser = user.copy();

            user.setState(AccountState.State.INACTIVATED);

            updateLocalUser(user);

            User newUser = findLocalUserById(userId);

            if (newUser != null) {
                UserChangedService.getInstance().userUpdated(oldUser, newUser);
            }

            LOGGER.error("Error on connecting to server for user: " + user.getAccount(), t);
        }
    }

    @Override
    public void disconnectFromComputer(String userId) throws IllegalArgumentException, NeedLoginWithQRCodeException, IOException, Exception {
        if (userId == null || userId.trim().length() < 1) {
            String errorMessage = ClopuccinoMessages.getMessage("at.least.one.empty.account.property");

            throw new IllegalArgumentException(errorMessage);
        } else {
            //* make sure the connection exists */
            ConnectSocket currentSocket = ConnectSocket.getInstance(userId);

            User user = userDao.findUserById(userId);

            if (currentSocket == null) {
                if (user != null && user.getState() != AccountState.State.INACTIVATED) {
                    User oldUser = user.copy();

                    user.setState(AccountState.State.INACTIVATED);

                    updateLocalUser(user);

                    User newUser = findLocalUserById(userId);

                    UserChangedService.getInstance().userUpdated(oldUser, newUser);

                    // DEBUG
//                    LOGGER.info(String.format("Changed status of user '%s' to '%s'", user.getNickname(), AccountState.State.INACTIVATED.name()));
                }
            } else {
//                // DEBUG: mark when in production
//                currentSocket.getSession().close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "User requested."));

                // DEBUG: Unmark when in production
                // disconnect from server

                // FIX: remove instance from Hashtable in ConnectSocket or not?

                HttpResponse response = internalDisconnectFromComputer(userId);

                int status = response.getStatusLine().getStatusCode();

                String nickname = (user != null) ? user.getNickname() : "";

                if (status == 200) {
                    LOGGER.info(String.format("User %s disconnected successfully.", nickname));

                    if (user != null && user.getState() != AccountState.State.INACTIVATED) {
                        User oldUser = user.copy();

                        user.setState(AccountState.State.INACTIVATED);

                        updateLocalUser(user);

                        User newUser = findLocalUserById(userId);

                        UserChangedService.getInstance().userUpdated(oldUser, newUser);

                        // DEBUG
//                        LOGGER.info(String.format("Changed status of user '%s' to '%s'", user.getNickname(), AccountState.State.INACTIVATED.name()));
                    }
                } else if (status == HttpServletResponse.SC_FORBIDDEN) {
                    // User session not exists.
                    // prompt user to reopen the application to login again.

                    String responseString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));

                    LOGGER.error(String.format("Error on disconnecting user %s because of no such connection.%n%s", nickname, (responseString != null) ? responseString : ""));

                    throw new NeedLoginWithQRCodeException(responseString);
                } else {
                    String responseString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));

                    String message = String.format("Error on disconnecting user: %s, status: %d, reason: '%s'", userId, status, responseString);

                    LOGGER.error(message);

                    throw new Exception(message);
                }
            }
        }
    }

    private HttpResponse internalDisconnectFromComputer(String userId) throws Exception {
        /* tell the connect socket from repository to disconnect itself */
        String path = "computer/disconnect";

        // find session id and lug server id from user id

        User user = userDao.findUserById(userId);

        if (user == null) {
            throw new Exception("User not found: " + userId);
        } else {
            String sessionId = user.getSessionId();

            Set<Header> headers = new HashSet<>();
            headers.add(new BasicHeader(Constants.HTTP_HEADER_FILELUG_SESSION_ID_NAME, sessionId));

            String lugServerId = user.getLugServerId();

            return serviceUtilities.doPostJson(lugServerId, path, headers, null, null, Constants.SO_TIMEOUT_IN_SECONDS_TO_DISCONNECT_CONNECT_SOCKET * 1000, Constants.CONNECT_TIMEOUT_IN_SECONDS_TO_DISCONNECT_CONNECT_SOCKET * 1000);
        }
    }

    @Override
    public int disconnect(String userId) throws IllegalArgumentException, NotRegisteredException, IOException, Exception {
        if (userId == null || userId.trim().length() < 1) {
            String errorMessage = ClopuccinoMessages.getMessage("at.least.one.empty.account.property");

            throw new IllegalArgumentException(errorMessage);
        } else {
            /* make sure the connection exists */
            ConnectSocket currentSocket = ConnectSocket.getInstance(userId);

            if (currentSocket == null || currentSocket.getSession() == null || !currentSocket.getSession().isOpen()) {
                return 200;
            } else {
                /* tell the connect socket from repository to disconnect itself */
                HttpResponse response = internalDisconnectFromComputer(userId);

                return response.getStatusLine().getStatusCode();
            }
        }
    }

    public void checkReconnectRecursively(final ScheduledExecutorService scheduledExecutorService, final FutureCallback<HttpResponse> callback, long delay, TimeUnit unit) {
        User adminUser = findAdministrator();

        List<String> userIds = findAllUserIds();

        if (adminUser != null && userIds != null && userIds.size() > 0) {
            Long computerId = Utility.getPreferenceLong(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, -1);

            try {
                // headers

                String adminSessionId = validateSessionAndGetNewIfNeededForUser(adminUser.getAccount());

                if (adminSessionId == null) {
                    adminSessionId = adminUser.getSessionId();
                }

                Header header = new BasicHeader(Constants.HTTP_HEADER_FILELUG_SESSION_ID_NAME, adminSessionId);
                Set<Header> headers = new HashSet<>();
                headers.add(header);

                // json

                List<String> encryptedUserComputerIds = new ArrayList<>();

                for (String userId : userIds) {
                    encryptedUserComputerIds.add(Utility.generateEncryptedUserComputerIdFrom(userId, computerId));
                }

                ObjectMapper mapper = Utility.createObjectMapper();

                String jsonString = mapper.writeValueAsString(encryptedUserComputerIds);

                serviceUtilities.doAsyncPost(null, "ping3", headers, jsonString, callback, Constants.SO_TIMEOUT_IN_SECONDS_TO_CHECK_RECONNECT * 1000, Constants.CONNECT_TIMEOUT_IN_SECONDS_TO_CHECK_RECONNECT * 1000);
            } catch (Throwable t) {
                if (HttpHostConnectException.class.isInstance(t) || ConnectTimeoutException.class.isInstance(t) || UnknownHostException.class.isInstance(t) || ConnectException.class.isInstance(t)) {
                    LOGGER.debug("Network Failure.\nClass: " + t.getClass().getName() + "\nMessage: " + t.getMessage());
                } else {
                    LOGGER.debug("Error on checking reconnect!\nClass: " + t.getClass().getName() + "\nMessage: " + t.getMessage());
                }
            }
        }

        // recursively,
        // event if no admin found,
        // so every period of Constants.INTERVAL_IN_MILLIS_TO_CHECK_RECONNECT (in TimeUnit.MILLISECONDS),
        // the method will invoke to check if the admin has been created and needs to reconnect.

        scheduledExecutorService.schedule(() -> {
            // DEBUG
//            LOGGER.info("Invoking checking-reconnect by thread: " + Thread.currentThread().getName() + "(" + Thread.currentThread().getId() + ")");

            checkReconnectRecursively(scheduledExecutorService, callback, delay, unit);
        }, delay, unit);
    }

    @Override
    public User findAdministrator() {
        User user;

        try {
            user = userDao.findAdministrator();
        } catch (Exception e) {
            user = null;

            LOGGER.error("Error on finding administrator\n" + e.getClass().getName() + "\n" + e.getMessage(), e);
        }

        return user;
    }

    @Override
    public boolean isAdministrator(String userId) {
        return userDao.isAdministrator(userId);
    }

    @Override
    public boolean hasAdministrator() {
        return userDao.hasAdministrator();
    }

    @Override
    public void createLocalUser(User user) {
        userDao.createUser(user);
    }

    @Override
    public User findLocalUserById(String userId) {
        return userDao.findUserById(userId);
    }

    @Override
    public String findLugServerIdByUserId(String userId) {
        return userDao.findLugServerIdByUserId(userId);
    }

    @Override
    public void deleteLocalUserById(String userId) {
        userDao.deleteUserById(userId);
    }

    @Override
    public void updateLocalUser(User user) {
        userDao.updateUser(user);
    }

    @Override
    public void disconnectAllUsers() {
        List<String> userIds = userDao.findAllUserIds();

        if (userIds != null && userIds.size() > 0) {
            for (String userId : userIds) {
                try {
                    User user = userDao.findUserById(userId);

                    if (user != null) {
                        disconnectFromComputer(userId);
                    }
                } catch (Exception e) {
                    LOGGER.error("Error on disconnect user. Error message:\n" + e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public List<String> findAllUserIds() {
        return userDao.findAllUserIds();
    }

    @Override
    public List<User> findAllLocalUsers() {
        return userDao.findAllUsers();
    }

    @Override
    public List<User> findNonAdminUsers() {
        return userDao.findNonAdminUsers();
    }

    @Override
    public List<User> findLocalUsersWithStates(List<AccountState.State> states) {
        return userDao.findUsersWithStates(states);
    }

    @Override
    public boolean isUserConnected(String userId) {
        String statusString = userDao.findUserStateById(userId);

        return statusString != null && AccountState.State.valueOf(statusString) == AccountState.State.ACTIVATED;
    }

    @Override
    public void truncateDesktopAllTables() {
        // delete user data first, so the user change event can be notified

        List<User> users = userDao.findAllUsers();

        if (users != null && users.size() > 0) {
            for (User user : users) {
                deleteLocalUserById(user.getAccount());

                UserChangedService.getInstance().userDeleted(user);
            }
        }

        userDao.truncateAllTables();
    }

    @Override
    public void syncUsersWithApprovedConnectionUsers() throws ComputerNotFoundException, SessionNotFoundException, Exception {
        User admin = userDao.findAdministrator();

        if (admin == null) {
            String message = "No administrator sepcified for this computer.";

            LOGGER.error(message);
            throw new Exception(message);
        }

        String sessionId = validateSessionAndGetNewIfNeededForUser(admin.getAccount());

        String path = "user/approved2";

        Set<Header> headers = new HashSet<>();

        headers.add(new BasicHeader(Constants.HTTP_HEADER_FILELUG_SESSION_ID_NAME, sessionId));

        HttpResponse response = serviceUtilities.doPost(path, headers, null);

        int status = response.getStatusLine().getStatusCode();

        String responseString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));

        if (status == HttpServletResponse.SC_OK) {
            List<ApprovedUserModel> approvedUsers;

            ObjectMapper mapper = Utility.createObjectMapper();

            try {
                approvedUsers = mapper.readValue(responseString, new TypeReference<List<ApprovedUserModel>>() {
                });
            } catch (Exception e) {
                // unexpected response format

                String message = Utility.localizedString("incorrect.data.content") + "\n" + responseString;
                LOGGER.error(message);

                throw new Exception(message);
            }

            List<String> oldUserIds = userDao.findAllUserIds();

            if (approvedUsers != null && approvedUsers.size() > 0) {
                for (ApprovedUserModel approvedUserModel : approvedUsers) {
                    String approvedUserId = approvedUserModel.getAccount();

                    User approvedUser = userDao.findUserById(approvedUserId);

                    if (approvedUser != null) {
                        // update user

                        oldUserIds.remove(approvedUserId);

                        User oldUser = approvedUser.copy();

                        // update status

                        approvedUser.setCountryId(approvedUserModel.getCountryId());
                        approvedUser.setPhoneNumber(approvedUserModel.getPhoneNumber());
                        approvedUser.setNickname(approvedUserModel.getNickname());
                        approvedUser.setShowHidden(approvedUserModel.isShowHidden());
                        approvedUser.setAllowAlias(approvedUserModel.isAllowAlias());

                        userDao.updateUser(approvedUser);

                        User newUser = approvedUser.copy();

                        // notify user updated

                        UserChangedService.getInstance().userUpdated(oldUser, newUser);

                        // DEBUG
//                        LOGGER.info(String.format("User '%s' updated.", newUser.getNickname()));

                        // login applied user if session id not found.

                        String userSessionId = newUser.getSessionId();

                        if (userSessionId == null || userSessionId.trim().length() < 1) {
                            loginApplyUserWithUserId(approvedUserId);
                        }
                    } else {
                        // create user

                        User userToCreate = new User();

                        userToCreate.setAccount(approvedUserId);
                        userToCreate.setCountryId(approvedUserModel.getCountryId());
                        userToCreate.setPhoneNumber(approvedUserModel.getPhoneNumber());
                        userToCreate.setNickname(approvedUserModel.getNickname());
                        userToCreate.setShowHidden(approvedUserModel.isShowHidden());
                        userToCreate.setAllowAlias(approvedUserModel.isAllowAlias());

                        userToCreate.setApproved(Boolean.TRUE);
                        userToCreate.setAdmin(Boolean.FALSE);
                        userToCreate.setState(AccountState.State.INACTIVATED);

                        userDao.createUser(userToCreate);

                        User newUser = findLocalUserById(approvedUserId);

                        // notify user created
                        UserChangedService.getInstance().userCreated(newUser);

                        // DEBUG
//                        LOGGER.info(String.format("User '%s' created.", newUser.getNickname()));

                        // login applied user and connect
                        loginApplyUserWithUserId(approvedUserId);
                    }
                }
            }

            // remove the left, except for the admin user

            if (oldUserIds.size() > 0) {
                for (String oldUserId : oldUserIds) {
                    if (!userDao.isAdministrator(oldUserId)) {
                        // delete user

                        // disconnect socket remotely first

                        User oldUser = userDao.findUserById(oldUserId);

                        if (oldUser != null) {
                            try {
                                disconnectFromComputer(oldUserId);
                            } catch (Exception e) {
                                LOGGER.error("Failed disconnect (remote) connection when deleting non-approved user: " + oldUserId, e);
                            }
                        }

                        // remove socket and disconnect it

                        ConnectSocket.removeInstance(oldUserId);

                        deleteLocalUserById(oldUserId);

                        // notify user deleted
                        UserChangedService.getInstance().userDeleted(oldUser);
                    }
                }
            }
        } else if (status == Constants.HTTP_STATUS_COMPUTER_NOT_FOUND) {
            throw new ComputerNotFoundException();
        } else if (status == HttpServletResponse.SC_FORBIDDEN) {
            throw new SessionNotFoundException(admin.getAccount());
        }
    }

    @Override
    public void loginApplyUserWithUserId(String applyUserId) throws Exception {
        /*
{
    "apply-user-id" : "9aaa3acb2bf8aa1353089eba94b4d1c11f3a520656abdab5305b5b3f905b994e", // 已授權用戶帳號編號
    "computer-id" : 3837763637383939,       // 此值必須存在
    "recovery-key":"012336272652",          // 此值必須存在
    "verification" : "bf94b4d1c5305b5b3f96abdab89eba05b994ea10a249aaa3acb28aa13533040cc4b24cec2783fc3402011f3a52065c16",
    "device-token": // 此值可不提供。若提供，則下面所有子項目除了「badge-number」可不提供之外，其他都要提供。
    {
        "device-token" : "1e39b345af9b036a2fc1066f2689143746f7d1220c23ff1491619a544a167c61",
        "notification-type" : "APNS",
        "device-type" : "OSX",
        "device-version" : "10.12.2",           // 作業系統版本
        "filelug-version" : "2.0.0",           // Filelug APP 大版號
        "filelug-build" : "2016.12.25.01",     // Filelug APP 小版號
        "badge-number" : 0                     // 此值可不提供
    },
    "sysprops" :
    {
        "desktop.version": "2.0.0",
        "locale": "zh_TW", // instead of "desktop.locale"
        "file.encoding": "MacRoman",
        "java.vm.info": "mixed mode",
        "user.dir": "/Users/user1/projects/clopuccino",
        "line.separator": "\n",
        "user.name": "user1",
        ：
        "user.country": "TW"
    }
}
        */

        String path = "user/loginau";

        long computerId = Utility.getPreferenceLong(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, 0);

        String recoveryKey = Utility.getPreference(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, "");

        // can't be null
        User admin = userDao.findAdministrator();

        String adminId = admin.getAccount();

        String adminSessionId = admin.getSessionId();

        String verification = Utility.generateVerificationToLoginApplyUser(adminId, applyUserId, computerId);

        ObjectMapper mapper = Utility.createObjectMapper();

        PropertiesSerializer propertiesSerializer = new PropertiesSerializer();
        propertiesSerializer.addToObjectMapper(mapper);

        ObjectNode rootNode = mapper.createObjectNode();

        // apply user id
        rootNode.put(PropertyConstants.PROPERTY_NAME_APPLY_USER_ID, applyUserId);

        // computer id
        rootNode.put(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, computerId);

        // recovery key
        rootNode.put(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, recoveryKey);

        // verification
        rootNode.put(PropertyConstants.PROPERTY_NAME_VERIFICATION, verification);

        // device-token

        DeviceToken deviceTokenObject = Utility.prepareDeviceTokenObject();

        JsonNode deviceTokenNode = mapper.valueToTree(deviceTokenObject);

        rootNode.set(PropertyConstants.PROPERTY_NAME_DEVICE_TOKEN, deviceTokenNode);

        // sysprops

        JsonNode syspropsNode = Utility.prepareJsonNodeFromSystemProperties(mapper);

        rootNode.set(PropertyConstants.PROPERTY_NAME_SYSTEM_PROPERTIES, syspropsNode);

        String jsonString = mapper.writeValueAsString(rootNode);

        Set<Header> headers = new HashSet<>();

        headers.add(new BasicHeader(Constants.HTTP_HEADER_FILELUG_SESSION_ID_NAME, adminSessionId));

        HttpResponse response = serviceUtilities.doPostJson(null, path, headers, null, jsonString);

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode == HttpServletResponse.SC_OK) {
            String responseString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));

            JsonNode responseNode = mapper.readTree(responseString);

            /*
{
    "account" : "9aaa3acb2bf8aa13533040cc4b24cec2208",
    "country-id" : "TW",
    "country-code" : 886,
    "phone" : "975009123", // 號碼不在前面加上 '0' 
    "phone-with-country" : "+886975009123",
    "nickname" : "Jellyfish",
    "show-hidden" : false,
    "session-id" : "1c11f3a520656abdab5305b5b3f905b994ea10a24c16783fc340",
    "computer-id" : 3837763637383939,
    "lug-server-id": "repo1",
    "allow-alias": true
}
            */

            JsonNode accountNode = responseNode.get(PropertyConstants.PROPERTY_NAME_ACCOUNT);

            JsonNode countryIdNode = responseNode.get(PropertyConstants.PROPERTY_NAME_COUNTRY_ID);

            JsonNode countryCodeNode = responseNode.get(PropertyConstants.PROPERTY_NAME_COUNTRY_CODE);

            JsonNode phoneNumberNode = responseNode.get(PropertyConstants.PROPERTY_NAME_PHONE_NUMBER);

            JsonNode phoneNumberWithCountryNode = responseNode.get(PropertyConstants.PROPERTY_NAME_PHONE_NUMBER_WITH_COUNTRY);

            JsonNode nicknameNode = responseNode.get(PropertyConstants.PROPERTY_NAME_NICKNAME);

            JsonNode showHiddenNode = responseNode.get(PropertyConstants.PROPERTY_NAME_SHOW_HIDDEN);

            JsonNode sessionIdNode = responseNode.get(PropertyConstants.PROPERTY_NAME_SESSION_ID);

            JsonNode computerIdNode = responseNode.get(PropertyConstants.PROPERTY_NAME_COMPUTER_ID);

            JsonNode lugServerIdNode = responseNode.get(PropertyConstants.PROPERTY_NAME_LUG_SERVER_ID);

            JsonNode allowAliasNode = responseNode.get(PropertyConstants.PROPERTY_NAME_ALLOW_ALIAS);

            String errorMessage;

            if (accountNode == null || accountNode.textValue() == null) {
                errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "account id");
            } else if (countryIdNode == null || countryIdNode.textValue() == null) {
                errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "country id");
            } else if (countryCodeNode == null) {
                errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "country code");
            } else if (phoneNumberNode == null || phoneNumberNode.textValue() == null) {
                errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "phone number");
            } else if (phoneNumberWithCountryNode == null || phoneNumberWithCountryNode.textValue() == null) {
                errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "phone number with country");
            } else if (showHiddenNode == null || !showHiddenNode.isBoolean()) {
                errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "showHidden");
            } else if (sessionIdNode == null || sessionIdNode.textValue() == null) {
                errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "session id");
            } else if (computerIdNode == null || !computerIdNode.isNumber()) {
                errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "computer id");
            } else if (lugServerIdNode == null || lugServerIdNode.textValue() == null) {
                errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "lug server id");
            } else if (allowAliasNode == null || !allowAliasNode.isBoolean()) {
                errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "allow alias");
            } else {
                errorMessage = null;
            }

            if (errorMessage != null) {
                LOGGER.error(String.format("Error on receiving message from server to login applied user: %s.%n%s", applyUserId, errorMessage));
            } else {
                // chekc if the computer id is idential with the returned one

                long returnedComputerId = computerIdNode.longValue();

                if (returnedComputerId != computerId) {
                    LOGGER.error("Computer id not expected. Expected: %d; received: %d", computerId, returnedComputerId);
                } else {

                    // Use the default nickname if no value in returned

                    String nickname;

                    if (nicknameNode == null || nicknameNode.textValue() == null) {
                        nickname = Utility.localizedString("default.nickname");
                    } else {
                        nickname = nicknameNode.textValue();
                    }

                    String account = accountNode.textValue();
                    String countryId = countryIdNode.textValue();
                    String phoneNumber = phoneNumberNode.textValue();
                    boolean showHidden = showHiddenNode.asBoolean();
                    String sessionId = sessionIdNode.textValue();
                    String lugServerId = lugServerIdNode.textValue();
                    boolean allowAlias = allowAliasNode.booleanValue();

                    // REMEMBER the applied user is not the administrator
                    final boolean IS_ADMIN = false;

                    // save the user as administrator because there's only administrator can login using QR code.
                    // Non-administrator user can only login by the administrator's device.

                    User user = findLocalUserById(account);

                    if (user == null) {
                        // Allowed alias if it's admin, otherwise false

                        user = new User(account, countryId, phoneNumber, sessionId, lugServerId, nickname, showHidden, 0L, IS_ADMIN, AccountState.State.INACTIVATED, Boolean.TRUE, allowAlias);

                        createLocalUser(user);

                        User newUser = findLocalUserById(account);

                        // notify user created

                        UserChangedService.getInstance().userCreated(newUser);

                        // DEBUG
//                        LOGGER.info(String.format("User '%s' created", nickname));
                    } else {
                        User oldUser = user.copy();

                        user.setSessionId(sessionId);
                        user.setLastSessionTime(System.currentTimeMillis());
                        user.setLugServerId(lugServerId);
                        user.setCountryId(countryId);
                        user.setPhoneNumber(phoneNumber);
                        user.setNickname(nickname);
                        user.setShowHidden(showHidden);
                        user.setState(AccountState.State.INACTIVATED);
                        user.setAdmin(IS_ADMIN);
                        user.setApproved(Boolean.TRUE);
                        user.setAllowAlias(allowAlias);

                        updateLocalUser(user);

                        User newUser = user.copy();

                        // notify user updated

                        UserChangedService.getInstance().userUpdated(oldUser, newUser);

                        // DEBUG
//                        LOGGER.info(String.format("User '%s' updated", nickname));
                    }
                }
            }
        } else if (statusCode == HttpServletResponse.SC_FORBIDDEN) {
            throw new SessionNotFoundException(adminId);
        } else {
            String responseErrorString = null;
            try {
                responseErrorString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));
            } catch (Exception e) {
                // ignore
            }

            String errorMessage = Utility.localizedString("failed.login.apply.user.with.message", String.valueOf(statusCode), responseErrorString != null ? responseErrorString : "");

            LOGGER.error(errorMessage);

            throw new Exception(errorMessage);
        }
    }

    @Override
    public String exchangeNewSessionWithOld(String oldSessionId, String userId, String countryId, String phoneNumber) throws Exception {
        /*
        {
                "sessionId" : "JFOEINFIAF1B7BEDC52C374DF4BD5F9699A0D5DFAUW902798UGMO4758409U589I43NJTRUFIE7YFH3I4U7TREOWIR09IWEKML",
                "verification" : "bf94b4d1c5305b5b3f96abdab89eba05b994ea10a249aaa3acb28aa13533040cc4b24cec2783fc3402011f3a52065c16",
                "locale" : "zh_TW",
                "device-token":
                            // 此值可不提供。若提供，則下面所有子項目除了「badge-number」可不提供之外，其他都要提供。
                {
                    "device-token" : "1e39b345af9b036a2fc1066f2689143746f7d1220c23ff1491619a544a167c61",
                    "notification-type" : "APNS",
                    "device-type" : "IOS",
                    "device-version" : "10.1.1",           // iOS/Android 作業系統版本
                    "filelug-version" : "1.5.2",           // Filelug APP 大版號
                    "filelug-build" : "2016.09.24.01",     // Filelug APP 小版號
                    "badge-number" : 0                     // 此值可不提供
                }
        }
        */

        String newSessionId = null;

        String path = "user/loginse";

        ObjectMapper mapper = Utility.createObjectMapper();

        ObjectNode rootNode = mapper.createObjectNode();

        // sessionId

        rootNode.put(PropertyConstants.PROPERTY_NAME_SESSION_ID_WITHOUT_DASH, oldSessionId);

        // verification

        if (phoneNumber.startsWith("0")) {
            phoneNumber = phoneNumber.substring(1);
        }

        String verification = Utility.generateVerificationForExchangeSession(userId, countryId, phoneNumber);

        rootNode.put(PropertyConstants.PROPERTY_NAME_VERIFICATION, verification);

        // locale

        String locale = Utility.getApplicationLocale();

        rootNode.put(PropertyConstants.PROPERTY_NAME_LOCALE, locale);

        // device-token

        DeviceToken deviceTokenObject = Utility.prepareDeviceTokenObject();

        JsonNode deviceTokenNode = mapper.valueToTree(deviceTokenObject);

        rootNode.set(PropertyConstants.PROPERTY_NAME_DEVICE_TOKEN, deviceTokenNode);

        String jsonString = mapper.writeValueAsString(rootNode);

        HttpResponse response = serviceUtilities.doPostJson(null, path, jsonString);

        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode == HttpServletResponse.SC_OK) {
            String responseString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));

            JsonNode responseNode = mapper.readTree(responseString);

            JsonNode newSessionIdNode = responseNode.get(PropertyConstants.PROPERTY_NAME_NEW_SESSION_ID);

            if (newSessionIdNode != null) {
                newSessionId = newSessionIdNode.textValue();

                // update the new session id and last session timestamp to local db

                if (newSessionId != null && newSessionId.trim().length() > 0) {
                    userDao.updateSessionById(userId, newSessionId);
                }
            }
        } else if (statusCode == HttpServletResponse.SC_FORBIDDEN) {

            throw new SessionNotFoundException(userId);
        } else {
            String responseErrorString = null;
            try {
                responseErrorString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));
            } catch (Exception e) {
                // ignore
            }

            String errorMessage = Utility.localizedString("failed.relogin.with.message", String.valueOf(statusCode), responseErrorString != null ? responseErrorString : "");

            LOGGER.error(errorMessage);

            throw new Exception(errorMessage);
        }

        return newSessionId;
    }

    @Override
    public String validateSessionAndGetNewIfNeededForUser(String userId) throws Exception {
        String newSessionId;

        User user = userDao.findUserById(userId);

        if (user == null) {
            throw new UserNotFoundException(Utility.localizedString("user.not.exists2"));
        } else {
            String oldSessionId = user.getSessionId();

            if (oldSessionId == null) {
                throw new Exception(Utility.localizedString("user.not.login"));
            } else if (checkTimeout(user.getLastSessionTime(), null)) {
                newSessionId = exchangeNewSessionWithOld(oldSessionId, userId, user.getCountryId(), user.getPhoneNumber());
            } else {
                newSessionId = oldSessionId;
            }
        }

        return newSessionId;
    }

    @Override
    public boolean checkTimeout(long lastAccessTime, Integer timeout) {
        if (timeout == null) {
            timeout = Constants.LESS_OF_CLIENT_SESSION_IDLE_TIMEOUT_IN_SECONDS;
        }

        Date currentDate = new Date();

        Date lastAccessDate = new Date();
        lastAccessDate.setTime(lastAccessTime);
        lastAccessDate = DateUtils.addSeconds(lastAccessDate, timeout);

        return currentDate.compareTo(lastAccessDate) > 0;
    }

    @Override
    public void removeAllUsers() {
        try {
            disconnectAllUsers();

            // clear all preferences except for computer-related ones.
            Utility.removeNonComputerRelatedPreferences();

            // truncate all tables
            truncateDesktopAllTables();
        } catch (Exception e) {
            LOGGER.error("Error on removing all users.", e);
        }
    }

    @Override
    public void resetApplication() {
        try {
            disconnectAllUsers();

            // clear preferences anyway
            Utility.clearPreferences();

            // truncate all tables
            truncateDesktopAllTables();
        } catch (Exception e) {
            LOGGER.error("Error on application reset.", e);
        }
    }
}
