package com.filelug.desktop;

/**
 * <code>NotRegisteredException</code> thrown when user not registered or registered but not verified yet.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class NotRegisteredException extends Exception {

    private static final long serialVersionUID = -2101550097843109094L;

    public NotRegisteredException() {
        super();
    }

    public NotRegisteredException(String message) {
        super(message);
    }

    public NotRegisteredException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotRegisteredException(Throwable cause) {
        super(cause);
    }

    protected NotRegisteredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
