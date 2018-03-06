package com.filelug.desktop.exception;

/**
 * <code>DirectoryNotFoundException</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class DirectoryNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 6431561795126783094L;

    public DirectoryNotFoundException() {
        super();
    }

    public DirectoryNotFoundException(Throwable cause) {
        super(cause);
    }

    public DirectoryNotFoundException(String message) {
        super(message);
    }

    public DirectoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
