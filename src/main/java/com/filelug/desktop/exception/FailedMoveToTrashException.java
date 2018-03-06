package com.filelug.desktop.exception;

/**
 * <code>FailedMoveToTrashException</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class FailedMoveToTrashException extends RuntimeException {
    private static final long serialVersionUID = -8284913504214949507L;

    public FailedMoveToTrashException() {
        super();
    }

    public FailedMoveToTrashException(Throwable cause) {
        super(cause);
    }

    public FailedMoveToTrashException(String message) {
        super(message);
    }

    public FailedMoveToTrashException(String message, Throwable cause) {
        super(message, cause);
    }
}
