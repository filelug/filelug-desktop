package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code>RequestListDirectoryChildrenModel</code> represents the request information of list directory children for websocket.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class RequestListDirectoryChildrenModel extends RequestModel {

    @JsonProperty("path")
    private String path;

    @JsonProperty("showHidden")
    private Boolean showHidden;

    public RequestListDirectoryChildrenModel() {
        super();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getShowHidden() {
        return showHidden;
    }

    public void setShowHidden(Boolean showHidden) {
        this.showHidden = showHidden;
    }
}
