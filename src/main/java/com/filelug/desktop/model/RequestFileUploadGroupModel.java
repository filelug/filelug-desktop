package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code>RequestFileUploadGroupModel</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class RequestFileUploadGroupModel extends RequestModel {

    @JsonProperty("file-upload-group")
    private FileUploadGroup fileUploadGroup;

    public RequestFileUploadGroupModel() {
        super();
    }

    public RequestFileUploadGroupModel(Integer sid, String operatorId, String locale, FileUploadGroup fileUploadGroup) {
        super(sid, operatorId, locale);

        this.fileUploadGroup = fileUploadGroup;
    }

    public FileUploadGroup getFileUploadGroup() {
        return fileUploadGroup;
    }

    public void setFileUploadGroup(FileUploadGroup fileUploadGroup) {
        this.fileUploadGroup = fileUploadGroup;
    }


}
