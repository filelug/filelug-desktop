package com.filelug.desktop.service.websocket;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelug.desktop.ClopuccinoMessages;
import com.filelug.desktop.Constants;
import com.filelug.desktop.Utility;
import com.filelug.desktop.model.*;
import com.filelug.desktop.service.*;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.File;
import java.util.Collection;

/**
 * <code>FileDownloadGroupWebSocketService</code> receives and process the web socket message from server with service id: DOWNLOAD_FILE_GROUP
 *
 * @author masonhsieh
 * @version 1.0
 */
public class FileDownloadGroupWebSocketService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(FileDownloadGroupWebSocketService.class.getSimpleName());

    private final Session session;

    private final String message;

    private final ConnectSocket connectSocket;

    public FileDownloadGroupWebSocketService(final Session session, final String message, final ConnectSocket connectSocket) {
        this.session = session;
        this.message = message;
        this.connectSocket = connectSocket;
    }

    public void onFileDownloadGroupWebSocket() {
        // requested to validate file paths of file download summary

        String operatorId = null;
        String clientLocale = null;
        String downloadGroupId = null;
        Collection<String> filePaths = null;
        Long fileSizeLimitInBytes = null;

        final ObjectMapper mapper = Utility.createObjectMapper();

        try {
            RequestFileDownloadGroupModel requestModel = mapper.readValue(message, RequestFileDownloadGroupModel.class);

            operatorId = requestModel.getOperatorId();
            clientLocale = requestModel.getLocale();
            downloadGroupId = requestModel.getDownloadGroupId();
            filePaths = requestModel.getFilePaths();
            fileSizeLimitInBytes = requestModel.getFileSizeLimitInBytes();

            if (!connectSocket.validate(true)) {
                ResponseFileDownloadGroupModel responseModel = new ResponseFileDownloadGroupModel(Sid.DOWNLOAD_FILE_GROUP, HttpServletResponse.SC_UNAUTHORIZED, ClopuccinoMessages.localizedMessage(clientLocale, "invalid.session"), operatorId, System.currentTimeMillis());

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else if (downloadGroupId == null) {
                ResponseFileDownloadGroupModel responseModel = new ResponseFileDownloadGroupModel(Sid.DOWNLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "download group id"), operatorId, System.currentTimeMillis());

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else if (filePaths == null || filePaths.size() < 1) {
                ResponseFileDownloadGroupModel responseModel = new ResponseFileDownloadGroupModel(Sid.DOWNLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "file download group"), operatorId, System.currentTimeMillis());

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else if (fileSizeLimitInBytes == null) {
                ResponseFileDownloadGroupModel responseModel = new ResponseFileDownloadGroupModel(Sid.DOWNLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "download file size limit"), operatorId, System.currentTimeMillis());

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else {
                boolean keepGoing = true;

                // Make sure all file path is a existing file, or a bundle directory, and the operator gets read permission to this file

                for (String filePath : filePaths) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        keepGoing = false;

                        ResponseFileDownloadGroupModel responseModel = new ResponseFileDownloadGroupModel(Sid.DOWNLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "file.not.found", filePath), operatorId, System.currentTimeMillis(), downloadGroupId);

                        session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));

                        break;
                    } else if (file.isDirectory() && !BundleDirectoryService.isBundleDirectory(file)) {
                        keepGoing = false;

                        ResponseFileDownloadGroupModel responseModel = new ResponseFileDownloadGroupModel(Sid.DOWNLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "not.file", filePath), operatorId, System.currentTimeMillis(), downloadGroupId);

                        session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));

                        break;
                    } else if (!file.canRead()) {
                        keepGoing = false;

                        ResponseFileDownloadGroupModel responseModel = new ResponseFileDownloadGroupModel(Sid.DOWNLOAD_FILE_GROUP, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "file.read.permission.denied", filePath), operatorId, System.currentTimeMillis(), downloadGroupId);

                        session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));

                        break;
                    } else if (file.length() > fileSizeLimitInBytes) {
                        keepGoing = false;

                        String fileSizeForRepresentation = Utility.representationFileSizeFromBytes(file.length());

                        ResponseFileDownloadGroupModel responseModel = new ResponseFileDownloadGroupModel(Sid.DOWNLOAD_FILE_GROUP, Constants.HTTP_STATUS_FILE_SIZE_LIMIT_EXCEEDED, ClopuccinoMessages.localizedMessage(clientLocale, "exceed.download.size.limit2", filePath, fileSizeForRepresentation), operatorId, System.currentTimeMillis(), downloadGroupId);

                        session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));

                        break;
                    }
                }

                if (keepGoing) {
                    ResponseFileDownloadGroupModel responseModel = new ResponseFileDownloadGroupModel(Sid.DOWNLOAD_FILE_GROUP, HttpServletResponse.SC_OK, null, operatorId, System.currentTimeMillis(), downloadGroupId);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                }
            }
        } catch (Exception e) {
            // websocket sent internal error
            try {
                String errorMessage = ClopuccinoMessages.localizedMessage(clientLocale, "error.invoke.service", "FILE_DOWNLOAD_GROUP", e.getMessage() != null ? e.getMessage() : "");

                LOGGER.error(errorMessage, e);

                ResponseFileDownloadGroupModel responseModel = new ResponseFileDownloadGroupModel(Sid.DOWNLOAD_FILE_GROUP,  HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage, operatorId, System.currentTimeMillis(), downloadGroupId);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } catch (Exception e1) {
                LOGGER.error("Error on sent internal error message! Class: " + e1.getClass().getName() + ", Reason: " + e.getMessage());
            }
        }
    }
}
