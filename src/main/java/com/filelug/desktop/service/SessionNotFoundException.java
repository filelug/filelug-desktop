package com.filelug.desktop.service;

/**
 * <code>SessionNotFoundException</code> throws when the client session not found on teh server.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class SessionNotFoundException extends Exception {

    private static final long serialVersionUID = 6785452929669583800L;

    public SessionNotFoundException() {
    }

    public SessionNotFoundException(Throwable cause) {
        super(cause);
    }

    public SessionNotFoundException(String userId) {
        super(userId);
    }

    public SessionNotFoundException(String userId, Throwable cause) {
        super(userId, cause);
    }

    public SessionNotFoundException(String userId, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(userId, cause, enableSuppression, writableStackTrace);
    }
}
