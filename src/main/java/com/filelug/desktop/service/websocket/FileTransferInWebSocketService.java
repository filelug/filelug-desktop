package com.filelug.desktop.service.websocket;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelug.desktop.ClopuccinoMessages;
import com.filelug.desktop.Constants;
import com.filelug.desktop.Utility;
import com.filelug.desktop.dao.FileTransferInDao;
import com.filelug.desktop.db.DatabaseConstants;
import com.filelug.desktop.model.*;
import com.filelug.desktop.service.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.client.methods.ZeroCopyConsumer;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.File;
import java.io.IOException;

/**
 * <code>FileTransferInWebSocketService</code> receives and process the web socket message from server with service id: UPLOAD_FILE2_V2
 *
 * @author masonhsieh
 * @version 1.0
 */
public class FileTransferInWebSocketService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(FileTransferInWebSocketService.class.getSimpleName());

    private final Session session;

    private final String message;

    private final ConnectSocket connectSocket;

    public FileTransferInWebSocketService(final Session session, final String message, final ConnectSocket connectSocket) {
        this.session = session;
        this.message = message;
        this.connectSocket = connectSocket;
    }

    public void onFileTransferInWebSocket() {
        // requested from device to download file from server

        String operatorId = null;
        String clientLocale = null;
        String clientSessionId = null;
        String transferKey = null;

        final ObjectMapper mapper = Utility.createObjectMapper();

        try {
            RequestFileUploadModel requestModel = mapper.readValue(message, RequestFileUploadModel.class);

            operatorId = requestModel.getOperatorId();
            clientLocale = requestModel.getLocale();

            clientSessionId = requestModel.getClientSessionId();
            transferKey = requestModel.getTransferKey();

            if (!connectSocket.validate(true) || clientSessionId == null || clientSessionId.trim().length() < 1) {
                ResponseFileUploadModel responseModel = new ResponseFileUploadModel(Sid.UPLOAD_FILE2_V2, HttpServletResponse.SC_UNAUTHORIZED, ClopuccinoMessages.localizedMessage(clientLocale, "invalid.session"), operatorId, clientSessionId, System.currentTimeMillis(), transferKey, DatabaseConstants.TRANSFER_STATUS_FAILURE);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else if (transferKey == null || transferKey.trim().length() < 1) {
                ResponseFileUploadModel responseModel = new ResponseFileUploadModel(Sid.UPLOAD_FILE2_V2, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "transformation key"), operatorId, clientSessionId, System.currentTimeMillis(), "", "");

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else {

                final String directory = requestModel.getDirectory();

                final String filename = requestModel.getFilename();

                // It's possible that the user re-upload file but the directory has been removed by the desktop user.
                // So check if the directory path is not a file only when the path exists.

                if (directory == null || (new File(directory).exists() && new File(directory).isFile())) {
                    ResponseFileUploadModel responseModel = new ResponseFileUploadModel(Sid.UPLOAD_FILE2_V2, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "directory.not.found", directory != null ? directory : ""), operatorId, clientSessionId, System.currentTimeMillis(), transferKey, DatabaseConstants.TRANSFER_STATUS_FAILURE);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else if (filename == null || filename.trim().length() < 1) {
                    ResponseFileUploadModel responseModel = new ResponseFileUploadModel(Sid.UPLOAD_FILE2_V2, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "filename.not.specified"), operatorId, clientSessionId, System.currentTimeMillis(), transferKey, DatabaseConstants.TRANSFER_STATUS_FAILURE);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else {
                    // Create if the directory not exists

                    if (!new File(directory).exists()) {
                        new File(directory).mkdirs();
                    }

                    String userId = connectSocket.getUserId();

                    String lugServerId = connectSocket.getLugServerId();

                    FileTransferInDao fileTransferInDao = connectSocket.getFileTransferInDao();

                    // save upload record to db

                    FileTransferIn fileTransferIn = new FileTransferIn(transferKey, userId, filename, directory, 0L, System.currentTimeMillis(), 0L, DatabaseConstants.TRANSFER_STATUS_PROCESSING);

                    boolean fileUploadeFound = fileTransferInDao.existingFileTransferInForTransferKey(transferKey);

                    if (!fileUploadeFound) {
                        fileTransferInDao.createFileTransferIn(fileTransferIn);
                    } else {
                        fileTransferInDao.updateFileTransferIn(fileTransferIn);
                    }

                    final String finalOperatorId = operatorId;
                    final String finalTransferKey = transferKey;
                    final String finalClientSessionId = clientSessionId;

                    final DirectoryService directoryService = new DefaultDirectoryService();

                    final File tmpFile = File.createTempFile(filename, String.valueOf(System.currentTimeMillis() + ".fldownload"), new File(directory));

                    ZeroCopyConsumer<File> consumer = new ZeroCopyConsumer<File>(tmpFile) {
                        @Override
                        protected File process(HttpResponse response, File file, ContentType contentType) throws Exception {
                            File processedFile = null;

                            int status = response.getStatusLine().getStatusCode();

                            if (status == HttpServletResponse.SC_OK && file != null) {
                                Header contentLengthHeader = response.getFirstHeader(HttpHeaders.CONTENT_LENGTH);

//                                try {
                                if (contentLengthHeader != null) {
                                    String expectFileSizeString = contentLengthHeader.getValue();

                                    long fileLength = file.length();
                                    long expectedFileSize = -1;
                                    try {
                                        expectedFileSize = Long.valueOf(expectFileSizeString);
                                    } catch (Exception e) {
                                        LOGGER.error("File size not a number: " + expectFileSizeString);
                                    }

                                    if (expectedFileSize > -1 && fileLength == expectedFileSize) {
                                        LOGGER.debug("Finished transfering in file '"+ file.getAbsolutePath() + "' from server.");

                                        File destFile = new File(directory, filename);

                                        // make sure file not overwritten
                                        if (destFile.exists()) {
                                            String baseFileName = FilenameUtils.getBaseName(filename);
                                            String extension = FilenameUtils.getExtension(filename);
                                            for (int suffix = 2; destFile.exists(); suffix++) {
                                                String newFilename = baseFileName + "_" + suffix;
                                                if (extension.length() > 0) {
                                                    newFilename = newFilename + "." + extension;
                                                }

                                                destFile = new File(directory, newFilename);
                                            }
                                        }

                                        boolean renameSuccess = file.renameTo(destFile);

                                        if (!renameSuccess) {
                                            throw new IOException(String.format("Failed to rename file: '%s' to '%s'", file.getAbsolutePath(), destFile.getAbsolutePath()));
                                        } else {
                                            LOGGER.info(String.format("File transferred in succesfully to path: '%s'", destFile.getAbsolutePath()));
                                        }

                                        // Make sure the file_uploaded is not deleted yet! If deleted, delete the file and return failure for file_uploaded not found.

                                        boolean fileUploadeFound = fileTransferInDao.existingFileTransferInForTransferKey(finalTransferKey);

                                        if (!fileUploadeFound) {
                                            // Not found, file should be deleted as well.

                                            String errorMessage = ClopuccinoMessages.getMessage("file.upload.data.not.found.or.deleted");

                                            LOGGER.info(errorMessage);

                                            if (destFile.exists()) {
                                                try {
                                                    if (destFile.delete()) {
                                                        LOGGER.info("File '" + destFile.getAbsolutePath() + "' deleted because the same file transferred in again.");
                                                    }
                                                } catch (Exception e) {
                                                    LOGGER.error("Failed to delete file '" + destFile.getAbsolutePath() + "'.", e);
                                                }
                                            }

                                            ResponseFileUploadModel responseModel = new ResponseFileUploadModel(Sid.UPLOAD_FILE2_V2, HttpServletResponse.SC_CONFLICT, errorMessage, finalOperatorId, finalClientSessionId, System.currentTimeMillis(), finalTransferKey, DatabaseConstants.TRANSFER_STATUS_FAILURE);
                                            session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                                        } else {
                                            processedFile = destFile;

                                            fileTransferInDao.updateFileTransferIn(DatabaseConstants.TRANSFER_STATUS_SUCCESS, destFile.length(), System.currentTimeMillis(), finalTransferKey);

                                            // Send process result to repository

                                            // Add client session id to repository
                                            ResponseFileUploadModel responseModel = new ResponseFileUploadModel(Sid.UPLOAD_FILE2_V2, HttpServletResponse.SC_OK, null, finalOperatorId, finalClientSessionId, System.currentTimeMillis(), finalTransferKey, DatabaseConstants.TRANSFER_STATUS_SUCCESS);
                                            session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                                        }
                                    } else {
                                        // deleted downloaded file
                                        deleteFileAndLog(file);

                                        throw new Exception(String.format("File length '%d' not expected to '%d'", fileLength, expectedFileSize));
                                    }
                                } else {
                                    // deleted downloaded file
                                    deleteFileAndLog(file);

                                    throw new Exception("Content length not exists and failed to used to check if file transferred in successfully.");
                                }
                            } else {
                                String responseContent = null;

                                if (file != null && file.exists()) {
                                    responseContent = FileUtils.readFileToString(file, Constants.DEFAULT_FILE_READ_WRITE_CHARSET);
                                }

                                LOGGER.error("Error on transfering file in with stauts=" + status + ", reason: \"" + (responseContent != null ? responseContent : "(empty)") + "\"");

                                // deleted downloaded file
                                deleteFileAndLog(file);

                                fileTransferInDao.updateFileTransferIn(DatabaseConstants.TRANSFER_STATUS_FAILURE, 0, System.currentTimeMillis(), finalTransferKey);

                                // Send process result to repository
                                ResponseFileUploadModel responseModel = new ResponseFileUploadModel(Sid.UPLOAD_FILE2_V2, status, "", finalOperatorId, finalClientSessionId, System.currentTimeMillis(), finalTransferKey, DatabaseConstants.TRANSFER_STATUS_FAILURE);
                                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                            }

                            return processedFile;
                        }
                    };

                    FutureCallback<File> callback = new FutureCallback<File>() {
                        @Override
                        public void completed(File result) {
//                            if (result != null) {
//                                LOGGER.debug("File downloaded to: " + result.getAbsolutePath());
//                            } else {
//                                LOGGER.debug("Failed to download file.");
//                            }
                        }

                        @Override
                        public void failed(Exception ex) {
                            LOGGER.error(String.format("Failed to transfer in file: '%s' at folder: '%s'", filename, directory), ex);

                            deleteFileAndLog(tmpFile);

                            fileTransferInDao.updateFileTransferIn(DatabaseConstants.TRANSFER_STATUS_FAILURE, 0, System.currentTimeMillis(), finalTransferKey);

                            // Send process result to repository
                            ResponseFileUploadModel responseModel = new ResponseFileUploadModel(Sid.UPLOAD_FILE2_V2, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage(), finalOperatorId, finalClientSessionId, System.currentTimeMillis(), finalTransferKey, DatabaseConstants.TRANSFER_STATUS_FAILURE);

                            try {
                                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                            } catch (Exception e2) {
                                LOGGER.error("Error on sending the result of file-transferred-in to server.", e2);
                            }
                        }

                        @Override
                        public void cancelled() {
                            LOGGER.warn(String.format("User canceled transfering file in with filename: '%s', folder: '%s'", filename, directory));
                        }
                    };

                    directoryService.doAsyncDownloadFile(userId, lugServerId, directory, filename, finalTransferKey, consumer, callback);
                }
            }
        } catch (Exception e) {
            // websocket sent internal error
            try {
                String errorMessage = ClopuccinoMessages.localizedMessage(clientLocale, "error.invoke.service", "UPLOAD_FILE2_V2", e.getMessage() != null ? e.getMessage() : "");

                LOGGER.error(errorMessage, e);

                ResponseFileUploadModel responseModel = new ResponseFileUploadModel(Sid.UPLOAD_FILE2_V2,  HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage, operatorId, clientSessionId, System.currentTimeMillis(), transferKey, DatabaseConstants.TRANSFER_STATUS_FAILURE);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } catch (Exception e1) {
                LOGGER.error("Error on sent internal error message! Class: " + e1.getClass().getName() + ", Reason: " + e.getMessage());
            }
        }
    }

    private void deleteFileAndLog(File file) {
        if (file != null && file.exists()) {
            try {
                if (file.delete()) {
                    LOGGER.info("File '" + file.getAbsolutePath() + "' deleted.");
                }
            } catch (Exception e) {
                LOGGER.error("Failed to delete file '" + file.getAbsolutePath() + "'.", e);
            }
        }
    }
}
