package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code>RequestFileDownloadModel</code> represents the request from repository to upload file to repository.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class RequestFileDownloadModel extends RequestModel {

    @JsonProperty("path")
    private String path;

    // keep the value of download key
    @JsonProperty("clientSessionId")
    private String clientSessionId;

    // for header RANGE
    @JsonProperty("range")
    private String range;

    @JsonProperty("availableBytes")
    private Long availableBytes;

    @JsonProperty("downloadSizeLimitInBytes")
    private Long downloadSizeLimitInBytes;

    public RequestFileDownloadModel() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getAvailableBytes() {
        return availableBytes;
    }

    public void setAvailableBytes(Long availableBytes) {
        this.availableBytes = availableBytes;
    }

    public String getClientSessionId() {
        return clientSessionId;
    }

    public void setClientSessionId(String clientSessionId) {
        this.clientSessionId = clientSessionId;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public Long getDownloadSizeLimitInBytes() {
        return downloadSizeLimitInBytes;
    }

    public void setDownloadSizeLimitInBytes(Long downloadSizeLimitInBytes) {
        this.downloadSizeLimitInBytes = downloadSizeLimitInBytes;
    }
}
