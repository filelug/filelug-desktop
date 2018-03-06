package com.filelug.desktop;

/**
 * <code>NeedLoginWithQRCodeException</code> throws when user needs to restart the application so it prompts the QR code for user to login again.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class NeedLoginWithQRCodeException extends Exception {
    private static final long serialVersionUID = 5777364334564222736L;

    public NeedLoginWithQRCodeException() {
    }

    public NeedLoginWithQRCodeException(String message) {
        super(message);
    }

    public NeedLoginWithQRCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NeedLoginWithQRCodeException(Throwable cause) {
        super(cause);
    }

    public NeedLoginWithQRCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
