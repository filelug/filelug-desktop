package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ResponseFileRenameModel extends ResponseModel {

    @JsonProperty("result")
    private FileRenameModel result;

    public ResponseFileRenameModel() {
        super();
    }

    public ResponseFileRenameModel(Integer sid, Integer status, String error, String operatorId, Long timestamp, FileRenameModel result) {
        super(sid, status, error, operatorId, timestamp);
        this.result = result;
    }

    public FileRenameModel getResult() {
        return result;
    }

    public void setResult(FileRenameModel result) {
        this.result = result;
    }
}
