package com.filelug.desktop.exception;

/**
 * <code>NullOrEmptyParameterException</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class NullOrEmptyParameterException extends RuntimeException {
    private static final long serialVersionUID = 5130942095485324660L;

    public NullOrEmptyParameterException() {
        super();
    }

    public NullOrEmptyParameterException(Throwable cause) {
        super(cause);
    }

    public NullOrEmptyParameterException(String message) {
        super(message);
    }

    public NullOrEmptyParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
