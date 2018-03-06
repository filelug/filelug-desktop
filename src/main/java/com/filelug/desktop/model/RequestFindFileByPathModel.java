package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code>RequestFindFileByPathModel</code> represents the request information of find file/directory by path for websocket.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class RequestFindFileByPathModel extends RequestModel {

    @JsonProperty("path")
    private String path;

    @JsonProperty("calculateSize")
    private Boolean calculateSize;

    public RequestFindFileByPathModel() {
        super();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getCalculateSize() {
        return calculateSize;
    }

    public void setCalculateSize(Boolean calculateSize) {
        this.calculateSize = calculateSize;
    }
}
