package com.filelug.desktop.dao;

import ch.qos.logback.classic.Logger;
import com.filelug.desktop.db.DatabaseAccess;
import com.filelug.desktop.db.DatabaseConstants;
import com.filelug.desktop.db.HyperSQLDatabaseAccess;
import com.filelug.desktop.model.AccountState;
import com.filelug.desktop.model.User;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>UserDao</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class UserDao {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("DAO_USER");

    private DatabaseAccess dbAccess;

    public UserDao() {
        dbAccess = new HyperSQLDatabaseAccess();
    }

    public UserDao(DatabaseAccess dbAccess) {
        this.dbAccess = dbAccess;
    }

//    public void createUser(String userId, String password, String nickname, Boolean showHidden, Long lastConnectTime, Boolean admin) {
//        User user = new User();
//        user.setAccount(userId);
//        user.setPasswd(password);
//        user.setNickname(nickname);
//        user.setShowHidden(showHidden);
//        user.setLastConnectTime(lastConnectTime);
//        user.setAdmin(admin);
//
//        createUser(user);
//    }

    public void createUser(User user) {
        Connection conn = null;
        PreparedStatement pStatement = null;

        String sessionId = user.getSessionId();
        Long lastAccessTime = user.getLastConnectTime();
        long currentTimestamp = System.currentTimeMillis();
        AccountState.State accountState = user.getState();

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_CREATE_AUTH_USER);

            pStatement.setString(1, user.getAccount());
            pStatement.setString(2, user.getCountryId());
            pStatement.setString(3, user.getPhoneNumber());
            pStatement.setString(4, sessionId);
            pStatement.setString(5, user.getLugServerId());
            pStatement.setString(6, user.getNickname());
            pStatement.setLong(7, lastAccessTime != null ? lastAccessTime : 0);
            pStatement.setLong(8, sessionId != null ? currentTimestamp : 0);
            pStatement.setLong(9, currentTimestamp);
            pStatement.setBoolean(10, user.getShowHidden());
            pStatement.setBoolean(11, user.getAdmin());
            pStatement.setString(12, accountState != null ? accountState.name() : AccountState.State.UNKNOWN.name());
            pStatement.setBoolean(13, user.getApproved());
            pStatement.setBoolean(14, user.isAllowAlias());

            pStatement.executeUpdate();

            LOGGER.debug("User " + user.getAccount() + " created.");
        } catch (Exception e) {
            LOGGER.error(String.format("Error on creating user '%s'%nerror message:%n%s", user.getAccount(), e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(null, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }
    }

    public boolean findUserExistsById(String userId) {
        boolean exists;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_USER_EXISTS_BY_ID);

            pStatement.setString(1, userId);

            resultSet = pStatement.executeQuery();

            exists = resultSet.next();
        } catch (Exception e) {
            exists = false;

            LOGGER.error(String.format("Error on finding if user '%s' exists.%nerror message:%n%s", userId, e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return exists;
    }

    public User findUserById(String userId) {
        User user = null;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_AUTH_USER_BY_ID);

            pStatement.setString(1, userId);

            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();

                user.setAccount(resultSet.getString(DatabaseConstants.COLUMN_NAME_AUTH_USER_ID));
                user.setCountryId(resultSet.getString(DatabaseConstants.COLUMN_NAME_COUNTRY_ID));
                user.setPhoneNumber(resultSet.getString(DatabaseConstants.COLUMN_NAME_PHONE_NUMBER));
                user.setSessionId(resultSet.getString(DatabaseConstants.COLUMN_NAME_SESSION_ID));
                user.setLugServerId(resultSet.getString(DatabaseConstants.COLUMN_NAME_LUG_SERVER_ID));
                user.setNickname(resultSet.getString(DatabaseConstants.COLUMN_NAME_NICKNAME));
                user.setLastConnectTime(resultSet.getLong(DatabaseConstants.COLUMN_NAME_LAST_CONNECT_TIME));
                user.setLastSessionTime(resultSet.getLong(DatabaseConstants.COLUMN_NAME_LAST_SESSION_TIME));
                user.setShowHidden(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_SHOW_HIDDEN));
                user.setAdmin(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_IS_ADMIN));
                user.setState(AccountState.State.valueOf(resultSet.getString(DatabaseConstants.COLUMN_NAME_CONNECTION_STATE)));
                user.setApproved(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_CONNECTION_APPROVED));
                user.setAllowAlias(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_ALLOW_ALIAS));

            }
        } catch (Exception e) {
            user = null;

            LOGGER.error(String.format("Error on finding user '%s'%nerror message:%n%s", userId, e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return user;
    }

    public String findLugServerIdByUserId(String userId) {
        String lugServerId = null;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_LUG_SERVER_ID_BY_USER_ID);

            pStatement.setString(1, userId);

            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                lugServerId = resultSet.getString(DatabaseConstants.COLUMN_NAME_LUG_SERVER_ID);
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Error on finding lug server id by user '%s'%nerror message:%n%s", userId, e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return lugServerId;
    }

    public void updateUser(User user) {
        Connection conn = null;
        PreparedStatement pStatement = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_UPDATE_USER);

            /*
                String SQL_UPDATE_USER = "UPDATE "
                                         + TABLE_NAME_AUTH_USER + " SET "
                                         + COLUMN_NAME_SESSION_ID + " = ?, "
                                         + COLUMN_NAME_LUG_SERVER_ID + " = ?, "
                                         + COLUMN_NAME_COUNTRY_ID + " = ?, "
                                         + COLUMN_NAME_PHONE_NUMBER + " = ?, "
                                         + COLUMN_NAME_NICKNAME + " = ?, "
                                         + COLUMN_NAME_LAST_CONNECT_TIME + " = ?, "
                                         + COLUMN_NAME_LAST_SESSION_TIME + " = ?, "
                                         + COLUMN_NAME_SHOW_HIDDEN + " = ?, "
                                         + COLUMN_NAME_IS_ADMIN + " = ?, "
                                         + COLUMN_NAME_CONNECTION_STATE + " = ?, "
                                         + COLUMN_NAME_CONNECTION_APPROVED+ " = ?, "
                                         + COLUMN_NAME_ALLOW_ALIAS + " = ? WHERE "
                                         + COLUMN_NAME_AUTH_USER_ID + " = ?";
            */

            pStatement.setString(1, user.getSessionId());
            pStatement.setString(2, user.getLugServerId());
            pStatement.setString(3, user.getCountryId());
            pStatement.setString(4, user.getPhoneNumber());
            pStatement.setString(5, user.getNickname());
            pStatement.setLong(6, user.getLastConnectTime());
            pStatement.setLong(7, user.getLastSessionTime());
            pStatement.setBoolean(8, user.getShowHidden());
            pStatement.setBoolean(9, user.getAdmin());
            pStatement.setString(10, user.getState() != null ? user.getState().name() : AccountState.State.UNKNOWN.name());
            pStatement.setBoolean(11, user.getApproved());
            pStatement.setBoolean(12, user.isAllowAlias());
            pStatement.setString(13, user.getAccount());

            pStatement.executeUpdate();

            LOGGER.debug("User " + user.getAccount() + " updated");
        } catch (Exception e) {
            LOGGER.error(String.format("Error on updating user '%s'%nerror message:%n%s", user.getAccount(), e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(null, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }
    }

    public boolean hasAdministrator() {
        boolean found = false;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_AUTH_USER_BY_IS_ADMIN);

            pStatement.setBoolean(1, true);

            resultSet = pStatement.executeQuery();

            found = resultSet.next();
        } catch (Exception e) {
            LOGGER.error(String.format("Error on finding if administrator exists%nerror message:%n%s", e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return found;
    }

    public User findAdministrator() {
        User user = null;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_AUTH_USER_BY_IS_ADMIN);

            pStatement.setBoolean(1, true);

            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();

                user.setAccount(resultSet.getString(DatabaseConstants.COLUMN_NAME_AUTH_USER_ID));
                user.setCountryId(resultSet.getString(DatabaseConstants.COLUMN_NAME_COUNTRY_ID));
                user.setPhoneNumber(resultSet.getString(DatabaseConstants.COLUMN_NAME_PHONE_NUMBER));
                user.setSessionId(resultSet.getString(DatabaseConstants.COLUMN_NAME_SESSION_ID));
                user.setLugServerId(resultSet.getString(DatabaseConstants.COLUMN_NAME_LUG_SERVER_ID));
                user.setNickname(resultSet.getString(DatabaseConstants.COLUMN_NAME_NICKNAME));
                user.setLastConnectTime(resultSet.getLong(DatabaseConstants.COLUMN_NAME_LAST_CONNECT_TIME));
                user.setLastSessionTime(resultSet.getLong(DatabaseConstants.COLUMN_NAME_LAST_SESSION_TIME));
                user.setShowHidden(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_SHOW_HIDDEN));
                user.setAdmin(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_IS_ADMIN));
                user.setState(AccountState.State.valueOf(resultSet.getString(DatabaseConstants.COLUMN_NAME_CONNECTION_STATE)));
                user.setApproved(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_CONNECTION_APPROVED));
                user.setAllowAlias(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_ALLOW_ALIAS));
            }
        } catch (Exception e) {
            user = null;

            LOGGER.error(String.format("Error on finding administrator%nerror message:%n%s", e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return user;
    }

    public String findUserStateById(String userId) {
        String userState = null;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_AUTH_USER_STATE_BY_ID);

            pStatement.setString(1, userId);

            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                userState = resultSet.getString(DatabaseConstants.COLUMN_NAME_CONNECTION_STATE);
            }
        } catch (Exception e) {
            userState = null;

            LOGGER.error(String.format("Error on finding state for user '%s'%nerror message:%n%s", userId, e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return userState;
    }

    public void updateUserStateById(String userId, AccountState.State newState) {
        Connection conn = null;
        PreparedStatement pStatement = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_UPDATE_USER_STATE_BY_ID);

            String nonNullState = newState != null ? newState.name() : AccountState.State.UNKNOWN.name();

            pStatement.setString(1, nonNullState);
            pStatement.setString(2, userId);

            pStatement.executeUpdate();

            LOGGER.debug("User " + userId + " updated state to " + nonNullState);
        } catch (Exception e) {
            LOGGER.error(String.format("Error on updating state '%s' for user '%s'%nerror message:%n%s", newState, userId, e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(null, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }
    }

    public boolean isAdministrator(String userId) {
        boolean found = false;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_IF_AUTH_USER_ADMIN);

            pStatement.setString(1, userId);

            resultSet = pStatement.executeQuery();

            found = resultSet.next();
        } catch (Exception e) {
            LOGGER.error(String.format("Error on finding if user '%s' is the administrator%nerror message:%n%s", userId, e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return found;
    }

    /**
     * Finds the ids of all users.
     *
     * @return The ids of all users.
     */
    public List<String> findAllUserIds() {
        List<String> userIds = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_ALL_AUTH_USER_IDS_ORDER_BY_IS_ADMIN_DESC);

            resultSet = pStatement.executeQuery();

            for (; resultSet.next(); ) {
                userIds.add(resultSet.getString(DatabaseConstants.COLUMN_NAME_AUTH_USER_ID));
            }
        } catch (Exception e) {
            userIds.clear();

            LOGGER.error(String.format("Error on finding the ids for all users%nerror message:%n%s", e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return userIds;
    }

    public List<User> findAllUsers() {
        List<User> users = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            // order by is-admin asc so the administrator will be the last one
            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_ALL_AUTH_USERS_ORDER_BY_IS_ADMIN_DESC);

            resultSet = pStatement.executeQuery();

            for (; resultSet.next(); ) {
                User user = new User();

                user.setAccount(resultSet.getString(DatabaseConstants.COLUMN_NAME_AUTH_USER_ID));
                user.setCountryId(resultSet.getString(DatabaseConstants.COLUMN_NAME_COUNTRY_ID));
                user.setPhoneNumber(resultSet.getString(DatabaseConstants.COLUMN_NAME_PHONE_NUMBER));
                user.setSessionId(resultSet.getString(DatabaseConstants.COLUMN_NAME_SESSION_ID));
                user.setLugServerId(resultSet.getString(DatabaseConstants.COLUMN_NAME_LUG_SERVER_ID));
                user.setNickname(resultSet.getString(DatabaseConstants.COLUMN_NAME_NICKNAME));
                user.setLastConnectTime(resultSet.getLong(DatabaseConstants.COLUMN_NAME_LAST_CONNECT_TIME));
                user.setLastSessionTime(resultSet.getLong(DatabaseConstants.COLUMN_NAME_LAST_SESSION_TIME));
                user.setShowHidden(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_SHOW_HIDDEN));
                user.setAdmin(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_IS_ADMIN));
                user.setState(AccountState.State.valueOf(resultSet.getString(DatabaseConstants.COLUMN_NAME_CONNECTION_STATE)));
                user.setApproved(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_CONNECTION_APPROVED));
                user.setAllowAlias(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_ALLOW_ALIAS));

                users.add(user);
            }
        } catch (Exception e) {
            users.clear();

            LOGGER.error(String.format("Error on finding all users%nerror message:%n%s", e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return users;
    }

    public List<User> findNonAdminUsers() {
        List<User> users = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            // order by is-admin asc so the administrator will be the last one
            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_NON_ADMIN_USERS);

            resultSet = pStatement.executeQuery();

            for (; resultSet.next(); ) {
                User user = new User();

                user.setAccount(resultSet.getString(DatabaseConstants.COLUMN_NAME_AUTH_USER_ID));
                user.setCountryId(resultSet.getString(DatabaseConstants.COLUMN_NAME_COUNTRY_ID));
                user.setPhoneNumber(resultSet.getString(DatabaseConstants.COLUMN_NAME_PHONE_NUMBER));
                user.setSessionId(resultSet.getString(DatabaseConstants.COLUMN_NAME_SESSION_ID));
                user.setLugServerId(resultSet.getString(DatabaseConstants.COLUMN_NAME_LUG_SERVER_ID));
                user.setNickname(resultSet.getString(DatabaseConstants.COLUMN_NAME_NICKNAME));
                user.setLastConnectTime(resultSet.getLong(DatabaseConstants.COLUMN_NAME_LAST_CONNECT_TIME));
                user.setLastSessionTime(resultSet.getLong(DatabaseConstants.COLUMN_NAME_LAST_SESSION_TIME));
                user.setShowHidden(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_SHOW_HIDDEN));
                user.setAdmin(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_IS_ADMIN));
                user.setState(AccountState.State.valueOf(resultSet.getString(DatabaseConstants.COLUMN_NAME_CONNECTION_STATE)));
                user.setApproved(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_CONNECTION_APPROVED));
                user.setAllowAlias(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_ALLOW_ALIAS));

                users.add(user);
            }
        } catch (Exception e) {
            users.clear();

            LOGGER.error(String.format("Error on finding all users%nerror message:%n%s", e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return users;
    }

    public List<User> findUsersWithStates(List<AccountState.State> states) {
        List<User> users = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        Array stateArray = null;

        try {
            conn = dbAccess.getConnection();

            // order by is-admin asc so the administrator will be the last one
            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_AUTH_USERS_WITH_STATES_ORDER_BY_IS_ADMIN_DESC);

            int size = states.size();
            Object[] stateStrings = new Object[size];

            for (int index = 0; index < size; index++) {
                stateStrings[index] = states.get(index).name();
            }

            stateArray = conn.createArrayOf("VARCHAR", stateStrings);

            pStatement.setArray(1, stateArray);

            resultSet = pStatement.executeQuery();

            for (; resultSet.next(); ) {
                User user = new User();

                user.setAccount(resultSet.getString(DatabaseConstants.COLUMN_NAME_AUTH_USER_ID));
                user.setCountryId(resultSet.getString(DatabaseConstants.COLUMN_NAME_COUNTRY_ID));
                user.setPhoneNumber(resultSet.getString(DatabaseConstants.COLUMN_NAME_PHONE_NUMBER));
                user.setSessionId(resultSet.getString(DatabaseConstants.COLUMN_NAME_SESSION_ID));
                user.setLugServerId(resultSet.getString(DatabaseConstants.COLUMN_NAME_LUG_SERVER_ID));
                user.setNickname(resultSet.getString(DatabaseConstants.COLUMN_NAME_NICKNAME));
                user.setLastConnectTime(resultSet.getLong(DatabaseConstants.COLUMN_NAME_LAST_CONNECT_TIME));
                user.setLastSessionTime(resultSet.getLong(DatabaseConstants.COLUMN_NAME_LAST_SESSION_TIME));
                user.setShowHidden(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_SHOW_HIDDEN));
                user.setAdmin(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_IS_ADMIN));
                user.setState(AccountState.State.valueOf(resultSet.getString(DatabaseConstants.COLUMN_NAME_CONNECTION_STATE)));
                user.setApproved(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_CONNECTION_APPROVED));
                user.setAllowAlias(resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_ALLOW_ALIAS));

                users.add(user);
            }
        } catch (Exception e) {
            users.clear();

            LOGGER.error(String.format("Error on finding users with states%nerror message:%n%s", e.getMessage()), e);
        } finally {
            if (stateArray != null) {
                try {
                    stateArray.free();
                } catch (Exception e) {
                    /* ignored */
                }
            }

            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return users;
    }

    public void truncateAllTables() {
        Connection conn = null;
        PreparedStatement pStatement = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_TRUNCATE_ALL_TABLES);

            pStatement.executeUpdate();

            LOGGER.debug("All tables truncated.");
        } catch (Exception e) {
            LOGGER.error(String.format("Error on truncating all tables%nerror message:%n%s", e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(null, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }
    }

    public void deleteUserById(String userId) {
        Connection conn = null;
        PreparedStatement pStatement = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_DELETE_USER_BY_ID);

            pStatement.setString(1, userId);

            pStatement.executeUpdate();

            LOGGER.debug("User " + userId + " deleted");
        } catch (Exception e) {
            LOGGER.error(String.format("Error on deleting user '%s'%nerror message:%n%s", userId, e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(null, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }
    }

    public boolean findAllowAliasById(String userId) {
        Boolean allowAlias = null;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_ALLOW_ALIAS_BY_ID);

            pStatement.setString(1, userId);

            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                allowAlias = resultSet.getBoolean(DatabaseConstants.COLUMN_NAME_ALLOW_ALIAS);
            }
        } catch (Exception e) {
            allowAlias = false;

            LOGGER.error(String.format("Error on finding if user '%s' exists.%nerror message:%n%s", userId, e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return allowAlias != null ? allowAlias : false;
    }

    public String findSessionIdById(String userId) throws SQLException {
        String sessionId = null;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_AUTH_USER_BY_ID);

            pStatement.setString(1, userId);

            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                sessionId = resultSet.getString(DatabaseConstants.COLUMN_NAME_SESSION_ID);
            }
        } catch (Exception e) {
            String errorMessage = String.format("Error on finding user session for user '%s'", userId);

            LOGGER.error(errorMessage, e);

            throw new SQLException(errorMessage, e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    // ignored
                }
            }
        }

        return sessionId;
    }

    public void updateSessionById(String userId, String newSessionId) {
        Connection conn = null;
        PreparedStatement pStatement = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_UPDATE_USER_SESSION);

            pStatement.setString(1, newSessionId);
            pStatement.setLong(2, System.currentTimeMillis());
            pStatement.setString(3, userId);

            pStatement.executeUpdate();
        } catch (Exception e) {
            LOGGER.error(String.format("Error on updating user '%s' with new session id: '%s'%nerror message:%n%s", userId, newSessionId, e.getMessage()), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(null, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }
    }
}
