package com.filelug.desktop.service;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.filelug.desktop.ClopuccinoMessages;
import com.filelug.desktop.Constants;
import com.filelug.desktop.PropertyConstants;
import com.filelug.desktop.Utility;
import com.filelug.desktop.dao.UserDao;
import com.filelug.desktop.db.DatabaseAccess;
import com.filelug.desktop.db.HyperSQLDatabaseAccess;
import com.filelug.desktop.model.User;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * <code>DefaultComputerService</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public final class DefaultComputerService implements ComputerService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("SERVICE_COMPUTER");

    private final UserDao userDao;

    private final UserService userService;

    private final ServiceUtilities serviceUtilities;

    public DefaultComputerService() {
        this(null);
    }

    public DefaultComputerService(DatabaseAccess dbAccess) {
        DatabaseAccess localDbAccess;

        if (dbAccess == null) {
            localDbAccess = new HyperSQLDatabaseAccess();
        } else {
            localDbAccess = dbAccess;
        }

        userDao = new UserDao(localDbAccess);

        userService = new DefaultUserService(localDbAccess);

        serviceUtilities = new ServiceUtilities();
    }

//    @Override
//    public int createComputer(ComputerModel computerModel) throws Exception {
//        String userId = computerModel.getAccount();
//        String encryptedPassword = computerModel.getPassword();
//        String nickname = computerModel.getNickname();
////        String verification = computerModel.getVerification();
//
//        if (nickname == null || nickname.trim().length() < 1) {
//            nickname = Utility.defaultNickname();
////            verification = Utility.generateVerification(userId, encryptedPassword, nickname);
//
//            computerModel.setNickname(nickname);
////            computerModel.setVerification(verification);
//        }
//
//        String computerGroup = computerModel.getGroupName();
//        String computerName = computerModel.getComputerName();
//
//        int status = 0;
//
//        /* do not check if nickname is empty, only make sure tha it is not null */
//        if (userId == null || encryptedPassword == null || nickname == null || computerGroup == null || computerName == null
//            || userId.trim().length() < 1 || encryptedPassword.trim().length() < 1 || computerGroup.trim().length() < 1 || computerName.trim().length() < 1
//            || encryptedPassword.equals(DigestUtils.sha256Hex(""))) {
//            String errorMessage = ClopuccinoMessages.getMessage("at.least.one.empty.account.property");
//
//            throw new IllegalArgumentException(errorMessage);
//        } else if (computerName.trim().length() < Constants.MIN_COMPUTER_NAME_LENGTH || computerName.trim().length() > Constants.MAX_COMPUTER_NAME_LENGTH) {
//            String errorMessage = ClopuccinoMessages.getMessage("computer.name.length.limit", String.valueOf(Constants.MIN_COMPUTER_NAME_LENGTH), String.valueOf(Constants.MAX_COMPUTER_NAME_LENGTH));
//
//            throw new IllegalArgumentException(errorMessage);
//        } else {
//            String oldComputerGroup = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, "");
//            String oldComputerName = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, "");
//            String oldRecoveryKey = Utility.getPreference(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, "");
//
//            ObjectMapper mapper = Utility.createObjectMapper();
//
//            if (computerGroup.equalsIgnoreCase(oldComputerGroup) && computerName.equalsIgnoreCase(oldComputerName) && oldRecoveryKey.trim().length() > 0) {
//                /* restore computer */
//
//                computerModel.setComputerName(oldComputerName);
//                computerModel.setGroupName(oldComputerGroup);
//                computerModel.setRecoveryKey(oldRecoveryKey);
//            } else {
//                /* new computer */
//
//                computerModel.setRecoveryKey(null);
//            }
//
//            String path = "computer/create";
//
//            computerModel.setVerification(Utility.generateVerification(userId, encryptedPassword, nickname));
//
//            String requestJson = mapper.writeValueAsString(computerModel);
//
//            try {
//                HttpResponse response = serviceUtilities.doPostJson(null, path, requestJson);
//
//                status = response.getStatusLine().getStatusCode();
//
//                String responseString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));
//
//                if (status == HttpServletResponse.SC_OK) {
//                    /* save to the preferences */
//
//                    Computer computer = mapper.readValue(responseString, Computer.class);
//
//                    // computer id
//                    Utility.putPreferenceLong(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, computer.getComputerId());
//
//                    // computer name
//                    ComputerNameState.getInstance().setComputerName(computer.getComputerName());
////                    Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, computer.getComputerName());
//
//                    // computer group
//                    Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, computer.getGroupName());
//
//                    // recovery key
//                    Utility.putPreference(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, computer.getRecoveryKey());
//                } else {
//                    LOGGER.error(Utility.localizedString("error.create.computer", computerModel.getComputerName(), String.valueOf(status), responseString));
//                }
//            } catch (UnknownHostException | HttpHostConnectException | ConnectTimeoutException e) {
//                status = HttpServletResponse.SC_REQUEST_TIMEOUT;
//
//                String message = ClopuccinoMessages.getMessage("no.network");
//
//                LOGGER.error(Utility.localizedString("error.create.computer", computerModel.getComputerName(), String.valueOf(status), message), e);
//            } catch (Exception e) {
//                status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
//
//                LOGGER.error(Utility.localizedString("error.create.computer", computerModel.getComputerName(), String.valueOf(status), e.getMessage()), e);
//            }
//        }
//
//        return status;
//    }

//    @Override
//    public boolean isComputerNameAvailable(ComputerModel computerModel) throws Exception {
//        String userId = computerModel.getAccount();
//        String encryptedPassword = computerModel.getPassword();
//        String nickname = computerModel.getNickname();
////        String verification = computerModel.getVerification();
//
//        if (nickname == null || nickname.trim().length() < 1) {
//            nickname = Utility.defaultNickname();
////            verification = Utility.generateVerification(userId, encryptedPassword, nickname);
//
//            computerModel.setNickname(nickname);
////            computerModel.setVerification(verification);
//        }
//
//        String computerName = computerModel.getComputerName();
//        String computerGroup = computerModel.getGroupName();
//
//        /* do not check if nickname is empty, only make sure tha it is not null */
//        if (userId == null || encryptedPassword == null || nickname == null || computerGroup == null || computerName == null
//            || userId.trim().length() < 1 || encryptedPassword.trim().length() < 1 || computerGroup.trim().length() < 1 || computerName.trim().length() < 1
//            || encryptedPassword.equals(DigestUtils.sha256Hex(""))) {
//            String errorMessage = ClopuccinoMessages.getMessage("at.least.one.empty.account.property");
//
//            throw new IllegalArgumentException(errorMessage);
//        } else if (computerName.trim().length() < Constants.MIN_COMPUTER_NAME_LENGTH || computerName.trim().length() > Constants.MAX_COMPUTER_NAME_LENGTH) {
//            String errorMessage = ClopuccinoMessages.getMessage("computer.name.length.limit", String.valueOf(Constants.MIN_COMPUTER_NAME_LENGTH), String.valueOf(Constants.MAX_COMPUTER_NAME_LENGTH));
//
//            throw new IllegalArgumentException(errorMessage);
//        } else {
//            /* Check if there are old computer information in preferences */
//            String oldComputerGroup = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, "");
//            String oldComputerName = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, "");
//            String oldRecoveryKey = Utility.getPreference(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, "");
//
//            ObjectMapper mapper = Utility.createObjectMapper();
//
//            if (computerGroup.equalsIgnoreCase(oldComputerGroup) && computerName.equalsIgnoreCase(oldComputerName) && oldRecoveryKey.trim().length() > 0) {
//                /* restore the old computer name */
//
//                computerModel.setComputerName(oldComputerName);
//                computerModel.setGroupName(oldComputerGroup);
//                computerModel.setRecoveryKey(oldRecoveryKey);
//            } else {
//                computerModel.setRecoveryKey(null);
//            }
//
//            computerModel.setVerification(Utility.generateVerification(userId, encryptedPassword, nickname));
//
//            String path = "computer/check-available";
//
//            String requestJson = mapper.writeValueAsString(computerModel);
//
//            try {
//                HttpResponse response = serviceUtilities.doPostJson(null, path, requestJson);
//
//                int status = response.getStatusLine().getStatusCode();
//
//                if (status == HttpServletResponse.SC_OK) {
//                    return true;
//                } else {
//                    String responseString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));
//
//                    String message = ClopuccinoMessages.getMessage("status.and.reason", String.valueOf(status), responseString);
//
////                    String message = String.format("Computer name: %s, Status: %d, Reason: %s", computerModel.getComputerName(), status, responseString);
//
//                    LOGGER.info(Utility.localizedString("error.check.computer.name", message));
//
//                    if (status != HttpServletResponse.SC_CONFLICT) {
//                        throw new Exception(message);
//                    } else {
//                        return false;
//                    }
//                }
//            } catch (UnknownHostException | HttpHostConnectException | ConnectTimeoutException e) {
//                String message = ClopuccinoMessages.getMessage("status.and.reason", String.valueOf(HttpServletResponse.SC_REQUEST_TIMEOUT), ClopuccinoMessages.getMessage("no.network"));
//
//                String responseMessage = Utility.localizedString("error.check.computer.name", message);
//
//                LOGGER.error(responseMessage, e);
//
//                throw new Exception(message);
//            } catch (Exception e) {
//                String responseMessage = Utility.localizedString("error.check.computer.name", e.getMessage());
//
//                LOGGER.error(responseMessage, e);
//
//                throw new Exception(e.getMessage());
//            }
//        }
//    }
//
//    @Override
//    public int changeComputer(ChangeComputerModel computerModel) throws Exception {
//        int status;
//
//        ObjectMapper mapper = Utility.createObjectMapper();
//
//        try {
//            String userId = computerModel.getAccount();
//            String password = computerModel.getPassword();
//            String nickname = computerModel.getNickname();
//
//            if (nickname == null || nickname.trim().length() < 1) {
//                nickname = Utility.defaultNickname();
//
//                computerModel.setNickname(nickname);
//            }
//
//            Long computerId = computerModel.getComputerId();
////            String oldComputerGroup = computerModel.getOldGroupName();
////            String oldComputerName = computerModel.getOldComputerName();
//            String oldRecoveryKey = computerModel.getOldRecoveryKey();
//            String newComputerGroup = computerModel.getNewGroupName();
//            String newComputerName = computerModel.getNewComputerName();
//
//            /* do not check if nickname is empty, only make sure tha it is not null */
//            if (userId == null || password == null || nickname == null || computerId == null || oldRecoveryKey == null || newComputerGroup == null || newComputerName == null
//                || userId.trim().length() < 1 || password.trim().length() < 1 || computerId < 0 || oldRecoveryKey.trim().length() < 1 || oldRecoveryKey.trim().length() < 1 || newComputerGroup.trim().length() < 1 || newComputerName.trim().length() < 1
//                || password.equals(DigestUtils.sha256Hex(""))) {
//                String errorMessage = ClopuccinoMessages.getMessage("at.least.one.empty.account.property");
//
//                throw new IllegalArgumentException(errorMessage);
//            } else {
//                String path = "computer/change";
//
//                computerModel.setVerification(Utility.generateVerification(userId, password, nickname));
//
//                String requestJson = mapper.writeValueAsString(computerModel);
//
//                HttpResponse response = serviceUtilities.doPostJson(null, path, requestJson);
//
//                status = response.getStatusLine().getStatusCode();
//
//                String responseString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));
//
//                if (status == HttpServletResponse.SC_OK) {
//                    /* save or update values in the preferences */
//
//                    Computer computer = mapper.readValue(responseString, Computer.class);
//
//                    String computerGroup = computer.getGroupName();
//                    String computerName = computer.getComputerName();
//                    String recoveryKey = computer.getRecoveryKey();
//
//                    Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, computerGroup);
//
//                    ComputerNameState.getInstance().setComputerName(computerName);
////                    Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, computerName);
//
//                    Utility.putPreference(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, recoveryKey);
//
//                    ComputerNameState computerNameState = ComputerNameState.getInstance();
//                    computerNameState.setComputerName(computerName);
//                } else {
//                    LOGGER.error(Utility.localizedString("error.change.computer", computerModel.getNewComputerName(), String.valueOf(status), responseString != null ? responseString : "ERROR"));
//                }
//            }
//        } catch (UnknownHostException | HttpHostConnectException | ConnectTimeoutException e) {
//            status = HttpServletResponse.SC_REQUEST_TIMEOUT;
//
//            String message = ClopuccinoMessages.getMessage("no.network");
//
//            LOGGER.error(Utility.localizedString("error.change.computer", computerModel.getNewComputerName(), String.valueOf(status), message), e);
//        } catch (Exception e) {
//            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
//
//            LOGGER.error(Utility.localizedString("error.change.computer", computerModel.getNewComputerName(), String.valueOf(status), e.getMessage()), e);
//        }
//
//        return status;
//    }

//    @Override
//    public int deleteComputer(ComputerModel computerModel) throws Exception {
//        ObjectMapper mapper = Utility.createObjectMapper();
//
//        int status;
//
//        try {
//            String userId = computerModel.getAccount();
//            String password = computerModel.getPassword();
//            String nickname = computerModel.getNickname();
//
//            if (nickname == null || nickname.trim().length() < 1) {
//                nickname = Utility.defaultNickname();
//                computerModel.setNickname(nickname);
//            }
//
//            Long computerId = computerModel.getComputerId();
//
//            String recoveryKey = computerModel.getRecoveryKey();
//
//            /* do not check if nickname is empty, only make sure tha it is not null */
//            if (userId == null || password == null || nickname == null || computerId == null || recoveryKey == null
//                || userId.trim().length() < 1 || password.trim().length() < 1 || computerId < 0 || recoveryKey.trim().length() < 1
//                || password.equals(DigestUtils.sha256Hex(""))) {
//                String errorMessage = ClopuccinoMessages.getMessage("at.least.one.empty.account.property");
//
//                throw new IllegalArgumentException(errorMessage);
//            } else {
//                String path = "computer/delete";
//
//                computerModel.setVerification(Utility.generateVerification(userId, password, nickname));
//
//                String requestJson = mapper.writeValueAsString(computerModel);
//
//                HttpResponse response = serviceUtilities.doPostJson(null, path, requestJson);
//
//                status = response.getStatusLine().getStatusCode();
//
//                String responseString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));
//
//                if (status == HttpServletResponse.SC_OK) {
//                    /* delete values in the preferences - no more deletion */
//
////                    Utility.removePreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP);
////                    Utility.removePreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME);
////                    Utility.removePreference(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY);
//                } else {
//                    LOGGER.error(Utility.localizedString("error.delete.computer", computerModel.getComputerName(), String.valueOf(status), responseString != null ? responseString : ""));
//                }
//            }
//        } catch (UnknownHostException | HttpHostConnectException | ConnectTimeoutException e) {
//            status = HttpServletResponse.SC_REQUEST_TIMEOUT;
//
//            String message = ClopuccinoMessages.getMessage("no.network");
//
//            LOGGER.error(Utility.localizedString("error.delete.computer", computerModel.getComputerName(), String.valueOf(status), message), e);
//        } catch (Exception e) {
//            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
//
//            LOGGER.error(Utility.localizedString("error.delete.computer", computerModel.getComputerName(), String.valueOf(status), e.getMessage()), e);
//        }
//
//        return status;
//    }

//    @Override
//    public String dispatchConnection(ConnectModel connectModel) throws Exception {
//        String userId = connectModel.getAccount();
////        String encryptedPassword = connectModel.getPassword();
//        String nickname = connectModel.getNickname();
//
//        Long computerId = connectModel.getComputerId();
//
//        /* do not check if nickname is empty, only make sure tha it is not null */
//        if (userId == null || nickname == null || computerId == null || userId.trim().length() < 1 || nickname.trim().length() < 1) {
//            String errorMessage = ClopuccinoMessages.getMessage("at.least.one.empty.account.property");
//
//            throw new IllegalArgumentException(errorMessage);
//        } else {
//            ObjectMapper mapper = Utility.createObjectMapper();
//
//            String path = "computer/dispatch";
//
//            try {
//                connectModel.setVerification(Utility.generateVerification(userId, encryptedPassword, nickname));
//
//                String requestJson = mapper.writeValueAsString(connectModel);
//
//                /* save or update truststore */
//                HttpResponse response = serviceUtilities.doPostJsonAndSaveCertificate(path, requestJson, true);
////                HttpResponse response = doPostJson(null, path, requestJson);
//
//                int status = response.getStatusLine().getStatusCode();
//
//                String responseString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));
//
//                if (status == HttpServletResponse.SC_OK) {
//                    UserComputer userComputer = mapper.readValue(responseString, UserComputer.class);
//
//                    return userComputer.getLugServerId();
//                } else if (status == Constants.HTTP_STATUS_COMPUTER_NOT_FOUND) { // computer not found
//                    String message = ClopuccinoMessages.getMessage("computer.not.found");
//
//                    LOGGER.error(message + " Computer id: " + computerId);
//
//                    // Do not reset just because computer not found. It may comes from connection error to the repository DB.
//
////                    /* stop reconnect process */
////
////                    ConnectSocket currentSocket = ConnectSocket.getInstance(userId);
////
////                    if (currentSocket != null) {
////                        currentSocket.setReadyDistroyAndStopCheckReconnect();
////                    }
////
////                    // reset application and prompt user
////                    SystemService systemService = new DefaultSystemService();
////                    systemService.resetApplicationAndPromptForComputerNotFound();
//
//                    throw new Exception(message);
//                } else {
//                    String message = ClopuccinoMessages.getMessage("status.and.reason", String.valueOf(status), responseString);
//
//                    throw new Exception(message);
//                }
//            } catch (UnknownHostException | HttpHostConnectException | ConnectTimeoutException e) {
//                String message = ClopuccinoMessages.getMessage("status.and.reason", String.valueOf(HttpServletResponse.SC_REQUEST_TIMEOUT), ClopuccinoMessages.getMessage("no.network"));
//
//                String responseMessage = Utility.localizedString("error.check.computer.name", message);
//
//                LOGGER.error(responseMessage, e);
//
//                throw new Exception(message);
//            } catch (Exception e) {
//                String message = Utility.localizedString("error.prepare.connection", e.getMessage());
//
//                LOGGER.error(message, e);
//
//                throw new Exception(e.getMessage());
//            }
//        }
//    }

//    @Override
//    public String dispatchConnection(String userId) throws Exception {
//        String lugServerId = null;
//
//        try {
//            ObjectMapper mapper = Utility.createObjectMapper();
//
//            String path = "computer/dispatch2";
//
//            String sessionId = userDao.findSessionIdById(userId);
//
//            if (sessionId == null) {
//                throw new Exception(ClopuccinoMessages.getMessage("session.not.exists"));
//            } else {
//                // save or update truststore before invoking the dispatch service
//
//                Set<Header> headers = new HashSet<>();
//
//                headers.add(new BasicHeader(Constants.HTTP_HEADER_FILELUG_SESSION_ID_NAME, sessionId));
//
//                HttpResponse response = serviceUtilities.doPostJson(null, path, headers);
//
//                int status = response.getStatusLine().getStatusCode();
//
//                String responseString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));
//
//                if (status == HttpServletResponse.SC_OK) {
//                    JsonNode responseNode = mapper.readTree(responseString);
//
//                    if (responseNode != null && responseNode.get(PropertyConstants.PROPERTY_NAME_LUG_SERVER_ID) != null) {
//                        lugServerId = responseNode.get(PropertyConstants.PROPERTY_NAME_LUG_SERVER_ID).textValue();
//                    }
//                } else {
//                    String message = ClopuccinoMessages.getMessage("status.and.reason", String.valueOf(status), responseString);
//
//                    throw new Exception(message);
//                }
//            }
//        } catch (UnknownHostException | HttpHostConnectException | ConnectTimeoutException e) {
//            String message = ClopuccinoMessages.getMessage("status.and.reason", String.valueOf(HttpServletResponse.SC_REQUEST_TIMEOUT), ClopuccinoMessages.getMessage("no.network"));
//
//            String responseMessage = Utility.localizedString("error.check.computer.name", message);
//
//            LOGGER.error(responseMessage, e);
//
//            throw new Exception(message);
//        } catch (Exception e) {
//            String message = Utility.localizedString("error.prepare.connection", e.getMessage());
//
//            LOGGER.error(message, e);
//
//            throw new Exception(e.getMessage());
//        }
//
//        return lugServerId;
//    }

    @Override
    public boolean checkComputerExisting() throws Exception {
        // Use true as default
        boolean found = true;

        try {
            User admin = userDao.findAdministrator();

            if (admin != null) {
                String userId = admin.getAccount();

                String sessionId = admin.getSessionId();

                String lugServerId = admin.getLugServerId();

                long computerId = Utility.getPreferenceLong(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, -1);

                String recoveryKey = Utility.getPreference(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, "");

                String computerGroup = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, "");

                String computerName = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, "");

                String verification = Utility.generateVerificationToCheckComputerExists(userId, computerId, recoveryKey);

                // path

                String path = "computer/exist";

                // header

                Set<Header> headers = new HashSet<>();

                headers.add(new BasicHeader(Constants.HTTP_HEADER_FILELUG_SESSION_ID_NAME, sessionId));

                // jsonString

                ObjectMapper mapper = Utility.createObjectMapper();

                ObjectNode rootNode = mapper.createObjectNode();

                rootNode.put(PropertyConstants.PROPERTY_NAME_USER_ID, userId);
                rootNode.put(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, computerId);
                rootNode.put(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, recoveryKey);
                rootNode.put(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, computerGroup);
                rootNode.put(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, computerName);
                rootNode.put(PropertyConstants.PROPERTY_NAME_VERIFICATION, verification);
                rootNode.put(PropertyConstants.PROPERTY_NAME_LOCALE, Utility.getApplicationLocale());

                String jsonString = mapper.writeValueAsString(rootNode);

                HttpResponse response = serviceUtilities.doPostJson(lugServerId, path, headers, null, jsonString);

                int status = response.getStatusLine().getStatusCode();

                if (status == HttpServletResponse.SC_OK) {
                    found = true;
                } else if (status == Constants.HTTP_STATUS_COMPUTER_NOT_FOUND) {
                    found = false;
                } else {
                    String responseString;

                    HttpEntity entity = response.getEntity();

                    if (entity != null) {
                        responseString = EntityUtils.toString(entity, Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));
                    } else {
                        responseString = "";
                    }

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

        return found;
    }

    @Override
    public void removeAllUsersFromComputer(FutureCallback<HttpResponse> callback) throws Exception {
        User admin = userDao.findAdministrator();

        if (admin != null) {
            String userId = admin.getAccount();

            String lugServerId = admin.getLugServerId();

            // get new session id if invalid
            String adminSessionId = userService.validateSessionAndGetNewIfNeededForUser(userId);

            if (adminSessionId == null) {
                adminSessionId = admin.getSessionId();
            }

            // headers

            Header header = new BasicHeader(Constants.HTTP_HEADER_FILELUG_SESSION_ID_NAME, adminSessionId);
            Set<Header> headers = new HashSet<>();
            headers.add(header);

            // json string

            long computerId = Utility.getPreferenceLong(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, 0L);

            String verification = Utility.generateRemoveAdminVerification(userId, computerId, adminSessionId);

            ObjectMapper mapper = Utility.createObjectMapper();

            ObjectNode rootNode = mapper.createObjectNode();

            rootNode.put(PropertyConstants.PROPERTY_NAME_VERIFICATION, verification);

            String jsonString = mapper.writeValueAsString(rootNode);

            serviceUtilities.doAsyncPost(lugServerId, "computer/rmusers", headers, jsonString, callback, Constants.SO_TIMEOUT_IN_SECONDS_DEFAULT * 1000, Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000);
        } else {
            throw new Exception("The administrator of this computer not found.");
        }
    }

//    @Override
//    public void removeAllUsersFromComputer(FutureCallback<HttpResponse> callback) throws Exception {
//        User admin = userDao.findAdministrator();
//
//        if (admin != null) {
//            String userId = admin.getAccount();
//
//            String nickname = admin.getNickname();
//
//            String lugServerId = admin.getLugServerId();
//
//            // get new session id if invalid
//            String adminSessionId = userService.validateSessionAndGetNewIfNeededForUser(userId);
//
//            if (adminSessionId == null) {
//                adminSessionId = admin.getSessionId();
//            }
//
//            RequestConfig config = RequestConfig.custom().setSocketTimeout(Constants.SO_TIMEOUT_IN_SECONDS_DEFAULT * 1000).setConnectTimeout(Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000).build();
//
//            HttpAsyncClientBuilder clientBuilder = HttpAsyncClientBuilder.create().setDefaultRequestConfig(config);
//
//            String fullPath = serviceUtilities.composeFullAddressWithLugServerId(lugServerId, "computer/rmusers");
//
//            // header
//
//            Header header = new BasicHeader(Constants.HTTP_HEADER_FILELUG_SESSION_ID_NAME, adminSessionId);
//            Set<Header> headers = new HashSet<>();
//            headers.add(header);
//            clientBuilder.setDefaultHeaders(headers);
//
//            HttpPost httpPost = new HttpPost(fullPath);
//
//            // body
//
//            long computerId = Utility.getPreferenceLong(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, 0L);
//
//            String verification = Utility.generateRemoveAdminVerification(userId, computerId, adminSessionId);
//
//            ObjectMapper mapper = Utility.createObjectMapper();
//
//            ObjectNode rootNode = mapper.createObjectNode();
//
//            rootNode.put(PropertyConstants.PROPERTY_NAME_VERIFICATION, verification);
//
//            String jsonString = mapper.writeValueAsString(rootNode);
//
//            httpPost.setEntity(new StringEntity(jsonString, ContentType.APPLICATION_JSON));
//
//            CloseableHttpAsyncClient httpClient = clientBuilder.build();
//
//            httpClient.start();
//
//            LOGGER.debug("Execute async request POST \"" + fullPath + "\" for user: " + nickname);
//
//            Future<HttpResponse> future = httpClient.execute(httpPost, callback);
//
//            try {
//                future.get();
//            } finally {
//                closeHttpClient(httpClient);
//            }
//        } else {
//            throw new Exception("The administrator of this computer not found.");
//        }
//    }

    private void closeHttpClient(CloseableHttpAsyncClient httpClient) {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (Exception e) {
                // ignored
            }
        }
    }

    //    @Override
//    public void changeAdministrator(String oldAdminUserId, String oldAdminPasswd, String oldAdminNickname, String newAdminUserId, String newAdminPasswd) throws Exception {
//        Long computerId = Utility.getPreferenceLong(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, -1);
//        String recoveryKey = Utility.getPreference(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, "");
//
//        ChangeAdministratorModel model = new ChangeAdministratorModel();
//
//        model.setOldAdminUserId(oldAdminUserId);
//        model.setOldAdminPassword(oldAdminPasswd);
//        model.setNewAdminUserId(newAdminUserId);
//        model.setNewAdminPassword(newAdminPasswd);
//        model.setComputerId(computerId);
//        model.setRecoveryKey(recoveryKey);
//        model.setVerification(Utility.generateVerificationToChangeComputerAdmin(oldAdminUserId, oldAdminPasswd, newAdminUserId, newAdminPasswd, computerId, recoveryKey));
//        model.setLocale(Utility.getApplicationLocale());
//
//
//        String path = "computer/chadmin";
//
//        ObjectMapper mapper = Utility.createObjectMapper();
//
//        String requestJson = mapper.writeValueAsString(model);
//
//        HttpResponse response = serviceUtilities.doPostJson(null, path, requestJson);
//
//        int status = response.getStatusLine().getStatusCode();
//
//        if (status == HttpServletResponse.SC_OK) {
//
//            /* update local db:
//             * 1. To old admin: update is_admin and clear password
//             * 2. To new admin: update is_admin and add password
//             */
//
//            User oldAdmin = userDao.findAdministrator();
//
//            oldAdmin.setAdmin(Boolean.FALSE);
////            oldAdmin.setPasswd("");
//
//            // FIX: See if the old guy should be allowed to access alias file
//
//            userDao.updateUser(oldAdmin);
//
//            User newAdmin = userDao.findUserById(newAdminUserId);
//
//            newAdmin.setAdmin(Boolean.TRUE);
//            newAdmin.setApproved(Boolean.TRUE);
////            newAdmin.setPasswd(newAdminPasswd);
//
//            // Admin should be able to access alias files
//            newAdmin.setAllowAlias(Boolean.TRUE);
//
//            userDao.updateUser(newAdmin);
//        } else {
//            /* throws status and error message */
//
//            String responseString = EntityUtils.toString(response.getEntity(), Charset.forName(Constants.DEFAULT_RESPONSE_ENTITY_CHARSET));
//
//            String errorMessage = Utility.localizedString("server.status", String.valueOf(status)) + "\n" + responseString;
//
//            throw new Exception(errorMessage);
//        }
//    }
}
