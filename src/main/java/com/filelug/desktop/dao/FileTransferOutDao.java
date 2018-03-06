package com.filelug.desktop.dao;

import ch.qos.logback.classic.Logger;
import com.filelug.desktop.Constants;
import com.filelug.desktop.db.DatabaseAccess;
import com.filelug.desktop.db.DatabaseConstants;
import com.filelug.desktop.db.HyperSQLDatabaseAccess;
import com.filelug.desktop.model.FileTransferOut;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <code>FileTransferOutDao</code> provides db access to data CRUD of file transferred out (e.g. from desktop to device).
 *
 * @author masonhsieh
 * @version 1.0
 */
public class FileTransferOutDao {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("DAO_FILE_OUT");


    private DatabaseAccess dbAccess;

    public FileTransferOutDao() {
        dbAccess = new HyperSQLDatabaseAccess();
    }

    public FileTransferOutDao(DatabaseAccess dbAccess) {
        this.dbAccess = dbAccess;
    }

    public void createFileTransferOut(FileTransferOut fileTransferOut) {
        Connection conn = null;
        PreparedStatement pStatement = null;

        String transferKey = fileTransferOut.getTransferKey();
        String userId = fileTransferOut.getUserId();
        String filePath = fileTransferOut.getFilePath();
        Long fileSize = fileTransferOut.getFileSize();
        Long fileLastModifiedDateInMillis = fileTransferOut.getFileLastModifiedDate();
        Long startTimestamp = fileTransferOut.getStartTimestamp();
        Long endTimestamp = fileTransferOut.getEndTimestamp();
        String status = fileTransferOut.getStatus();

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_CREATE_FILE_TRANSFER_OUT);
            pStatement.setString(1, transferKey);
            pStatement.setString(2, userId);
            pStatement.setString(3, filePath);
            pStatement.setLong(4, (fileSize != null ? fileSize : 0));
            pStatement.setLong(5, (fileLastModifiedDateInMillis != null ? fileLastModifiedDateInMillis : 0));
            pStatement.setLong(6, (startTimestamp != null ? startTimestamp : 0));
            pStatement.setLong(7, (endTimestamp != null ? endTimestamp : 0));
            pStatement.setString(8, status);

            pStatement.executeUpdate();

            SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_PATTERN);
            String dateString = format.format(new Date());

            LOGGER.debug(String.format("File transferred-out created for user '%s'%nat: %s%ntransfer key=%s%nfile path=%s%nfile size=%d%nlast modified=%d%nstart timestamp=%d%nend timestamp=%d%nstatus=%s", userId, dateString, transferKey, filePath, fileSize, fileLastModifiedDateInMillis, startTimestamp, endTimestamp, status));
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_PATTERN);
            String dateString = format.format(new Date());

            LOGGER.error(String.format("Error on creating file transferred-out for user '%s'%nat: %s%ntransfer key=%s%nfile path=%s%nfile size=%d%nlast modified=%d%nstart timestamp=%d%nend timestamp=%d%nstatus=%s%nerror message:%n%s", userId, dateString, transferKey, filePath, fileSize, fileLastModifiedDateInMillis, startTimestamp, endTimestamp, status, errorMessage), e);
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

    /**
     * Update file transferred-out with trasnfer key.
     */
    public void updateFileTransferOut(FileTransferOut fileTransferOut) {
        Connection conn = null;
        PreparedStatement pStatement = null;

        Long fileSize = fileTransferOut.getFileSize();
        Long fileLastModifiedDateInMillis = fileTransferOut.getFileLastModifiedDate();
        Long startTimestamp = fileTransferOut.getStartTimestamp();
        String status = fileTransferOut.getStatus();
        Long endTimestamp = fileTransferOut.getEndTimestamp();
        String transferKey = fileTransferOut.getTransferKey();

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_UPDATE_FILE_TRANSFER_OUT);
            pStatement.setLong(1, (fileSize != null ? fileSize : 0));
            pStatement.setLong(2, (fileLastModifiedDateInMillis != null ? fileLastModifiedDateInMillis : 0));
            pStatement.setLong(3, (startTimestamp != null ? startTimestamp : 0));
            pStatement.setString(4, status);
            pStatement.setLong(5, (endTimestamp != null ? endTimestamp : 0));
            pStatement.setString(6, transferKey);

            pStatement.executeUpdate();

            SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_PATTERN);
            String dateString = format.format(new Date(startTimestamp));

            LOGGER.debug(String.format("File transferred-out updated at: %s%ntransfer key=%s%nfile size=%d%nfile last modified date=%d%nstart timestamp=%d%nstatus=%s%nend timestamp=%d", dateString, transferKey, fileSize, fileLastModifiedDateInMillis, startTimestamp, status, endTimestamp));
        } catch (Exception e) {
            SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_PATTERN);
            String dateString = format.format(new Date(startTimestamp));

            String errorMessage = e.getMessage();

            String message = String.format("Error on updating file transferred-out at: %s%ntransfer key=%s%nfile size=%d%nfile last modified date=%d%nstart timestamp=%d%nstatus=%s%nend timestamp=%d%nerror message:%n%s", dateString, transferKey, fileSize, fileLastModifiedDateInMillis, startTimestamp, status, endTimestamp, errorMessage);

            LOGGER.error(message, e);
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

    public boolean updateFileTransferOutStatus(String transferKey, String status, long endTimestamp) {
        int updatedCount = 0;

        Connection conn = null;
        PreparedStatement pStatement = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_UPDATE_FILE_TRANSFER_OUT_STATUS);
            pStatement.setString(1, status);
            pStatement.setLong(2, endTimestamp);
            pStatement.setString(3, transferKey);

            updatedCount = pStatement.executeUpdate();

            SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_PATTERN);
            String dateString = format.format(new Date(endTimestamp));

            LOGGER.debug(String.format("File transferred-out status updated at: %s.%ntransfer key=%s%nstatus=%s", dateString, transferKey, status));
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_PATTERN);
            String dateString = format.format(new Date(endTimestamp));

            LOGGER.error(String.format("Error on updating file transferred-out status at: %s%ntransfer key=%s%nstatus=%s%nerror message:%n%s", dateString, transferKey, status, errorMessage), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(null, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return updatedCount > 0;
    }

    public void updateStatusProcessingToFailureForTimeoutStartTimestamp(long minimumTimestamp) {
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_PROCESSING_FILE_TRANSFER_OUT_BY_TIMEOUT_START_TIMESTAMP);
            pStatement.setLong(1, minimumTimestamp);

            boolean found = false;

            resultSet = pStatement.executeQuery();

            for (; resultSet.next(); ) {
                found = true;

                String transferKey = resultSet.getString(DatabaseConstants.COLUMN_NAME_TRANSFER_OUT_KEY);
                String userId = resultSet.getString(DatabaseConstants.COLUMN_NAME_AUTH_USER_ID);
                String filePath = resultSet.getString(DatabaseConstants.COLUMN_NAME_FILE_PATH);
                long fileSize = resultSet.getLong(DatabaseConstants.COLUMN_NAME_FILE_SIZE);
                long startTimestamp = resultSet.getLong(DatabaseConstants.COLUMN_NAME_START_TIMESTAMP);
                String status = resultSet.getString(DatabaseConstants.COLUMN_NAME_STATUS);

                String message = String.format("Timeout transferred-out file found.%ndownload key: %s%nuser: %s%nfile path: %s%nfile size in bytes: %s%nstart timestamp: %s%nstatus: %s%n", transferKey, userId, filePath, String.valueOf(fileSize), String.valueOf(startTimestamp), status);
                LOGGER.debug(message);
            }

            if (found) {
                LOGGER.debug("Start updating download status to failure for timeout start-timestamp");

                resultSet.close();
                pStatement.close();

                pStatement = conn.prepareStatement(DatabaseConstants.SQL_UPDATE_FILE_TRANSFER_OUT_STATUS_PROCESSING_TO_FAILURE_FOR_TIMEOUT_START_TIMESTAMP);
                pStatement.setLong(1, minimumTimestamp);

                pStatement.executeUpdate();
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on updating file transferred-out status to failure for timeout transfer: %s%nerror message:%n%s", String.valueOf(minimumTimestamp), errorMessage), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }
    }

    public List<FileTransferOut> findAllFileTransferOutForUser(String userId, boolean successOnly) throws Exception {
        List<FileTransferOut> fileTransferOuts = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            if (successOnly) {
                pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_SUCCESS_FILE_TRANSFER_OUT_BY_USER);
            } else {
                pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_ALL_FILE_TRANSFER_OUT_BY_USER);
            }

            pStatement.setString(1, userId);

            resultSet = pStatement.executeQuery();

            for (; resultSet.next();) {
                String transferKey = resultSet.getString(DatabaseConstants.COLUMN_NAME_TRANSFER_OUT_KEY);
                String filePath = resultSet.getString(DatabaseConstants.COLUMN_NAME_FILE_PATH);
                long fileSize = resultSet.getLong(DatabaseConstants.COLUMN_NAME_FILE_SIZE);
                long fileLastModifiedInMillis = resultSet.getLong(DatabaseConstants.COLUMN_NAME_LAST_MODIFIED_DATE);
                long startTimestamp = resultSet.getLong(DatabaseConstants.COLUMN_NAME_START_TIMESTAMP);
                long endTimestamp = resultSet.getLong(DatabaseConstants.COLUMN_NAME_END_TIMESTAMP);
                String status = resultSet.getString(DatabaseConstants.COLUMN_NAME_STATUS);

                fileTransferOuts.add(new FileTransferOut(transferKey, userId, filePath, fileSize, fileLastModifiedInMillis, startTimestamp, endTimestamp, status));
            }
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return fileTransferOuts;
    }

    public void updateFileTransferOutSize(String transferKey, long fileSize) {
        Connection conn = null;
        PreparedStatement pStatement = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_UPDATE_FILE_TRANSFER_OUT_SIZE);
            pStatement.setLong(1, fileSize);
            pStatement.setString(2, transferKey);

            pStatement.executeUpdate();

            LOGGER.debug(String.format("File transferred-out size updated to '%s' for transfer key=%s", String.valueOf(fileSize), transferKey));
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on updating file transferred-out size updated to '%s' for trasnfer Key=%s%nerror message:%n%s", String.valueOf(fileSize), transferKey, errorMessage), e);
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

    public long sumFilesTransferOutSizeForUser(String userId) {
        long fileSizeSum = 0;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_SUM_TRANSFER_OUT_FILE_SIZE_BY_USER);

            pStatement.setString(1, userId);

            resultSet = pStatement.executeQuery();

            if(resultSet.next()) {
                fileSizeSum = resultSet.getLong(1);
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on finding sum-up file size of the transferred-out files for user: %s%nerror message:%n%s", userId, errorMessage), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return fileSizeSum;
    }

    public FileTransferOut findFileTransferOutForTransferKey(String transferKey) {
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        FileTransferOut fileTransferOut = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_FILE_TRANSFER_OUT_BY_TRANSFER_KEY);

            pStatement.setString(1, transferKey);

            resultSet = pStatement.executeQuery();

            if(resultSet.next()) {
                String userId = resultSet.getString(DatabaseConstants.COLUMN_NAME_AUTH_USER_ID);
                String filePath = resultSet.getString(DatabaseConstants.COLUMN_NAME_FILE_PATH);
                long fileSize = resultSet.getLong(DatabaseConstants.COLUMN_NAME_FILE_SIZE);
                long fileLastModifiedInMillis = resultSet.getLong(DatabaseConstants.COLUMN_NAME_LAST_MODIFIED_DATE);
                long startTimestamp = resultSet.getLong(DatabaseConstants.COLUMN_NAME_START_TIMESTAMP);
                long endTimestamp = resultSet.getLong(DatabaseConstants.COLUMN_NAME_END_TIMESTAMP);
                String status = resultSet.getString(DatabaseConstants.COLUMN_NAME_STATUS);

                fileTransferOut = new FileTransferOut(transferKey, userId, filePath, fileSize, fileLastModifiedInMillis, startTimestamp, endTimestamp, status);
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on finding file transferred-out for transfer key: %s%nerror message:%n%s", transferKey, errorMessage), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return fileTransferOut;
    }

    /**
     *
     * @return  number of file transferred-out found for the transfer key.
     * The pk is transfer key, so the value should be either 0 (not found) or 1.
     * @param transferKey
     */
    public int countFilesTransferOutForTransferKey(String transferKey) {
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        int count = 0;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_COUNT_FILE_TRANSFER_OUT_BY_TRANSFER_KEY);

            pStatement.setString(1, transferKey);

            resultSet = pStatement.executeQuery();

            if(resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on count file transferred-out for transfer key: %s%nerror message:%n%s", transferKey, errorMessage), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return count;
    }

    /**
     * @return  true if the file transferred-out found for the transfer key.
     */
    public boolean existingFileTransferOutForTransferKey(String transferKey) {
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        boolean exists = false;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_IF_EXISTS_FILE_TRANSFER_OUT_BY_TRANSFER_KEY);

            pStatement.setString(1, transferKey);

            resultSet = pStatement.executeQuery();

            exists = resultSet.next();
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on finding if the transferred-out file exists with transfer key: %s%nerror message:%n%s", transferKey, errorMessage), e);
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
}
