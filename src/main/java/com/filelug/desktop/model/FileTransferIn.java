package com.filelug.desktop.model;

/**
 * <code>FileTransferIn</code> is the data model for table: FILE_UPLOADED
 *
 * @author masonhsieh
 * @version 1.0
 */
public class FileTransferIn {

    private String transferKey;

    private String userId;

    private String filename;

    private String directory;

    private Long fileSize;

    private Long startTimestamp;

    private Long endTimestamp;

    private String status;

    public FileTransferIn() {
    }

    public FileTransferIn(String transferKey, String userId, String filename, String directory, Long fileSize, Long startTimestamp, Long endTimestamp, String status) {
        this.transferKey = transferKey;
        this.userId = userId;
        this.filename = filename;
        this.directory = directory;
        this.fileSize = fileSize;
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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
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
        final StringBuilder sb = new StringBuilder("FileTransferIn{");
        sb.append("transferKey='").append(transferKey).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", filename='").append(filename).append('\'');
        sb.append(", directory='").append(directory).append('\'');
        sb.append(", fileSize=").append(fileSize);
        sb.append(", startTimestamp=").append(startTimestamp);
        sb.append(", endTimestamp=").append(endTimestamp);
        sb.append(", status='").append(status).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
