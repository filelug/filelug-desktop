package com.filelug.desktop.model;

/**
 * <code>UserConnectionModel</code> is the model to show the connection status of a user.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class UserConnectionModel implements Cloneable {

    private String userId;

    private String nickname;

    private AccountState.State connectionState = AccountState.State.UNKNOWN;

    public UserConnectionModel() {
    }

    public UserConnectionModel(String userId, String nickname, AccountState.State connectionState) {
        this.userId = userId;
        this.nickname = nickname;
        this.connectionState = connectionState;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public AccountState.State getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(AccountState.State connectionState) {
        this.connectionState = connectionState;
    }

    public UserConnectionModel copy() {
        UserConnectionModel copy;

        try {
            copy = (UserConnectionModel) this.clone();
        } catch (Exception e) {
            copy = new UserConnectionModel(userId, nickname, connectionState);
        }

        return copy;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserConnectionModel{");
        sb.append("userId='").append(userId).append('\'');
        sb.append(", nickname='").append(nickname).append('\'');
        sb.append(", connectionState=").append(connectionState);
        sb.append('}');
        return sb.toString();
    }
}
