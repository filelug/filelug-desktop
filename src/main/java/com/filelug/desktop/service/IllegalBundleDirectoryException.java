package com.filelug.desktop.service;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class IllegalBundleDirectoryException extends RuntimeException {

    private static final long serialVersionUID = 2240261216658575340L;

    public IllegalBundleDirectoryException() {
    }

    public IllegalBundleDirectoryException(Throwable cause) {
        super(cause);
    }

    public IllegalBundleDirectoryException(String message) {
        super(message);
    }

    public IllegalBundleDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalBundleDirectoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
