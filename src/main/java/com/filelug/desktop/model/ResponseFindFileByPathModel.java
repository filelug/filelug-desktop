package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ResponseFindFileByPathModel extends ResponseModel {

    @JsonProperty("result")
    private HierarchicalModel result;

    public ResponseFindFileByPathModel() {
        super();
    }

    public ResponseFindFileByPathModel(Integer sid, Integer status, String error, String operatorId, Long timestamp, HierarchicalModel result) {
        super(sid, status, error, operatorId, timestamp);
        this.result = result;
    }

    public HierarchicalModel getResult() {
        return result;
    }

    public void setResult(HierarchicalModel result) {
        this.result = result;
    }
}
