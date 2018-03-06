package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>UserComputerConnectionStatus</code> represents connection status of a user computer.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class UserComputerConnectionStatus implements Cloneable {

    @JsonProperty("user-id")
    private String userId;

    @JsonProperty("connected")
    private Boolean socketConnected;

    @JsonProperty("need-reconnect")
    private Boolean needReconnect;

    @JsonIgnore
    private Long computerId;

    public UserComputerConnectionStatus() {
    }

    public UserComputerConnectionStatus(String userId, Boolean needReconnect, Boolean socketConnected) {
        this.userId = userId;
        this.needReconnect = needReconnect;
        this.socketConnected = socketConnected;
    }

    public Boolean getNeedReconnect() {
        return needReconnect;
    }

    public void setNeedReconnect(Boolean needReconnect) {
        this.needReconnect = needReconnect;
    }

    public Boolean getSocketConnected() {
        return socketConnected;
    }

    public void setSocketConnected(Boolean socketConnected) {
        this.socketConnected = socketConnected;
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

    @Override
    public UserComputerConnectionStatus clone() throws CloneNotSupportedException {
        UserComputerConnectionStatus newInstance = new UserComputerConnectionStatus();

        newInstance.setUserId(userId);
        newInstance.setSocketConnected(socketConnected);
        newInstance.setNeedReconnect(needReconnect);
        newInstance.setComputerId(computerId);

        return newInstance;
    }

//    public static List<UserComputerConnectionStatus> copyList(List<UserComputerConnectionStatus> userComputerConnectionStatuses) {
//        if (userComputerConnectionStatuses == null) {
//            return null;
//        } else {
//            List<UserComputerConnectionStatus> cloned = new ArrayList<>();
//
//            for (UserComputerConnectionStatus userComputerConnectionStatus : userComputerConnectionStatuses) {
//                try {
//                    cloned.add(userComputerConnectionStatus.clone());
//                } catch (Exception e) {
//                    // ignored
//                }
//            }
//
//            return cloned;
//        }
//    }

    public static UserComputerConnectionStatus findUserComputerConnectionStatusByUserId(List<UserComputerConnectionStatus> computerConnectionStatuses, String userId) {
        if (computerConnectionStatuses == null || computerConnectionStatuses.size() < 1 || userId == null || userId.trim().length() < 1) {
            return null;
        } else {
            for (UserComputerConnectionStatus connectionStatus : computerConnectionStatuses) {
                if (userId.equals(connectionStatus.getUserId())) {
                    return connectionStatus;
                }
            }

            return null;
        }
    }

//    public static void removeFromList(List<UserComputerConnectionStatus> userComputerConnectionStatuses, String userId) {
//        if (userComputerConnectionStatuses != null && userComputerConnectionStatuses.size() > 0) {
//
//
//            for (Iterator<UserComputerConnectionStatus> iterator = userComputerConnectionStatuses.iterator(); iterator.hasNext(); ) {
//                UserComputerConnectionStatus userComputerConnectionStatus = iterator.next();
//
//                String foundUserId = userComputerConnectionStatus.getUserId();
//                if (foundUserId != null && foundUserId.equals(userId)) {
//                    iterator.remove();
//                }
//            }
//
//
//            for (UserComputerConnectionStatus userComputerConnectionStatus : userComputerConnectionStatuses) {
//                if ()
//            }
//        }
//    }
}
