package com.filelug.desktop.db;

import ch.qos.logback.classic.Logger;
import com.filelug.desktop.OSUtility;
import org.apache.commons.dbcp2.BasicDataSource;
import com.filelug.desktop.Utility;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * <code>HyperSQLDatabaseAccess</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class HyperSQLDatabaseAccess implements DatabaseAccess {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("DB");

    private static final String dbDriver = "org.hsqldb.jdbcDriver";

    private static final String dbParentPath = OSUtility.getApplicationDataDirectoryFile().getAbsolutePath();

    private static final String dbStartUrl = "jdbc:hsqldb:file:" + dbParentPath + "/db/filelugdb;ifexists=false";

    private static final String dbShutdownUrl = "jdbc:hsqldb:file:" + dbParentPath + "/db/filelugdb;shutdown=true";

//    private static final String dbStartUrl = "jdbc:hsqldb:file:db/filelugdb;ifexists=false";
//
//    private static final String dbShutdownUrl = "jdbc:hsqldb:file:db/filelugdb;shutdown=true";

    private static final String dbUser = "filelugdbadmin";

//    private static final File dbDir = new File(new File(System.getProperty("user.dir"), "db"), "filelugdb");

    private static final int dbInitialSize = 10;

    private static final boolean dbTestWhileIdle = true;

    private static final String dbValidationQuery = "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS";

    private static final boolean dbPoolPreparedStatement = true;

    private static BasicDataSource dataSource;

    public void initDatabase() throws Exception {
        /* 設定connection pool */
        if (dataSource == null) {
            dataSource = new BasicDataSource();

            dataSource.setDriverClassName(dbDriver);
            dataSource.setUrl(dbStartUrl);
            dataSource.setUsername(dbUser);
            dataSource.setPassword(Utility.generateDbPassword(dbUser));

            dataSource.setDefaultAutoCommit(true);

            dataSource.setInitialSize(dbInitialSize);
            dataSource.setTestWhileIdle(dbTestWhileIdle);
            dataSource.setValidationQuery(dbValidationQuery);
            dataSource.setPoolPreparedStatements(dbPoolPreparedStatement);
        }

        /* create tables */
        Connection conn = null;
        Statement statement = null;
        ResultSet rs = null;

        try {
            conn = dataSource.getConnection();

            /* create table for log backup and archive if not exists */
            DatabaseMetaData dbMetaData = conn.getMetaData();

            /* table user */
            rs = dbMetaData.getTables(null, null, TABLE_NAME_AUTH_USER, new String[]{"TABLE"});

            if (statement == null || statement.isClosed()) {
                statement = conn.createStatement();
            }

            if (!rs.next()) {
                statement.executeUpdate(SQL_CREATE_TABLE_AUTH_USER);
                statement.executeUpdate(SQL_CREATE_INDEX_AUTH_USER_IS_ADMIN);
                statement.executeUpdate(SQL_CREATE_INDEX_AUTH_USER_ADDED_TIME);
                statement.executeUpdate(SQL_CREATE_INDEX_AUTH_USER_SESSION_ID);
                statement.executeUpdate(SQL_CREATE_INDEX_AUTH_USER_COUNTRY_ID);
                statement.executeUpdate(SQL_CREATE_INDEX_AUTH_USER_PHONE_NUMBER);
//            } else {
//                if (!SUPPORT_MULTIPLE_USERS) {
//                    if (statement == null || statement.isClosed()) {
//                        statement = conn.createStatement();
//                    }
//
//                    rs.close();
//                    rs = statement.executeQuery(SQL_FIND_AUTH_USER_COUNT);
//
//                    if (rs.next()) {
//                        int rowCount = rs.getInt(1);
//
//                        if (rowCount > 1) {
//                            String message = ClopuccinoMessages.getMessage("multiple.users.not.supported");
//                            LOGGER.error(message);
//                        }
//                    }
//                }
            } else {
                // if column SESSION_ID not exists, do the followings:
                // 1. add column 'SESSION_ID' to table AUTH_USER
                // 2. add column 'LUG_SERVER_ID' to table AUTH_USER
                // 3. change column: 'PASSWD' to NULLABLE
                // 4. delete table: BOOKMARK and ROOT_DIRECTORIES

                rs.close();

                rs = dbMetaData.getColumns(null, null, TABLE_NAME_AUTH_USER, COLUMN_NAME_SESSION_ID);

                if (!rs.next()) {
                    statement.executeUpdate(SQL_ALTER_TABLE_ADD_COLUMN_AUTH_USER_SESSION_ID);
                    statement.executeUpdate(SQL_ALTER_TABLE_ADD_COLUMN_AUTH_USER_LUG_SERVER_ID);
                    statement.executeUpdate(SQL_ALTER_TABLE_ADD_COLUMN_AUTH_USER_LAST_SESSION_TIME);
                    statement.executeUpdate(SQL_ALTER_TABLE_ALTER_COLUMN_AUTH_USER_PASSWD_NULLABLE);
                    statement.executeUpdate(SQL_DROP_TABLE_BOOKMARK);
                    statement.executeUpdate(SQL_DROP_TABLE_ROOT_DIRECTORIES);
                }
            }

            rs.close();

//            /* table bookmark */
//            rs = dbMetaData.getTables(null, null, TABLE_NAME_BOOKMARK, new String[]{"TABLE"});
//
//            if (!rs.next()) {
//                if (statement == null || statement.isClosed()) {
//                    statement = conn.createStatement();
//                }
//                statement.executeUpdate(SQL_CREATE_TABLE_BOOKMARK);
//                statement.executeUpdate(SQL_CREATE_INDEX_BOOKMARK_PATH);
//                statement.executeUpdate(SQL_CREATE_INDEX_BOOKMARK_LABEL);
//                statement.executeUpdate(SQL_CREATE_FOREIGN_KEY_BOOKMARK_USER);
//
//                statement.close();
//            }
//
//            rs.close();
//
//            /* table root directory */
//            rs = dbMetaData.getTables(null, null, TABLE_NAME_ROOT_DIRECTORY, new String[]{"TABLE"});
//
//            if (!rs.next()) {
//                if (statement == null || statement.isClosed()) {
//                    statement = conn.createStatement();
//                }
//                statement.executeUpdate(SQL_CREATE_TABLE_ROOT_DIRECTORY);
//                statement.executeUpdate(SQL_CREATE_INDEX_ROOT_DIRECTORY_PATH);
//                statement.executeUpdate(SQL_CREATE_INDEX_ROOT_DIRECTORY_LABEL);
//                statement.executeUpdate(SQL_CREATE_FOREIGN_KEY_ROOT_DIRECTORY_USER);
//
//                statement.close();
//            }

            /* table file upload */
            rs = dbMetaData.getTables(null, null, TABLE_NAME_FILE_TRANSFER_IN, new String[]{"TABLE"});

            if (!rs.next()) {
                if (statement == null || statement.isClosed()) {
                    statement = conn.createStatement();
                }
                statement.executeUpdate(SQL_CREATE_TABLE_FILE_TRANSFER_IN);
                statement.executeUpdate(SQL_CREATE_INDEX_FILE_TRANSFER_IN_END_TIMESTAMP);
                statement.executeUpdate(SQL_CREATE_FOREIGN_KEY_FILE_TRANSFER_IN_USER);

                statement.close();
            }

            rs.close();

            /* table file download */
            rs = dbMetaData.getTables(null, null, TABLE_NAME_FILE_TRANSFER_OUT, new String[]{"TABLE"});

            if (!rs.next()) {
                if (statement == null || statement.isClosed()) {
                    statement = conn.createStatement();
                }
                statement.executeUpdate(SQL_CREATE_TABLE_FILE_TRANSFER_OUT);
                statement.executeUpdate(SQL_CREATE_INDEX_FILE_TRANSFER_OUT_END_TIMESTAMP);
                statement.executeUpdate(SQL_CREATE_FOREIGN_KEY_FILE_TRANSFER_OUT_USER);

                statement.close();
            } else {
                // add column LAST_MODIFIED_DATE if not exists

                rs = dbMetaData.getColumns(null, null, TABLE_NAME_FILE_TRANSFER_OUT, COLUMN_NAME_LAST_MODIFIED_DATE);

                if (!rs.next()) {
                    if (statement == null || statement.isClosed()) {
                        statement = conn.createStatement();
                    }
                    statement.executeUpdate(SQL_ADD_COLUMN_FILE_TRANSFER_OUT_LAST_MODIFIED_DATE);

                    statement.close();
                }

            }
        } finally {
            close(rs, statement, null, conn);
        }
    }

    public BasicDataSource getDataSource() {
        return dataSource;
    }

    public Connection getConnection() throws Exception {
        return dataSource.getConnection();
    }

    public void closeDataSource() throws Exception {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }

        DriverManager.getConnection(dbShutdownUrl, dbUser, Utility.generateDbPassword(dbUser));
    }

    public void close(ResultSet rs, Statement statement, PreparedStatement pStatement, Connection conn) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        } catch (Exception e) {
            /* ignored */
        }

        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
        } catch (Exception e) {
            /* ignored */
        }

        try {
            if (pStatement != null && !pStatement.isClosed()) {
                pStatement.close();
            }
        } catch (Exception e) {
            /* ignored */
        }

        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            /* ignored */
        }
    }
}
