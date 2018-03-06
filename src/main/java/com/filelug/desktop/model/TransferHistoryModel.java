package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code>TransferHistoryModel</code> for the information of a downloaded/uploaded file
 *
 * @author masonhsieh
 * @version 1.0
 */
public class TransferHistoryModel {

    @JsonProperty("fileSize")
    private long fileSize;

    @JsonProperty("endTimestamp")
    private long endTimestamp;

    @JsonProperty("filename")
    private String filename;

    public TransferHistoryModel() {
    }

    public TransferHistoryModel(long fileSize, long endTimestamp, String filename) {
        this.fileSize = fileSize;
        this.endTimestamp = endTimestamp;
        this.filename = filename;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
