package com.filelug.desktop.service;

import com.filelug.desktop.model.User;
import com.filelug.desktop.model.UserChangedEvent;

import java.awt.event.ActionEvent;
import java.util.Observable;

/**
 * <code>PopupMenuItemClickNotificationService</code> notifies the menu items of the JPopupMenu clicked.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class PopupMenuItemClickNotificationService extends Observable {

    private static PopupMenuItemClickNotificationService theInstance;

    public static PopupMenuItemClickNotificationService getInstance() {
        if (theInstance == null) {
            theInstance = new PopupMenuItemClickNotificationService();
        }

        return theInstance;
    }

    private PopupMenuItemClickNotificationService() {
    }

    public void menuItemClicked(ActionEvent event) {
        ActionEvent copy = new ActionEvent(event.getSource(), event.getID(), event.getActionCommand(), event.getWhen(), event.getModifiers());

        setChanged();
        notifyObservers(copy);
    }
}
