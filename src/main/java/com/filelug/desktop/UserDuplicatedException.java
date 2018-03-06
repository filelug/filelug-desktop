package com.filelug.desktop;

/**
 * <code>UserDuplicatedException</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class UserDuplicatedException extends Exception {

    public UserDuplicatedException() {
        super();
    }

    public UserDuplicatedException(String message) {
        super(message);
    }

    public UserDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserDuplicatedException(Throwable cause) {
        super(cause);
    }

    protected UserDuplicatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
