package com.filelug.desktop.service.websocket;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelug.desktop.ClopuccinoMessages;
import com.filelug.desktop.Constants;
import com.filelug.desktop.Utility;
import com.filelug.desktop.db.DatabaseConstants;
import com.filelug.desktop.model.FileUploadGroup;
import com.filelug.desktop.model.RequestFileUploadGroupModel;
import com.filelug.desktop.model.ResponseFileUploadGroupModel;
import com.filelug.desktop.service.Sid;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.File;
import java.util.List;

/**
 * <code>FileUploadGroupWebSocketService</code> receives and process the web socket message from server with service id: UPLOAD_FILE_GROUP
 *
 * @author masonhsieh
 * @version 1.0
 */
public class FileUploadGroupWebSocketService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(FileUploadGroupWebSocketService.class.getSimpleName());

    private final Session session;

    private final String message;

    private final ConnectSocket connectSocket;

    public FileUploadGroupWebSocketService(final Session session, final String message, final ConnectSocket connectSocket) {
        this.session = session;
        this.message = message;
        this.connectSocket = connectSocket;
    }

    public void onFileUploadGroupWebSocket() {
        // requested to create file upload summary

        String operatorId = null;
        String clientLocale = null;
        FileUploadGroup fileUploadGroup = null;
        String uploadGroupId = null;

        final ObjectMapper mapper = Utility.createObjectMapper();

        try {
            RequestFileUploadGroupModel requestModel = mapper.readValue(message, RequestFileUploadGroupModel.class);

            operatorId = requestModel.getOperatorId();
            clientLocale = requestModel.getLocale();
            fileUploadGroup = requestModel.getFileUploadGroup();

            if (!connectSocket.validate(true)) {
                ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_UNAUTHORIZED, ClopuccinoMessages.localizedMessage(clientLocale, "invalid.session"), operatorId, System.currentTimeMillis());

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else if (fileUploadGroup == null) {
                ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "file upload group"), operatorId, System.currentTimeMillis());

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else {
                uploadGroupId = fileUploadGroup.getUploadGroupId();

                // uplodd directory includes subdirectory value, e.g.
                // upload directory ends with 'subdirectoryValue' below, if any.
                String uploadDirectory = fileUploadGroup.getUploadGroupDirectory();

                Integer subdirectoryType = fileUploadGroup.getSubdirectoryType();
                Integer descriptionType = fileUploadGroup.getDescriptionType();
                Integer notificationType = fileUploadGroup.getNotificationType();
                String subdirectoryValue = fileUploadGroup.getSubdirectoryValue();
                String descriptionValue = fileUploadGroup.getDescriptionValue();
                List<String> uploadKeys = fileUploadGroup.getUploadKeys();

                if (uploadGroupId == null || uploadGroupId.trim().length() < 1) {
                    long failureTimestamp = System.currentTimeMillis();
                    String createdStatus = DatabaseConstants.CREATED_IN_DESKTOP_STATUS_FAILURE;

                    ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "upload group id"), operatorId, failureTimestamp, uploadGroupId, failureTimestamp, createdStatus);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else if (uploadDirectory == null || uploadDirectory.trim().length() < 1) {
                    long failureTimestamp = System.currentTimeMillis();
                    String createdStatus = DatabaseConstants.CREATED_IN_DESKTOP_STATUS_FAILURE;

                    ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "upload directory"), operatorId, failureTimestamp, uploadGroupId, failureTimestamp, createdStatus);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else if (subdirectoryType == null) {
                    long failureTimestamp = System.currentTimeMillis();
                    String createdStatus = DatabaseConstants.CREATED_IN_DESKTOP_STATUS_FAILURE;

                    ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "type of subdirectory"), operatorId, failureTimestamp, uploadGroupId, failureTimestamp, createdStatus);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else if (descriptionType == null) {
                    long failureTimestamp = System.currentTimeMillis();
                    String createdStatus = DatabaseConstants.CREATED_IN_DESKTOP_STATUS_FAILURE;

                    ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "type of description"), operatorId, failureTimestamp, uploadGroupId, failureTimestamp, createdStatus);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else if (notificationType == null) {
                    long failureTimestamp = System.currentTimeMillis();
                    String createdStatus = DatabaseConstants.CREATED_IN_DESKTOP_STATUS_FAILURE;

                    ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "type of notification"), operatorId, failureTimestamp, uploadGroupId, failureTimestamp, createdStatus);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else if (subdirectoryType != 0 && (subdirectoryValue == null || subdirectoryValue.trim().length() < 1)) {
                    long failureTimestamp = System.currentTimeMillis();
                    String createdStatus = DatabaseConstants.CREATED_IN_DESKTOP_STATUS_FAILURE;

                    ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "subdirectory"), operatorId, failureTimestamp, uploadGroupId, failureTimestamp, createdStatus);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else if (descriptionType != 0 && (descriptionValue == null || descriptionValue.trim().length() < 1)) {
                    long failureTimestamp = System.currentTimeMillis();
                    String createdStatus = DatabaseConstants.CREATED_IN_DESKTOP_STATUS_FAILURE;

                    ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "description"), operatorId, failureTimestamp, uploadGroupId, failureTimestamp, createdStatus);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else if (uploadKeys == null || uploadKeys.size() < 1) {
                    long failureTimestamp = System.currentTimeMillis();
                    String createdStatus = DatabaseConstants.CREATED_IN_DESKTOP_STATUS_FAILURE;

                    ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "file upload keys"), operatorId, failureTimestamp, uploadGroupId, failureTimestamp, createdStatus);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else {
                    boolean keepGoing = true;
                    File destinationDirectory;

                    if (subdirectoryType != 0) {
                        // create directory if not exists

                        // uploadDirectory includes subdirectory value, e.g.
                        // uploadDirectory ends with 'subdirectoryValue' below, if any.
                        destinationDirectory = new File(uploadDirectory);

                        File subdirectoryParent = destinationDirectory.getParentFile();

                        if (!subdirectoryParent.exists() || subdirectoryParent.isFile()) {
                            // parent not exists

                            keepGoing = false;

                            long failureTimestamp = System.currentTimeMillis();
                            String createdStatus = DatabaseConstants.CREATED_IN_DESKTOP_STATUS_FAILURE;

                            ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "directory.not.found", subdirectoryParent.getAbsolutePath()), operatorId, failureTimestamp, uploadGroupId, failureTimestamp, createdStatus);

                            session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                        } else {
                            destinationDirectory.mkdir();
                        }
                    } else {
                        // no subdirectory
                        destinationDirectory = new File(uploadDirectory);

                        if (!destinationDirectory.exists() || destinationDirectory.isFile()) {
                            // destination not exists

                            keepGoing = false;

                            long failureTimestamp = System.currentTimeMillis();
                            String createdStatus = DatabaseConstants.CREATED_IN_DESKTOP_STATUS_FAILURE;

                            ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "directory.not.found", destinationDirectory.getAbsolutePath()), operatorId, failureTimestamp, uploadGroupId, failureTimestamp, createdStatus);

                            session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                        }

//                        // no subdirectory, and no description
//
//                        sendOKToResponseFileUploadGroup(session, operatorId, uploadGroupId, mapper);
                    }

                    if (keepGoing) {
                        if (descriptionType != 0) {
                            try {
                                String fileBaseName = ClopuccinoMessages.getMessage("file.upload.group.description.base.name");

                                File descriptionFile = new File(destinationDirectory, fileBaseName + ".txt");

                                // test if description file exists.
                                // create readme.txt file, if readme.txt exists, use readme-<n>.txt instead

                                boolean descriptionFileExists = descriptionFile.exists();

                                for (int suffix = 2; descriptionFileExists; suffix++) {
                                    String newFileBaseName = fileBaseName + "_" + suffix;

                                    descriptionFile = new File(destinationDirectory, newFileBaseName + ".txt");

                                    descriptionFileExists = descriptionFile.exists();
                                }

                                FileUtils.write(descriptionFile, descriptionValue, Constants.DEFAULT_RESPONSE_ENTITY_CHARSET);

                                sendOKToResponseFileUploadGroup(session, operatorId, uploadGroupId, mapper);
                            } catch (SecurityException e) {
                                // no right to create subdirectory

                                long failureTimestamp = System.currentTimeMillis();
                                String createdStatus = DatabaseConstants.CREATED_IN_DESKTOP_STATUS_FAILURE;

                                ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ClopuccinoMessages.localizedMessage(clientLocale, "no.permission.create.directory", destinationDirectory.getAbsolutePath()), operatorId, failureTimestamp, uploadGroupId, failureTimestamp, createdStatus);

                                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                            }
                        } else {
                            sendOKToResponseFileUploadGroup(session, operatorId, uploadGroupId, mapper);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // websocket sent internal error
            try {
                String errorMessage = ClopuccinoMessages.localizedMessage(clientLocale, "error.invoke.service", "FILE_UPLOAD_GROUP", e.getMessage() != null ? e.getMessage() : "");

                LOGGER.error(errorMessage, e);

                long failureTimestamp = System.currentTimeMillis();
                String createdStatus = DatabaseConstants.CREATED_IN_DESKTOP_STATUS_FAILURE;

                ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP,  HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage, operatorId, failureTimestamp, uploadGroupId, failureTimestamp, createdStatus);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } catch (Exception e1) {
                LOGGER.error("Error on sent internal error message! Class: " + e1.getClass().getName() + ", Reason: " + e.getMessage());
            }
        }
    }

    private void sendOKToResponseFileUploadGroup(Session session, String operatorId, String uploadGroupId, ObjectMapper mapper) throws JsonProcessingException {
        long createdTimestamp = System.currentTimeMillis();
        String createdStatus = DatabaseConstants.CREATED_IN_DESKTOP_STATUS_SUCCESS;

        ResponseFileUploadGroupModel responseModel = new ResponseFileUploadGroupModel(Sid.UPLOAD_FILE_GROUP, HttpServletResponse.SC_OK, null, operatorId, createdTimestamp, uploadGroupId, createdTimestamp, createdStatus);

        session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
    }
}
