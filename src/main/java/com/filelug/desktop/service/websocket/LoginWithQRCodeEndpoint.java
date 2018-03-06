package com.filelug.desktop.service.websocket;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelug.desktop.ClopuccinoMessages;
import com.filelug.desktop.Constants;
import com.filelug.desktop.PropertyConstants;
import com.filelug.desktop.Utility;
import com.filelug.desktop.model.*;
import com.filelug.desktop.service.*;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * <code>LoginWithQRCodeEndpoint</code> receives the following messages from server, in order:
 * <ol>
 *     <li>Sid.GET_QR_CODE_V2: QR code from server</li>
 *     <li>Sid.LOGIN_BY_QR_CODE_V2: session id, user, and computer information after QR code verified and automatically login by the server</li>
 * </ol>
 *
 * @author masonhsieh
 * @version 1.0
 */
@ClientEndpoint
public class LoginWithQRCodeEndpoint {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(LoginWithQRCodeEndpoint.class.getSimpleName());

    private final UserService userService;

    public LoginWithQRCodeEndpoint() {
        userService = new DefaultUserService();
    }


    @OnOpen
    public void onOpen(Session session) {
        // DEBUG
//        LOGGER.info("Websocket opened with session: " + session.getId());

        session.setMaxBinaryMessageBufferSize(Integer.MAX_VALUE);
        session.setMaxTextMessageBufferSize(Integer.MAX_VALUE);
        session.setMaxIdleTimeout(Constants.IDLE_TIMEOUT_IN_SECONDS_TO_CONNECT_SOCKET * 1000);
    }

    @OnMessage
    public void onMessage(Session session, String text) {
        // DEBUG
//        LOGGER.info("Message received: " + text);

        ObjectMapper mapper = Utility.createObjectMapper();

        try {
            JsonNode jsonObject = mapper.readTree(text);

            JsonNode sidNode = jsonObject.findValue("sid");

            if (sidNode != null && sidNode.isNumber()) {
                int sid = sidNode.intValue();

                if (sid == Sid.GET_QR_CODE_V2) {
                    onGetQRCodeMessage(jsonObject);
                } else if (sid == Sid.LOGIN_BY_QR_CODE_V2) {
                    onLoginByQRCodeMessage(session, jsonObject);
                }

            }

        } catch (Exception e) {
            LOGGER.error("Error on receiving message:\n" + text, e);
        }
    }

    // on getting QR code from server
    private void onGetQRCodeMessage(JsonNode jsonNode) {
        /*
        {
            "sid" : 21101,
            "status" : 200,
            "error" : "",
            "timestamp" : 1386505788544, // date time in millis
            "qr-code" : "FOIF8QJO3I48OJFDOFHDUOGBLDSHCNOIFHIEHFUHGDT67GJKBHEIMDOI"
        }
        */

        JsonNode statusNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_STATUS);

        if (statusNode != null && statusNode.asInt(0) > 0) {
            int status = statusNode.asInt();

            if (HttpServletResponse.SC_OK == status) {
                JsonNode qrCodeNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_QR_CODE);

                if (qrCodeNode != null && qrCodeNode.textValue() != null) {
                    String code = qrCodeNode.textValue();

                    // remove PROPERTY_NAME_QR_CODE first to dispose the current QRCodeWindow, if any
                    String oldQRCode = Utility.getPreference(PropertyConstants.PROPERTY_NAME_QR_CODE, "");

                    if (oldQRCode.trim().length() > 0) {
                        Utility.removePreference(PropertyConstants.PROPERTY_NAME_QR_CODE);

                        // sleep for 0.3 seconds to make time to dispose the current window before new window shows.

                        try {
                            Thread.sleep(300);
                        } catch (Throwable t) {
                            // ignored.
                        }
                    }

                    Utility.putPreference(PropertyConstants.PROPERTY_NAME_QR_CODE, code);
                }
            } else {
                String errorMessage = ServiceUtilities.findErrorMessageFromResponseNode(jsonNode, "No error message");

                LOGGER.error("Error on generate QR code from server: " + errorMessage);

                // TODO: Error to generate qr code from server, need try again
            }
        }
    }

    // on getting the information when login by QR code
    private void onLoginByQRCodeMessage(Session session, JsonNode jsonNode) {
        /*
        {
            "sid": 21102,
            "status": 200,
            "timestamp": 1483160168082,
            "account": "3C4F352D2DB829D033C64B8A379D27F6CBB5C328352B0957D68B6681BE40652E00D531BF43E28ABA3762D9960BFA45E0E1C26966E04EA9A0FF0EB4FDEC87272D",
            "country-id": "TW",
            "country-code" : 886,
            "phone" : "975009123", // 號碼不在前面加上 '0' 
            "phone-with-country" : "+886975009123",
            "nickname": "小威（測試）",
            "show-hidden": false,
            "session-id": "060551E556F945E1187CD17F48C453B9B8147C8F8967AED7DEECED8F1FF043DCFCCC263953DC6E408029A976FEF99D4646F6CB5E935D75A94DB8FAEA223B8307",
            "computer-id": 5,
            "computer-group": "GENERAL",
            "computer-name": "MASONHSIEH_3",
            "recovery-key": "caee616419f4e3804a60b820c2be687c465383f2cfd5eaa74b097e774e165c82",
            "lug-server-id": "aa"
        }
        */

        try {
            // this will close the QRCodeWindow as well
            Utility.removePreference(PropertyConstants.PROPERTY_NAME_QR_CODE);

            JsonNode statusNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_STATUS);

            if (statusNode == null || !statusNode.isInt() || statusNode.asInt(0) != 200) {
                String errorMessage = ClopuccinoMessages.getMessage("error.login.try.again");

                JsonNode errorNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_ERROR);

                if (errorNode != null && errorNode.textValue() != null) {
                    errorMessage = errorMessage + "\n" + errorNode.textValue();
                }

                int option = Utility.showConfirmDialogWithMessageInTextArea(null, errorMessage, ClopuccinoMessages.getMessage("error"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));

                if (option == JOptionPane.OK_OPTION) {
                    // re-get QR code again
                    loginWithQRCode();
                }
            } else {
                int status = statusNode.asInt();

                if (HttpServletResponse.SC_OK == status) {
                    JsonNode accountNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_ACCOUNT);

                    JsonNode countryIdNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_COUNTRY_ID);

                    JsonNode countryCodeNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_COUNTRY_CODE);

                    JsonNode phoneNumberNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_PHONE_NUMBER);

                    JsonNode phoneNumberWithCountryNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_PHONE_NUMBER_WITH_COUNTRY);

                    JsonNode nicknameNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_NICKNAME);

                    JsonNode showHiddenNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_SHOW_HIDDEN);

                    JsonNode sessionIdNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_SESSION_ID);

                    JsonNode computerIdNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_COMPUTER_ID);

                    JsonNode computerGroupNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP);

                    JsonNode computerNameNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME);

                    JsonNode recoveryKeyNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY);

                    JsonNode lugServerIdNode = jsonNode.get(PropertyConstants.PROPERTY_NAME_LUG_SERVER_ID);

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
                        // nickname could be null
//                    } else if (nicknameNode == null || nicknameNode.textValue() == null) {
//                        errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "nickname");
                    } else if (showHiddenNode == null || !showHiddenNode.isBoolean()) {
                        errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "showHidden");
                    } else if (sessionIdNode == null || sessionIdNode.textValue() == null) {
                        errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "session id");
                    } else if (computerIdNode == null || !computerIdNode.isNumber()) {
                        errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "computer id");
                    } else if (computerGroupNode == null || computerGroupNode.textValue() == null) {
                        errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "computer group");
                    } else if (computerNameNode == null || computerNameNode.textValue() == null) {
                        errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "computer name");
                    } else if (recoveryKeyNode == null || recoveryKeyNode.textValue() == null) {
                        errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "recovery key");
                    } else if (lugServerIdNode == null || lugServerIdNode.textValue() == null) {
                        errorMessage = ClopuccinoMessages.getMessage("cannot.empty", "lug server id");
                    } else {
                        errorMessage = null;
                    }

                    if (errorMessage != null) {
                        LOGGER.error("Error on receiving message from server to login by QR code.\n" + errorMessage);

                        SwingUtilities.invokeLater(() -> {
                            String displayMessage = Utility.localizedString("failed.login.user.need.restart");

                            int option = Utility.showConfirmDialog(null, displayMessage, Utility.localizedString("error"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));
//                            int option = JOptionPane.showConfirmDialog(null, displayMessage, Utility.localizedString("error"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));

                            if (option == JOptionPane.OK_OPTION) {
                                System.exit(1);
                            }
                        });
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
//                        int countryCode = countryCodeNode.asInt(0);
                        String phoneNumber = phoneNumberNode.textValue();
//                        String phoneNumberWithCountry = phoneNumberWithCountryNode.textValue();
                        boolean showHidden = showHiddenNode.asBoolean();
                        String sessionId = sessionIdNode.textValue();
                        long computerId = computerIdNode.longValue();
                        String computerGroup = computerGroupNode.textValue();
                        String computerName = computerNameNode.textValue();
                        String recoveryKey = recoveryKeyNode.textValue();
                        String lugServerId = lugServerIdNode.textValue();

                        // DEBUG
//                        LOGGER.info(String.format("user country code: %d, phone number with country: %s", countryCode, phoneNumberWithCountry));

                        // save the user as administrator because there's only administrator can login using QR code.
                        // Non-administrator user can only login by the administrator's device.

                        User user = userService.findLocalUserById(account);

                        if (user == null) {
                            // Allowed alias if it's admin, otherwise false

                            user = new User(account, countryId, phoneNumber, sessionId, lugServerId, nickname, showHidden, 0L, Boolean.TRUE, AccountState.State.INACTIVATED, Boolean.TRUE, Boolean.TRUE);

                            userService.createLocalUser(user);

                            User newUser = userService.findLocalUserById(account);

                            // notify user created

                            UserChangedService.getInstance().userCreated(newUser);
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
                            user.setAdmin(Boolean.TRUE);
                            user.setApproved(Boolean.TRUE);
                            user.setAllowAlias(Boolean.TRUE);

                            userService.updateLocalUser(user);

                            User newUser = user.copy();

                            // notify user updated

                            UserChangedService.getInstance().userUpdated(oldUser, newUser);
                        }

                        // save computer information

                        // computer id
                        Utility.putPreferenceLong(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, computerId);

                        // computer name
                        Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, computerName);

                        // computer group
                        Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, computerGroup);

                        // recovery key
                        Utility.putPreference(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, recoveryKey);

                        // save session - saved to table: auth_user
//                        Utility.putPreference(PropertyConstants.PROPERTY_NAME_SESSION_ID, sessionId);

                        // close session before connecting to the server

                        if (session != null && session.isOpen()) {
                            try {
                                session.close();
                            } catch (IOException e) {
                                LOGGER.error("Error on closing login session.", e);
                            }
                        }

                        // connect to computer by admin, will handled by ConnectSocket.
                        // don't have to get new lug server id by invoking ComputerService.dispatchConnection(userId)
                        // because the current lug server id is just got from server.
                        connectFromComputer(account, sessionId, lugServerId);
                    }
                } else {
                    String errorMessage = ServiceUtilities.findErrorMessageFromResponseNode(jsonNode, "No error message");

                    LOGGER.error("Error to loging from server: " + errorMessage);

                    // TODO: Error to login from server, need try again
                }
            }
        } finally {
            // if not closed

            if (session != null && session.isOpen()) {
                try {
                    session.close();
                } catch (IOException e) {
                    LOGGER.error("Error on closing login session.", e);
                }
            }
        }
    }

    private void connectFromComputer(String userId, String sessionId, String lugServerId) {
        // connect using administrator

        ConnectModel connectModel = new ConnectModel();

        // Actually, we don't disconnect for the same user before connecting, so it's time to refactory to ignore user id.
        // computerId is not used, but userId IS USED when connecting from computer because we need use it to disconnect current connection, if any.

        connectModel.setSid(Sid.CONNECT_V2);
        connectModel.setAccount(userId);
        connectModel.setSessionId(sessionId);
        connectModel.setLugServerId(lugServerId);
        connectModel.setLocale(Utility.getApplicationLocale());

        // device-token

        DeviceToken deviceTokenObject = Utility.prepareDeviceTokenObject();

        connectModel.setDeviceToken(deviceTokenObject);

        // properties

        Properties properties = Utility.prepareSystemProperties();

        connectModel.setProperties(properties);

        ConnectResponseState connectResponseState = new ConnectResponseState(connectModel);

        try {
            userService.connectFromComputer(connectResponseState);
        } catch (Exception e) {
            User user = userService.findLocalUserById(userId);

            if (user != null && user.getState() != AccountState.State.INACTIVATED) {
                User oldUser = user.copy();

                user.setState(AccountState.State.INACTIVATED);

                userService.updateLocalUser(user);

                User newUser = userService.findLocalUserById(userId);

                UserChangedService.getInstance().userUpdated(oldUser, newUser);
            }

            String message;

            if (UnknownHostException.class.isInstance(e) || HttpHostConnectException.class.isInstance(e) || ConnectTimeoutException.class.isInstance(e)) {
                message = Utility.localizedString("no.network");
            } else {
                message = e.getMessage().trim().length() > 0 ? Utility.localizedString("error.active.user", e.getMessage()) : Utility.localizedString("error.active.user2");
            }

            int option = Utility.showConfirmDialogWithMessageInTextArea(null, message, Utility.localizedString("error"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));

            if (option == JOptionPane.OK_OPTION) {
                // re-get QR code again
                loginWithQRCode();
            }
        }
    }

    private void loginWithQRCode() {
        try {
            userService.loginUserOrGetQRCode();
        } catch (Exception e) {
            LOGGER.error("Failed to login or get QR code.", e);

            // TODO: close Session and prompt to try again.

            String errorMessage = ClopuccinoMessages.getMessage("error.connect.to.server.try.again");

            int option = Utility.showConfirmDialog(null, errorMessage, ClopuccinoMessages.getMessage("error"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));

            if (option == JOptionPane.YES_OPTION) {
                loginWithQRCode();
            }
        }
    }
}
