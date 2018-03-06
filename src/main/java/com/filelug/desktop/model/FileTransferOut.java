package com.filelug.desktop.model;

/**
 * <code>FileTransferOut</code> is the data model for table: FILE_DOWNLOADED
 *
 * @author masonhsieh
 * @version 1.0
 */
public class FileTransferOut {

    private String transferKey;

    private String userId;

    private String filePath;

    private Long fileSize;

    // in milli-sec
    private Long fileLastModifiedDate;

    private Long startTimestamp;

    private Long endTimestamp;

    private String status;

    public FileTransferOut() {
    }

    public FileTransferOut(String transferKey, String userId, String filePath, Long fileSize, Long fileLastModifiedDate, Long startTimestamp, Long endTimestamp, String status) {
        this.transferKey = transferKey;
        this.userId = userId;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.fileLastModifiedDate = fileLastModifiedDate;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.status = status;
    }

    public String getTransferKey() {
        return transferKey;
    }

    public void setTransferKey(String transferKey) {
        this.transferKey = transferKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getFileLastModifiedDate() {
        return fileLastModifiedDate;
    }

    public void setFileLastModifiedDate(Long fileLastModifiedDate) {
        this.fileLastModifiedDate = fileLastModifiedDate;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FileTransferOut{");
        sb.append("transferKey='").append(transferKey).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", filePath='").append(filePath).append('\'');
        sb.append(", fileSize=").append(fileSize);
        sb.append(", fileLastModifiedDate=").append(fileLastModifiedDate);
        sb.append(", startTimestamp=").append(startTimestamp);
        sb.append(", endTimestamp=").append(endTimestamp);
        sb.append(", status='").append(status).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
