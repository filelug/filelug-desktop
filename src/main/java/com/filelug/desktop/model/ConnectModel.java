package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Properties;

/**
 * <code>ConnectModel</code> is the model for connect service.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ConnectModel {

    @JsonProperty("sid")
    private Integer sid;

    @JsonProperty("session-id")
    private String sessionId;

    @JsonProperty("country-id")
    private String countryId;

    @JsonProperty("phone")
    private String phoneNumber;

    @JsonProperty("account")
    private String account;

    @JsonProperty("adminAccount")
    private String adminAccount;

    @JsonProperty("passwd")
    private String password;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("verification")
    private String verification;

    @JsonProperty("computer-id")
    private Long computerId;

    @JsonProperty("computer-group")
    private String groupName;

    @JsonProperty("computer-name")
    private String computerName;

    @JsonProperty("locale")
    private String locale;

    @JsonProperty("lug-server-id")
    private String lugServerId;

    @JsonProperty("device-token")
    private DeviceToken deviceToken;

    @JsonProperty("sysprops")
    private Properties properties;

    // The property has been deprecated since 2.0.0 because it is redundant.
    @JsonProperty("should-self-approved")
    private Boolean shouldSelfApproved;


    public ConnectModel() {
    }

    /**
     * This is for constructing an ConnectResponseState for an administrator's connection.
     * Remember to set the following properties before connecting:
     * Sid,
     * verification,
     * lugServerId and
     * properties
     */
    public ConnectModel(String countryId, String phoneNumber, String account, String password, String nickname, Long computerId, String locale) {
        this();

        this.countryId = countryId;
        this.phoneNumber = phoneNumber;
        this.account = account;
        this.password = password;
        this.nickname = nickname;
        this.computerId = computerId;
        this.locale = locale;
    }

    /**
     * This is for constructing an ConnectResponseState for an administrator's connection WITHOUT PASSWORD.
     * Remember to set the following properties before connecting:
     * Sid,
     * properties
     */
    public ConnectModel(String account, String nickname, Long computerId, String locale, String lugServerId) {
        this();

        this.account = account;
        this.nickname = nickname;
        this.computerId = computerId;
        this.locale = locale;
        this.lugServerId = lugServerId;
    }

    /**
     * This is for constructing an ConnectResponseState for an non-administrator's connection.
     * Remember to set the following properties before connecting:
     * Sid,
     * verification,
     * lugServerId and
     * properties
     */
    public ConnectModel(String account, String adminAccount, String adminPassword, String adminNickname, Long computerId, String locale) {
        this();

        this.account = account;
        this.adminAccount = adminAccount;
        this.password = adminPassword;
        this.nickname = adminNickname;
        this.computerId = computerId;
        this.locale = locale;
    }

    public ConnectModel(String account, String adminAccount, String adminPassword, String adminNickname, String verification, Long computerId, String lugServerId, String locale) {
        this();

        this.account = account;
        this.adminAccount = adminAccount;
        this.password = adminPassword;
        this.nickname = adminNickname;
        this.verification = verification;
        this.computerId = computerId;
        this.lugServerId = lugServerId;
        this.locale = locale;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAdminAccount() {
        return adminAccount;
    }

    public void setAdminAccount(String adminAccount) {
        this.adminAccount = adminAccount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }

    public Long getComputerId() {
        return computerId;
    }

    public void setComputerId(Long computerId) {
        this.computerId = computerId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLugServerId() {
        return lugServerId;
    }

    public void setLugServerId(String lugServerId) {
        this.lugServerId = lugServerId;
    }

    public DeviceToken getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(DeviceToken deviceToken) {
        this.deviceToken = deviceToken;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Boolean getShouldSelfApproved() {
        return shouldSelfApproved;
    }

    public void setShouldSelfApproved(Boolean shouldSelfApproved) {
        this.shouldSelfApproved = shouldSelfApproved;
    }
}
