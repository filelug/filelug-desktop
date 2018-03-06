package com.filelug.desktop.service;

import java.util.Observable;

/**
 * <code>ResetApplicationNotificationService</code> notifies the Observers that the application should be reset.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ResetApplicationNotificationService extends Observable {

    public enum REASON {
        SESSION_OR_USER_NOT_FOUND, COMPUTER_NOT_FOUND, REMOVE_ALL_USERS, REFRESH_QR_CODE
    }

    private static ResetApplicationNotificationService theInstance;

    public static ResetApplicationNotificationService getInstance() {
        if (theInstance == null) {
            theInstance = new ResetApplicationNotificationService();
        }

        return theInstance;
    }

    private ResetApplicationNotificationService() {
    }

    public void applicationShouldReset(REASON reason) {
        setChanged();

        if (reason != null) {
            notifyObservers(reason);
        } else {
            notifyObservers();
        }
    }

    public String representationStringFromReason(REASON reason) {
        String representationString;

        if (reason != null) {
            if (reason == REASON.SESSION_OR_USER_NOT_FOUND) {
                representationString = "User or session not found.";
            } else if (reason == REASON.COMPUTER_NOT_FOUND) {
                representationString = "The computer has been deleted.";
            } else if (reason == REASON.REMOVE_ALL_USERS) {
                representationString = "User requested to remove administrator account.";
            } else if (reason == REASON.REFRESH_QR_CODE) {
                representationString = "User requested to refresh QR code.";
            } else {
                representationString = "Unknown reason.";
            }
        } else {
            representationString = null;
        }

        return representationString;
    }

//    public void applicationShouldReset(REASON reason) {
//        setChanged();
//
//        if (reason != null) {
//            String message;
//
//            if (reason == REASON.SESSION_OR_USER_NOT_FOUND) {
//                message = "User or session not found.";
//            } else if (reason == REASON.COMPUTER_NOT_FOUND) {
//                message = "The computer has been deleted.";
//            } else {
//                message = "Unknown reason.";
//            }
//
//            notifyObservers(message);
//        } else {
//            notifyObservers();
//        }
//    }
}
