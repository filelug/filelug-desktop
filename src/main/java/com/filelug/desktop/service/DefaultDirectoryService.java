package com.filelug.desktop.service;

import ch.qos.logback.classic.Logger;
import com.filelug.desktop.ClopuccinoMessages;
import com.filelug.desktop.Constants;
import com.filelug.desktop.OSUtility;
import com.filelug.desktop.Utility;
import com.filelug.desktop.db.DatabaseAccess;
import com.filelug.desktop.db.DatabaseConstants;
import com.filelug.desktop.db.HyperSQLDatabaseAccess;
import com.filelug.desktop.exception.*;
import com.filelug.desktop.model.HierarchicalFactory;
import com.filelug.desktop.model.HierarchicalModel;
import com.google.common.io.ByteStreams;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.client.methods.ZeroCopyConsumer;
import org.apache.http.nio.client.methods.ZeroCopyPost;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;


/**
 * <code>DefaultDirectoryService</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class DefaultDirectoryService implements DirectoryService, DatabaseConstants {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("SERVICE_DIRECTORY");

    private final ServiceUtilities serviceUtilities;

//    private final UserDao userDao;

    private final UserService userService;

    public DefaultDirectoryService() {
        this(null);
    }

    public DefaultDirectoryService(DatabaseAccess dbAccess) {
        this.serviceUtilities = new ServiceUtilities();

        DatabaseAccess localDbAccess;

        if (dbAccess != null) {
            localDbAccess = dbAccess;
        } else {
            localDbAccess = new HyperSQLDatabaseAccess();
        }

//        userDao = new UserDao(localDbAccess);

        userService = new DefaultUserService(localDbAccess);
    }

    @Override
    public HierarchicalModel createDirectory(HierarchicalModel directoryModel) throws Exception {
        String parent = Utility.correctDirectoryPath(directoryModel.getParent());

        if (parent.trim().length() < 1) {
            throw new NullOrEmptyParameterException("parent");
        }
//        if (parent == null || parent.trim().length() < 1) {
//            throw new NullOrEmptyParameterException("parent");
//        }

        String name = directoryModel.getName();

        if (name == null || name.trim().length() < 1) {
            throw new NullOrEmptyParameterException("name");
        }

        // For Windows, only names of the system roots end with ':'
        if (OSUtility.isWindows() && parent.endsWith(":")) {
            parent = parent + File.separator;
        }

        File newDirectory = new File(parent, name);

        if (newDirectory.exists()) {
            throw new DuplicatedDirectoryException(newDirectory.getAbsolutePath());
        }

        Boolean readable = directoryModel.getReadable();
        if (readable != null) {
            newDirectory.setReadable(readable);
        }

        Boolean writable = directoryModel.getWritable();
        if (writable != null) {
            newDirectory.setWritable(writable);
        }

        Boolean executable = directoryModel.getExecutable();
        if (executable != null) {
            newDirectory.setExecutable(executable);
        }

        newDirectory.mkdirs();

        return HierarchicalFactory.createHierarchical(newDirectory, newDirectory, false, true);
    } // end createDirectory(HierarchicalModel)

    @Override
    public HierarchicalModel updateDirectory(HierarchicalModel directoryModel) throws Exception {
        String parent = Utility.correctDirectoryPath(directoryModel.getParent());

        if (parent == null || parent.trim().length() < 1) {
            throw new NullOrEmptyParameterException("parent");
        }

        String name = directoryModel.getName();

        if (name == null || name.trim().length() < 1) {
            throw new NullOrEmptyParameterException("name");
        }

        // For Windows, only names of the system roots end with ':'
        if (OSUtility.isWindows() && parent.endsWith(":")) {
            parent = parent + File.separator;
        }

        File newDirectory = new File(parent, name);

        if (!newDirectory.exists()) {
            throw new DirectoryNotFoundException(newDirectory.getAbsolutePath());
        }

        if (newDirectory.isFile()) {
            throw new NotDirectoryException(newDirectory.getAbsolutePath());
        }

        Boolean readable = directoryModel.getReadable();
        if (readable != null) {
            newDirectory.setReadable(readable);
        }

        Boolean writable = directoryModel.getWritable();
        if (writable != null) {
            newDirectory.setWritable(writable);
        }

        Boolean executable = directoryModel.getExecutable();
        if (executable != null) {
            newDirectory.setExecutable(executable);
        }

        return HierarchicalFactory.createHierarchical(newDirectory, newDirectory, false, true);

    } // end updateDirectory(HierarchicalModel)

    @Override
    public HierarchicalModel findByPath(String path, Boolean includingSize) throws Exception {
        File file = new File(path);

        if (!file.exists()) {
            throw new FileNotFoundException(path);
        }

        HierarchicalModel model = HierarchicalFactory.createHierarchical(file, file, includingSize, true);

        return model;
    }

    @Override
    public HierarchicalModel moveDirectory(String sourcePath, String targetPath, boolean showSizeInReturn) throws Exception {
        if (sourcePath == null || sourcePath.trim().length() < 1) {
            throw new NullOrEmptyParameterException("source");
        }

        if (targetPath == null || targetPath.trim().length() < 1) {
            throw new NullOrEmptyParameterException("target");
        }

        File sourceFile = new File(sourcePath);

        if (!sourceFile.exists()) {
            throw new DirectoryNotFoundException(sourcePath);
        }

        if (sourceFile.isFile()) {
            throw new NotDirectoryException(sourcePath);
        }

        if (!sourceFile.canWrite()) {
            throw new FilePermissionDeniedException(sourcePath, "write");
        }

        File targetFile = new File(targetPath);

        if (targetFile.exists()) {
            throw new DuplicatedDirectoryException(targetPath);
        }

        FileUtils.moveDirectory(sourceFile, targetFile);

        return HierarchicalFactory.createHierarchical(targetFile, targetFile, showSizeInReturn, true);
    } // end move(String, String)

    @Override
    public HierarchicalModel copyDirectory(String sourcePath, String targetPath, boolean showSizeInReturn) throws Exception {
        if (sourcePath == null || sourcePath.trim().length() < 1) {
            throw new NullOrEmptyParameterException("source");
        }

        if (targetPath == null || targetPath.trim().length() < 1) {
            throw new NullOrEmptyParameterException("target");
        }

        File sourceFile = new File(sourcePath);

        if (!sourceFile.exists()) {
            throw new DirectoryNotFoundException(sourcePath);
        }

        if (sourceFile.isFile()) {
            throw new NotDirectoryException(sourcePath);
        }

        if (!sourceFile.canRead()) {
            throw new FilePermissionDeniedException(sourcePath, "read");
        }

        File targetFile = new File(targetPath);

        if (targetFile.exists()) {
            throw new DuplicatedDirectoryException(targetPath);
        }

        FileUtils.copyDirectory(sourceFile, targetFile);

        return HierarchicalFactory.createHierarchical(targetFile, targetFile, showSizeInReturn, true);
    } // end copy(String, String)

    @Override
    public HierarchicalModel delete(String path, Boolean forever, boolean showSizeInReturn) throws Exception {
        if (path == null || path.trim().length() < 1) {
            throw new NullOrEmptyParameterException("path");
        }

        if (forever == null) {
            throw new NullOrEmptyParameterException("forever");
        }

        File fileOrDirectory = new File(path);

        if (!fileOrDirectory.exists()) {
            throw new DirectoryNotFoundException(path);
        }

        /* check if file can be deleted */
        if (fileOrDirectory.isFile()) {
            RandomAccessFile randomAccessFile = null;
            FileLock lock = null;

            try {
                randomAccessFile = new RandomAccessFile(fileOrDirectory, "rw");
                lock = randomAccessFile.getChannel().tryLock();

                if (lock == null) {
                    throw new FailedMoveToTrashException(path, new IOException(ClopuccinoMessages.getMessage("failed.move.to.trash.lock.by.other")));
                }
            } catch (Exception e) {
                LOGGER.error("Trying locking file failed!", e);
                throw new FailedMoveToTrashException(path);
            } finally {
                if (lock != null) {
                    try {
                        lock.release();
                    } catch (Exception e) {
                        /* ignored */
                    }
                }

                if (randomAccessFile != null) {
                    try {
                        randomAccessFile.close();
                    } catch (Exception e) {
                        /* ignored */
                    }
                }
            }
        }

        HierarchicalModel model = HierarchicalFactory.createHierarchical(fileOrDirectory, fileOrDirectory, showSizeInReturn, false);

        /* 不支援檔案與目錄永久刪除 */
        if (!forever) {
            com.sun.jna.platform.FileUtils fileUtils = com.sun.jna.platform.FileUtils.getInstance();

            if (!fileUtils.hasTrash()) {
                throw new UnsupportedTrashException();
            } else {
                try {
                    fileUtils.moveToTrash(new File[]{fileOrDirectory});
                } catch (Exception e) {
                    LOGGER.error("Failed to move file to trash: " + path, e);

                    throw new FailedMoveToTrashException(path, e);
                }
            }
        } else {
            throw new UnsupportedOperationException();
        }

//        /* 作業系統要有資源回收桶才支援非永久刪除 */
//        if (!forever && !com.sun.jna.platform.FileUtils.getInstance().hasTrash()) {
//            throw new UnsupportedTrashException();
//        }
//
//        File directory = new File(path);
//
//        if (!directory.exists()) {
//            throw new DirectoryNotFoundException(path);
//        }
//
//        if (directory.isFile()) {
//            throw new NotDirectoryException(path);
//        }
//
//        if (!directory.canWrite()) {
//            throw new FilePermissionDeniedException(path, "write");
//        }
//
//        HierarchicalModel model = HierarchicalFactory.createHierarchical(directory, false, true)
//
//        if (!forever) {
//            com.sun.jna.platform.FileUtils jnaFileUtils = com.sun.jna.platform.FileUtils.getInstance();
//            jnaFileUtils.moveToTrash(new File[]{directory});
//        } else {
//            FileUtils.forceDelete(directory);
//        }

        return model;
    } // end delete(String, Boolean)

//    @Override
//    public Response downloadFile(String path, HttpHeaders requestHeaders) throws Exception {
//        if (path == null || path.trim().length() < 1) {
//            throw new NullOrEmptyParameterException("path");
//        }
//
//        File file = new File(path);
//
//        if (!file.exists()) {
//            throw new FileNotFoundException(path);
//        }
//
//        if (!file.isFile()) {
//            throw new NotFileException(path);
//        }
//
//        String mt = HierarchicalModel.prepareContentType(file);
//
//        /**
//         * File name should be encoded when download by all browsers except for Safari(5):
//         * (1)Chrome and IE 7/8/10: filename=[encoded filename]
//         * (2)Safari(5): filename=[ISO-8859-1 encoded file name]
//         * (3)Safari(6) and Firefox: filename*=UTF-8''[encoded filename]
//         *
//         * Note that the user agent string of Chrome contains 'Safari'
//         *
//         * Header 'User-Agent' on Mac:
//         * Chrome:   Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.0 Safari/537.1
//         * Safari 6: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/536.30.1 (KHTML, like Gecko) Version/6.0.5 Safari/536.30.1
//         * Firefox:  Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/21.0
//         *
//         * Header 'User-Agent' on Windows XP:
//         * Chrome:                       Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36
//         * IE 8(Compatibility View):     Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; InfoPath.2; .NET CLR 2.0.50727)
//         * IE 8(Non-Compatibility View): Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; InfoPath.2; .NET CLR 2.0.50727)
//         * Firefox:                      Mozilla/5.0 (Windows NT 5.1; rv:21.0) Gecko/20100101 Firefox/21.0
//         * Safari 5:                     Mozilla/5.0 (Windows NT 5.1) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2
//         *
//         * Header 'User-Agent' on Windows 7:
//         * IE 10(Compatible): Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)
//         */
//
//        /* DEBUG */
////        MultivaluedMap<String, String> allHeaders = requestHeaders.getRequestHeaders();
////        Set<Map.Entry<String, List<String>>> headerSet = allHeaders.entrySet();
////
////        System.out.println("Request Headers:");
////
////        for (Map.Entry<String, List<String>> headerEntry : headerSet) {
////            System.out.println(headerEntry.getKey() + ":" + Arrays.toString(headerEntry.getValue().toArray()));
////        }
//
//        List<String> userAgents = requestHeaders.getRequestHeader("User-Agent");
//
//        String userAgent = userAgents != null && userAgents.size() > 0 ? userAgents.get(0) : "";
//
//        String contentDisposition;
//        if (userAgent.contains("Chrome") || userAgent.contains("MSIE")) {
//            /* Chrome and IE8: filename=[encoded filename] */
//            contentDisposition = "attachment; filename=" + Utility.realUrlEncode(file.getName());
//        } else if (userAgent.contains("Safari") && userAgent.contains("Version/5")) {
//            /* Safari(5): filename=[plain file name string] */
//            contentDisposition = "attachment; filename=" + new String(file.getName().getBytes(Constants.DEFAULT_FILENAME_CHARSET), "ISO-8859-1");
//        } else {
//            /* Safari(6) and Firefox: filename*=UTF-8''[encoded filename] */
//            contentDisposition = "attachment; filename*=UTF-8''" + Utility.realUrlEncode(file.getName());
//        }
//
//        /* attachment; filename*=UTF-8''filename */
//        return Response.ok(file, mt).header("Content-Disposition", contentDisposition).header("Content-Length", file.length()).build();
//    } // end downloadFile(String)
//
//    public void doAsyncUploadFile(String lugServerId, final String filePath, String clientSessionId, long startingByte, HttpAsyncResponseConsumer<HttpResponse> consumer, FutureCallback<HttpResponse> callback, boolean deleteFileAfterUploaded) throws Exception {
//        RequestConfig config = RequestConfig.custom().setSocketTimeout(Constants.SO_TIMEOUT_IN_SECONDS_TO_FILE_UPLOAD * 1000).setConnectTimeout(Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000).build();
//
//        HttpAsyncClientBuilder clientBuilder = HttpAsyncClientBuilder.create().setDefaultRequestConfig(config);
//
//        Set<Header> newHeaders = new HashSet<>();
//
//        if (clientSessionId != null && clientSessionId.trim().length() > 0) {
//            // the client session id is actually the download key sent from repository, and no need to generate another new one again
//            newHeaders.add(new BasicHeader(Constants.HTTP_HEADER_AUTHORIZATION_NAME, clientSessionId));
//        }
//
//        String fullPath = serviceUtilities.composeFullAddressWithLugServerId(lugServerId, "directory/supload");
//
//        String fileExtension = FilenameUtils.getExtension(filePath);
//
//        String contentTypeString = HierarchicalModel.prepareContentTypeFromFileExtension(fileExtension);
//
//        ContentType contentType = ContentType.create(contentTypeString);
//
//        CloseableHttpAsyncClient httpClient = null;
//
//        File uploadFile = new File(filePath);
//
//        // Content-Length - DO NOT ADD THIS, clientBuilder will add later before sending file
//
//        try {
//            // This method not supports resume-downloading, don't have to prepare for partial content
//
//            if (newHeaders.size() > 0) {
//                clientBuilder.setDefaultHeaders(newHeaders);
//            }
//
//            ZeroCopyPost producer = new ZeroCopyPost(fullPath, uploadFile, contentType);
//
//            LOGGER.debug("Execute async request POST " + fullPath);
//
//            httpClient = clientBuilder.build();
//
//            httpClient.start();
//
//            Future<HttpResponse> future = httpClient.execute(producer, consumer, callback);
//
//            future.get();
//        } finally {
//            if (httpClient != null) {
//                try {
//                    httpClient.close();
//                } catch (Exception e) {
//                    // ignored
//                }
//            }
//
//            if (deleteFileAfterUploaded && uploadFile.exists()) {
//                try {
//                    if (uploadFile.delete()) {
//                        LOGGER.info("Deleted file: " + uploadFile.getAbsolutePath());
//                    }
//                } catch (Exception e) {
//                    // ignored
//                }
//            }
//        }
//    }
//
//    public void doAsyncUploadFile2(String lugServerId, final String filePath, String clientSessionId, long startingByte, HttpAsyncResponseConsumer<HttpResponse> consumer, FutureCallback<HttpResponse> callback, boolean deleteFileAfterUploaded) throws Exception {
//        // The headers will be received by the server and set to the response header to the device
//        // So the request headers are not usual request headers for partial content
//
//        RequestConfig config = RequestConfig.custom().setSocketTimeout(Constants.SO_TIMEOUT_IN_SECONDS_TO_FILE_UPLOAD * 1000).setConnectTimeout(Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000).build();
//
//        HttpAsyncClientBuilder clientBuilder = HttpAsyncClientBuilder.create().setDefaultRequestConfig(config);
//
//        Set<Header> newHeaders = new HashSet<>();
//
//        if (clientSessionId != null && clientSessionId.trim().length() > 0) {
//            // the client session id is actually the download key sent from repository, and no need to generate another new one again
//            newHeaders.add(new BasicHeader(Constants.HTTP_HEADER_AUTHORIZATION_NAME, clientSessionId));
//        }
//
//        String fullPath = serviceUtilities.composeFullAddressWithLugServerId(lugServerId, "directory/supload2");
//
//        String fileExtension = FilenameUtils.getExtension(filePath);
//
//        String contentTypeString = HierarchicalModel.prepareContentTypeFromFileExtension(fileExtension);
//
//        ContentType contentType = ContentType.create(contentTypeString);
//
//        File partialFile = null;
//        CloseableHttpAsyncClient httpClient = null;
//
//        File uploadFile = new File(filePath);
//
//        long fullFileLength = uploadFile.length();
//
//        // Last-Modified (Required by iOS to support resumable download)
//        // Get Last-Modified before partialFile or the value will be the creation timestamp of the partial file.
//        long lastModifiedInMillis = uploadFile.lastModified();
//        newHeaders.add(new BasicHeader(Constants.HTTP_HEADER_NAME_FILE_LAST_MODIFIED, String.valueOf(lastModifiedInMillis)));
//
//        // Content-Length - DO NOT ADD THIS, clientBuilder will add later before sending file, no matter full content or partial content
//
//        try {
//            // prepare for partial content
//            if (startingByte > 0 && startingByte < fullFileLength) {
//                FileOutputStream fileOutputStream = null;
//                FileInputStream fileInputStream = null;
//                try {
//                    partialFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), fileExtension != null ? "." + fileExtension : "");
//
//                    fileOutputStream = new FileOutputStream(partialFile, false);
//
//                    fileInputStream = new FileInputStream(uploadFile);
//
//                    long skippedBytes = fileInputStream.skip(startingByte);
//
//                    LOGGER.debug(skippedBytes + " bytes skipped for partial uploading.");
//
//                    ByteStreams.copy(fileInputStream, fileOutputStream);
//
//                    fileOutputStream.flush();
//
//                    uploadFile = partialFile;
//
//                    // Content-Range
//                    newHeaders.add(new BasicHeader(Constants.HTTP_HEADER_NAME_FILE_CONTENT_RANGE, "bytes " + startingByte + "-" + (fullFileLength - 1) + "/" + fullFileLength));
//                } catch (Exception e) {
//                    LOGGER.error("Error on copy partial file from: '" + uploadFile.getAbsolutePath() + "' to '" + (partialFile != null ? partialFile.getAbsolutePath() : "(Unknown file)") + "'", e);
//                } finally {
//                    if (fileInputStream != null) {
//                        try {
//                            fileInputStream.close();
//                        } catch (Exception e) {
//                            // ignored
//                        }
//                    }
//
//                    if (fileOutputStream != null) {
//                        try {
//                            fileOutputStream.close();
//                        } catch (Exception e) {
//                            // ignored
//                        }
//                    }
//                }
//            }
//
//            clientBuilder.setDefaultHeaders(newHeaders);
//
//            ZeroCopyPost producer = new ZeroCopyPost(fullPath, uploadFile, contentType);
//
//            LOGGER.debug("Execute async request POST " + fullPath);
//
//            httpClient = clientBuilder.build();
//
//            httpClient.start();
//
//            if (startingByte > 0) {
//                LOGGER.info(String.format("Resume transfering file out from path: '%s'", filePath));
//            } else {
//                LOGGER.info(String.format("Start transfering file out from path: '%s'", filePath));
//            }
//
//            Future<HttpResponse> future = httpClient.execute(producer, consumer, callback);
//
//            future.get();
//        } finally {
//            if (httpClient != null) {
//                try {
//                    httpClient.close();
//                } catch (Exception e) {
//                    // ignored
//                }
//            }
//
//            if (partialFile != null) {
//                try {
//                    if (partialFile.delete()) {
//                        LOGGER.debug("Deleted partial file: " + partialFile.getAbsolutePath());
//                    }
//                } catch (Exception e) {
//                    // ignored
//                }
//            }
//
//            if (deleteFileAfterUploaded && uploadFile.exists()) {
//                try {
//                    if (uploadFile.delete()) {
//                        LOGGER.info("Deleted file: " + uploadFile.getAbsolutePath());
//                    }
//                } catch (Exception e) {
//                    // ignored
//                }
//            }
//        }
//    }

    public void doAsyncUploadFile3(String userId, String lugServerId, final String filePath, String fileTransferKey, long startingByte, HttpAsyncResponseConsumer<HttpResponse> consumer, FutureCallback<HttpResponse> callback, boolean deleteFileAfterUploaded) throws Exception {
        // The headers will be received by the server and set to the response header to the device
        // So the request headers are not usual request headers for partial content

        RequestConfig config = RequestConfig.custom().setSocketTimeout(Constants.SO_TIMEOUT_IN_SECONDS_TO_FILE_UPLOAD * 1000).setConnectTimeout(Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000).build();

        HttpAsyncClientBuilder clientBuilder = HttpAsyncClientBuilder.create().setDefaultRequestConfig(config);

        Set<Header> newHeaders = new HashSet<>();

        if (fileTransferKey != null && fileTransferKey.trim().length() > 0) {
            // use the download key sent from server, and no need to generate another new one again
            newHeaders.add(new BasicHeader(Constants.HTTP_HEADER_NAME_UPLOAD_KEY, fileTransferKey));
        }

        String fullPath = serviceUtilities.composeFullAddressWithLugServerId(lugServerId, "directory/supload3");

        String fileExtension = FilenameUtils.getExtension(filePath);

        String contentTypeString = HierarchicalModel.prepareContentTypeFromFileExtension(fileExtension);

        ContentType contentType = ContentType.create(contentTypeString);

        File partialFile = null;
        CloseableHttpAsyncClient httpClient = null;

        File uploadFile = new File(filePath);

        long fullFileLength = uploadFile.length();

        // Last-Modified (Required by iOS to support resumable download)
        // Get Last-Modified before partialFile or the value will be the creation timestamp of the partial file.
        long lastModifiedInMillis = uploadFile.lastModified();
        newHeaders.add(new BasicHeader(Constants.HTTP_HEADER_NAME_FILE_LAST_MODIFIED, String.valueOf(lastModifiedInMillis)));

        // Content-Length - DO NOT ADD THIS, clientBuilder will add later before sending file, no matter full content or partial content

        try {
            // prepare for partial content
            if (startingByte > 0 && startingByte < fullFileLength) {
                FileOutputStream fileOutputStream = null;
                FileInputStream fileInputStream = null;
                try {
                    partialFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), fileExtension != null ? "." + fileExtension : "");

                    fileOutputStream = new FileOutputStream(partialFile, false);

                    fileInputStream = new FileInputStream(uploadFile);

                    long skippedBytes = fileInputStream.skip(startingByte);

                    LOGGER.debug(skippedBytes + " bytes skipped for partial uploading.");

                    ByteStreams.copy(fileInputStream, fileOutputStream);

                    fileOutputStream.flush();

                    uploadFile = partialFile;

                    // Content-Range
                    newHeaders.add(new BasicHeader(Constants.HTTP_HEADER_NAME_FILE_CONTENT_RANGE, "bytes " + startingByte + "-" + (fullFileLength - 1) + "/" + fullFileLength));
                } catch (Exception e) {
                    LOGGER.error("Error on copy partial file from: '" + uploadFile.getAbsolutePath() + "' to '" + (partialFile != null ? partialFile.getAbsolutePath() : "(Unknown file)") + "'", e);
                } finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e) {
                            // ignored
                        }
                    }

                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (Exception e) {
                            // ignored
                        }
                    }
                }
            }

            // add session id

            String deviceSessionId = userService.validateSessionAndGetNewIfNeededForUser(userId);
//            String deviceSessionId = userDao.findSessionIdById(userId);

            newHeaders.add(new BasicHeader(Constants.HTTP_HEADER_FILELUG_SESSION_ID_NAME, deviceSessionId));

            clientBuilder.setDefaultHeaders(newHeaders);

            ZeroCopyPost producer = new ZeroCopyPost(fullPath, uploadFile, contentType);

            LOGGER.debug("Execute async request POST " + fullPath);

            httpClient = clientBuilder.build();

            httpClient.start();

            if (startingByte > 0) {
                LOGGER.info(String.format("Resume transfering file out from path: '%s'", filePath));
            } else {
                LOGGER.info(String.format("Start transfering file out from path: '%s'", filePath));
            }

            Future<HttpResponse> future = httpClient.execute(producer, consumer, callback);

            future.get();
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    // ignored
                }
            }

            if (partialFile != null) {
                try {
                    if (partialFile.delete()) {
                        LOGGER.debug("Deleted partial file: " + partialFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    // ignored
                }
            }

            if (deleteFileAfterUploaded && uploadFile.exists()) {
                try {
                    if (uploadFile.delete()) {
                        LOGGER.info("Deleted file: " + uploadFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    // ignored
                }
            }
        }
    }

    public void copyStreams(final InputStream from, final OutputStream to, final int buffersize) throws IOException {
        ByteStreams.copy(from, to);
    }

    public HttpResponse doFileDownload(String lugServerId, String directory, String filename, String uploadKey, String clientSessionId) throws Exception {
        String fullPath = serviceUtilities.composeFullAddressWithLugServerId(lugServerId, "directory/sdownload");

        return internalDoFileDownload(fullPath, lugServerId, directory, filename, uploadKey, clientSessionId);
    }

    public void doAsyncDownloadFile(String userId, String lugServerId, String directory, String filename, String fileTransferKey, ZeroCopyConsumer<File> consumer, FutureCallback<File> callback) throws Exception {
        // Encodes the character '+' in the transfer key with '%2B'
        String encodedTransferKey = StringUtils.replace(fileTransferKey, "+", "%2B");

        String fullPath = String.format("%s?t=%s", serviceUtilities.composeFullAddressWithLugServerId(lugServerId, "directory/sdownload3"), encodedTransferKey);

        RequestConfig config = RequestConfig.custom().setSocketTimeout(Constants.SO_TIMEOUT_IN_SECONDS_TO_FILE_DOWNLOAD * 1000).setConnectTimeout(Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000).build();

        HttpAsyncClientBuilder clientBuilder = HttpAsyncClientBuilder.create().setDefaultRequestConfig(config);

        // add session id

        Set<Header> newHeaders = new HashSet<>();

        String desktopSessionId = userService.validateSessionAndGetNewIfNeededForUser(userId);
//        String desktopSessionId = userDao.findSessionIdById(userId);

        newHeaders.add(new BasicHeader(Constants.HTTP_HEADER_FILELUG_SESSION_ID_NAME, desktopSessionId));

        clientBuilder.setDefaultHeaders(newHeaders);

        HttpAsyncRequestProducer producer = HttpAsyncMethods.createGet(fullPath);

        LOGGER.debug("Execute async request GET " + fullPath);

        CloseableHttpAsyncClient httpClient = clientBuilder.build();

        try {
            httpClient.start();

            String fileAbsolutePath = new File(directory, filename).getAbsolutePath();

            LOGGER.info(String.format("Start transfering file in to path: '%s'", fileAbsolutePath));

            Future<File> future = httpClient.execute(producer, consumer, callback);

            File result = future.get();

            if (result != null) {
                LOGGER.debug(String.format("File transferred in to path: '%s'", result.getAbsolutePath()));
            } else {
                LOGGER.info(String.format("File failed to transfer in to path: '%s'", fileAbsolutePath));
            }
        } finally {
            httpClient.close();
        }
    }

//    public void doAsyncDownloadFile(String lugServerId, String directory, String filename, String uploadKey, String clientSessionId, ZeroCopyConsumer<File> consumer, FutureCallback<File> callback) throws Exception {
//        String fullPath = serviceUtilities.composeFullAddressWithLugServerId(lugServerId, "directory/sdownload2");
//
//        RequestConfig config = RequestConfig.custom().setSocketTimeout(Constants.SO_TIMEOUT_IN_SECONDS_TO_FILE_DOWNLOAD * 1000).setConnectTimeout(Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000).build();
//
//        HttpAsyncClientBuilder clientBuilder = HttpAsyncClientBuilder.create().setDefaultRequestConfig(config);
//
//        Set<Header> newHeaders = new HashSet<>();
//
//        if (uploadKey != null && uploadKey.trim().length() > 0) {
//            newHeaders.add(new BasicHeader(Constants.HTTP_HEADER_AUTHORIZATION_NAME, uploadKey));
//        }
//
//        if (newHeaders.size() > 0) {
//            clientBuilder.setDefaultHeaders(newHeaders);
//        }
//
//        HttpAsyncRequestProducer producer = HttpAsyncMethods.createPost(fullPath, "", ContentType.TEXT_PLAIN);
//
//        LOGGER.debug("Execute async request POST " + fullPath);
//
//        CloseableHttpAsyncClient httpClient = clientBuilder.build();
//
//        try {
//            httpClient.start();
//
//            String fileAbsolutePath = new File(directory, filename).getAbsolutePath();
//
//            LOGGER.info(String.format("Start transfering file in to path: '%s'", fileAbsolutePath));
//
//            Future<File> future = httpClient.execute(producer, consumer, callback);
//
//            File result = future.get();
//
//            if (result != null) {
//                LOGGER.debug(String.format("File transferred in to path: '%s'", result.getAbsolutePath()));
//            } else {
//                LOGGER.info(String.format("File failed to transfer in to path: '%s'", fileAbsolutePath));
//            }
//        } finally {
//            httpClient.close();
//        }
//    }

    public HttpResponse doFileDownload2(String lugServerId, String directory, String filename, String uploadKey, String clientSessionId) throws Exception {
        String fullPath = serviceUtilities.composeFullAddressWithLugServerId(lugServerId, "directory/sdownload2");

        return internalDoFileDownload(fullPath, lugServerId, directory, filename, uploadKey, clientSessionId);
    }

    private HttpResponse internalDoFileDownload(String fullPath, String lugServerId, String directory, String filename, String uploadKey, String clientSessionId) throws Exception {
        HttpResponse response;

        RequestConfig config = RequestConfig.custom().setSocketTimeout(Constants.SO_TIMEOUT_IN_SECONDS_TO_FILE_DOWNLOAD * 1000).setConnectTimeout(Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000).build();

        HttpClientBuilder clientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(config);

        Set<Header> newHeaders = new HashSet<>();

        if (uploadKey != null && uploadKey.trim().length() > 0) {
            newHeaders.add(new BasicHeader(Constants.HTTP_HEADER_AUTHORIZATION_NAME, uploadKey));
        }

        if (newHeaders.size() > 0) {
            clientBuilder.setDefaultHeaders(newHeaders);
        }

        HttpClient httpClient = clientBuilder.build();

        HttpPost httpPost = new HttpPost(fullPath);

        LOGGER.debug("Execute request POST " + httpPost.getURI().toString());

        response = httpClient.execute(httpPost);

        return response;
    }

    @Override
    public void copy(File source, File dest) throws Exception {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

        try {
            sourceChannel = new FileInputStream(source).getChannel();

            destChannel = new FileOutputStream(dest).getChannel();

            long sourceFileSize = sourceChannel.size();

            MappedByteBuffer byteBuffer = sourceChannel.map(FileChannel.MapMode.READ_ONLY, 0, sourceFileSize);

            destChannel.write(byteBuffer);
        } finally {
            if (sourceChannel != null) {
                try {
                    sourceChannel.close();
                } catch (Exception e) {
                    // ignored
                }
            }

            if (destChannel != null) {
                try {
                    destChannel.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
    }
}
