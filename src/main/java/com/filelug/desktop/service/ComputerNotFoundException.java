package com.filelug.desktop.service;

/**
 * <code>ComputerNotFoundException</code> throws when the computer not found in the server.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ComputerNotFoundException extends Exception {

    public ComputerNotFoundException() {
    }

    public ComputerNotFoundException(Throwable cause) {
        super(cause);
    }

    public ComputerNotFoundException(String userId) {
        super(userId);
    }

    public ComputerNotFoundException(String userId, Throwable cause) {
        super(userId, cause);
    }

    public ComputerNotFoundException(String userId, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(userId, cause, enableSuppression, writableStackTrace);
    }
}
