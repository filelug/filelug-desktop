package com.filelug.desktop.model;

/**
 * <code>UserConnectionEvent</code> used to description the notification of UserConnectionModel changed.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class UserConnectionEvent {

    public enum ChangeType {
        USER_CONNECTION_ADDED, USER_CONNECTION_UPDATED
//        USER_CONNECTION_ADDED, USER_CONNECTION_REMOVED, USER_CONNECTION_UPDATED
    }

    // Apply for ChangeType of USER_CONNECTION_UPDATED and USER_CONNECTION_REMOVED
    private UserConnectionModel oldUserConnectionModel;

    // Apply only for ChangeType of USER_CONNECTION_ADDED
    private UserConnectionModel newUserConnectionModel;

    private ChangeType changeType;

    public UserConnectionEvent(ChangeType changeType, UserConnectionModel oldUserConnectionModel, UserConnectionModel newUserConnectionModel) {
        this.changeType = changeType;
        this.oldUserConnectionModel = oldUserConnectionModel;
        this.newUserConnectionModel = newUserConnectionModel;
    }

    public UserConnectionModel getOldUserConnectionModel() {
        return oldUserConnectionModel;
    }

    public UserConnectionModel getNewUserConnectionModel() {
        return newUserConnectionModel;
    }

    public ChangeType getChangeType() {
        return changeType;
    }
}
