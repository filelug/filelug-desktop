package com.filelug.desktop.dao;

import ch.qos.logback.classic.Logger;
import com.filelug.desktop.Constants;
import com.filelug.desktop.db.DatabaseAccess;
import com.filelug.desktop.db.DatabaseConstants;
import com.filelug.desktop.db.HyperSQLDatabaseAccess;
import com.filelug.desktop.model.FileTransferIn;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <code>FileTransferInDao</code> provides db access to data CRUD of file transferred in (e.g. from device to desktop).
 *
 * @author masonhsieh
 * @version 1.0
 */
public class FileTransferInDao {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("DAO_FILE_IN");


    private DatabaseAccess dbAccess;

    public FileTransferInDao() {
        dbAccess = new HyperSQLDatabaseAccess();
    }

    public FileTransferInDao(DatabaseAccess dbAccess) {
        this.dbAccess = dbAccess;
    }

    public void createFileTransferIn(FileTransferIn fileTransferIn) {
        Connection conn = null;
        PreparedStatement pStatement = null;

        String transferKey = fileTransferIn.getTransferKey();
        String userId = fileTransferIn.getUserId();
        String filename = fileTransferIn.getFilename();
        String directory = fileTransferIn.getDirectory();
        Long fileSize = fileTransferIn.getFileSize();
        Long startTimestamp = fileTransferIn.getStartTimestamp();
        Long endTimestamp = fileTransferIn.getEndTimestamp();
        String status = fileTransferIn.getStatus();
        
        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_CREATE_FILE_TRANSFER_IN);
            pStatement.setString(1, transferKey);
            pStatement.setString(2, userId);
            pStatement.setString(3, filename);
            pStatement.setString(4, directory);
            pStatement.setLong(5, (fileSize != null ? fileSize : 0));
            pStatement.setLong(6, (startTimestamp != null ? startTimestamp : 0));
            pStatement.setLong(7, (endTimestamp != null ? endTimestamp : 0));
            pStatement.setString(8, status);

            pStatement.executeUpdate();

            SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_PATTERN);
            String dateString = format.format(new Date(System.currentTimeMillis()));

            LOGGER.debug(String.format("User '%s' starts transfering file in at: %s%nupload key=%s%ndirectory=%s%nfilename=%s%nfile size=%d%nstart timestamp=%d%nend timestamp=%d%nstatus=%s", userId, dateString, transferKey, directory, filename, fileSize, startTimestamp, endTimestamp, status));
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_PATTERN);
            String dateString = format.format(new Date(System.currentTimeMillis()));

            LOGGER.error(String.format("Error on user '%s' starting to trasnfer file in at: %s%nupload key=%s%ndirectory=%s%nfilename=%s%nfile size=%d%nstart timestamp=%d%nend timestamp=%d%nstatus=%s%nerror message:%n%s", userId, dateString, transferKey, directory, filename, fileSize, startTimestamp, endTimestamp, status, errorMessage), e);
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

    public boolean updateFileTransferInStatus(String transferKey, String status, long endTimestamp) {
        int updateCount = 0;

        Connection conn = null;
        PreparedStatement pStatement = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_UPDATE_FILE_TRANSFER_IN_WITHOUT_FILE_SIZE);
            pStatement.setString(1, status);
            pStatement.setLong(2, endTimestamp);
            pStatement.setString(3, transferKey);

            updateCount = pStatement.executeUpdate();

            SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_PATTERN);
            String dateString = format.format(new Date(endTimestamp));

            LOGGER.info(String.format("End transfering file in at: %s.%nupload key=%s%nstaus=%s", dateString, transferKey, status));
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            SimpleDateFormat format = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_PATTERN);
            String dateString = format.format(new Date(endTimestamp));

            LOGGER.error(String.format("Error on ending transfering file in at: %s%nupload key=%s%nstatus=%s%nerror message:%n%s", dateString, transferKey, status, errorMessage), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(null, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return updateCount > 0;
    }

    public void updateFileTransferInSize(String transferKey, long fileSize) {
        Connection conn = null;
        PreparedStatement pStatement = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_UPDATE_FILE_TRANSFER_IN_SIZE);
            pStatement.setLong(1, fileSize);
            pStatement.setString(2, transferKey);

            pStatement.executeUpdate();

            LOGGER.info(String.format("Updating the size of file transferred in: %s.%nupload key=%s", String.valueOf(fileSize), transferKey));
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Updating the size of file transferred in: %s.%nupload key=%s%nerror message:%n%s", String.valueOf(fileSize), transferKey, errorMessage), e);
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

    public List<FileTransferIn> findAllFilesTransferInForUser(String userId, boolean successOnly) throws Exception {
        List<FileTransferIn> fileTransferIns = new ArrayList<>();

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            if (successOnly) {
                pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_SUCCESS_FILE_TRANSFER_IN_BY_USER);
            } else {
                pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_ALL_FILE_TRANSFER_IN_BY_USER);
            }

            pStatement.setString(1, userId);

            resultSet = pStatement.executeQuery();

            for (; resultSet.next();) {
                String transferKey = resultSet.getString(DatabaseConstants.COLUMN_NAME_TRANSFER_OUT_KEY);
                String filename = resultSet.getString(DatabaseConstants.COLUMN_NAME_FILENAME);
                String directory = resultSet.getString(DatabaseConstants.COLUMN_NAME_DIRECTORY);
                long fileSize = resultSet.getLong(DatabaseConstants.COLUMN_NAME_FILE_SIZE);
                long startTimestamp = resultSet.getLong(DatabaseConstants.COLUMN_NAME_START_TIMESTAMP);
                long endTimestamp = resultSet.getLong(DatabaseConstants.COLUMN_NAME_END_TIMESTAMP);
                String status = resultSet.getString(DatabaseConstants.COLUMN_NAME_STATUS);

                fileTransferIns.add(new FileTransferIn(transferKey, userId, filename, directory, fileSize, startTimestamp, endTimestamp, status));
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

        return fileTransferIns;
    }

    public void updateStatusProcessingToFailureForTimeoutStartTimestamp(long minimumTimestamp) {
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_PROCESSING_FILE_TRANSFER_IN_BY_TIMEOUT_START_TIMESTAMP);
            pStatement.setLong(1, minimumTimestamp);

            boolean found = false;

            resultSet = pStatement.executeQuery();

            for (; resultSet.next(); ) {
                found = true;

                String uploadKey = resultSet.getString(DatabaseConstants.COLUMN_NAME_TRANSFER_IN_KEY);
                String userId = resultSet.getString(DatabaseConstants.COLUMN_NAME_AUTH_USER_ID);
                String filename = resultSet.getString(DatabaseConstants.COLUMN_NAME_FILENAME);
                String directory = resultSet.getString(DatabaseConstants.COLUMN_NAME_DIRECTORY);
                long fileSize = resultSet.getLong(DatabaseConstants.COLUMN_NAME_FILE_SIZE);
                long startTimestamp = resultSet.getLong(DatabaseConstants.COLUMN_NAME_START_TIMESTAMP);
                String status = resultSet.getString(DatabaseConstants.COLUMN_NAME_STATUS);

                String message = String.format("Timeout file-transferred-in found.%ntrasnfer key: %s%nuser: %s%nfilename: %s%ndirectory: %s%nfile size in bytes: %s%nstart timestamp: %s%nstatus: %s%n", uploadKey, userId, filename, directory, String.valueOf(fileSize), String.valueOf(startTimestamp), status);
                LOGGER.info(message);
            }

            if (found) {
                LOGGER.info("Start updating file-transferred-in status to failure for timeout start-timestamp");

                resultSet.close();
                pStatement.close();

                pStatement = conn.prepareStatement(DatabaseConstants.SQL_UPDATE_FILE_TRANSFER_IN_STATUS_PROCESSING_TO_FAILURE_FOR_TIMEOUT_START_TIMESTAMP);
                pStatement.setLong(1, minimumTimestamp);

                pStatement.executeUpdate();
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on updating file-transferred-in status to failure for timeout transfer: %s%nerror message:%n%s", String.valueOf(minimumTimestamp), errorMessage), e);
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

    /**
     * @return 0 if not found for the trasnfer key or any error occurred
     */
    public long findFileTransferInSizeForTransferKey(String transferKey) {
        long fileSize = 0;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_FILE_TRANSFER_IN_SIZE_BY_TRANSFER_KEY);

            pStatement.setString(1, transferKey);

            resultSet = pStatement.executeQuery();

            if(resultSet.next()) {
                fileSize = resultSet.getLong(DatabaseConstants.COLUMN_NAME_FILE_SIZE);
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on finding the size of transferred-in file for trasnfer key: %s%nerror message:%n%s", transferKey, errorMessage), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    /* ignored */
                }
            }
        }

        return fileSize;
    }

    /**
     *
     * @return number of file transferred-in found for the trasnfer key.
     * The pk is trasnfer key, so the value should be either 0 (not found) or 1.
     * @param transferKey
     */
    public int countFileTransferInForTransferKey(String transferKey) {
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        int count = 0;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_COUNT_FILE_TRANSFER_IN_BY_TRANSFER_KEY);

            pStatement.setString(1, transferKey);

            resultSet = pStatement.executeQuery();

            if(resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on counting tranferred-in files for trasnfer key: %s%nerror message:%n%s", transferKey, errorMessage), e);
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

    public long sumFilesTransferInSizeForUser(String userId) {
        long fileSizeSum = 0;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_SUM_FILE_TRANSFER_IN_SIZE_BY_USER);

            pStatement.setString(1, userId);

            resultSet = pStatement.executeQuery();

            if(resultSet.next()) {
                fileSizeSum = resultSet.getLong(1);
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on finding sum-up file size of the transferred-in files for user: %s%nerror message:%n%s", userId, errorMessage), e);
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

    /**
     * Update file transferred-in with trasnfer key. If not found, do nothing.
     */
    public void updateFileTransferIn(String status, long fileSize, long endTimestamp, String trasnferKey) {
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_COUNT_FILE_TRANSFER_IN_BY_TRANSFER_KEY);

            pStatement.setString(1, trasnferKey);

            resultSet = pStatement.executeQuery();

            if(resultSet.next() && resultSet.getInt(1) > 0) {
                /* found */
                pStatement.close();
                resultSet.close();

                pStatement = conn.prepareStatement(DatabaseConstants.SQL_UPDATE_FILE_TRANSFER_IN);
                pStatement.setString(1, status);
                pStatement.setLong(2, fileSize);
                pStatement.setLong(3, endTimestamp);
                pStatement.setString(4, trasnferKey);

                pStatement.executeUpdate();

                LOGGER.debug(String.format("Updated file transferred-in for trasnfer key: %s, status: %s, file size: %d, end time stamp: %d", trasnferKey, status, fileSize, endTimestamp));
            } else {
                LOGGER.warn("Failed to update file transferred-in because the file transferred-in with trasnfer key: " + trasnferKey + " not exists.");
            }
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on update file transferred-in for trasnfer key: %s, status: %s, file size: %d, end time stamp: %d%nerror message:%n%s", trasnferKey, status, fileSize, endTimestamp, errorMessage), e);
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

    /**
     * Update file transferred-in with trasnfer key. Chek first to make sure the FileTransferIn with the specified transfer key exists in the current table.
     */
    public void updateFileTransferIn(FileTransferIn fileTransferIn) {
        Connection conn = null;
        PreparedStatement pStatement = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_UPDATE_FILE_TRANSFER_IN2);

            pStatement.setString(1, fileTransferIn.getUserId());
            pStatement.setString(2, fileTransferIn.getFilename());
            pStatement.setString(3, fileTransferIn.getDirectory());
            pStatement.setLong(4, fileTransferIn.getFileSize());
            pStatement.setLong(5, fileTransferIn.getStartTimestamp());
            pStatement.setLong(6, fileTransferIn.getEndTimestamp());
            pStatement.setString(7, fileTransferIn.getStatus());
            pStatement.setString(8, fileTransferIn.getTransferKey());

            pStatement.executeUpdate();

            LOGGER.debug(String.format("Updated file transferred-in:%n%s", fileTransferIn));
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on update file transferred-in:%n%s%nError message:%n%s", fileTransferIn, errorMessage), e);
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

    public boolean deleteFileTransferInIfExists(String transferKey) {
        boolean success = false;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_IF_EXISTS_FILE_TRANSFER_IN_BY_TRANSFER_KEY);

            pStatement.setString(1, transferKey);

            resultSet = pStatement.executeQuery();

            if (resultSet.next()) {
                // Exists, so delete it.

                pStatement.close();

                pStatement = conn.prepareStatement(DatabaseConstants.SQL_DELETE_FILE_TRANSFER_IN_BY_TRANSFER_KEY);

                pStatement.setString(1, transferKey);

                pStatement.executeUpdate();

                pStatement.close();

                success = true;

                LOGGER.debug(String.format("Successfully deleted file transferred-in for trasnfer key='%s'", transferKey));
            }
        } catch (Exception e) {
            success = false;

            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on deleting file transferred-in for trasnfer key='%s'%nError message:%n%s%n", transferKey, errorMessage), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    // ignored
                }
            }
        }

        return success;
    }

    public boolean existingFileTransferInForTransferKey(String transferKey) {
        boolean found = false;

        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet resultSet = null;

        try {
            conn = dbAccess.getConnection();

            pStatement = conn.prepareStatement(DatabaseConstants.SQL_FIND_IF_EXISTS_FILE_TRANSFER_IN_BY_TRANSFER_KEY);

            pStatement.setString(1, transferKey);

            resultSet = pStatement.executeQuery();

            found = resultSet.next();
        } catch (Exception e) {
            String errorMessage = e.getMessage();

            LOGGER.error(String.format("Error on finding if the transferred-in file exits for trasnfer key='%s'%nError message:%n%s%n", transferKey, errorMessage), e);
        } finally {
            if (dbAccess != null) {
                try {
                    dbAccess.close(resultSet, null, pStatement, conn);
                } catch (Exception e) {
                    // ignored
                }
            }
        }

        return found;
    }
}
