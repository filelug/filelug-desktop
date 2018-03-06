package com.filelug.desktop.db;

import com.filelug.desktop.model.AccountState;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public interface DatabaseConstants {

    /* TABLE: BOOKMARK related */

    String TABLE_NAME_TO_BE_DELETED_BOOKMARK = "BOOKMARK";

//    String TABLE_NAME_BOOKMARK = "BOOKMARK";
//
//    String COLUMN_NAME_BOOKMARK_ID = "BOOKMARK_ID";
//
//    String COLUMN_NAME_BOOKMARK_PATH = "BOOKMARK_PATH";
//
//    String COLUMN_NAME_BOOKMARK_LABEL = "BOOKMARK_LABEL";
//
//    String COLUMN_NAME_BOOKMARK_USER = "BOOKMARK_USER";
//
//    String INDEX_NAME_BOOKMARK_PATH = "INDEX_D_BOOKMARK_PATH";
//
//    String INDEX_NAME_BOOKMARK_LABEL = "INDEX_D_BOOKMARK_LABEL";

    /* TABLE: USER related */

    String TABLE_NAME_AUTH_USER = "AUTH_USER";

    String INDEX_NAME_AUTH_USER_IS_ADMIN = "INDEX_D_AUTH_USER_IS_ADMIN";

    String INDEX_NAME_AUTH_USER_ADDED_TIME = "INDEX_D_AUTH_USER_ADDED_TIME";

    String INDEX_NAME_AUTH_USER_SESSION_ID = "INDEX_D_AUTH_USER_SESSION_ID";

    String INDEX_NAME_AUTH_USER_COUNTRY_ID = "INDEX_D_AUTH_USER_COUNTRY_ID";

    String INDEX_NAME_AUTH_USER_PHONE_NUMBER = "INDEX_D_AUTH_USER_PHONE_NUMBER";

    String COLUMN_NAME_AUTH_USER_ID = "AUTH_USER_ID";

    String COLUMN_NAME_SESSION_ID = "SESSION_ID";

    String COLUMN_NAME_LUG_SERVER_ID = "LUG_SERVER_ID";

//    String COLUMN_NAME_TO_BE_DELETED_COUNTRY_ID = "COUNTRY_ID";
//
//    String COLUMN_NAME_TO_BE_DELETED_PHONE_NUMBER = "PHONE_NUMBER";

    String COLUMN_NAME_COUNTRY_ID = "COUNTRY_ID";

    String COLUMN_NAME_PHONE_NUMBER = "PHONE_NUMBER";

//    String COLUMN_NAME_TO_BE_DELETED_PASSWD = "PASSWD";

    String COLUMN_NAME_PASSWD = "PASSWD";

    String COLUMN_NAME_NICKNAME = "NICKNAME";

    String COLUMN_NAME_LAST_CONNECT_TIME = "LAST_CONNECT_TIME";

    String COLUMN_NAME_LAST_SESSION_TIME = "LAST_SESSION_TIME";

    String COLUMN_NAME_ADDED_TIME = "ADDED_TIME";

    String COLUMN_NAME_SHOW_HIDDEN = "SHOW_HIDDEN";

    String COLUMN_NAME_IS_ADMIN = "IS_ADMIN";

    String COLUMN_NAME_CONNECTION_STATE = "CONNECTION_STATE";

    // if the user has applied connection to this computer and approved.
    String COLUMN_NAME_CONNECTION_APPROVED = "CONNECTION_APPROVED";

    String COLUMN_NAME_ALLOW_ALIAS = "ALLOW_ALIAS";

//    /* TABLE: ROOT_DIRECTORY related */

    String TABLE_NAME_TO_BE_DELETED_ROOT_DIRECTORY = "ROOT_DIRECTORY";

//    String TABLE_NAME_ROOT_DIRECTORY = "ROOT_DIRECTORY";
//
//    String COLUMN_NAME_ROOT_DIRECTORY_ID = "ROOT_DIRECTORY_ID";
//
//    String COLUMN_NAME_ROOT_DIRECTORY_PATH = "ROOT_DIRECTORY_PATH";
//
//    String COLUMN_NAME_ROOT_DIRECTORY_LABEL = "ROOT_DIRECTORY_LABEL";
//
//    String COLUMN_NAME_ROOT_DIRECTORY_USER = "ROOT_DIRECTORY_USER";
//
//    String INDEX_NAME_ROOT_DIRECTORY_PATH = "INDEX_D_ROOT_DIRECTORY_PATH";
//
//    String INDEX_NAME_ROOT_DIRECTORY_LABEL = "INDEX_D_ROOT_DIRECTORY_LABEL";
//
//    String INDEX_NAME_ROOT_DIRECTORY_IS_MAIN = "INDEX_D_ROOT_DIRECTORY_IS_MAIN";
//
//    /* DDL - TABLE: BOOKMARK */
//
//    String SQL_CREATE_TABLE_BOOKMARK = "CREATE TABLE " + TABLE_NAME_BOOKMARK + " ("
//                                       + COLUMN_NAME_BOOKMARK_ID + " BIGINT PRIMARY KEY, "
//                                       + COLUMN_NAME_BOOKMARK_PATH + " VARCHAR(1024) NOT NULL, "
//                                       + COLUMN_NAME_BOOKMARK_LABEL + " VARCHAR(1024) NOT NULL, "
//                                       + COLUMN_NAME_BOOKMARK_USER + " VARCHAR(1024) NOT NULL)";
//
//    String SQL_CREATE_INDEX_BOOKMARK_PATH = "CREATE INDEX " + INDEX_NAME_BOOKMARK_PATH + " ON " + TABLE_NAME_BOOKMARK + "(" + COLUMN_NAME_BOOKMARK_PATH + ")";
//
//    String SQL_CREATE_INDEX_BOOKMARK_LABEL = "CREATE INDEX " + INDEX_NAME_BOOKMARK_LABEL + " ON " + TABLE_NAME_BOOKMARK + "(" + COLUMN_NAME_BOOKMARK_LABEL + ")";
//
//    String SQL_CREATE_FOREIGN_KEY_BOOKMARK_USER = "ALTER TABLE " + TABLE_NAME_BOOKMARK + " ADD FOREIGN KEY (" + COLUMN_NAME_BOOKMARK_USER + ") REFERENCES " + TABLE_NAME_AUTH_USER + "(" + COLUMN_NAME_AUTH_USER_ID + ") ON DELETE CASCADE";
//
//    /* find all bookmarks */
//    String SQL_FIND_ALL_BOOKMARKS_ORDER_BY_ID_DESC = "SELECT "
//                                                     + COLUMN_NAME_BOOKMARK_ID + ", "
//                                                     + COLUMN_NAME_BOOKMARK_PATH + ", "
//                                                     + COLUMN_NAME_BOOKMARK_LABEL + ", "
//                                                     + COLUMN_NAME_BOOKMARK_USER
//                                                     + " FROM "
//                                                     + TABLE_NAME_BOOKMARK
//                                                     + " WHERE "
//                                                     + COLUMN_NAME_BOOKMARK_USER
//                                                     + " = ? ORDER BY "
//                                                     + COLUMN_NAME_BOOKMARK_ID + " DESC";
//
//    /* find bookmark count by path */
//    String SQL_FIND_BOOKMARK_COUNT_BY_ID = "SELECT COUNT(*) FROM "
//                                           + TABLE_NAME_BOOKMARK
//                                           + " WHERE "
//                                           + COLUMN_NAME_BOOKMARK_ID
//                                           + " = ?";
//
//    /* find bookmark count by path */
//    String SQL_FIND_BOOKMARK_COUNT_BY_PTH = "SELECT COUNT(*) FROM "
//                                            + TABLE_NAME_BOOKMARK
//                                            + " WHERE "
//                                            + COLUMN_NAME_BOOKMARK_PATH
//                                            + " = ? AND "
//                                            + COLUMN_NAME_BOOKMARK_USER + " = ?";
//
//    /* find bookmark count by path */
//    String SQL_FIND_BOOKMARK_COUNT_BY_PTH_EXCEPT_SELF = "SELECT COUNT(*) FROM "
//                                                        + TABLE_NAME_BOOKMARK
//                                                        + " WHERE "
//                                                        + COLUMN_NAME_BOOKMARK_PATH
//                                                        + " = ? AND "
//                                                        + COLUMN_NAME_BOOKMARK_ID
//                                                        + " != ? AND "
//                                                        + COLUMN_NAME_BOOKMARK_USER  // For testing id except-self, we need narrow to the same user
//                                                        + " = ?";
//
//    /* find bookmark count by path */
//    String SQL_FIND_BOOKMARK_COUNT_BY_LABEL = "SELECT COUNT(*) FROM "
//                                              + TABLE_NAME_BOOKMARK
//                                              + " WHERE "
//                                              + COLUMN_NAME_BOOKMARK_LABEL
//                                              + " = ? AND "
//                                              + COLUMN_NAME_BOOKMARK_USER + " = ?";
//
//    /* find bookmark count by path */
//    String SQL_FIND_BOOKMARK_COUNT_BY_LABEL_EXCEPT_SELF = "SELECT COUNT(*) FROM "
//                                                          + TABLE_NAME_BOOKMARK
//                                                          + " WHERE "
//                                                          + COLUMN_NAME_BOOKMARK_LABEL
//                                                          + " = ? AND "
//                                                          + COLUMN_NAME_BOOKMARK_ID
//                                                          + " != ? AND "
//                                                          + COLUMN_NAME_BOOKMARK_USER + " = ?";
//
//    /* create bookmark */
//    String SQL_CREATE_BOOKMARK = "INSERT INTO "
//                                 + TABLE_NAME_BOOKMARK + "("
//                                 + COLUMN_NAME_BOOKMARK_ID + ", "
//                                 + COLUMN_NAME_BOOKMARK_PATH + ", "
//                                 + COLUMN_NAME_BOOKMARK_LABEL + ", "
//                                 + COLUMN_NAME_BOOKMARK_USER + ") VALUES(?, ?, ?, ?)";
//
//    /* find bookmark by path */
//    String SQL_FIND_BOOKMARK_BY_PTH = "SELECT "
//                                      + COLUMN_NAME_BOOKMARK_ID + ", "
//                                      + COLUMN_NAME_BOOKMARK_PATH + ", "
//                                      + COLUMN_NAME_BOOKMARK_LABEL + ", "
//                                      + COLUMN_NAME_BOOKMARK_USER
//                                      + " FROM "
//                                      + TABLE_NAME_BOOKMARK
//                                      + " WHERE "
//                                      + COLUMN_NAME_BOOKMARK_PATH
//                                      + " = ? AND "
//                                      + COLUMN_NAME_BOOKMARK_USER + " = ?";
//
//    /* find bookmark by id */
//    String SQL_FIND_BOOKMARK_BY_ID = "SELECT "
//                                     + COLUMN_NAME_BOOKMARK_ID + ", "
//                                     + COLUMN_NAME_BOOKMARK_PATH + ", "
//                                     + COLUMN_NAME_BOOKMARK_LABEL + ", "
//                                     + COLUMN_NAME_BOOKMARK_USER
//                                     + " FROM "
//                                     + TABLE_NAME_BOOKMARK
//                                     + " WHERE "
//                                     + COLUMN_NAME_BOOKMARK_ID + " = ?";
//
//    /* update bookmark */
//    String SQL_UPDATE_BOOKMARK = "UPDATE "
//                                 + TABLE_NAME_BOOKMARK
//                                 + " SET "
//                                 + COLUMN_NAME_BOOKMARK_PATH
//                                 + " = ?, "
//                                 + COLUMN_NAME_BOOKMARK_LABEL
//                                 + " = ? WHERE "
//                                 + COLUMN_NAME_BOOKMARK_ID + " = ?";
//
//    /* delete bookmark by id */
//    String SQL_DELETE_BOOKMARK_BY_ID = "DELETE FROM "
//                                       + TABLE_NAME_BOOKMARK
//                                       + " WHERE "
//                                       + COLUMN_NAME_BOOKMARK_ID + " = ?";

    /* DDL - TABLE: AUTH_USER */

    String SQL_CREATE_TABLE_AUTH_USER = "CREATE TABLE "
                                        + TABLE_NAME_AUTH_USER + "("
                                        + COLUMN_NAME_AUTH_USER_ID + " VARCHAR(1024) PRIMARY KEY, "
                                        + COLUMN_NAME_COUNTRY_ID + " VARCHAR(8) NOT NULL, "
                                        + COLUMN_NAME_PHONE_NUMBER + " VARCHAR(24) NOT NULL, "
                                        + COLUMN_NAME_SESSION_ID + " VARCHAR(1024) NULL, "
                                        + COLUMN_NAME_LUG_SERVER_ID + " VARCHAR(1024) NULL, "
                                        + COLUMN_NAME_NICKNAME + " VARCHAR(1024) NOT NULL, "
                                        + COLUMN_NAME_LAST_CONNECT_TIME + " BIGINT DEFAULT 0 NOT NULL, "
                                        + COLUMN_NAME_LAST_SESSION_TIME + " BIGINT DEFAULT 0 NOT NULL, "
                                        + COLUMN_NAME_ADDED_TIME + " BIGINT DEFAULT 0 NOT NULL, "           // when the user is added to the db
                                        + COLUMN_NAME_SHOW_HIDDEN + " BOOLEAN DEFAULT false, "
                                        + COLUMN_NAME_IS_ADMIN + " BOOLEAN DEFAULT false, "
                                        + COLUMN_NAME_CONNECTION_STATE + " VARCHAR(1024) DEFAULT '" + AccountState.State.UNKNOWN.name() + "' NOT NULL, "
                                        + COLUMN_NAME_CONNECTION_APPROVED + " BOOLEAN DEFAULT false NOT NULL, "
                                        + COLUMN_NAME_ALLOW_ALIAS + " BOOLEAN DEFAULT true)";
//    String SQL_CREATE_TABLE_AUTH_USER = "CREATE TABLE "
//                                        + TABLE_NAME_AUTH_USER + "("
//                                        + COLUMN_NAME_AUTH_USER_ID + " VARCHAR(1024) PRIMARY KEY, "
//                                        + COLUMN_NAME_COUNTRY_ID + " VARCHAR(8) NOT NULL, "
//                                        + COLUMN_NAME_PHONE_NUMBER + " VARCHAR(24) NOT NULL, "
//                                        + COLUMN_NAME_PASSWD + " VARCHAR(1024) NULL, "                      // no password for non-admin
//                                        + COLUMN_NAME_NICKNAME + " VARCHAR(1024) NOT NULL, "
//                                        + COLUMN_NAME_LAST_CONNECT_TIME + " BIGINT DEFAULT 0 NOT NULL, "    // not used now
//                                        + COLUMN_NAME_ADDED_TIME + " BIGINT DEFAULT 0 NOT NULL, "           // when the user is added to the db
//                                        + COLUMN_NAME_SHOW_HIDDEN + " BOOLEAN DEFAULT false, "
//                                        + COLUMN_NAME_IS_ADMIN + " BOOLEAN DEFAULT false, "
//                                        + COLUMN_NAME_CONNECTION_STATE + " VARCHAR(1024) DEFAULT '" + AccountState.State.UNKNOWN.name() + "' NOT NULL, "
//                                        + COLUMN_NAME_CONNECTION_APPROVED + " BOOLEAN DEFAULT false NOT NULL, "
//                                        + COLUMN_NAME_ALLOW_ALIAS + " BOOLEAN DEFAULT true)";

    String SQL_CREATE_INDEX_AUTH_USER_IS_ADMIN = "CREATE INDEX " + INDEX_NAME_AUTH_USER_IS_ADMIN + " ON " + TABLE_NAME_AUTH_USER + "(" + COLUMN_NAME_IS_ADMIN + ")";

    String SQL_CREATE_INDEX_AUTH_USER_ADDED_TIME = "CREATE INDEX " + INDEX_NAME_AUTH_USER_ADDED_TIME + " ON " + TABLE_NAME_AUTH_USER + "(" + COLUMN_NAME_ADDED_TIME + ")";

    String SQL_CREATE_INDEX_AUTH_USER_SESSION_ID = "CREATE INDEX " + INDEX_NAME_AUTH_USER_SESSION_ID + " ON " + TABLE_NAME_AUTH_USER + "(" + COLUMN_NAME_SESSION_ID + ")";

    String SQL_CREATE_INDEX_AUTH_USER_COUNTRY_ID = "CREATE INDEX " + INDEX_NAME_AUTH_USER_COUNTRY_ID + " ON " + TABLE_NAME_AUTH_USER + "(" + COLUMN_NAME_COUNTRY_ID + ")";

    String SQL_CREATE_INDEX_AUTH_USER_PHONE_NUMBER = "CREATE INDEX " + INDEX_NAME_AUTH_USER_PHONE_NUMBER + " ON " + TABLE_NAME_AUTH_USER + "(" + COLUMN_NAME_PHONE_NUMBER + ")";

    String SQL_ALTER_TABLE_ADD_COLUMN_AUTH_USER_SESSION_ID = "ALTER TABLE " + TABLE_NAME_AUTH_USER + " ADD COLUMN " + COLUMN_NAME_SESSION_ID + " VARCHAR(1024) NULL BEFORE " + COLUMN_NAME_NICKNAME;

    String SQL_ALTER_TABLE_ADD_COLUMN_AUTH_USER_LUG_SERVER_ID = "ALTER TABLE " + TABLE_NAME_AUTH_USER + " ADD COLUMN " + COLUMN_NAME_LUG_SERVER_ID + " VARCHAR(1024) NULL BEFORE " + COLUMN_NAME_NICKNAME;

    String SQL_ALTER_TABLE_ADD_COLUMN_AUTH_USER_LAST_SESSION_TIME = "ALTER TABLE " + TABLE_NAME_AUTH_USER + " ADD COLUMN " + COLUMN_NAME_LAST_SESSION_TIME + " BIGINT DEFAULT 0 NOT NULL BEFORE " + COLUMN_NAME_ADDED_TIME;

    String SQL_ALTER_TABLE_ALTER_COLUMN_AUTH_USER_PASSWD_NULLABLE = "ALTER TABLE " + TABLE_NAME_AUTH_USER + " ALTER COLUMN " + COLUMN_NAME_PASSWD + " SET NULL";

//    String SQL_ALTER_TABLE_DELETE_COLUMN_AUTH_USER_COUNTRY_ID = "ALTER TABLE " + TABLE_NAME_AUTH_USER + " DROP COLUMN " + COLUMN_NAME_TO_BE_DELETED_COUNTRY_ID + " CASCADE";
//
//    String SQL_ALTER_TABLE_DELETE_COLUMN_AUTH_USER_PHONE_NUMBER = "ALTER TABLE " + TABLE_NAME_AUTH_USER + " DROP COLUMN " + COLUMN_NAME_TO_BE_DELETED_PHONE_NUMBER + " CASCADE";
//
//    String SQL_ALTER_TABLE_DELETE_COLUMN_AUTH_USER_PASSWD = "ALTER TABLE " + TABLE_NAME_AUTH_USER + " DROP COLUMN " + COLUMN_NAME_TO_BE_DELETED_PASSWD + " CASCADE";

    String SQL_DROP_TABLE_BOOKMARK = "DROP TABLE IF EXISTS " + TABLE_NAME_TO_BE_DELETED_BOOKMARK + " CASCADE";

    String SQL_DROP_TABLE_ROOT_DIRECTORIES = "DROP TABLE IF EXISTS " + TABLE_NAME_TO_BE_DELETED_ROOT_DIRECTORY + " CASCADE";

    /* find user count */
    String SQL_FIND_AUTH_USER_COUNT = "SELECT COUNT(*) FROM " + TABLE_NAME_AUTH_USER;

    /* find user by id */
    String SQL_FIND_AUTH_USER_BY_ID = "SELECT "
                                      + COLUMN_NAME_AUTH_USER_ID + ", "
                                      + COLUMN_NAME_COUNTRY_ID + ", "
                                      + COLUMN_NAME_PHONE_NUMBER + ", "
                                      + COLUMN_NAME_SESSION_ID + ", "
                                      + COLUMN_NAME_LUG_SERVER_ID + ", "
                                      + COLUMN_NAME_NICKNAME + ", "
                                      + COLUMN_NAME_LAST_CONNECT_TIME + ", "
                                      + COLUMN_NAME_LAST_SESSION_TIME + ", "
                                      + COLUMN_NAME_ADDED_TIME + ", "
                                      + COLUMN_NAME_SHOW_HIDDEN + ", "
                                      + COLUMN_NAME_IS_ADMIN + ", "
                                      + COLUMN_NAME_CONNECTION_STATE + ", "
                                      + COLUMN_NAME_CONNECTION_APPROVED + ", "
                                      + COLUMN_NAME_ALLOW_ALIAS
                                      + " FROM "
                                      + TABLE_NAME_AUTH_USER
                                      + " WHERE "
                                      + COLUMN_NAME_AUTH_USER_ID + " = ?";

    /* find user state by user id */
    String SQL_FIND_AUTH_USER_STATE_BY_ID = "SELECT "
                                      + COLUMN_NAME_CONNECTION_STATE
                                      + " FROM "
                                      + TABLE_NAME_AUTH_USER
                                      + " WHERE "
                                      + COLUMN_NAME_AUTH_USER_ID + " = ?";

    // find lug server id by user id
    String SQL_FIND_LUG_SERVER_ID_BY_USER_ID = "SELECT "
                                               + COLUMN_NAME_LUG_SERVER_ID
                                               + " FROM "
                                               + TABLE_NAME_AUTH_USER
                                               + " WHERE "
                                               + COLUMN_NAME_AUTH_USER_ID + " = ?";

    /* find all user ids */
    String SQL_FIND_ALL_AUTH_USER_IDS_ORDER_BY_IS_ADMIN_DESC = "SELECT "
                                                              + COLUMN_NAME_AUTH_USER_ID + ", "
                                                              + COLUMN_NAME_IS_ADMIN + ", "
                                                              + COLUMN_NAME_ADDED_TIME
                                                              + " FROM "
                                                              + TABLE_NAME_AUTH_USER
                                                               + " ORDER BY "
                                                               + COLUMN_NAME_IS_ADMIN
                                                               + " DESC, " + COLUMN_NAME_ADDED_TIME + " ASC";

    /* find all users */
    String SQL_FIND_ALL_AUTH_USERS_ORDER_BY_IS_ADMIN_DESC = "SELECT "
                                                            + COLUMN_NAME_AUTH_USER_ID + ", "
                                                            + COLUMN_NAME_COUNTRY_ID + ", "
                                                            + COLUMN_NAME_PHONE_NUMBER + ", "
                                                            + COLUMN_NAME_SESSION_ID + ", "
                                                            + COLUMN_NAME_LUG_SERVER_ID + ", "
                                                            + COLUMN_NAME_NICKNAME + ", "
                                                            + COLUMN_NAME_LAST_CONNECT_TIME + ", "
                                                            + COLUMN_NAME_LAST_SESSION_TIME + ", "
                                                            + COLUMN_NAME_ADDED_TIME + ", "
                                                            + COLUMN_NAME_SHOW_HIDDEN + ", "
                                                            + COLUMN_NAME_IS_ADMIN + ", "
                                                            + COLUMN_NAME_CONNECTION_STATE   + ", "
                                                            + COLUMN_NAME_CONNECTION_APPROVED  + ", "
                                                            + COLUMN_NAME_ALLOW_ALIAS
                                                            + " FROM "
                                                            + TABLE_NAME_AUTH_USER
                                                            + " ORDER BY "
                                                            + COLUMN_NAME_IS_ADMIN + " DESC, "
                                                            + COLUMN_NAME_ADDED_TIME + " ASC";

    /* find all users */
    String SQL_FIND_NON_ADMIN_USERS = "SELECT "
                                      + COLUMN_NAME_AUTH_USER_ID + ", "
                                      + COLUMN_NAME_COUNTRY_ID + ", "
                                      + COLUMN_NAME_PHONE_NUMBER + ", "
                                      + COLUMN_NAME_SESSION_ID + ", "
                                      + COLUMN_NAME_LUG_SERVER_ID + ", "
                                      + COLUMN_NAME_NICKNAME + ", "
                                      + COLUMN_NAME_LAST_CONNECT_TIME + ", "
                                      + COLUMN_NAME_LAST_SESSION_TIME + ", "
                                      + COLUMN_NAME_ADDED_TIME + ", "
                                      + COLUMN_NAME_SHOW_HIDDEN + ", "
                                      + COLUMN_NAME_IS_ADMIN + ", "
                                      + COLUMN_NAME_CONNECTION_STATE   + ", "
                                      + COLUMN_NAME_CONNECTION_APPROVED + ", "
                                      + COLUMN_NAME_ALLOW_ALIAS
                                      + " FROM "
                                      + TABLE_NAME_AUTH_USER
                                      + " WHERE "
                                      + COLUMN_NAME_IS_ADMIN
                                      + " = false ORDER BY "
                                      + COLUMN_NAME_ADDED_TIME + " ASC";

    /* find users with specified states */
    /* UNNEST(?) is the way for HSQLDB to support WHERE IN clause */
    String SQL_FIND_AUTH_USERS_WITH_STATES_ORDER_BY_IS_ADMIN_DESC = "SELECT "
                                                                    + COLUMN_NAME_AUTH_USER_ID + ", "
                                                                    + COLUMN_NAME_COUNTRY_ID + ", "
                                                                    + COLUMN_NAME_PHONE_NUMBER + ", "
                                                                    + COLUMN_NAME_SESSION_ID + ", "
                                                                    + COLUMN_NAME_LUG_SERVER_ID + ", "
                                                                    + COLUMN_NAME_NICKNAME + ", "
                                                                    + COLUMN_NAME_LAST_CONNECT_TIME + ", "
                                                                    + COLUMN_NAME_LAST_SESSION_TIME + ", "
                                                                    + COLUMN_NAME_ADDED_TIME + ", "
                                                                    + COLUMN_NAME_SHOW_HIDDEN + ", "
                                                                    + COLUMN_NAME_IS_ADMIN + ", "
                                                                    + COLUMN_NAME_CONNECTION_STATE + ", "
                                                                    + COLUMN_NAME_CONNECTION_APPROVED + ", "
                                                                    + COLUMN_NAME_ALLOW_ALIAS
                                                                    + " FROM "
                                                                    + TABLE_NAME_AUTH_USER
                                                                    + " WHERE "
                                                                    + COLUMN_NAME_CONNECTION_STATE
                                                                    + " IN ( UNNEST(?) ) ORDER BY "
                                                                    + COLUMN_NAME_IS_ADMIN + " DESC, "
                                                                    + COLUMN_NAME_ADDED_TIME + " ASC";

    /* find by is administrator */
    String SQL_FIND_AUTH_USER_BY_IS_ADMIN = "SELECT "
                                            + COLUMN_NAME_AUTH_USER_ID + ", "
                                            + COLUMN_NAME_COUNTRY_ID + ", "
                                            + COLUMN_NAME_PHONE_NUMBER + ", "
                                            + COLUMN_NAME_SESSION_ID + ", "
                                            + COLUMN_NAME_LUG_SERVER_ID + ", "
                                            + COLUMN_NAME_NICKNAME + ", "
                                            + COLUMN_NAME_LAST_CONNECT_TIME + ", "
                                            + COLUMN_NAME_LAST_SESSION_TIME + ", "
                                            + COLUMN_NAME_ADDED_TIME + ", "
                                            + COLUMN_NAME_SHOW_HIDDEN + ", "
                                            + COLUMN_NAME_IS_ADMIN + ", "
                                            + COLUMN_NAME_CONNECTION_STATE + ", "
                                            + COLUMN_NAME_CONNECTION_APPROVED + ", "
                                            + COLUMN_NAME_ALLOW_ALIAS
                                            + " FROM "
                                            + TABLE_NAME_AUTH_USER
                                            + " WHERE "
                                            + COLUMN_NAME_IS_ADMIN + " = ?";

    String SQL_FIND_IF_AUTH_USER_ADMIN = "SELECT "
                                         + COLUMN_NAME_AUTH_USER_ID + ", "
                                         + COLUMN_NAME_COUNTRY_ID + ", "
                                         + COLUMN_NAME_PHONE_NUMBER + ", "
                                         + COLUMN_NAME_SESSION_ID + ", "
                                         + COLUMN_NAME_LUG_SERVER_ID + ", "
                                         + COLUMN_NAME_NICKNAME + ", "
                                         + COLUMN_NAME_LAST_CONNECT_TIME + ", "
                                         + COLUMN_NAME_LAST_SESSION_TIME + ", "
                                         + COLUMN_NAME_ADDED_TIME + ", "
                                         + COLUMN_NAME_SHOW_HIDDEN + ", "
                                         + COLUMN_NAME_IS_ADMIN + ", "
                                         + COLUMN_NAME_CONNECTION_STATE + ", "
                                         + COLUMN_NAME_CONNECTION_APPROVED + ", "
                                         + COLUMN_NAME_ALLOW_ALIAS
                                         + " FROM "
                                         + TABLE_NAME_AUTH_USER
                                         + " WHERE "
                                         + COLUMN_NAME_AUTH_USER_ID + " = ? AND "
                                         + COLUMN_NAME_IS_ADMIN + " = true";

    /* find user by id */
    String SQL_FIND_USER_EXISTS_BY_ID = "SELECT "
                                        + COLUMN_NAME_AUTH_USER_ID
                                        + " FROM "
                                        + TABLE_NAME_AUTH_USER
                                        + " WHERE "
                                        + COLUMN_NAME_AUTH_USER_ID + " = ?";

    /* find allow-alias by id */
    String SQL_FIND_ALLOW_ALIAS_BY_ID = "SELECT "
                                        + COLUMN_NAME_ALLOW_ALIAS
                                        + " FROM "
                                        + TABLE_NAME_AUTH_USER
                                        + " WHERE "
                                        + COLUMN_NAME_AUTH_USER_ID + " = ?";

    /* create user */
    String SQL_CREATE_AUTH_USER = "INSERT INTO "
                                  + TABLE_NAME_AUTH_USER + "("
                                  + COLUMN_NAME_AUTH_USER_ID + ", "
                                  + COLUMN_NAME_COUNTRY_ID + ", "
                                  + COLUMN_NAME_PHONE_NUMBER + ", "
                                  + COLUMN_NAME_SESSION_ID + ", "
                                  + COLUMN_NAME_LUG_SERVER_ID + ", "
                                  + COLUMN_NAME_NICKNAME + ", "
                                  + COLUMN_NAME_LAST_CONNECT_TIME + ", "
                                  + COLUMN_NAME_LAST_SESSION_TIME + ", "
                                  + COLUMN_NAME_ADDED_TIME + ", "
                                  + COLUMN_NAME_SHOW_HIDDEN + ", "
                                  + COLUMN_NAME_IS_ADMIN + ", "
                                  + COLUMN_NAME_CONNECTION_STATE + ", "
                                  + COLUMN_NAME_CONNECTION_APPROVED+ ", "
                                  + COLUMN_NAME_ALLOW_ALIAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

//    /* clear all data in table user */
//    String SQL_TRUNCATE_AUTH_USER = "TRUNCATE TABLE " + TABLE_NAME_AUTH_USER;
//
//    /* update show Hidden by user id */
//    String SQL_UPDATE_SHOW_HIDDEN_BY_ID = "UPDATE "
//                                          + TABLE_NAME_AUTH_USER
//                                          + " SET "
//                                          + COLUMN_NAME_SHOW_HIDDEN
//                                          + " = ? WHERE "
//                                          + COLUMN_NAME_AUTH_USER_ID + " = ?";
//
//    /* update nickname by user id */
//    String SQL_UPDATE_USER_NICKNAME_BY_ID = "UPDATE "
//                                            + TABLE_NAME_AUTH_USER
//                                            + " SET "
//                                            + COLUMN_NAME_NICKNAME
//                                            + " = ? WHERE "
//                                            + COLUMN_NAME_AUTH_USER_ID + " = ?";

    /* update user */
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

    /* update user session id */
    String SQL_UPDATE_USER_SESSION = "UPDATE "
                                     + TABLE_NAME_AUTH_USER + " SET "
                                     + COLUMN_NAME_SESSION_ID + " = ?, "
                                     + COLUMN_NAME_LAST_SESSION_TIME + " = ? WHERE "
                                     + COLUMN_NAME_AUTH_USER_ID + " = ?";

//    /* update user w/o password */
//    String SQL_UPDATE_USER_WITHOUT_PASSWD = "UPDATE "
//                                            + TABLE_NAME_AUTH_USER + " SET "
//                                            + COLUMN_NAME_COUNTRY_ID + " = ?, "
//                                            + COLUMN_NAME_PHONE_NUMBER + " = ?, "
//                                            + COLUMN_NAME_NICKNAME + " = ?, "
//                                            + COLUMN_NAME_LAST_CONNECT_TIME + " = ?, "
//                                            + COLUMN_NAME_SHOW_HIDDEN + " = ?, "
//                                            + COLUMN_NAME_IS_ADMIN + " = ?, "
//                                            + COLUMN_NAME_CONNECTION_STATE + " = ?, "
//                                            + COLUMN_NAME_CONNECTION_APPROVED+ " = ?, "
//                                            + COLUMN_NAME_ALLOW_ALIAS + " = ? WHERE "
//                                            + COLUMN_NAME_AUTH_USER_ID + " = ?";
//
//    /* update user w/o password */
//    String SQL_UPDATE_USER_WITH_PASSWD = "UPDATE "
//                                         + TABLE_NAME_AUTH_USER + " SET "
//                                         + COLUMN_NAME_COUNTRY_ID + " = ?, "
//                                         + COLUMN_NAME_PHONE_NUMBER + " = ?, "
//                                         + COLUMN_NAME_NICKNAME + " = ?, "
//                                         + COLUMN_NAME_PASSWD + " = ?, "
//                                         + COLUMN_NAME_LAST_CONNECT_TIME + " = ?, "
//                                         + COLUMN_NAME_SHOW_HIDDEN + " = ?, "
//                                         + COLUMN_NAME_IS_ADMIN + " = ?, "
//                                         + COLUMN_NAME_CONNECTION_STATE + " = ?, "
//                                         + COLUMN_NAME_CONNECTION_APPROVED+ " = ?, "
//                                         + COLUMN_NAME_ALLOW_ALIAS + " = ? WHERE "
//                                         + COLUMN_NAME_AUTH_USER_ID + " = ?";

    /* update user state by user id */
    String SQL_UPDATE_USER_STATE_BY_ID = "UPDATE "
                                         + TABLE_NAME_AUTH_USER
                                         + " SET "
                                         + COLUMN_NAME_CONNECTION_STATE
                                         + " = ? WHERE "
                                         + COLUMN_NAME_AUTH_USER_ID + " = ?";

    /* delete user by user id */
    String SQL_DELETE_USER_BY_ID = "DELETE FROM "
                                   + TABLE_NAME_AUTH_USER
                                   + " WHERE "
                                   + COLUMN_NAME_AUTH_USER_ID + " = ?";

    /* truncate all tables */
    String SQL_TRUNCATE_ALL_TABLES = "TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK";

    /* DDL - TABLE: ROOT_DIRECTORY */

//    String SQL_CREATE_TABLE_ROOT_DIRECTORY = "CREATE TABLE "
//                                             + TABLE_NAME_ROOT_DIRECTORY + " ("
//                                             + COLUMN_NAME_ROOT_DIRECTORY_ID + " BIGINT PRIMARY KEY, "
//                                             + COLUMN_NAME_ROOT_DIRECTORY_PATH + " VARCHAR(1024) NOT NULL, "
//                                             + COLUMN_NAME_ROOT_DIRECTORY_LABEL + " VARCHAR(1024) NOT NULL, "
//                                             + COLUMN_NAME_ROOT_DIRECTORY_USER + " VARCHAR(1024) NOT NULL)";
//
//    String SQL_CREATE_INDEX_ROOT_DIRECTORY_PATH = "CREATE INDEX " + INDEX_NAME_ROOT_DIRECTORY_PATH + " ON " + TABLE_NAME_ROOT_DIRECTORY + "(" + COLUMN_NAME_ROOT_DIRECTORY_PATH + ")";
//
//    String SQL_CREATE_INDEX_ROOT_DIRECTORY_LABEL = "CREATE INDEX " + INDEX_NAME_ROOT_DIRECTORY_LABEL + " ON " + TABLE_NAME_ROOT_DIRECTORY + "(" + COLUMN_NAME_ROOT_DIRECTORY_LABEL + ")";
//
//    String SQL_CREATE_FOREIGN_KEY_ROOT_DIRECTORY_USER = "ALTER TABLE " + TABLE_NAME_ROOT_DIRECTORY + " ADD FOREIGN KEY (" + COLUMN_NAME_ROOT_DIRECTORY_USER + ") REFERENCES " + TABLE_NAME_AUTH_USER + "(" + COLUMN_NAME_AUTH_USER_ID + ") ON DELETE CASCADE";
//
//    /* find all rootDirectories of the specified user */
//    String SQL_FIND_ALL_ROOT_DIRECTORIES_ORDER_BY_ID_DESC = "SELECT "
//                                                            + COLUMN_NAME_ROOT_DIRECTORY_ID + ", "
//                                                            + COLUMN_NAME_ROOT_DIRECTORY_PATH + ", "
//                                                            + COLUMN_NAME_ROOT_DIRECTORY_LABEL + ", "
//                                                            + COLUMN_NAME_ROOT_DIRECTORY_USER
//                                                            + " FROM "
//                                                            + TABLE_NAME_ROOT_DIRECTORY
//                                                            + " WHERE "
//                                                            + COLUMN_NAME_ROOT_DIRECTORY_USER
//                                                            + " = ? ORDER BY "
//                                                            + COLUMN_NAME_ROOT_DIRECTORY_ID + " DESC";
//
//    /* find root directory ids for the specified user */
//    String SQL_FIND_ROOT_DIRECTORY_IDS_BY_USER_ID = "SELECT "
//                                                    + COLUMN_NAME_ROOT_DIRECTORY_ID
//                                                    + " FROM "
//                                                    + TABLE_NAME_ROOT_DIRECTORY
//                                                    + " WHERE "
//                                                    + COLUMN_NAME_ROOT_DIRECTORY_USER + " = ?";
//
//    /* find rootDirectory count by path */
//    String SQL_FIND_ROOT_DIRECTORY_COUNT_BY_ID = "SELECT COUNT(*) FROM "
//                                                 + TABLE_NAME_ROOT_DIRECTORY
//                                                 + " WHERE "
//                                                 + COLUMN_NAME_ROOT_DIRECTORY_USER
//                                                 + " = ? AND "
//                                                 + COLUMN_NAME_ROOT_DIRECTORY_ID + " = ?";
//
//    /* find rootDirectory count by path */
//    String SQL_FIND_ROOT_DIRECTORY_COUNT_BY_PATH = "SELECT COUNT(*) FROM "
//                                                   + TABLE_NAME_ROOT_DIRECTORY
//                                                   + " WHERE "
//                                                   + COLUMN_NAME_ROOT_DIRECTORY_USER
//                                                   + " = ? AND "
//                                                   + COLUMN_NAME_ROOT_DIRECTORY_PATH + " = ?";
//
//    /* find rootDirectory count by path */
//    String SQL_FIND_ROOT_DIRECTORY_COUNT_BY_PTH_EXCEPT_SELF = "SELECT COUNT(*) FROM "
//                                                              + TABLE_NAME_ROOT_DIRECTORY
//                                                              + " WHERE "
//                                                              + COLUMN_NAME_ROOT_DIRECTORY_PATH
//                                                              + " = ? AND "
//                                                              + COLUMN_NAME_ROOT_DIRECTORY_USER
//                                                              + " = ? AND "
//                                                              + COLUMN_NAME_ROOT_DIRECTORY_ID + " != ?";
//
//    /* find rootDirectory count by path */
//    String SQL_FIND_ROOT_DIRECTORY_COUNT_BY_LABEL = "SELECT COUNT(*) FROM "
//                                                    + TABLE_NAME_ROOT_DIRECTORY
//                                                    + " WHERE "
//                                                    + COLUMN_NAME_ROOT_DIRECTORY_USER
//                                                    + " = ? AND "
//                                                    + COLUMN_NAME_ROOT_DIRECTORY_LABEL + " = ?";
//
//    /* find rootDirectory count by path */
//    String SQL_FIND_ROOT_DIRECTORY_COUNT_BY_LABEL_EXCEPT_SELF = "SELECT COUNT(*) FROM "
//                                                                + TABLE_NAME_ROOT_DIRECTORY
//                                                                + " WHERE "
//                                                                + COLUMN_NAME_ROOT_DIRECTORY_LABEL
//                                                                + " = ? AND "
//                                                                + COLUMN_NAME_ROOT_DIRECTORY_USER
//                                                                + " = ? AND "
//                                                                + COLUMN_NAME_ROOT_DIRECTORY_ID + " != ?";
//
//    /* create rootDirectory */
//    String SQL_CREATE_ROOT_DIRECTORY = "INSERT INTO "
//                                       + TABLE_NAME_ROOT_DIRECTORY + "("
//                                       + COLUMN_NAME_ROOT_DIRECTORY_ID + ", "
//                                       + COLUMN_NAME_ROOT_DIRECTORY_PATH + ", "
//                                       + COLUMN_NAME_ROOT_DIRECTORY_LABEL + ", "
//                                       + COLUMN_NAME_ROOT_DIRECTORY_USER + ") VALUES(?, ?, ?, ?)";
//
//    /* find rootDirectory by path */
//    String SQL_FIND_ROOT_DIRECTORY_BY_PTH = "SELECT "
//                                            + COLUMN_NAME_ROOT_DIRECTORY_ID + ", "
//                                            + COLUMN_NAME_ROOT_DIRECTORY_PATH + ", "
//                                            + COLUMN_NAME_ROOT_DIRECTORY_LABEL + ", "
//                                            + COLUMN_NAME_ROOT_DIRECTORY_USER
//                                            + " FROM "
//                                            + TABLE_NAME_ROOT_DIRECTORY
//                                            + " WHERE "
//                                            + COLUMN_NAME_ROOT_DIRECTORY_USER
//                                            + " = ? AND "
//                                            + COLUMN_NAME_ROOT_DIRECTORY_PATH + " = ?";
//
//    /* find rootDirectory by id */
//    String SQL_FIND_ROOT_DIRECTORY_BY_ID = "SELECT "
//                                           + COLUMN_NAME_ROOT_DIRECTORY_ID + ", "
//                                           + COLUMN_NAME_ROOT_DIRECTORY_PATH + ", "
//                                           + COLUMN_NAME_ROOT_DIRECTORY_LABEL + ", "
//                                           + COLUMN_NAME_ROOT_DIRECTORY_USER
//                                           + " FROM "
//                                           + TABLE_NAME_ROOT_DIRECTORY
//                                           + " WHERE "
//                                           + COLUMN_NAME_ROOT_DIRECTORY_USER
//                                           + " = ? AND "
//                                           + COLUMN_NAME_ROOT_DIRECTORY_ID + " = ?";
//
//    String SQL_FIND_DISTINCT_USER_IDS_WITH_ROOT_DIRECTORIES_ORDER_BY_ID_ASC = "SELECT DISTINCT("
//                                                                              + COLUMN_NAME_ROOT_DIRECTORY_USER
//                                                                              + ") FROM "
//                                                                              + TABLE_NAME_ROOT_DIRECTORY;
//
//    /* update rootDirectory */
//    String SQL_UPDATE_ROOT_DIRECTORY = "UPDATE "
//                                       + TABLE_NAME_ROOT_DIRECTORY + " SET "
//                                       + COLUMN_NAME_ROOT_DIRECTORY_PATH + " = ?, "
//                                       + COLUMN_NAME_ROOT_DIRECTORY_LABEL
//                                       + " = ? WHERE "
//                                       + COLUMN_NAME_ROOT_DIRECTORY_ID + " = ?";
//
//    /* delete rootDirectory by id */
//    String SQL_DELETE_ROOT_DIRECTORY_BY_ID = "DELETE FROM "
//                                             + TABLE_NAME_ROOT_DIRECTORY
//                                             + " WHERE "
//                                             + COLUMN_NAME_ROOT_DIRECTORY_ID + " = ?";

    // Status of creating file upload group, coming from repository DatabaseConstants

    String CREATED_IN_DESKTOP_STATUS_SUCCESS = "success";

    String CREATED_IN_DESKTOP_STATUS_FAILURE = "failure";


    // TABLE: FILE_UPLOADED related

    String TABLE_NAME_FILE_TRANSFER_IN = "FILE_UPLOADED";

    String COLUMN_NAME_TRANSFER_IN_KEY = "UPLOAD_KEY";

    String COLUMN_NAME_FILENAME = "FILENAME";

    String COLUMN_NAME_DIRECTORY = "DIRECTORY";

    String COLUMN_NAME_FILE_SIZE = "FILE_SIZE";

    String COLUMN_NAME_LAST_MODIFIED_DATE = "LAST_MODIFIED_DATE";

    String COLUMN_NAME_START_TIMESTAMP = "START_TIMESTAMP";

    String COLUMN_NAME_END_TIMESTAMP = "END_TIMESTAMP";

    String COLUMN_NAME_STATUS = "STATUS";

    String INDEX_NAME_FILE_TRANSFER_IN_END_TIMESTAMP = "INDEX_D_FILE_UPLOADED_END_TIMESTAMP";

    String TRANSFER_STATUS_PROCESSING = "processing";

    String TRANSFER_STATUS_SUCCESS = "success";

    String TRANSFER_STATUS_FAILURE = "failure";


    // DDL - TABLE: FILE_UPLOADED (For transferred-in files)

    String SQL_CREATE_TABLE_FILE_TRANSFER_IN = "CREATE TABLE "
                                               + TABLE_NAME_FILE_TRANSFER_IN + "("
                                               + COLUMN_NAME_TRANSFER_IN_KEY + " VARCHAR(1024) PRIMARY KEY, "
                                               + COLUMN_NAME_AUTH_USER_ID + " VARCHAR(1024) NOT NULL, "
                                               + COLUMN_NAME_FILENAME + " VARCHAR(1024) NOT NULL, "
                                               + COLUMN_NAME_DIRECTORY + " VARCHAR(1024) NOT NULL, "
                                               + COLUMN_NAME_FILE_SIZE + " BIGINT DEFAULT 0, "
                                               + COLUMN_NAME_START_TIMESTAMP + " BIGINT DEFAULT 0, "
                                               + COLUMN_NAME_END_TIMESTAMP + " BIGINT DEFAULT 0, "
                                               + COLUMN_NAME_STATUS + " VARCHAR(1024))";

    String SQL_CREATE_INDEX_FILE_TRANSFER_IN_END_TIMESTAMP = "CREATE INDEX " + INDEX_NAME_FILE_TRANSFER_IN_END_TIMESTAMP + " ON " + TABLE_NAME_FILE_TRANSFER_IN + "(" + COLUMN_NAME_END_TIMESTAMP + ")";

    String SQL_CREATE_FOREIGN_KEY_FILE_TRANSFER_IN_USER = "ALTER TABLE " + TABLE_NAME_FILE_TRANSFER_IN + " ADD FOREIGN KEY (" + COLUMN_NAME_AUTH_USER_ID + ") REFERENCES " + TABLE_NAME_AUTH_USER + "(" + COLUMN_NAME_AUTH_USER_ID + ") ON DELETE CASCADE";

    // find a uploaded-file by uploadKey
    String SQL_FIND_FILE_TRANSFER_IN_BY_TRANSFER_KEY = "SELECT "
                                                       + COLUMN_NAME_TRANSFER_IN_KEY + ", "
                                                       + COLUMN_NAME_AUTH_USER_ID + ", "
                                                       + COLUMN_NAME_FILENAME + ", "
                                                       + COLUMN_NAME_DIRECTORY + ", "
                                                       + COLUMN_NAME_FILE_SIZE + ", "
                                                       + COLUMN_NAME_START_TIMESTAMP + ", "
                                                       + COLUMN_NAME_END_TIMESTAMP + ", "
                                                       + COLUMN_NAME_STATUS
                                                       + " FROM "
                                                       + TABLE_NAME_FILE_TRANSFER_IN
                                                       + " WHERE "
                                                       + COLUMN_NAME_TRANSFER_IN_KEY + " = ?";

    /* create FILE_UPLOADED */
    String SQL_CREATE_FILE_TRANSFER_IN = "INSERT INTO "
                                         + TABLE_NAME_FILE_TRANSFER_IN + "("
                                         + COLUMN_NAME_TRANSFER_IN_KEY + ", "
                                         + COLUMN_NAME_AUTH_USER_ID + ", "
                                         + COLUMN_NAME_FILENAME + ", "
                                         + COLUMN_NAME_DIRECTORY + ", "
                                         + COLUMN_NAME_FILE_SIZE + ", "
                                         + COLUMN_NAME_START_TIMESTAMP + ", "
                                         + COLUMN_NAME_END_TIMESTAMP + ", "
                                         + COLUMN_NAME_STATUS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    /* update file transferred-in by transfer key for column end timestamp, file size and status */
    String SQL_UPDATE_FILE_TRANSFER_IN = "UPDATE "
                                         + TABLE_NAME_FILE_TRANSFER_IN
                                         + " SET "
                                         + COLUMN_NAME_STATUS
                                         + " = ?, "
                                         + COLUMN_NAME_FILE_SIZE
                                         + " = ?, "
                                         + COLUMN_NAME_END_TIMESTAMP
                                         + " = ? WHERE "
                                         + COLUMN_NAME_TRANSFER_IN_KEY + " = ?";

    String SQL_UPDATE_FILE_TRANSFER_IN2 = "UPDATE "
                                          + TABLE_NAME_FILE_TRANSFER_IN
                                          + " SET "
                                          + COLUMN_NAME_AUTH_USER_ID
                                          + " = ?, "
                                          + COLUMN_NAME_FILENAME
                                          + " = ?, "
                                          + COLUMN_NAME_DIRECTORY
                                          + " = ?, "
                                          + COLUMN_NAME_FILE_SIZE
                                          + " = ?, "
                                          + COLUMN_NAME_START_TIMESTAMP
                                          + " = ?, "
                                          + COLUMN_NAME_END_TIMESTAMP
                                          + " = ?, "
                                          + COLUMN_NAME_STATUS
                                          + " = ? WHERE "
                                          + COLUMN_NAME_TRANSFER_IN_KEY + " = ?";

    /* update file transferred-in by transfer key for column end timestamp and status */
    String SQL_UPDATE_FILE_TRANSFER_IN_WITHOUT_FILE_SIZE = "UPDATE "
                                                           + TABLE_NAME_FILE_TRANSFER_IN
                                                           + " SET "
                                                           + COLUMN_NAME_STATUS
                                                           + " = ?, "
                                                           + COLUMN_NAME_END_TIMESTAMP
                                                           + " = ? WHERE "
                                                           + COLUMN_NAME_TRANSFER_IN_KEY + " = ?";

    /* update file transferred-in by transfer key for column file size */
    String SQL_UPDATE_FILE_TRANSFER_IN_SIZE = "UPDATE "
                                              + TABLE_NAME_FILE_TRANSFER_IN
                                              + " SET "
                                              + COLUMN_NAME_FILE_SIZE
                                              + " = ? WHERE "
                                              + COLUMN_NAME_TRANSFER_IN_KEY + " = ?";

    /* update the status of transferred-in files from processing to failure by timeout startTimestamp */
    String SQL_UPDATE_FILE_TRANSFER_IN_STATUS_PROCESSING_TO_FAILURE_FOR_TIMEOUT_START_TIMESTAMP = "UPDATE "
                                                                                                  + TABLE_NAME_FILE_TRANSFER_IN
                                                                                                  + " SET "
                                                                                                  + COLUMN_NAME_STATUS
                                                                                                  + " = '"
                                                                                                  + TRANSFER_STATUS_FAILURE
                                                                                                  + "' WHERE "
                                                                                                  + COLUMN_NAME_STATUS
                                                                                                  + " = '"
                                                                                                  + TRANSFER_STATUS_PROCESSING
                                                                                                  + "' AND "
                                                                                                  + COLUMN_NAME_START_TIMESTAMP + " < ?";

    /* find transferred-in files by user, order by start timestamp desc */
    String SQL_FIND_ALL_FILE_TRANSFER_IN_BY_USER = "SELECT "
                                                   + COLUMN_NAME_TRANSFER_IN_KEY + ", "
                                                   + COLUMN_NAME_AUTH_USER_ID + ", "
                                                   + COLUMN_NAME_FILENAME + ", "
                                                   + COLUMN_NAME_DIRECTORY + ", "
                                                   + COLUMN_NAME_FILE_SIZE + ", "
                                                   + COLUMN_NAME_START_TIMESTAMP + ", "
                                                   + COLUMN_NAME_END_TIMESTAMP + ", "
                                                   + COLUMN_NAME_STATUS
                                                   + " FROM "
                                                   + TABLE_NAME_FILE_TRANSFER_IN
                                                   + " WHERE "
                                                   + COLUMN_NAME_AUTH_USER_ID
                                                   + " = ? ORDER BY "
                                                   + COLUMN_NAME_START_TIMESTAMP + " DESC";

    String SQL_FIND_SUCCESS_FILE_TRANSFER_IN_BY_USER = "SELECT "
                                                       + COLUMN_NAME_TRANSFER_IN_KEY + ", "
                                                       + COLUMN_NAME_AUTH_USER_ID + ", "
                                                       + COLUMN_NAME_FILENAME + ", "
                                                       + COLUMN_NAME_DIRECTORY + ", "
                                                       + COLUMN_NAME_FILE_SIZE + ", "
                                                       + COLUMN_NAME_START_TIMESTAMP + ", "
                                                       + COLUMN_NAME_END_TIMESTAMP + ", "
                                                       + COLUMN_NAME_STATUS
                                                       + " FROM "
                                                       + TABLE_NAME_FILE_TRANSFER_IN
                                                       + " WHERE "
                                                       + COLUMN_NAME_AUTH_USER_ID
                                                       + " = ? AND "
                                                       + COLUMN_NAME_STATUS
                                                       + " = '"
                                                       + TRANSFER_STATUS_SUCCESS
                                                       + "' ORDER BY "
                                                       + COLUMN_NAME_END_TIMESTAMP + " DESC";

    /* find the transferred-in files by timeout startTimestamp */
    String SQL_FIND_PROCESSING_FILE_TRANSFER_IN_BY_TIMEOUT_START_TIMESTAMP = "SELECT "
                                                                             + COLUMN_NAME_TRANSFER_IN_KEY + ", "
                                                                             + COLUMN_NAME_AUTH_USER_ID + ", "
                                                                             + COLUMN_NAME_FILENAME + ", "
                                                                             + COLUMN_NAME_DIRECTORY + ", "
                                                                             + COLUMN_NAME_FILE_SIZE + ", "
                                                                             + COLUMN_NAME_START_TIMESTAMP + ", "
                                                                             + COLUMN_NAME_END_TIMESTAMP + ", "
                                                                             + COLUMN_NAME_STATUS
                                                                             + " FROM "
                                                                             + TABLE_NAME_FILE_TRANSFER_IN
                                                                             + " WHERE "
                                                                             + COLUMN_NAME_STATUS
                                                                             + " = '"
                                                                             + TRANSFER_STATUS_PROCESSING
                                                                             + "' AND "
                                                                             + COLUMN_NAME_START_TIMESTAMP + " < ?";

    /* find size of the transferred-in file by transfer key */
    String SQL_FIND_FILE_TRANSFER_IN_SIZE_BY_TRANSFER_KEY = "SELECT "
                                                            + COLUMN_NAME_FILE_SIZE
                                                            + " FROM "
                                                            + TABLE_NAME_FILE_TRANSFER_IN
                                                            + " WHERE "
                                                            + COLUMN_NAME_TRANSFER_IN_KEY + " = ?";

    /* find file size of the transferred-in file by transfer key */
    String SQL_FIND_SUM_FILE_TRANSFER_IN_SIZE_BY_USER = "SELECT SUM("
                                                        + COLUMN_NAME_FILE_SIZE
                                                        + ") FROM "
                                                        + TABLE_NAME_FILE_TRANSFER_IN
                                                        + " WHERE "
                                                        + COLUMN_NAME_AUTH_USER_ID
                                                        + " = ? AND "
                                                        + COLUMN_NAME_STATUS
                                                        + " = '"
                                                        + TRANSFER_STATUS_PROCESSING + "'";

    /* check existing by transfer key */
    String SQL_COUNT_FILE_TRANSFER_IN_BY_TRANSFER_KEY = "SELECT count(*) FROM "
                                                        + TABLE_NAME_FILE_TRANSFER_IN
                                                        + " WHERE "
                                                        + COLUMN_NAME_TRANSFER_IN_KEY + " = ?";

    // find if file exists by transfer key
    String SQL_FIND_IF_EXISTS_FILE_TRANSFER_IN_BY_TRANSFER_KEY = "SELECT "
                                                                 + COLUMN_NAME_TRANSFER_IN_KEY
                                                                 + " FROM "
                                                                 + TABLE_NAME_FILE_TRANSFER_IN
                                                                 + " WHERE "
                                                                 + COLUMN_NAME_TRANSFER_IN_KEY + " = ?";

    // delete transferred-in file by transfer key
    String SQL_DELETE_FILE_TRANSFER_IN_BY_TRANSFER_KEY = "DELETE FROM "
                                                         + TABLE_NAME_FILE_TRANSFER_IN
                                                         + " WHERE "
                                                         + COLUMN_NAME_TRANSFER_IN_KEY + " = ?";


    /* TABLE: FILE_DOWNLOADED related */

    String TABLE_NAME_FILE_TRANSFER_OUT = "FILE_DOWNLOADED";

    String COLUMN_NAME_TRANSFER_OUT_KEY = "DOWNLOAD_KEY";

    String COLUMN_NAME_FILE_PATH = "FILE_PATH";

    String INDEX_NAME_FILE_TRANSFER_OUT_END_TIMESTAMP = "INDEX_D_FILE_DOWNLOADED_END_TIMESTAMP";

    /* DDL - TABLE: FILE_DOWNLOADED */

    String SQL_CREATE_TABLE_FILE_TRANSFER_OUT = "CREATE TABLE "
                                                + TABLE_NAME_FILE_TRANSFER_OUT + "("
                                                + COLUMN_NAME_TRANSFER_OUT_KEY + " VARCHAR(1024) PRIMARY KEY, "
                                                + COLUMN_NAME_AUTH_USER_ID + " VARCHAR(1024) NOT NULL, "
                                                + COLUMN_NAME_FILE_PATH + " VARCHAR(1024) NOT NULL, "
                                                + COLUMN_NAME_FILE_SIZE + " BIGINT DEFAULT 0, "
                                                + COLUMN_NAME_LAST_MODIFIED_DATE + " BIGINT DEFAULT 0 NULL, "
                                                + COLUMN_NAME_START_TIMESTAMP + " BIGINT DEFAULT 0, "
                                                + COLUMN_NAME_END_TIMESTAMP + " BIGINT DEFAULT 0, "
                                                + COLUMN_NAME_STATUS + " VARCHAR(1024))";

    String SQL_CREATE_INDEX_FILE_TRANSFER_OUT_END_TIMESTAMP = "CREATE INDEX " + INDEX_NAME_FILE_TRANSFER_OUT_END_TIMESTAMP + " ON " + TABLE_NAME_FILE_TRANSFER_OUT + "(" + COLUMN_NAME_END_TIMESTAMP + ")";

    String SQL_CREATE_FOREIGN_KEY_FILE_TRANSFER_OUT_USER = "ALTER TABLE " + TABLE_NAME_FILE_TRANSFER_OUT + " ADD FOREIGN KEY (" + COLUMN_NAME_AUTH_USER_ID + ") REFERENCES " + TABLE_NAME_AUTH_USER + "(" + COLUMN_NAME_AUTH_USER_ID + ") ON DELETE CASCADE";

    // add COLUMN_NAME_LAST_MODIFIED_DATE and its index if column not found
    String SQL_ADD_COLUMN_FILE_TRANSFER_OUT_LAST_MODIFIED_DATE = "ALTER TABLE " + TABLE_NAME_FILE_TRANSFER_OUT + " ADD " + COLUMN_NAME_LAST_MODIFIED_DATE + " BIGINT DEFAULT 0 NULL";

    /* count a transferred-out file by transfer key */
    String SQL_COUNT_FILE_TRANSFER_OUT_BY_TRANSFER_KEY = "SELECT count(*) FROM " + TABLE_NAME_FILE_TRANSFER_OUT + " WHERE " + COLUMN_NAME_TRANSFER_OUT_KEY + " = ?";

    /* find a transferred-out file by transfer key */
    String SQL_FIND_FILE_TRANSFER_OUT_BY_TRANSFER_KEY = "SELECT "
                                                        + COLUMN_NAME_TRANSFER_OUT_KEY + ", "
                                                        + COLUMN_NAME_AUTH_USER_ID + ", "
                                                        + COLUMN_NAME_FILE_PATH + ", "
                                                        + COLUMN_NAME_FILE_SIZE + ", "
                                                        + COLUMN_NAME_LAST_MODIFIED_DATE + ", "
                                                        + COLUMN_NAME_START_TIMESTAMP + ", "
                                                        + COLUMN_NAME_END_TIMESTAMP + ", "
                                                        + COLUMN_NAME_STATUS
                                                        + " FROM "
                                                        + TABLE_NAME_FILE_TRANSFER_OUT
                                                        + " WHERE "
                                                        + COLUMN_NAME_TRANSFER_OUT_KEY + " = ?";

    /* find if transferred-out file exists by transfer key */
    String SQL_FIND_IF_EXISTS_FILE_TRANSFER_OUT_BY_TRANSFER_KEY = "SELECT "
                                                                  + COLUMN_NAME_TRANSFER_OUT_KEY
                                                                  + " FROM "
                                                                  + TABLE_NAME_FILE_TRANSFER_OUT
                                                                  + " WHERE "
                                                                  + COLUMN_NAME_TRANSFER_OUT_KEY + " = ?";

    /* create FILE_DOWNLOADED */
    String SQL_CREATE_FILE_TRANSFER_OUT = "INSERT INTO "
                                          + TABLE_NAME_FILE_TRANSFER_OUT + "("
                                          + COLUMN_NAME_TRANSFER_OUT_KEY + ", "
                                          + COLUMN_NAME_AUTH_USER_ID + ", "
                                          + COLUMN_NAME_FILE_PATH + ", "
                                          + COLUMN_NAME_FILE_SIZE + ", "
                                          + COLUMN_NAME_LAST_MODIFIED_DATE + ", "
                                          + COLUMN_NAME_START_TIMESTAMP + ", "
                                          + COLUMN_NAME_END_TIMESTAMP + ", "
                                          + COLUMN_NAME_STATUS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
//    String SQL_CREATE_FILE_TRANSFER_OUT = "INSERT INTO "
//                                          + TABLE_NAME_FILE_TRANSFER_OUT + "("
//                                          + COLUMN_NAME_TRANSFER_OUT_KEY + ", "
//                                          + COLUMN_NAME_AUTH_USER_ID + ", "
//                                          + COLUMN_NAME_FILE_PATH + ", "
//                                          + COLUMN_NAME_START_TIMESTAMP + ", "
//                                          + COLUMN_NAME_END_TIMESTAMP + ", "
//                                          + COLUMN_NAME_STATUS + ") VALUES (?, ?, ?, ?, ?, ?)";

    /* update the status of the transferred-out file by transfer key for column end timestamp and status */
    String SQL_UPDATE_FILE_TRANSFER_OUT_STATUS = "UPDATE "
                                                 + TABLE_NAME_FILE_TRANSFER_OUT
                                                 + " SET "
                                                 + COLUMN_NAME_STATUS
                                                 + " = ?, "
                                                 + COLUMN_NAME_END_TIMESTAMP
                                                 + " = ? WHERE "
                                                 + COLUMN_NAME_TRANSFER_OUT_KEY + " = ?";
//    String SQL_UPDATE_FILE_TRANSFER_OUT = "UPDATE "
//                                          + TABLE_NAME_FILE_TRANSFER_OUT
//                                          + " SET "
//                                          + COLUMN_NAME_STATUS
//                                          + " = ?, "
//                                          + COLUMN_NAME_END_TIMESTAMP
//                                          + " = ?, "
//                                          + COLUMN_NAME_FILE_SIZE
//                                          + " = ? WHERE "
//                                          + COLUMN_NAME_TRANSFER_OUT_KEY + " = ?";

    /* update transferred-out files by transfer key */
    String SQL_UPDATE_FILE_TRANSFER_OUT = "UPDATE "
                                          + TABLE_NAME_FILE_TRANSFER_OUT
                                          + " SET "
                                          + COLUMN_NAME_FILE_SIZE
                                          + " = ?, "
                                          + COLUMN_NAME_LAST_MODIFIED_DATE
                                          + " = ?, "
                                          + COLUMN_NAME_START_TIMESTAMP
                                          + " = ?, "
                                          + COLUMN_NAME_STATUS
                                          + " = ?, "
                                          + COLUMN_NAME_END_TIMESTAMP
                                          + " = ? WHERE "
                                          + COLUMN_NAME_TRANSFER_OUT_KEY + " = ?";

    /* update transferred-out files status from processing to failure by timeout startTimestamp */
    String SQL_UPDATE_FILE_TRANSFER_OUT_STATUS_PROCESSING_TO_FAILURE_FOR_TIMEOUT_START_TIMESTAMP = "UPDATE "
                                                                                                   + TABLE_NAME_FILE_TRANSFER_OUT
                                                                                                   + " SET "
                                                                                                   + COLUMN_NAME_STATUS
                                                                                                   + " = '"
                                                                                                   + TRANSFER_STATUS_FAILURE
                                                                                                   + "' WHERE "
                                                                                                   + COLUMN_NAME_STATUS
                                                                                                   + " = '"
                                                                                                   + TRANSFER_STATUS_PROCESSING
                                                                                                   + "' AND "
                                                                                                   + COLUMN_NAME_START_TIMESTAMP + " < ?";

    /* update transferred-out files by transfer key for column end timestamp and status */
    String SQL_UPDATE_FILE_TRANSFER_OUT_SIZE = "UPDATE "
                                               + TABLE_NAME_FILE_TRANSFER_OUT
                                               + " SET "
                                               + COLUMN_NAME_FILE_SIZE
                                               + " = ? WHERE "
                                               + COLUMN_NAME_TRANSFER_OUT_KEY
                                               + " = ?";

    /* find transferred-out files by user, order by start timestamp desc */
    String SQL_FIND_ALL_FILE_TRANSFER_OUT_BY_USER = "SELECT "
                                                    + COLUMN_NAME_TRANSFER_OUT_KEY + ", "
                                                    + COLUMN_NAME_AUTH_USER_ID + ", "
                                                    + COLUMN_NAME_FILE_PATH + ", "
                                                    + COLUMN_NAME_FILE_SIZE + ", "
                                                    + COLUMN_NAME_LAST_MODIFIED_DATE + ", "
                                                    + COLUMN_NAME_START_TIMESTAMP + ", "
                                                    + COLUMN_NAME_END_TIMESTAMP + ", "
                                                    + COLUMN_NAME_STATUS
                                                    + " FROM "
                                                    + TABLE_NAME_FILE_TRANSFER_OUT
                                                    + " WHERE "
                                                    + COLUMN_NAME_AUTH_USER_ID
                                                    + " = ? ORDER BY "
                                                    + COLUMN_NAME_START_TIMESTAMP + " DESC";

    /* find transferred-out files by user, order by start timestamp desc */
    String SQL_FIND_SUCCESS_FILE_TRANSFER_OUT_BY_USER = "SELECT "
                                                        + COLUMN_NAME_TRANSFER_OUT_KEY + ", "
                                                        + COLUMN_NAME_AUTH_USER_ID + ", "
                                                        + COLUMN_NAME_FILE_PATH + ", "
                                                        + COLUMN_NAME_FILE_SIZE + ", "
                                                        + COLUMN_NAME_LAST_MODIFIED_DATE + ", "
                                                        + COLUMN_NAME_START_TIMESTAMP + ", "
                                                        + COLUMN_NAME_END_TIMESTAMP + ", "
                                                        + COLUMN_NAME_STATUS
                                                        + " FROM "
                                                        + TABLE_NAME_FILE_TRANSFER_OUT
                                                        + " WHERE "
                                                        + COLUMN_NAME_AUTH_USER_ID
                                                        + " = ? AND "
                                                        + COLUMN_NAME_STATUS
                                                        + " = '"
                                                        + TRANSFER_STATUS_SUCCESS
                                                        + "' ORDER BY "
                                                        + COLUMN_NAME_END_TIMESTAMP + " DESC";

    /* find transferred-out files transfer key by timeout startTimestamp */
    String SQL_FIND_PROCESSING_FILE_TRANSFER_OUT_BY_TIMEOUT_START_TIMESTAMP = "SELECT "
                                                                              + COLUMN_NAME_TRANSFER_OUT_KEY + ", "
                                                                              + COLUMN_NAME_AUTH_USER_ID + ", "
                                                                              + COLUMN_NAME_FILE_PATH + ", "
                                                                              + COLUMN_NAME_FILE_SIZE + ", "
                                                                              + COLUMN_NAME_START_TIMESTAMP + ", "
                                                                              + COLUMN_NAME_END_TIMESTAMP + ", "
                                                                              + COLUMN_NAME_STATUS +
                                                                              " FROM "
                                                                              + TABLE_NAME_FILE_TRANSFER_OUT
                                                                              + " WHERE "
                                                                              + COLUMN_NAME_STATUS
                                                                              + " = '"
                                                                              + TRANSFER_STATUS_PROCESSING
                                                                              + "' AND "
                                                                              + COLUMN_NAME_START_TIMESTAMP + " < ?";

    /* find file size of the transferred-out file by transfer key */
    String SQL_FIND_SUM_TRANSFER_OUT_FILE_SIZE_BY_USER = "SELECT SUM("
                                                         + COLUMN_NAME_FILE_SIZE
                                                         + ") FROM "
                                                         + TABLE_NAME_FILE_TRANSFER_OUT
                                                         + " WHERE "
                                                         + COLUMN_NAME_AUTH_USER_ID
                                                         + " = ? AND "
                                                         + COLUMN_NAME_STATUS
                                                         + " = '"
                                                         + TRANSFER_STATUS_PROCESSING + "'";


}
