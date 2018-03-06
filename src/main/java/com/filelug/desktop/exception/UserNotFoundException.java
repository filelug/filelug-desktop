package com.filelug.desktop.exception;

/**
 * <code>UserNotFoundException</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -4288402924089799236L;

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
