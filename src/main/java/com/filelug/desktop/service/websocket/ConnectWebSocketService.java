package com.filelug.desktop.service.websocket;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelug.desktop.ClopuccinoMessages;
import com.filelug.desktop.Constants;
import com.filelug.desktop.PropertyConstants;
import com.filelug.desktop.Utility;
import com.filelug.desktop.dao.UserDao;
import com.filelug.desktop.model.*;
import com.filelug.desktop.service.Sid;
import com.filelug.desktop.service.UserChangedService;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;

/**
 * <code>ConnectWebSocketService</code> receives and process the web socket message from server with service id: CONNECT_V2
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ConnectWebSocketService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ConnectWebSocketService.class.getSimpleName());

    private final Session session;

    private final String message;

    private final ConnectSocket connectSocket;

    public ConnectWebSocketService(final Session session, final String message, final ConnectSocket connectSocket) {
        this.session = session;
        this.message = message;
        this.connectSocket = connectSocket;
    }

    /**
     * If 200 status code received, remove the preference 'qr-code' to close the QRCodeWindow, if any.
     * If the status code received is not 200, sleep for 2 seconds, then prompt the error message and ask if
     * the user wants to try again. If the user choose yes, get the QR code again; if the user choose no, remove the preference 'qr-code'.
     */
    public void connectFromComputerWebSocket() {
        UserDao userDao = connectSocket.getUserDao();

        try {
            ObjectMapper mapper = Utility.createObjectMapper();

            ResponseUserComputerModel responseModel = mapper.readValue(message, ResponseUserComputerModel.class);

            Integer status = responseModel.getStatus();

            if (HttpServletResponse.SC_OK == status) {
                // assign user info
                ApprovedUserModel approvedUserModel = responseModel.getApprovedUserModel();

                String responseUserId = approvedUserModel.getAccount();
                String responseNickname = approvedUserModel.getNickname();
                String responseDesktopSessionId = approvedUserModel.getDesktopSessionId();
                Boolean responseShowHidden = approvedUserModel.isShowHidden();
                Boolean responseAllowAlias = approvedUserModel.isAllowAlias();

                String responseComputerGroup = responseModel.getComputerGroup();
                String responseComputerName = responseModel.getComputerName();
                Long responseLastAccessTime = responseModel.getTimestamp();

                if (responseUserId != null && responseUserId.trim().length() > 0
                    && responseNickname != null && responseNickname.trim().length() > 0
                    && responseDesktopSessionId != null && responseDesktopSessionId.trim().length() > 0
                    && responseShowHidden != null
                    && responseAllowAlias != null
                    && responseLastAccessTime != null) {

                    // DEBUG
//                    LOGGER.info(String.format("User '%s' connected successfully", responseNickname));

                    // remove QR code in preference to dispose QRCodeWindow
                    Utility.removePreference(PropertyConstants.PROPERTY_NAME_QR_CODE);

//                    // Remove PROPERTY_NAME_V1_XXX in preferences to prevent them go back to version 1.x
//                    Utility.removeV1Preferences();

                    connectSocket.setUserId(responseUserId);
                    connectSocket.setNickname(responseNickname);
                    connectSocket.setLastAccessTime(responseLastAccessTime);

                    LOGGER.debug("Socket connected for user '" + connectSocket.getNickname() + "'\nuser id: " + connectSocket.getUserId());

                    /* 1. save user nickname
                     * 2. create linkage between this instance and user, session
                     * 3. create default root directory if not exists
                     * 4. start ping to repository periodically
                     */

                    String errorMessage;

                    // 1. save user nickname
                    User user = userDao.findUserById(responseUserId);

                    if (user != null) {
                        User oldUser = user.copy();

                        user.setNickname(responseNickname);
                        user.setState(AccountState.State.ACTIVATED);
                        user.setLastConnectTime(responseLastAccessTime);
                        user.setShowHidden(responseShowHidden);
                        user.setAllowAlias(responseAllowAlias);
                        user.setSessionId(responseDesktopSessionId);
                        user.setLastSessionTime(System.currentTimeMillis());

                        userDao.updateUser(user);

                        User newUser = userDao.findUserById(responseUserId);

                        UserChangedService.getInstance().userUpdated(oldUser, newUser);

                        // 2. update computer name if any
                        // When device invoke change computer name and the ConnectSocket not alive,
                        // the computer name here will not change until now.

                        if (responseComputerGroup != null && responseComputerName != null) {
                            String oldComputerGroup = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, "");

                            if (oldComputerGroup.length() < 1 || !oldComputerGroup.equals(responseComputerGroup)) {
                                Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, responseComputerGroup);
                            }

                            String oldComputerName = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, "");

                            if (oldComputerName.length() < 1 || !oldComputerName.equals(responseComputerName)) {
                                Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, responseComputerName);
                            }
                        }

                        // 3. create linkage between this instance and server session

                        ConnectSocket oldConnectSocket = ConnectSocket.getInstance(responseUserId);

                        if (oldConnectSocket != null && oldConnectSocket != connectSocket) {
                            ConnectSocket.removeInstance(responseUserId);

                            ConnectSocket.putInstance(responseUserId, connectSocket);

                            // DEBUG
//                            LOGGER.info("Connection socket attached to user: " + responseUserId);
                        }

                        errorMessage = "";

                        // use only one check service to check all ConnectSockets instead.

//                        // 3. start ping to repository periodically
//                        CheckReconnectService checkReconnectService = new CheckReconnectService(this);
//                        checkReconnectService.start();
                    } else {
                        /* User should be created before requesting connection to the server.
                         * So evil things happens!
                         */

                        LOGGER.error("User " + approvedUserModel.toString() + " should be created in other places before requesting connection to the respository.");

//                        connectSocket.setReadyDistroyAndStopCheckReconnect();

                        errorMessage = ClopuccinoMessages.getMessage("user.not.found", responseUserId);

                        try {
                            // server 剛剛建立並 attached 的 socket 也要一併移除，因此不能只使用 disconnect()
                            ConnectSocketUtilities.closeSessionWithBadDataStatusCode(connectSocket.getSession(), errorMessage);
//                            getSession().close(StatusCode.BAD_DATA, errorMessage);

                            LOGGER.error("Close socket for non-existing user: '" + responseUserId + "'.");
                        } catch (Exception e) {
                            // ignored
                        }
                    }

//                    AccountState.getInstance().setState(responseUserId, AccountState.State.ACTIVATED);
                } else {
                    // Incorrect format of the response data

                    String userId = connectSocket.getUserId();

                    try {
                        // server 剛剛建立並 attached 的 socket 也要一併移除，因此不能只使用 disconnect()

                        ConnectSocketUtilities.closeSessionWithBadDataStatusCode(connectSocket.getSession(), "Incorrect response data format for service: " + Sid.CONNECT_V2);

                        String errorMessage = ClopuccinoMessages.getMessage("incorrect.data.content");

                        LOGGER.error(errorMessage + " User(" + userId + ") closed the session from server.");
                    } catch (Exception e) {
                        // ignored
                    }

                    if (userId != null) {
                        User user = userDao.findUserById(userId);

                        if (user != null && user.getState() != AccountState.State.INACTIVATED) {
                            User oldUser = user.copy();

                            userDao.updateUserStateById(userId, AccountState.State.INACTIVATED);

                            User newUser = userDao.findUserById(userId);

                            UserChangedService.getInstance().userUpdated(oldUser, newUser);
                        }
                    }

//                    AccountState.getInstance().setState(responseUserId, AccountState.State.INACTIVATED);
                }
            } else {
                // response status is not 200

                // stop reconnect when 400,401,403,404,460,467

//                if (status == HttpServletResponse.SC_BAD_REQUEST
//                    || status == HttpServletResponse.SC_UNAUTHORIZED
//                    || status == HttpServletResponse.SC_FORBIDDEN
//                    || status == HttpServletResponse.SC_NOT_FOUND
//                    || status == Constants.HTTP_STATUS_COMPUTER_NOT_FOUND
//                    || status == Constants.HTTP_STATUS_SHOULD_UPDATE_PHONE_NUMBER) {
//
//                    // stop reconnect process
//                    connectSocket.setReadyDistroyAndStopCheckReconnect();
//                }

                String errorMessage = responseModel.getError();

                LOGGER.error(String.format("Failed to connect to server with status: %d, message: '%s'", status, errorMessage));

                // Server close the session after send text to desktop when the return status is not SC_OK,
                // so the desktop don't have to close socket again.
//                try {
//                    // repository 並沒有attached新建立的socket，因此只使用disconnect()，不能用close()
//                    getSession().disconnect();
//
//                    LOGGER.error("Disconnect socket for: " + errorMessage);
//                } catch (Exception e) {
//                        /* ignored */
//                }

                String userId = connectSocket.getUserId();

                if (userId != null) {
                    User user = userDao.findUserById(userId);

                    if (user != null && user.getState() != AccountState.State.INACTIVATED) {
                        User oldUser = user.copy();

                        userDao.updateUserStateById(userId, AccountState.State.INACTIVATED);

                        User newUser = userDao.findUserById(userId);

                        UserChangedService.getInstance().userUpdated(oldUser, newUser);
                    }
//                    AccountState.getInstance().setState(userId, AccountState.State.INACTIVATED);
                }

//                if (connectSocket.getConnectResponseState() != null) {
//                    connectSocket.getConnectResponseState().setState(status, errorMessage, null);
//                }
            }
        } catch (Exception e) {
            String userId = connectSocket.getUserId();

            String errorMessage = ClopuccinoMessages.getMessage("error.active.user3", (userId != null ? userId : "Not specified yet."), message, e.getMessage());

            LOGGER.error(errorMessage, e);

            // Server close the session after send text to desktop when the return status is not SC_OK,
            // so the desktop don't have to close socket again.
//            try {
//                // repository 並沒有attached新建立的socket，因此只使用disconnect()，不能用close()
//                getSession().disconnect();
//
//                LOGGER.error("Disconnect socket for: " + errorMessage);
//            } catch (Exception ex) {
//                // ignored
//            }

            if (userId != null) {
                User user = userDao.findUserById(userId);

                if (user != null && user.getState() != AccountState.State.INACTIVATED) {
                    User oldUser = user.copy();

                    user.setState(AccountState.State.ACTIVATED);

                    userDao.updateUser(user);

                    User newUser = userDao.findUserById(userId);

                    UserChangedService.getInstance().userUpdated(oldUser, newUser);
                }
                
//                AccountState.getInstance().setState(userId, AccountState.State.INACTIVATED);
            }

//            if (connectSocket.getConnectResponseState() != null) {
//                connectSocket.getConnectResponseState().setState(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage, null);
//            }
        } finally {
            connectSocket.getCloseLatch().countDown();
        }
    }

    /* add computer info to the response model */
//    public void connectFromComputerWebSocket() {
//        try {
//            ObjectMapper mapper = Utility.createObjectMapper();
//
//            ResponseUserModel responseModel = mapper.readValue(message, ResponseUserModel.class);
//
//            Integer status = responseModel.getStatus();
//
//            if (HttpServletResponse.SC_OK == status) {
//                // assign user info
//                ApprovedUserModel approvedUserModel = responseModel.getApprovedUserModel();
//
//                String responseUserId = approvedUserModel.getAccount();
//                String responseNickname = approvedUserModel.getNickname();
//                String responseDesktopSessionId = approvedUserModel.getDesktopSessionId();
//                Boolean responseShowHidden = approvedUserModel.isShowHidden();
//                Boolean responseAllowAlias = approvedUserModel.isAllowAlias();
//
//                Long responseLastAccessTime = responseModel.getTimestamp();
//
//                if (responseUserId != null && responseUserId.trim().length() > 0
//                    && responseNickname != null && responseNickname.trim().length() > 0
//                    && responseDesktopSessionId != null && responseDesktopSessionId.trim().length() > 0
//                    && responseShowHidden != null
//                    && responseAllowAlias != null
//                    && responseLastAccessTime != null) {
//
//                    // remove QR code in preference to dispose QRCodeWindow
//                    Utility.removePreference(PropertyConstants.PROPERTY_NAME_QR_CODE);
//
//                    connectSocket.setUserId(responseUserId);
//                    connectSocket.setNickname(responseNickname);
//                    connectSocket.setLastAccessTime(responseLastAccessTime);
//
//                    UserDao userDao = connectSocket.getUserDao();
//
//                    LOGGER.debug("Socket connected for user '" + connectSocket.getNickname() + "'\nuser id: " + connectSocket.getUserId());
//
//                    /* 1. save user nickname
//                     * 2. create linkage between this instance and user, session
//                     * 3. create default root directory if not exists
//                     * 4. start ping to repository periodically
//                     */
//
//                    String errorMessage;
//
//                    // 1. save user nickname
//                    User user = userDao.findUserById(responseUserId);
//
//                    if (user != null) {
//                        user.setNickname(responseNickname);
//                        user.setLastConnectTime(responseLastAccessTime);
//                        user.setShowHidden(responseShowHidden);
//                        user.setAllowAlias(responseAllowAlias);
//                        user.setSessionId(responseDesktopSessionId);
//                        user.setLastSessionTime(System.currentTimeMillis());
//
//                        userDao.updateUser(user);
//
//                        // 2, create linkage between this instance and server session
//
//                        ConnectSocket oldConnectSocket = ConnectSocket.getInstance(responseUserId);
//
//                        if (oldConnectSocket != null && oldConnectSocket != connectSocket) {
//                            ConnectSocket.removeInstance(responseUserId);
//
//                            ConnectSocket.putInstance(responseUserId, connectSocket);
//
//                            LOGGER.debug("Connection socket attached to user: " + responseUserId);
//                        }
//
//                        errorMessage = "";
//
//                        // use only one check service to check all ConnectSockets instead.
//
////                        // 3. start ping to repository periodically
////                        CheckReconnectService checkReconnectService = new CheckReconnectService(this);
////                        checkReconnectService.start();
//                    } else {
//                        /* User should be created in other places before requesting connection to the respository.
//                         * So evil things happens!
//                         */
//
//                        LOGGER.error("User " + approvedUserModel.toString() + " should be created in other places before requesting connection to the respository.");
//
//                        connectSocket.setReadyDistroyAndStopCheckReconnect();
//
//                        errorMessage = ClopuccinoMessages.getMessage("user.not.found", responseUserId);
//
//                        try {
//                            // repository 剛剛建立並attached的socket也要一併移除，因此不能只使用disconnect()
//                            ConnectSocketUtilities.closeSessionWithBadDataStatusCode(connectSocket.getSession(), errorMessage);
////                            getSession().close(StatusCode.BAD_DATA, errorMessage);
//
//                            LOGGER.error("Close socket for non-existing user: '" + responseUserId + "'.");
//                        } catch (Exception e) {
//                            // ignored
//                        }
//                    }
//
//                    AccountState.getInstance().setState(responseUserId, AccountState.State.ACTIVATED);
//
//                    if (connectSocket.getConnectResponseState() != null) {
//                        connectSocket.getConnectResponseState().setState(status, errorMessage, userDao.findUserById(responseUserId));
//                    }
//                } else {
//                    String errorMessage = ClopuccinoMessages.getMessage("incorrect.data.content");
//
//                    connectSocket.setReadyDistroyAndStopCheckReconnect();
//
//                    LOGGER.error("Stop checking reconnect for: " + errorMessage);
//
//                    try {
//                        // repository 剛剛建立並attached的socket也要一併移除，因此不能只使用disconnect()
//                        ConnectSocketUtilities.closeSessionWithBadDataStatusCode(connectSocket.getSession(), "User: '" + responseUserId + "' not found in desktop.");
////                        getSession().close(StatusCode.BAD_DATA, "User: '" + userId + "' not found in desktop.");
//
//                        LOGGER.error("Close socket for: " + errorMessage);
//                    } catch (Exception e) {
//                        // ignored
//                    }
//
//                    AccountState.getInstance().setState(responseUserId, AccountState.State.INACTIVATED);
//
//                    if (connectSocket.getConnectResponseState() != null) {
//                        connectSocket.getConnectResponseState().setState(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage, null);
//                    }
//                }
//            } else {
//                // stop reconnect when 400,401,403,404,460,467
//
//                if (status == HttpServletResponse.SC_BAD_REQUEST
//                    || status == HttpServletResponse.SC_UNAUTHORIZED
//                    || status == HttpServletResponse.SC_FORBIDDEN
//                    || status == HttpServletResponse.SC_NOT_FOUND
//                    || status == Constants.HTTP_STATUS_COMPUTER_NOT_FOUND
//                    || status == Constants.HTTP_STATUS_SHOULD_UPDATE_PHONE_NUMBER) {
//
//                    // stop reconnect process
//                    connectSocket.setReadyDistroyAndStopCheckReconnect();
//                }
//
//                String errorMessage = responseModel.getError();
//
//                LOGGER.error(errorMessage);
//
//                // Server close the session after send text to desktop when the return status is not SC_OK,
//                // so the desktop don't have to close socket again.
////                try {
////                    // repository 並沒有attached新建立的socket，因此只使用disconnect()，不能用close()
////                    getSession().disconnect();
////
////                    LOGGER.error("Disconnect socket for: " + errorMessage);
////                } catch (Exception e) {
////                        /* ignored */
////                }
//
//                String userId = connectSocket.getUserId();
//
//                if (userId != null) {
//                    AccountState.getInstance().setState(userId, AccountState.State.INACTIVATED);
//                }
//
//                if (connectSocket.getConnectResponseState() != null) {
//                    connectSocket.getConnectResponseState().setState(status, errorMessage, null);
//                }
//            }
//        } catch (Exception e) {
//            String userId = connectSocket.getUserId();
//
//            String errorMessage = ClopuccinoMessages.getMessage("error.active.user3", (userId != null ? userId : "Not specified yet."), message, e.getMessage());
//
//            LOGGER.error(errorMessage, e);
//
//            // Server close the session after send text to desktop when the return status is not SC_OK,
//            // so the desktop don't have to close socket again.
////            try {
////                // repository 並沒有attached新建立的socket，因此只使用disconnect()，不能用close()
////                getSession().disconnect();
////
////                LOGGER.error("Disconnect socket for: " + errorMessage);
////            } catch (Exception ex) {
////                // ignored
////            }
//
//            if (userId != null) {
//                AccountState.getInstance().setState(userId, AccountState.State.INACTIVATED);
//            }
//
//            if (connectSocket.getConnectResponseState() != null) {
//                connectSocket.getConnectResponseState().setState(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage, null);
//            }
//        } finally {
//            connectSocket.getCloseLatch().countDown();
//        }
//    }
}
