package com.filelug.desktop.service;

/**
 * <code>Sid</code> lists ID of services between repository and server.
 *
 * @author masonhsieh
 * @version 1.0
 */
public interface Sid {

    /* ----------------------- VERSION 1 ----------------------- */

    int CONNECT = 1001;
    int CHECK_RECONNECT = 1003;
    int CHANGE_PASSWORD = 1004;
    int CHANGE_NICKNAME = 1005;
    int REGISTER = 1999;

    int LIST_ALL_BOOKMARKS = 2001;
    int LIST_BOOKMARKS_AND_ROOTS = 2002;
    int FIND_BOOKMARK_BY_ID = 2003;
    int CREATE_BOOKMARK = 2004;
    int UPDATE_BOOKMARK = 2005;
    int DELETE_BOOKMARK_BY_ID = 2006;

    int SYNCHRONIZE_BOOKMARKS = 2605;

    int LIST_CHILDREN = 3002;
    int FIND_BY_PATH = 3003;

    // upload file to server. In server, this is of file download from device
    int DOWNLOAD_FILE = 3004;

    int FILE_RENAME = 3005;

    /* download file from repository. In repository, this is of file upload from device */
    int UPLOAD_FILE = 3006;

    /* check if the directory exists and the file name not exists. In repository, this is of checking allow file upload from device */
    int ALLOW_UPLOAD_FILE = 3007;

    int CONFIRM_UPLOAD_FILE = 3008;

    int ALLOW_DOWNLOAD_FILE = 3009;

    // download file from server. In server, this is of file upload from device
    // No need to use confirm upload to confirm uploading.
    int UPLOAD_FILE2 = 3011;

    int UPLOAD_FILE_GROUP = 3012;
    int DELETE_UPLOAD_FILE = 3013;
    int DOWNLOAD_FILE_GROUP = 3014;

    // upload file to server. In server, this is of file download from device (V2)
    int DOWNLOAD_FILE2 = 3015;

    // It supports on desktop 1.x only, so if the desktop received this service id from server,
    // desktop should response that the device should upgrade to the latest version.
    int LIST_ALL_ROOT_DIRECTORIES = 4001;

    int PING = 9001;
    int UPDATE_SOFTWARE = 9002;
    int NEW_SOFTWARE_NOTIFY = 9003;
    int UNSUPPORTED = 9999;

    /* ----------------------- VERSION 2 ----------------------- */

    int CONNECT_V2 = 21001;
    int CHANGE_NICKNAME_V2 = 21005;
    int DELETE_COMPUTER_V2 = 21006;
    int CHANGE_COMPUTER_NAME_V2 = 21007;

    int GET_QR_CODE_V2 = 21101;
    int LOGIN_BY_QR_CODE_V2 = 21102;

    int FILE_RENAME_V2 = 23005;
    int UPLOAD_FILE2_V2 = 23011;
    int DOWNLOAD_FILE2_V2 = 23015;

    int LIST_ALL_ROOT_DIRECTORIES_V2 = 24001;
}
