package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code>UserComputer</code> represents user computer table data.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class UserComputer implements Cloneable {

    @JsonProperty("user-computer-id")
    private String userComputerId;

    @JsonProperty("user-id")
    private String userId;

    @JsonProperty("computer-id")
    private Long computerId;

    @JsonProperty("computer-admin-id")
    private String computerAdminId;

    @JsonProperty("computer-group")
    private String groupName;

    @JsonProperty("computer-name")
    private String computerName;

    @JsonProperty("lug-server-id")
    private String lugServerId;


    public UserComputer() {
    }

    public String getUserComputerId() {
        return userComputerId;
    }

    public void setUserComputerId(String userComputerId) {
        this.userComputerId = userComputerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getComputerId() {
        return computerId;
    }

    public void setComputerId(Long computerId) {
        this.computerId = computerId;
    }

    public String getComputerAdminId() {
        return computerAdminId;
    }

    public void setComputerAdminId(String computerAdminId) {
        this.computerAdminId = computerAdminId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    public String getLugServerId() {
        return lugServerId;
    }

    public void setLugServerId(String lugServerId) {
        this.lugServerId = lugServerId;
    }


}
