package com.filelug.desktop.service;


import com.filelug.desktop.model.HierarchicalModel;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.methods.ZeroCopyConsumer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <code>DirectoryService</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public interface DirectoryService {

//    List<HierarchicalModel> list(String path) throws Exception;

    HierarchicalModel createDirectory(HierarchicalModel directoryModel) throws Exception;

    HierarchicalModel updateDirectory(HierarchicalModel directoryModel) throws Exception;

    HierarchicalModel findByPath(String path, Boolean includingSize) throws Exception;

    HierarchicalModel moveDirectory(String sourcePath, String targetPath, boolean showSizeInReturn) throws Exception;

    HierarchicalModel copyDirectory(String sourcePath, String targetPath, boolean showSizeInReturn) throws Exception;

    HierarchicalModel delete(String path, Boolean recycle, boolean showSizeInReturn) throws Exception;

//    Response downloadFile(String path, HttpHeaders requestHeaders) throws Exception;
//
//    void doAsyncUploadFile(String lugServerId, final String filePath, String clientSessionId, long startingByte, HttpAsyncResponseConsumer<HttpResponse> consumer, FutureCallback<HttpResponse> callback, boolean deleteFileAfterUploaded) throws Exception;
//
//    void doAsyncUploadFile2(String lugServerId, final String filePath, String clientSessionId, long startingByte, HttpAsyncResponseConsumer<HttpResponse> consumer, FutureCallback<HttpResponse> callback, boolean deleteFileAfterUploaded) throws Exception;

    void doAsyncUploadFile3(String userId, String lugServerId, final String filePath, String fileTransferKey, long startingByte, HttpAsyncResponseConsumer<HttpResponse> consumer, FutureCallback<HttpResponse> callback, boolean deleteFileAfterUploaded) throws Exception;

    void copyStreams(final InputStream from, final OutputStream to, final int buffersize) throws IOException;

    HttpResponse doFileDownload(String lugServerId, String directory, String filename, String uploadKey, String clientSessionId) throws Exception;

    void doAsyncDownloadFile(String userId, String lugServerId, String directory, String filename, String fileTransferKey, ZeroCopyConsumer<File> consumer, FutureCallback<File> callback) throws Exception;

    HttpResponse doFileDownload2(String lugServerId, String directory, String filename, String uploadKey, String clientSessionId) throws Exception;

    void copy(File source, File dest) throws Exception;
}
