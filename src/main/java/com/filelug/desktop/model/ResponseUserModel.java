package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code>ResponseUserModel</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ResponseUserModel extends ResponseModel {

    @JsonProperty("approved-user")
    private ApprovedUserModel approvedUserModel;

    public ResponseUserModel() {
        super();
    }

    public ResponseUserModel(Integer sid, Integer status, String error, String operatorId, Long timestamp, ApprovedUserModel approvedUserModel) {
        super(sid, status, error, operatorId, timestamp);
        this.approvedUserModel = approvedUserModel;
    }

    public ApprovedUserModel getApprovedUserModel() {
        return approvedUserModel;
    }

    public void setApprovedUserModel(ApprovedUserModel approvedUserModel) {
        this.approvedUserModel = approvedUserModel;
    }

//    @JsonProperty("user")
//    private User user;
//
//    public ResponseUserModel() {
//        super();
//    }
//
//    public ResponseUserModel(Integer sid, Integer status, String error, String operatorId, Long timestamp, User user) {
//        super(sid, status, error, operatorId, timestamp);
//        this.user = user;
//    }
//
//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
}
