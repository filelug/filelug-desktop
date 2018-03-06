package com.filelug.desktop.model;

import java.util.Observable;

/**
 * <code>ConnectResponseState</code> used as a observable object
 * to notify the message received from ConnectSocket.onConnectWebSocket
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ConnectResponseState extends Observable {

    private ConnectModel connectModel;

    private int statusCode;

    private String responseMessage;

    /* user object from response message */
    private User responseUser;

    private Object argumentWhenNotify;

    public ConnectResponseState(ConnectModel connectModel) {
        this.connectModel = connectModel;

        this.statusCode = -1;
        this.responseMessage = "";
    }

    public ConnectResponseState(ConnectModel connectModel, Object argumentWhenNotify) {
        this(connectModel);

        this.argumentWhenNotify = argumentWhenNotify;
    }

    public void setState(int statusCode, String responseMessage, User responseUser) {
        this.statusCode = statusCode;
        this.responseMessage = responseMessage;
        this.responseUser = responseUser;

        setChanged();

        if (this.argumentWhenNotify != null) {
            notifyObservers(argumentWhenNotify);
        } else {
            notifyObservers();
        }
    }

    public ConnectModel getConnectModel() {
        return connectModel;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public User getResponseUser() {
        return responseUser;
    }

    public Object getArgumentWhenNotify() {
        return argumentWhenNotify;
    }
}
