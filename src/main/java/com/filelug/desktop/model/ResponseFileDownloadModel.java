package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code>ResponseFileDownloadModel</code> represents the response information of Sid.DOWNLOAD_FILE.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ResponseFileDownloadModel extends ResponseModel {

    // unique key for all the download requests in repository
    @JsonProperty("transferKey")
    private String transferKey;

    @JsonProperty("path")
    private String path;

    @JsonProperty("fileSize")
    private Long fileSize;

    public ResponseFileDownloadModel() {
        super();
    }

    public ResponseFileDownloadModel(Integer sid, Integer status, String error, String operatorId, Long timestamp, String transferKey, String path, Long fileSize) {
        super(sid, status, error, operatorId, timestamp);
        this.transferKey = transferKey;
        this.path = path;
        this.fileSize = fileSize;
    }

    public String getTransferKey() {
        return transferKey;
    }

    public void setTransferKey(String transferKey) {
        this.transferKey = transferKey;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
