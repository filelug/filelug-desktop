package com.filelug.desktop.model;

/**
 * <code>UserChangedEvent</code> used to description the notification of the adding, changing and removing of a User.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class UserChangedEvent {

    public enum ChangeType {
        USER_ADDED, USER_REMOVED, USER_UPDATED
    }

    // Apply for ChangeType of USER_UPDATED and USER_REMOVED
    private User oldUser;

    // Apply only for ChangeType of USER_ADDED
    private User newUser;

    private ChangeType changeType;

    public UserChangedEvent(ChangeType changeType, User oldUser, User newUser) {
        this.changeType = changeType;
        this.oldUser = oldUser;
        this.newUser = newUser;
    }

    public User getOldUser() {
        return oldUser;
    }

    public User getNewUser() {
        return newUser;
    }

    public ChangeType getChangeType() {
        return changeType;
    }
}
