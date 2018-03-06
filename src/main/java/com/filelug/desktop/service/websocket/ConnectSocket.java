package com.filelug.desktop.service.websocket;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelug.desktop.*;
import com.filelug.desktop.dao.FileTransferInDao;
import com.filelug.desktop.dao.FileTransferOutDao;
import com.filelug.desktop.dao.UserDao;
import com.filelug.desktop.db.DatabaseAccess;
import com.filelug.desktop.db.HyperSQLDatabaseAccess;
import com.filelug.desktop.model.*;
import com.filelug.desktop.service.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.*;
import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@ClientEndpoint
public class ConnectSocket {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("SOCKET");

    /**
     * The class instance will be added to instances after connection built successfully.
     * key=user id
     */
    private static final Hashtable<String, ConnectSocket> instances = new Hashtable<>();

    private Session session;

    private String userId;

    private String nickname;

    /* in ms */
    private Long lastAccessTime;

    private CountDownLatch closeLatch;

    private final UserDao userDao;

    private final FileTransferOutDao fileTransferOutDao;

    private final FileTransferInDao fileTransferInDao;

    private final UserService userService;

//    /* flag to indicated the instance should be destroyed after new instance created successfully */
//    private boolean readyToDistroyed = false;

    private ConnectResponseState connectResponseState;

    private String lugServerId;

    public ConnectSocket(String userId, ConnectResponseState connectResponseState, CountDownLatch closeLatch, String lugServerId) {
        this.userId = userId;
        this.connectResponseState = connectResponseState;
        this.closeLatch = closeLatch;
        this.lugServerId = lugServerId;

        DatabaseAccess dbAccess = new HyperSQLDatabaseAccess();

        userDao = new UserDao(dbAccess);

        fileTransferOutDao = new FileTransferOutDao(dbAccess);

        fileTransferInDao = new FileTransferInDao(dbAccess);

        userService = new DefaultUserService();

//        if (this.connectResponseState != null) {
//            this.connectResponseState.addObserver(this);
//        }
    }

    public static void putInstance(String userId, ConnectSocket socket) {
        instances.put(userId, socket);
    }

    public static Set<String> getAllConnectSocketUsers() {
        return instances.keySet();
    }

    public static void removeInstance(String userId) {
        ConnectSocket connectSocket = ConnectSocket.getInstance(userId);

        if (connectSocket != null) {
//            connectSocket.setReadyDistroyAndStopCheckReconnect();

            Session oldSession = connectSocket.getSession();

            if (oldSession != null) {
                ConnectSocketUtilities.closeSession(oldSession, CloseReason.CloseCodes.NORMAL_CLOSURE, "Socket removed from instance list in client.");
            }

//            connectSocket.getConnectResponseState().deleteObservers();

            instances.remove(userId);
        }
    }

    public static ConnectSocket getInstance(String userId) {
        return instances.get(userId);
    }

    public static Hashtable<String, ConnectSocket> getInstances() {
        return instances;
    }

    public boolean validate(boolean updateAccessTimeToNowIfValid) {
        boolean valid = false;

        if (getSession() != null && getSession().isOpen()) {
            if (updateAccessTimeToNowIfValid) {
                updateLastAccessTimeToNow();
            }

            valid = true;
        }
//        if (getSession() != null && getSession().isOpen() && !readyToDistroyed) {
//            if (updateAccessTimeToNowIfValid) {
//                updateLastAccessTimeToNow();
//            }
//
//            valid = true;
//        }

        return valid;
    }

    public Session getSession() {
        return session;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public String getLugServerId() {
        return lugServerId;
    }

    public void setLugServerId(String lugServerId) {
        this.lugServerId = lugServerId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public ConnectResponseState getConnectResponseState() {
        return connectResponseState;
    }

    public void setConnectResponseState(ConnectResponseState connectResponseState) {
        this.connectResponseState = connectResponseState;
    }

    public void updateLastAccessTimeToNow() {
        lastAccessTime = System.currentTimeMillis();
    }

    public void setCloseLatch(CountDownLatch closeLatch) {
        this.closeLatch = closeLatch;
    }

//    /**
//     * Set flag to stop process to check reconnect.
//     */
//    public void setReadyDistroyAndStopCheckReconnect() {
//        readyToDistroyed = true;
//
//        LOGGER.info("Ready to destroy and stop checking if it needs to re-connect.");
//    }
//
//    public void setReadyToDistroyed() {
//        readyToDistroyed = true;
//    }
//
//    public boolean isReadyToDistroyed() {
//        return readyToDistroyed;
//    }

    public UserDao getUserDao() {
        return userDao;
    }

    public CountDownLatch getCloseLatch() {
        return closeLatch;
    }

    public FileTransferOutDao getFileTransferOutDao() {
        return fileTransferOutDao;
    }

    public FileTransferInDao getFileTransferInDao() {
        return fileTransferInDao;
    }

    public UserService getUserService() {
        return userService;
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        LOGGER.info(String.format("Socket closed with close code: %d - reason: %s", closeReason.getCloseCode().getCode(), closeReason.getReasonPhrase()));

        this.session = null;

        countDownCloseLatch();

        /* Do not remove ConnectSocket from Map here to prevent asynchronized removed newly created ConnectSocket  */
//        ConnectSocket.removeInstance(userId);

        if (closeReason.getCloseCode() == CloseReason.CloseCodes.UNEXPECTED_CONDITION) {
//            setReadyDistroyAndStopCheckReconnect();

            // clear unnecessary data in preferences
            Utility.removePreference(PropertyConstants.PROPERTY_NAME_QR_CODE);

            LOGGER.error("The connection is closed for potential security reason.\nYou need to activate user manually if you want.");
        }
    }

    @OnError
    public void onError(Session session, Throwable t) {
        String errorMessage = String.format("Socket error for user: '%s', error:%n%s", userId, t != null ? t.getMessage() : "(Empty error message)");

        LOGGER.error(errorMessage, t);
    }

    private void countDownCloseLatch() {
        if (this.closeLatch != null) {
            try {
                this.closeLatch.countDown();
            } catch (Exception e) {
                // ignored
            }
        }
    }

    public static void closeAllConnectSockets() throws Exception {
        if (instances.size() > 0) {
            Set<Map.Entry<String, ConnectSocket>> entries = instances.entrySet();

            // Use iterator to remove ConnectSocket in loop
            Iterator<Map.Entry<String, ConnectSocket>> iterator = entries.iterator();

            for (; iterator.hasNext(); ) {
                Map.Entry<String, ConnectSocket> entry = iterator.next();

                ConnectSocket currentConnectSocket = entry.getValue();

                if (currentConnectSocket != null) {
                    currentConnectSocket.countDownCloseLatch();

//                    currentConnectSocket.setReadyDistroyAndStopCheckReconnect();

                    Session session = currentConnectSocket.getSession();

                    if (session != null && session.isOpen()) {
                        String userNickname = currentConnectSocket.getNickname();

                        session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Desktop from user: " + (userNickname != null ? userNickname : "Unknown") + " shutdown."));
                    }
                }

                iterator.remove();
            }
        }
    }

    @OnOpen
    public void onConnect(Session session) {
        LOGGER.debug(String.format("Connected with server: %s%n", session.getRequestURI().toString()));

        session.setMaxBinaryMessageBufferSize(Integer.MAX_VALUE);
        session.setMaxTextMessageBufferSize(Integer.MAX_VALUE);
        session.setMaxIdleTimeout(Constants.IDLE_TIMEOUT_IN_SECONDS_TO_CONNECT_SOCKET * 1000);

        this.session = session;
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        Integer sid = Utility.findSidFromJson(message);

//        String errorMessage;

        if (sid == null) {
            onUnsupportedWebSocket(session, message, null);
        } else {
            switch (sid) {
                case Sid.CONNECT_V2:
                    onConnectFromComputerWebSocket(session, message);

                    break;
                case Sid.DELETE_COMPUTER_V2:
                    onDeleteComputerWebSocket(session, message);

                    break;
                case Sid.CHANGE_COMPUTER_NAME_V2:
                    onChangeComputerNameWebSocket(session, message);

                    break;
                case Sid.LIST_ALL_ROOT_DIRECTORIES_V2:
                    onListRootsWebSocket(session, message);

                    break;
                case Sid.LIST_CHILDREN:
                    onListDirectoryChildrenWebSocket(session, message);

                    break;
                case Sid.FIND_BY_PATH:
                    onFindFileByPathWebSocket(session, message);

                    break;
                case Sid.DOWNLOAD_FILE2_V2:
                    onFileTransferOutWebSocket3(session, message);

                    break;
                case Sid.FILE_RENAME:
                    onFileRenamehWebSocket(session, message);

                    break;
                case Sid.UPLOAD_FILE2_V2:
                    final Session finalSession = session;
                    final String finalMessage = message;

                    Utility.getExecutorService().execute(() -> onFileTransferInWebSocket2(finalSession, finalMessage));

                    break;
                case Sid.UPLOAD_FILE_GROUP:
                    onFileUploadGroupWebSocket(session, message);

                    break;
                case Sid.DELETE_UPLOAD_FILE:
                    onDeleteFileUploadWebSocket(session, message);

                    break;
                case Sid.DOWNLOAD_FILE_GROUP:
                    onFileDownloadGroupWebSocket(session, message);

                    break;
                case Sid.UPDATE_SOFTWARE:
                    onUpdateSoftwareWebSocket(session, message);

                    break;
                case Sid.NEW_SOFTWARE_NOTIFY:
                    onNewSoftwareNotifyWebSocket(session, message);

                    break;
                // For services only for V1 -- Notify users to upgrade the Filelug on their devices
                case Sid.CONNECT:
                case Sid.LIST_ALL_BOOKMARKS:
                case Sid.LIST_BOOKMARKS_AND_ROOTS:
                case Sid.FIND_BOOKMARK_BY_ID:
                case Sid.CREATE_BOOKMARK:
                case Sid.UPDATE_BOOKMARK:
                case Sid.DELETE_BOOKMARK_BY_ID:
                case Sid.SYNCHRONIZE_BOOKMARKS:
                case Sid.UPLOAD_FILE:
                case Sid.UPLOAD_FILE2:
                case Sid.DOWNLOAD_FILE:
                case Sid.DOWNLOAD_FILE2:
                case Sid.ALLOW_UPLOAD_FILE:
                case Sid.CONFIRM_UPLOAD_FILE:
                case Sid.ALLOW_DOWNLOAD_FILE:
                case Sid.LIST_ALL_ROOT_DIRECTORIES:
                    onReceiveV1OnlyServiceWebSocket(session, message, sid);

                    break;
                case Sid.UNSUPPORTED:
                    onReceiveFromUnsupportedWebSocket(session, message);

                    break;
                default:
                    onUnsupportedWebSocket(session, message, sid);
            }
        }
    }

    private void onFileTransferOutWebSocket3(final Session session, String message) {
        FileTransferOutWebSocketService fileTransferOutWebSocketService = new FileTransferOutWebSocketService(session, message, this);

        fileTransferOutWebSocketService.onFileTransferOutWebSocket();
    }

    private void onFileTransferInWebSocket2(final Session session, String message) {

        FileTransferInWebSocketService fileTransferInWebSocketService = new FileTransferInWebSocketService(session, message, this);

        fileTransferInWebSocketService.onFileTransferInWebSocket();
    }

    private void onFileUploadGroupWebSocket(final Session session, String message) {

        FileUploadGroupWebSocketService fileUploadGroupWebSocketService = new FileUploadGroupWebSocketService(session, message, this);

        fileUploadGroupWebSocketService.onFileUploadGroupWebSocket();
    }

    private void onDeleteFileUploadWebSocket(final Session session, String message) {
        // requested to download file from repository
        String operatorId = null;
        String clientLocale = null;
        String clientSessionId = null;

        final ObjectMapper mapper = Utility.createObjectMapper();

        try {
            RequestDeleteFileUploadModel requestModel = mapper.readValue(message, RequestDeleteFileUploadModel.class);

            operatorId = requestModel.getOperatorId();
            clientLocale = requestModel.getLocale();
            clientSessionId = requestModel.getClientSessionId();

            final String transferKey = requestModel.getTransferKey();

            if (!validate(true) || clientSessionId == null || clientSessionId.trim().length() < 1) {
                ResponseModel responseModel = new ResponseModel(Sid.DELETE_UPLOAD_FILE, HttpServletResponse.SC_UNAUTHORIZED, ClopuccinoMessages.localizedMessage(clientLocale, "invalid.session"), operatorId, clientSessionId, System.currentTimeMillis());

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else if (transferKey == null || transferKey.trim().length() < 1) {
                ResponseModel responseModel = new ResponseModel(Sid.DELETE_UPLOAD_FILE, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "transformation key"), operatorId, clientSessionId, System.currentTimeMillis());

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else {
                // delete file_uploaded in db
                fileTransferInDao.deleteFileTransferInIfExists(transferKey);

                ResponseModel responseModel = new ResponseModel(Sid.DELETE_UPLOAD_FILE, HttpServletResponse.SC_OK, null, operatorId, clientSessionId, System.currentTimeMillis());

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            }
        } catch (Exception e) {
            /* websocket sent internal error */
            try {
                String errorMessage = ClopuccinoMessages.localizedMessage(clientLocale, "error.invoke.service", "DELETE_FILE_UPLOAD", e.getMessage() != null ? e.getMessage() : "");

                LOGGER.error(errorMessage, e);

                ResponseModel responseModel = new ResponseModel(Sid.DELETE_UPLOAD_FILE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage, operatorId, clientSessionId, System.currentTimeMillis());

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } catch (Exception e1) {
                LOGGER.error("Error on sent internal error message! Class: " + e1.getClass().getName() + ", Reason: " + e.getMessage());
            }
        }
    }

    private void onFileDownloadGroupWebSocket(final Session session, String message) {

        FileDownloadGroupWebSocketService fileDownloadGroupWebSocketService = new FileDownloadGroupWebSocketService(session, message, this);

        fileDownloadGroupWebSocketService.onFileDownloadGroupWebSocket();
    }

    private void onFindFileByPathWebSocket(final Session session, String message) {
        FindByPathWebSocketService findByPathWebSocketService = new FindByPathWebSocketService(session, message, this);

        findByPathWebSocketService.onFindByPathWebSocket();
    }

    private void onFileRenamehWebSocket(final Session session, String message) {
        /* requested to find the information of the specified file */
        String operatorId = null;
        String clientLocale = null;

        ObjectMapper mapper = Utility.createObjectMapper();

        try {
            RequestFileRenameModel requestModel = mapper.readValue(message, RequestFileRenameModel.class);

            operatorId = requestModel.getOperatorId();
            clientLocale = requestModel.getLocale();

            if (!validate(true)) {
                ResponseFileRenameModel responseModel = new ResponseFileRenameModel(Sid.FILE_RENAME, HttpServletResponse.SC_UNAUTHORIZED, ClopuccinoMessages.localizedMessage(clientLocale, "invalid.session"), operatorId, System.currentTimeMillis(), null);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else {
                String oldPath = requestModel.getPath();

                if (oldPath == null || !new File(oldPath).exists()) {
                    ResponseFileRenameModel responseModel = new ResponseFileRenameModel(Sid.FILE_RENAME, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "directory.or.file.not.found", oldPath != null ? oldPath : ""), operatorId, System.currentTimeMillis(), null);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else {
                    String newFilename = requestModel.getFilename();

                    File file = new File(oldPath);

                    File parentFile = file.getParentFile();

                    File newFile = new File(parentFile, newFilename);

                    if (newFile.exists()) {
                        // HttpServletResponse.SC_CONFLICT
                        ResponseFileRenameModel responseModel = new ResponseFileRenameModel(Sid.FILE_RENAME, HttpServletResponse.SC_CONFLICT, ClopuccinoMessages.localizedMessage(clientLocale, "directory.or.file.duplicated", newFile.getAbsolutePath()), operatorId, System.currentTimeMillis(), null);

                        session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                    } else {
                        String oldFilename = file.getName();

                        try {
                            boolean success = file.renameTo(newFile);

                            if (success) {
                                FileRenameModel fileRenameModel = new FileRenameModel(oldPath, newFile.getAbsolutePath(), oldFilename, newFilename);

                                ResponseFileRenameModel responseModel = new ResponseFileRenameModel(Sid.FILE_RENAME, HttpServletResponse.SC_OK, null, operatorId, System.currentTimeMillis(), fileRenameModel);

                                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                            } else {
                                // possible permission denied
                                ResponseFileRenameModel responseModel = new ResponseFileRenameModel(Sid.FILE_RENAME, HttpServletResponse.SC_FORBIDDEN, ClopuccinoMessages.localizedMessage(clientLocale, "file.permission.rename.denied", file.getAbsolutePath()), operatorId, System.currentTimeMillis(), null);

                                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                            }
                        } catch (SecurityException e) {
                            // HttpServletResponse.SC_FORBIDDEN
                            ResponseFileRenameModel responseModel = new ResponseFileRenameModel(Sid.FILE_RENAME, HttpServletResponse.SC_FORBIDDEN, ClopuccinoMessages.localizedMessage(clientLocale, "file.permission.rename.denied", file.getAbsolutePath()), operatorId, System.currentTimeMillis(), null);

                            session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                        }
                    }
                }
            }
        } catch (Exception e) {
            /* websocket sent internal error */
            try {
                String errorMessage = ClopuccinoMessages.localizedMessage(clientLocale, "error.invoke.service", "FileRename", e.getMessage() != null ? e.getMessage() : "");

                LOGGER.error(errorMessage, e);

                ResponseFileRenameModel responseModel = new ResponseFileRenameModel(Sid.FILE_RENAME, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage, operatorId, System.currentTimeMillis(), null);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } catch (Exception e1) {
                LOGGER.error("Error on sent internal error message! Class: " + e1.getClass().getName() + ", Reason: " + e.getMessage());
            }
        }
    } // end onFileRenamehWebSocket(Session, String)

    private void onListDirectoryChildrenWebSocket(final Session session, String message) {
        ListDirectoryChildrenWebSocketService listDirectoryChildrenWebSocketService = new ListDirectoryChildrenWebSocketService(session, message, this);

        listDirectoryChildrenWebSocketService.onListDirectoryChildrenWebSocket();
    }

    private void onListRootsWebSocket(final Session session, String message) {
        ListRootsWebSocketService listRootsWebSocketService = new ListRootsWebSocketService(session, message, this);

        listRootsWebSocketService.onListRootsWebSocket();
    }

    /**
     * If 200 status code received, remove the preference 'qr-code' to close the QRCodeWindow, if any.
     * If the status code received is not 200, sleep for 2 seconds, then prompt the error message and ask if
     * the user wants to try again. If the user choose yes, get the QR code again; if the user choose no, remove the preference 'qr-code'.
     */
    private void onConnectFromComputerWebSocket(final Session session, String message) {

        ConnectWebSocketService connectWebSocketService = new ConnectWebSocketService(session, message, this);

        connectWebSocketService.connectFromComputerWebSocket();
    }

    private void onDeleteComputerWebSocket(final Session session, final String message) {
        DeleteComputerWebSocketService deleteComputerWebSocketService = new DeleteComputerWebSocketService(session, message, this);

        deleteComputerWebSocketService.deleteComputer();
    }

    private void onChangeComputerNameWebSocket(final Session session, final String message) {
        ChangeComputerNameWebSocketService changeComputerNameWebSocketService = new ChangeComputerNameWebSocketService(session, message, this);

        changeComputerNameWebSocketService.changeComputerName();
    }

    private void onReceiveV1OnlyServiceWebSocket(final Session session, final String message, Integer sid) {
        LOGGER.warn(String.format("Sid: %d is not supported now. Filelug APP on the device needs to be upgraded to the latest version.", sid));

        // find locale if any

        String foundLocale = null;

        ObjectMapper mapper = Utility.createObjectMapper();

        try {
            JsonNode receivedNode = mapper.readTree(message);

            JsonNode localeNode = receivedNode.findValue("locale");

            if (localeNode != null) {
                foundLocale = localeNode.textValue();
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Error on findiing error from V1 only service: %d%nMessage:%n%s", sid, message), e);
        }

        // Notify users to upgrade the Filelug on their devices

        try {
            String errorMessage = ClopuccinoMessages.localizedMessage(foundLocale, "device.need.update");

            ResponseModel responseModel = new ResponseModel(Sid.UNSUPPORTED, Constants.HTTP_STATUS_DEVICE_VERSION_TOO_OLD, errorMessage, null, System.currentTimeMillis());

            session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
        } catch (JsonProcessingException e) {
            int httpStatusCode = HttpServletResponse.SC_BAD_REQUEST;

            processOnMessageException(session, Sid.UNSUPPORTED, e, httpStatusCode);
        } catch (Exception e) {
            int httpStatusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

            processOnMessageException(session, Sid.UNSUPPORTED, e, httpStatusCode);
        } finally {
            countDownCloseLatch();
        }
    }

    private void onUnsupportedWebSocket(Session session, String message, Integer sid) {
        LOGGER.error(String.format("Unsupported message received.%nSid '%d'%nMessage:%n%s", sid, message));

        try {
            String errorMessage = sid + " is an unsupported service or you need to connect first in order to use this service.";
            ResponseModel responseModel = new ResponseModel(Sid.UNSUPPORTED, HttpServletResponse.SC_NOT_FOUND, errorMessage, null, System.currentTimeMillis());

            ObjectMapper mapper = Utility.createObjectMapper();

            session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
        } catch (JsonProcessingException e) {
            int httpStatusCode = HttpServletResponse.SC_BAD_REQUEST;

            processOnMessageException(session, Sid.UNSUPPORTED, e, httpStatusCode);
        } catch (Exception e) {
            int httpStatusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

            processOnMessageException(session, Sid.UNSUPPORTED, e, httpStatusCode);
        }
    }

    private void onReceiveFromUnsupportedWebSocket(final Session session, String message) {
        LOGGER.error(String.format("Unsupported message received.%nMessage:%n%s", message));

        try {
            ObjectMapper mapper = Utility.createObjectMapper();

            ResponseModel responseModel = mapper.readValue(message, ResponseModel.class);

            Integer status = responseModel.getStatus();
            String errorMessage = responseModel.getError();

            LOGGER.error("Service Unsupported. Statuse: " + status + ", message: " + errorMessage);

        } catch (Exception e) {
            String errorMessage = String.format("Error on response unsupported service to client. Message from repository: %s Error Message: %s", message, e.getMessage());
            LOGGER.error(errorMessage);
        } finally {
            countDownCloseLatch();
        }
    }

    private void onUpdateSoftwareWebSocket(final Session session, String message) {
        /* requested to update software */
        String operatorId = null;
        String clientLocale = null;

        ObjectMapper mapper = Utility.createObjectMapper();

        try {
            RequestModel requestModel = mapper.readValue(message, RequestModel.class);

            operatorId = requestModel.getOperatorId();
            clientLocale = requestModel.getLocale();

            if (!validate(true)) {
                ResponseModel responseModel = new ResponseModel(Sid.UPDATE_SOFTWARE, HttpServletResponse.SC_UNAUTHORIZED, ClopuccinoMessages.localizedMessage(clientLocale, "invalid.session"), operatorId, System.currentTimeMillis());

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else {
                // FIX: Consider how to shutdown this!!
                ScheduledFuture scheduledFuture = Executors.newSingleThreadScheduledExecutor().schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // close all sockets after 60 seconds
                            Iterator<Map.Entry<String, ConnectSocket>> iterator = ConnectSocket.instances.entrySet().iterator();

                            while (iterator.hasNext()) {
                                iterator.next();
                                iterator.remove();
                            }

                            LOGGER.info("All connected sockets are stopped.");
                        } catch (Exception e) {
                            LOGGER.error("Failed to download software or stop connected sockets", e);
                        }
                    }
                }, Constants.DEFAULT_DELAY_TO_REMOVE_ALL_SOCKETS_IN_SECONDS, TimeUnit.SECONDS);

                try {
                    // download software and save to temp
                    String tempFile = File.createTempFile(Constants.DOWNLOADED_SOFTWARE_TEMP_FILE_PREFIX, String.valueOf(System.currentTimeMillis())).getAbsolutePath();

                    SystemService systemService = new DefaultSystemService();
                    int status = systemService.downloadSoftware(tempFile);

                    if (status == HttpServletResponse.SC_OK) {
                        /* move temp file to patch file */
                        File downloadFile = new File(tempFile);
                        if (downloadFile.exists()) {
                            LOGGER.info("Updated softwared downloaded to temp file: " + tempFile);

                            File parentDestFile = OSUtility.getApplicationDataDirectoryFile();
                            File destFile = new File(parentDestFile, Constants.SOFTWARE_PATCH_FILE_NAME);

                            if (destFile.exists()) {
                                if (!destFile.delete()) {
                                    LOGGER.error("Can't delete old patch file: " + destFile.getAbsolutePath() + "\nRemove manually and update software again.");
                                }
                            }

                            FileUtils.moveFile(downloadFile, destFile);

                            LOGGER.info("Downloaded software moved to: " + destFile.getAbsolutePath());
                        }

                        /* wait for all connect sockets closed */
                        Object nullObject = scheduledFuture.get();

                        System.exit(Constants.EXIT_CODE_ON_SOFTWARE_UPDATE);
                    } else {
                        LOGGER.error("Failed to download updated patch. Status: " + status);
                    }
                } catch (Exception e) {
                    LOGGER.error("Error on downloading software or preparing patch file", e);
                }

                ResponseModel responseModel = new ResponseModel(Sid.UPDATE_SOFTWARE, HttpServletResponse.SC_OK, null, operatorId, System.currentTimeMillis());

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            }
        } catch (Exception e) {
            /* websocket sent internal error */
            try {
                String errorMessage = ClopuccinoMessages.localizedMessage(clientLocale, "error.invoke.service", "update software", e.getMessage() != null ? e.getMessage() : "");

                LOGGER.error(errorMessage, e);

                ResponseModel responseModel = new ResponseModel(Sid.UPDATE_SOFTWARE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage, operatorId, System.currentTimeMillis());

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } catch (Exception e1) {
                LOGGER.error("Error on sent internal error message! Class: " + e1.getClass().getName() + ", Reason: " + e.getMessage());
            }
        }
    }

    private void onNewSoftwareNotifyWebSocket(final Session session, String message) {
        /* requested to show new software available information */
        String operatorId = null;
        String clientLocale = null;

        ObjectMapper mapper = Utility.createObjectMapper();

        try {
            RequestVersionModel requestVersionModel = mapper.readValue(message, RequestVersionModel.class);

            operatorId = requestVersionModel.getOperatorId();
            clientLocale = requestVersionModel.getLocale();

            if (validate(true)) {
                // make sure the received current version is the same with the current running version
                String currentVersion = requestVersionModel.getCurrentVersion();

                String currentRunningVersion = System.getProperty(PropertyConstants.PROPERTY_NAME_FILELUG_DESKTOP_CURRENT_VERSION, Constants.DEFAULT_DESKTOP_VERSION);

                if (currentVersion.equals(currentRunningVersion)) {
                    // save the latest version to preference

                    String latestVersion = requestVersionModel.getLatestVersion();

                    String downloadUrl = requestVersionModel.getDownloadUrl();

                    NewVersionAvailableState newVersionAvailableState = NewVersionAvailableState.getInstance();

                    newVersionAvailableState.setNewVersion(latestVersion, downloadUrl);
                }
            }
        } catch (Exception e) {
            /* websocket sent internal error */
            try {
                String errorMessage = ClopuccinoMessages.localizedMessage(clientLocale, "error.invoke.service", "new software notification", e.getMessage() != null ? e.getMessage() : "");

                LOGGER.error(errorMessage, e);
            } catch (Exception e1) {
                LOGGER.error("Error on sent internal error message! Class: " + e1.getClass().getName() + ", Reason: " + e.getMessage());
            }
        }
    }

    private void processOnMessageException(Session session, Integer sid, Exception e, int httpStatusCode) {
        String errorMessage = String.format("Error on processing received message.%n%s%n%s%n", e.getClass().getName(), e.getMessage());

        ResponseModel responseModel = new ResponseModel(sid, httpStatusCode, errorMessage, null, System.currentTimeMillis());

        try {
            ObjectMapper mapper = Utility.createObjectMapper();

            Future future = session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));

            future.get(Constants.FUTURE_WAIT_TIMEOUT_IN_SECONDS_DEFAULT, TimeUnit.SECONDS);
        } catch (Exception e1) {
            /* ignored */
        }
    }
}