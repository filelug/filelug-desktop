package com.filelug.desktop.exception;

/**
 * <code>NotDirectoryException</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class NotDirectoryException extends RuntimeException {
    private static final long serialVersionUID = -5953807796884333300L;

    public NotDirectoryException() {
        super();
    }

    public NotDirectoryException(Throwable cause) {
        super(cause);
    }

    public NotDirectoryException(String message) {
        super(message);
    }

    public NotDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
