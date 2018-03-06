package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code>User</code> represent the user.
 *
 * @author masonhsieh
 * @version 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Cloneable {

    @JsonProperty("account")
    private String account;

    @JsonIgnore
    private String countryId;

    @JsonIgnore
    private String phoneNumber;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("showHidden")
    private Boolean showHidden;

    @JsonIgnore
    private String sessionId;

    @JsonIgnore
    private String lugServerId;

    // The timestamp of latest ConnectSocket connected to server
    @JsonIgnore
    private Long lastConnectTime;

    // The timestamp of latest client session used to invoke a service of server
    @JsonIgnore
    private Long lastSessionTime;

    @JsonIgnore
    private Boolean admin;

    @JsonIgnore
    private AccountState.State state;

    @JsonIgnore
    private Boolean approved;

    @JsonIgnore
    private Boolean allowAlias;

    public User() {
    }

    public User(String account, String countryId, String phoneNumber, String sessionId, String lugServerId, String nickname, Boolean showHidden, Long lastConnectTime, Boolean admin, AccountState.State state, Boolean approved, Boolean allowAlias) {
        this.account = account;
        this.countryId = countryId;
        this.phoneNumber = phoneNumber;
        this.sessionId = sessionId;
        this.lugServerId = lugServerId;
        this.nickname = nickname;
        this.showHidden = showHidden;
        this.lastConnectTime = lastConnectTime;
        this.admin = admin;
        this.state = state;
        this.approved = approved;
        this.allowAlias = allowAlias;
    }

    @Override
    public User clone() throws CloneNotSupportedException {
        // shallow clone, not a deep one
        return (User) super.clone();
    }

    public User copy() {
        return new User(this.account, countryId, phoneNumber, this.sessionId, this.lugServerId, this.nickname, this.showHidden, this.lastConnectTime, this.admin, this.state, this.approved, this.allowAlias);
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getLugServerId() {
        return lugServerId;
    }

    public void setLugServerId(String lugServerId) {
        this.lugServerId = lugServerId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getShowHidden() {
        return showHidden;
    }

    public void setShowHidden(Boolean showHidden) {
        this.showHidden = showHidden;
    }

    public Long getLastConnectTime() {
        return lastConnectTime;
    }

    public void setLastConnectTime(Long lastConnectTime) {
        this.lastConnectTime = lastConnectTime;
    }

    public Long getLastSessionTime() {
        return lastSessionTime;
    }

    public void setLastSessionTime(Long lastSessionTime) {
        this.lastSessionTime = lastSessionTime;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public AccountState.State getState() {
        return state;
    }

    public void setState(AccountState.State state) {
        this.state = state;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean isAllowAlias() {
        return allowAlias;
    }

    public void setAllowAlias(Boolean allowAlias) {
        this.allowAlias = allowAlias;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("account='").append(account).append('\'');
        sb.append(", countryId='").append(countryId).append('\'');
        sb.append(", phoneNumber='").append(phoneNumber).append('\'');
        sb.append(", nickname='").append(nickname).append('\'');
        sb.append(", showHidden=").append(showHidden);
        sb.append(", sessionId='").append(sessionId).append('\'');
        sb.append(", lugServerId='").append(lugServerId).append('\'');
        sb.append(", lastConnectTime=").append(lastConnectTime);
        sb.append(", lastSessionTime=").append(lastSessionTime);
        sb.append(", admin=").append(admin);
        sb.append(", state=").append(state);
        sb.append(", approved=").append(approved);
        sb.append(", allowAlias=").append(allowAlias);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        User user = (User) o;

        return account.equals(user.account);
    }

    @Override
    public int hashCode() {
        return account.hashCode();
    }
}
