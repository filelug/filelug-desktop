package com.filelug.desktop.exception;

/**
 * <code>BookmarkNotFoundException</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class DuplicatedDirectoryException extends RuntimeException {
    private static final long serialVersionUID = -8763420370140977604L;

    public DuplicatedDirectoryException() {
        super();
    }

    public DuplicatedDirectoryException(Throwable cause) {
        super(cause);
    }

    public DuplicatedDirectoryException(String message) {
        super(message);
    }

    public DuplicatedDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
