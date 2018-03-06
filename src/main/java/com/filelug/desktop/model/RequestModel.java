package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code>RequestModel</code> represents the basic request information for websocket.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class RequestModel {

    @JsonProperty("sid")
    private Integer sid;

    @JsonProperty("operatorId")
    private String operatorId;

    @JsonProperty("locale")
    private String locale;

    public RequestModel() {
    }

    public RequestModel(Integer sid, String operatorId, String locale) {
        this.sid = sid;
        this.operatorId = operatorId;
        this.locale = locale;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
