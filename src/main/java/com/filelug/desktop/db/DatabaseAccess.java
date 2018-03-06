package com.filelug.desktop.db;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * <code>DatabaseAccess</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public interface DatabaseAccess extends DatabaseConstants {

    void initDatabase() throws Exception;

    BasicDataSource getDataSource();

    Connection getConnection() throws Exception;

    void closeDataSource() throws Exception;

    void close(ResultSet rs, Statement statement, PreparedStatement pStatement, Connection conn);

}
