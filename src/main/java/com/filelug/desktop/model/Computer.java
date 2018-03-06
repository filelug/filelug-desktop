package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code>Computer</code> represents the computer data.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class Computer implements Cloneable {

    @JsonProperty("computer-id")
    private Long computerId;

    @JsonProperty("computer-group")
    private String groupName;

    @JsonProperty("computer-name")
    private String computerName;

    @JsonProperty("recoveryKey")
    private String recoveryKey;

    @JsonProperty("admin")
    private String userId;


    public Computer() {
    }

    public Long getComputerId() {
        return computerId;
    }

    public void setComputerId(Long computerId) {
        this.computerId = computerId;
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

    public String getRecoveryKey() {
        return recoveryKey;
    }

    public void setRecoveryKey(String recoveryKey) {
        this.recoveryKey = recoveryKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Computer{");
        sb.append("computerId='").append(computerId).append('\'');
        sb.append(", groupName='").append(groupName).append('\'');
        sb.append(", computerName='").append(computerName).append('\'');
        sb.append(", recoveryKey='").append(recoveryKey).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
