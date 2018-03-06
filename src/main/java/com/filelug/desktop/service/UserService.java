package com.filelug.desktop.service;

import com.filelug.desktop.NeedLoginWithQRCodeException;
import com.filelug.desktop.NotRegisteredException;
import com.filelug.desktop.model.AccountState;
import com.filelug.desktop.model.ConnectResponseState;
import com.filelug.desktop.model.User;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <code>UserService</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public interface UserService {

    /**
     * Finds the lug server id to connect at current moment.
     *
     * @return The lug server id to connect to.
     */
    String dispatchConnection(String userId) throws Exception;

    // The user state change will not be notified.
    void connectFromComputer(ConnectResponseState connectResponseState) throws IllegalArgumentException, NotRegisteredException, IOException, Exception;

    void connectUser(User user, boolean needUpdateLugServerId);

    /**
     * DO NOT INVOKE THIS METHOD FOR RE-CONNECTING COMPUTER FOR THE SAME USER!!
     */
    void disconnectFromComputer(String userId) throws IllegalArgumentException, NeedLoginWithQRCodeException, IOException, Exception;

    Session loginUserOrGetQRCode() throws Exception;

    // The user state change will not be notified.
    int disconnect(String userId) throws IllegalArgumentException, NotRegisteredException, IOException, Exception;

    void checkReconnectRecursively(final ScheduledExecutorService scheduledExecutorService, final FutureCallback<HttpResponse> callback, long delay, TimeUnit unit);

    User findAdministrator();

    boolean isAdministrator(String userId);

    boolean hasAdministrator();

    // find user from db
    User findLocalUserById(String userId);

    String findLugServerIdByUserId(String userId);

    // delete user from db
    void deleteLocalUserById(String userId);

    // create user to db
    void createLocalUser(User user);

    // update user to db
    void updateLocalUser(User user);

    // The user state change will be notified.
    void disconnectAllUsers();

    List<String> findAllUserIds();

    List<User> findAllLocalUsers();

    /**
     * Finds all local users except for the administrator.
     */
    List<User> findNonAdminUsers();

    List<User> findLocalUsersWithStates(List<AccountState.State> states);

    boolean isUserConnected(String userId);

    /**
     * Truncates all tables in the desktop.
     */
    void truncateDesktopAllTables();

    /**
     * Download approved apply-connection users and update the user table,
     * except for the administrator, who will not be deleted if not found in downloaded data.
     *
     * @throws SessionNotFoundException The session of the admin not found. It's possible that it has been a long time for the computer to connect to the server or the admin of this computer has been deleted.
     * If the computer itself has been deleted, it won't throw SessionNotFoundException, but UserNotFoundException.
     * @throws ComputerNotFoundException The computer not found in the server.
     */
    void syncUsersWithApprovedConnectionUsers() throws ComputerNotFoundException, SessionNotFoundException, Exception;

    /**
     * Get new session id with the old one.
     *
     * @param oldSessionId The session id used to be changed.
     * @return The new session id
     */
    String exchangeNewSessionWithOld(String oldSessionId, String userId, String countryId, String phoneNumber) throws Exception;

    /**
     * Login for the specified applied user.
     *
     * @param applyUserId The specified applied user.
     * @throws Exception Thrown on failure to login for the applied user login.
     */
    public void loginApplyUserWithUserId(String applyUserId) throws Exception;

    /**
     * Asks for new session to replace the old session for the specified user.
     *
     * @param userId The user id of the session owner.
     * @return The new session id.
     *
     * @throws SessionNotFoundException When session of the user not found.
     */
    String validateSessionAndGetNewIfNeededForUser(String userId) throws Exception;

    /**
     * Checks if the specified lastAccessTime is time-out based on the specified idle time interval.
     *
     * @param lastAccessTime The specified lastAccessTime
     * @param timeout The specified idle time interval, in seconds. If the value is null,
     *                use the value Constants.LESS_OF_CLIENT_SESSION_IDLE_TIMEOUT_IN_SECONDS.
     *
     * @return true if the specified lastAccessTime is time-out.
     */
    boolean checkTimeout(long lastAccessTime, Integer timeout);

    /**
     * Resets administrator account.
     */
    void removeAllUsers();

    /**
     * Resets application.
     */
    void resetApplication();
}

