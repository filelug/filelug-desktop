package com.filelug.desktop.service;

import com.filelug.desktop.model.User;
import com.filelug.desktop.model.UserChangedEvent;
import com.filelug.desktop.model.UserConnectionEvent;
import com.filelug.desktop.model.UserConnectionModel;

import java.util.*;

/**
 * <code>UserChangedService</code> notifies the adding, changing and removing of a <code>User</code>.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class UserChangedService extends Observable {

    private static UserChangedService theInstance;

    public static UserChangedService getInstance() {
        if (theInstance == null) {
            theInstance = new UserChangedService();
        }

        return theInstance;
    }

    private UserChangedService() {
    }

    public void userUpdated(User oldUser, User newUser) {
        if (oldUser != null && newUser != null) {
            User oldCopy = oldUser.copy();

            User newCopy = newUser.copy();

            setChanged();
            notifyObservers(new UserChangedEvent(UserChangedEvent.ChangeType.USER_UPDATED, oldCopy, newCopy));
        }
    }

    public void userCreated(User newUser) {
        if (newUser != null) {
            User copy = newUser.copy();

            setChanged();
            notifyObservers(new UserChangedEvent(UserChangedEvent.ChangeType.USER_ADDED, null, copy));
        }
    }

    public void userDeleted(User oldUser) {
        if (oldUser != null) {
            User copy = oldUser.copy();

            setChanged();
            notifyObservers(new UserChangedEvent(UserChangedEvent.ChangeType.USER_REMOVED, copy, null));
        }
    }
}
