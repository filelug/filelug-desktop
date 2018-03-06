package com.filelug.desktop.service.websocket;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelug.desktop.ClopuccinoMessages;
import com.filelug.desktop.Constants;
import com.filelug.desktop.Utility;
import com.filelug.desktop.dao.FileTransferOutDao;
import com.filelug.desktop.db.DatabaseConstants;
import com.filelug.desktop.model.*;
import com.filelug.desktop.service.*;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.protocol.BasicAsyncResponseConsumer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.File;

/**
 * <code>FileTransferOutWebSocketService</code> receives and process the web socket message from server with service id: DOWNLOAD_FILE2_V2
 *
 * @author masonhsieh
 * @version 1.0
 */
public class FileTransferOutWebSocketService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(FileTransferOutWebSocketService.class.getSimpleName());

    private final Session session;

    private final String message;

    private final ConnectSocket connectSocket;

    public FileTransferOutWebSocketService(final Session session, final String message, final ConnectSocket connectSocket) {
        this.session = session;
        this.message = message;
        this.connectSocket = connectSocket;
    }

    public void onFileTransferOutWebSocket() {
        // requested to upload file to server

        String operatorId = null;
        String clientLocale = null;
        String clientSessionId = null; // keep the value of download key
        String filePath = null;

        ObjectMapper mapper = Utility.createObjectMapper();

        try {
            RequestFileDownloadModel requestModel = mapper.readValue(message, RequestFileDownloadModel.class);

            operatorId = requestModel.getOperatorId();
            clientLocale = requestModel.getLocale();
            filePath = requestModel.getPath();
            long availableBytes = requestModel.getAvailableBytes();
            long downloadSizeLimitInBytes = requestModel.getDownloadSizeLimitInBytes();

            // Actually the client session id is the download key
            clientSessionId = requestModel.getClientSessionId();

            if (!connectSocket.validate(true)) {
                ResponseFileDownloadModel responseModel = new ResponseFileDownloadModel(Sid.DOWNLOAD_FILE2_V2, HttpServletResponse.SC_UNAUTHORIZED, ClopuccinoMessages.localizedMessage(clientLocale, "invalid.session"), operatorId, System.currentTimeMillis(), clientSessionId, filePath, 0L);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else if (clientSessionId == null || clientSessionId.trim().length() < 1) {
                ResponseFileDownloadModel responseModel = new ResponseFileDownloadModel(Sid.DOWNLOAD_FILE2_V2, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "empty.client.session"), operatorId, System.currentTimeMillis(), clientSessionId, filePath, 0L);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else if (filePath == null || !new File(filePath).exists()) {
                ResponseFileDownloadModel responseModel = new ResponseFileDownloadModel(Sid.DOWNLOAD_FILE2_V2, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "directory.or.file.not.found", filePath != null ? filePath : ""), operatorId, System.currentTimeMillis(), clientSessionId, filePath, 0L);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else if (!new File(filePath).isFile() && !BundleDirectoryService.isBundleDirectory(new File(filePath))) {
                ResponseFileDownloadModel responseModel = new ResponseFileDownloadModel(Sid.DOWNLOAD_FILE2_V2, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "not.file", filePath), operatorId, System.currentTimeMillis(), clientSessionId, filePath, 0L);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else if (!new File(filePath).canRead()) {
                ResponseFileDownloadModel responseModel = new ResponseFileDownloadModel(Sid.DOWNLOAD_FILE2_V2, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "file.read.permission.denied", filePath), operatorId, System.currentTimeMillis(), clientSessionId, filePath, 0L);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else if (availableBytes < 1) {
                ResponseFileDownloadModel responseModel = new ResponseFileDownloadModel(Sid.DOWNLOAD_FILE2_V2, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "param.null.or.empty", "available volumes"), operatorId, System.currentTimeMillis(), clientSessionId, filePath, 0L);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else if (availableBytes < new File(filePath).length()) {
                String fileSizeForRepresentation = Utility.representationFileSizeFromBytes(availableBytes);
                ResponseFileDownloadModel responseModel = new ResponseFileDownloadModel(Sid.DOWNLOAD_FILE2_V2, HttpServletResponse.SC_FORBIDDEN, ClopuccinoMessages.localizedMessage(clientLocale, "not.enough.transfer.bytes", fileSizeForRepresentation), operatorId, System.currentTimeMillis(), clientSessionId, filePath, 0L);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else if (downloadSizeLimitInBytes < new File(filePath).length()) {
                String fileSizeForRepresentation = Utility.representationFileSizeFromBytes(new File(filePath).length());
                ResponseFileDownloadModel responseModel = new ResponseFileDownloadModel(Sid.DOWNLOAD_FILE2_V2, Constants.HTTP_STATUS_FILE_SIZE_LIMIT_EXCEEDED, ClopuccinoMessages.localizedMessage(clientLocale, "exceed.download.size.limit", fileSizeForRepresentation), operatorId, System.currentTimeMillis(), clientSessionId, filePath, 0L);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else {
                // Check if a bundle directory
                // Bundle directory not supports resume-download
                // for the header value Last-Modified is always different than the last one
                // because the zip file is generate on the fly.

                File zipFile = null;

                File directory = new File(filePath);

                if (BundleDirectoryService.isBundleDirectory(directory)) {
                    try {
                        zipFile = BundleDirectoryService.createZipFileFromBundleDirectory(directory, false);

                        zipFile.deleteOnExit();

                        filePath = zipFile.getAbsolutePath();
                    } catch (Exception e) {
                        LOGGER.error("Error on creating zip file from bundle directory: " + filePath, e);
                    }
                }

                if (directory.isDirectory() && zipFile == null) {
                    ResponseFileDownloadModel responseModel = new ResponseFileDownloadModel(Sid.DOWNLOAD_FILE2_V2, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "not.file", filePath), operatorId, System.currentTimeMillis(), clientSessionId, filePath, 0L);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else {
                    boolean deleteFileAfterUpload = (zipFile != null);

                    final String range = requestModel.getRange();
                    final String transferKey = clientSessionId;
                    final String filePathCopy = filePath;

                    File file = new File(filePath);

                    final long fileSize = file.length();

                    final long lastModifiedDateInMillis = file.lastModified();

                    // It's possible that the FileDownloaded with the same downloadKey exists
                    // because resume-downloading is supported.

                    long startingBytes = findStartingByteFromRange(range);

                    final FileTransferOutDao fileTransferOutDao = connectSocket.getFileTransferOutDao();

                    final String userId = connectSocket.getUserId();

                    final String lugServerId = connectSocket.getLugServerId();

                    // If stargingBytes is 0, meaning transfer file from start, the file size and last modified date need to be updated;
                    // if not from start, compare the values of file size and last modified date in local db with the values for the current file to make sure file is not modified.

                    FileTransferOut fileTransferOut = fileTransferOutDao.findFileTransferOutForTransferKey(transferKey);

                    if (startingBytes < 1 || fileTransferOut == null) {
                        // transfer from start

                        startingBytes = 0L;

                        fileTransferOut = new FileTransferOut(transferKey, userId, filePath, fileSize, lastModifiedDateInMillis, System.currentTimeMillis(), 0L, DatabaseConstants.TRANSFER_STATUS_PROCESSING);

                        fileTransferOutDao.createFileTransferOut(fileTransferOut);
                    } else {
                        final Long originalFileSize = fileTransferOut.getFileSize();
                        final Long originalLastModifiedDateInMillis = fileTransferOut.getFileLastModifiedDate();

                        if (startingBytes >= fileSize || fileSize != originalFileSize || lastModifiedDateInMillis != originalLastModifiedDateInMillis) {
                            // transfer from start

                            startingBytes = 0L;

                            fileTransferOut.setFilePath(filePath);
                            fileTransferOut.setFileSize(fileSize);
                            fileTransferOut.setFileLastModifiedDate(lastModifiedDateInMillis);
                        }

                        // remain the old start timestamp
//                        fileTransferOut.setStartTimestamp(System.currentTimeMillis());

                        fileTransferOut.setStatus(DatabaseConstants.TRANSFER_STATUS_PROCESSING);
                        fileTransferOut.setEndTimestamp(0L);

                        fileTransferOutDao.updateFileTransferOut(fileTransferOut);
                    }

                    final long finalStartingBytes = startingBytes;

                    Utility.getExecutorService().execute(() -> {
                        try {
                            HttpAsyncResponseConsumer<HttpResponse> consumer = new BasicAsyncResponseConsumer();

                            FutureCallback<HttpResponse> callback = new FutureCallback<HttpResponse>() {
                                @Override
                                public void completed(HttpResponse result) {
                                    int status = result.getStatusLine().getStatusCode();

                                    // Consider partial content 206 as successfully downloaded file
                                    if (status == HttpServletResponse.SC_OK || status == HttpServletResponse.SC_PARTIAL_CONTENT) {
                                        fileTransferOutDao.updateFileTransferOutStatus(transferKey, DatabaseConstants.TRANSFER_STATUS_SUCCESS, System.currentTimeMillis());

                                        LOGGER.info(String.format("File transferred out successfully: '%s'", filePathCopy));
                                    } else {
                                        fileTransferOutDao.updateFileTransferOutStatus(transferKey, DatabaseConstants.TRANSFER_STATUS_FAILURE, System.currentTimeMillis());

                                        if (status == Constants.HTTP_STATUS_CLIENT_CLOSE_REQUEST) {
                                            // user canceled
                                            LOGGER.info(String.format("User canceled transfering file out: '%s'", filePathCopy));
                                        } else {
                                            LOGGER.info(String.format("File transferred out completely with error. Status code: %d\nfile: '%s'", status, filePathCopy));
                                        }
                                    }
                                }

                                @Override
                                public void failed(Exception ex) {
                                    fileTransferOutDao.updateFileTransferOutStatus(transferKey, DatabaseConstants.TRANSFER_STATUS_FAILURE, System.currentTimeMillis());

                                    String errorMessage = ex.getMessage();

                                    if (errorMessage != null && errorMessage.toLowerCase().contains("connection reset by peer")) {
                                        LOGGER.warn("User canceled the file to transfer out. File path: " + filePathCopy);
                                    } else if (errorMessage != null && errorMessage.toLowerCase().contains("broken pipe")) {
                                        LOGGER.warn("Network reset or user canceled the file to transfer out. File path: " + filePathCopy);
                                    } else {
                                        LOGGER.error("Failed on transfering file out. file: " + filePathCopy, ex);
                                    }
                                }

                                @Override
                                public void cancelled() {
                                    LOGGER.warn("User canceled transfering out file: " + filePathCopy);
                                }
                            };

                            final DirectoryService directoryService = new DefaultDirectoryService();

                            directoryService.doAsyncUploadFile3(userId, lugServerId, filePathCopy, transferKey, finalStartingBytes, consumer, callback, deleteFileAfterUpload);
                        } catch (Exception e) {
                            // already handled by FutureCallback,failed(Exception), DO NOT HANDLED AGAIN.

//                                fileTransferOutDao.updateFileDownload(DatabaseConstants.TRANSFER_STATUS_FAILURE, System.currentTimeMillis(), 0, downloadKey);
//
//                                String errorMessage = e.getMessage();
//
//                                if (errorMessage != null && errorMessage.contains("Connection reset by peer")) {
//                                    LOGGER.warn("User canceled the file download. File path: " + filePathCopy);
//                                } else {
//                                    LOGGER.error("Failed on upload file to server. File path: " + filePathCopy, e);
//                                }
                        }
                    });

                    ResponseFileDownloadModel responseModel = new ResponseFileDownloadModel(Sid.DOWNLOAD_FILE2_V2, HttpServletResponse.SC_OK, null, operatorId, System.currentTimeMillis(), clientSessionId, filePath, fileSize);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                }
            }
        } catch (Exception e) {
            /* websocket sent internal error */
            try {
                String errorMessage = ClopuccinoMessages.localizedMessage(clientLocale, "error.invoke.service", "DOWNLOAD_FILE2_V2", e.getMessage() != null ? e.getMessage() : "");

                LOGGER.error(errorMessage, e);

                ResponseFileDownloadModel responseModel = new ResponseFileDownloadModel(Sid.DOWNLOAD_FILE2_V2, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage, operatorId, System.currentTimeMillis(), clientSessionId, filePath, 0L);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } catch (Exception e1) {
                LOGGER.error("Error on sent internal error message! Class: " + e1.getClass().getName() + ", Reason: " + e.getMessage());
            }
        }
    }

    private static long findStartingByteFromRange(String range) {
        long startingBytes = 0L;

        String rangePrefix = "bytes=";

        if (range != null && range.startsWith(rangePrefix) && range.contains("-")) { // bytes=9500-
            int indexHyphen = range.indexOf("-");

            String rangeNumber = range.substring(rangePrefix.length(), indexHyphen);

            try {
                startingBytes = Long.parseLong(rangeNumber);
            } catch (Exception e) {
                LOGGER.debug("Range header value not supported: " + range);
            }
        } else {
            LOGGER.debug("Range header value not supported: " + range);
        }

        return startingBytes;
    }
}
