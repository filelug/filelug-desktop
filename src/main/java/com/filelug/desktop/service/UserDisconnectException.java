package com.filelug.desktop.service;

/**
 * <code>UserDisconnectException</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class UserDisconnectException extends Exception {
    private static final long serialVersionUID = -516843572390079799L;

    public UserDisconnectException() {
    }

    public UserDisconnectException(Throwable cause) {
        super(cause);
    }

    public UserDisconnectException(String message) {
        super(message);
    }

    public UserDisconnectException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDisconnectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
