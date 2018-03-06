package com.filelug.desktop;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public interface Constants {

    float DEFAULT_FONT_SIZE = 12f;

    /* settings - preferences */
//    String PREFS_KEY_SERVER_STOP_PORT = "server.stop.port";
//
//    String PREFS_KEY_SERVER_STOP_KEY = "server.stop.key";
//
//    String PREFS_KEY_SERVER_ADDRESS = "server.address";
//
//    String PREFS_KEY_USE_SSL = "server.ssl";
//
//    String PREFS_KEY_SERVER_NAME = "server.admin.computer.name";
//
//    String PREFS_KEY_COUNTRY_ID = "account.country.id";
//
//    String PREFS_KEY_PHONE_NUMBER= "account.phone.number";
//
//    String PREFS_KEY_ACCOUNT_NAME = "account.registered.name";
//
//    String PREFS_KEY_ACCOUNT_PASSWORD = "account.registered.password";
//
//    String PREFS_KEY_ACCOUNT_NICKNAME = "account.nickname";
//
//    String PREFS_KEY_ACCOUNT_STATE = "account.state";
//
//    String PREFS_KEY_COMPUTER_ID = "server.computer.id";
//
//    String PREFS_KEY_COMPUTER_GROUP = "server.computer.group";
//
//    String PREFS_KEY_COMPUTER_NAME = "server.computer.name";
//
//    String PREFS_KEY_COMPUTER_RECOVERY_KEY = "server.computer.recovery.key";
//
//    String PREFS_KEY_SUPPORT_SYSTEM_TRAY = "support-system-tray";
//
////    String PREFS_KEY_FILELUG_DESKTOP_LATEST_VERSION = "desktop.latest.version";
//
//    String PREFS_KEY_APP_RESET = "app.reset";
//
//    String PREFS_KEY_QR_CODE = "qr.code";
//
//    int DEFAULT_SERVER_STOP_PORT = 51530;
//
//    String DEFAULT_SERVER_STOP_KEY = "stop_filelug_server";

    int N_THREADS = 10;

//    int PING_SERVER_INTERVAL_IN_MILLIS = 2000; // every 2 seconds
//
//    int PING_SERVER_TIMES = 10;

    String DEFAULT_COMUPTER_GROUP = "GENERAL";

    /* command without connection port */
//    String COMMAND_UNIX_START_SERVER = "java -DSTOP.PORT=%d -DSTOP.KEY=%s -jar start.jar";
//
//    String COMMAND_UNIX_STOP_SERVER = "java -DSTOP.PORT=%d -DSTOP.KEY=%s -jar start.jar --stop";
//
//    String COMMAND_WINDOWS_START_SERVER = "java -DSTOP.PORT=%d -DSTOP.KEY=%s -jar start.jar";
//
//    String COMMAND_WINDOWS_STOP_SERVER = "java -DSTOP.PORT=%d -DSTOP.KEY=%s -jar start.jar --stop";

    String PREFS_FILE_NAME = "filelug-desktop.properties";

    int MIN_COMPUTER_NAME_LENGTH = 6;

    int MAX_COMPUTER_NAME_LENGTH = 20;

    String WINDOWS_TRAY_ICON_FILENAME = "Filelug_Icon_32Bits_16.png";
    String OSX_TRAY_ICON_FILENAME = "Filelug_Icon_32Bits_32.png";
    String DEFAULT_TRAY_ICON_FILENAME = "Filelug_Icon_32Bits_48.png";

    DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss 'GMT'Z");

    String DEFAULT_DATE_FORMAT_PATTERN = "yyyy/MM/dd HH:mm:ss 'GMT'Z";

    SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT_PATTERN);

    String HTTP_HEADER_LAST_MODIFIED_DATE_FORMAT_PATTERN = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";

    // interval in millis to check if the connection between repository and desktop must be re-built: 15 seconds
    long INTERVAL_IN_MILLIS_TO_CHECK_RECONNECT = 15 * 1000;

    // CHECK_RECONNECT_INTERVAL_IN_MILLIS must larger than
    // SO_TIMEOUT_IN_SECONDS_TO_CHECK_RECONNECT + CONNECT_TIMEOUT_IN_SECONDES_DEFAULT
    int SO_TIMEOUT_IN_SECONDS_TO_CHECK_RECONNECT = 10;
    int CONNECT_TIMEOUT_IN_SECONDS_TO_CHECK_RECONNECT = 2;

    int SO_TIMEOUT_IN_SECONDS_TO_DISCONNECT_CONNECT_SOCKET = 10;

    int CONNECT_TIMEOUT_IN_SECONDS_TO_DISCONNECT_CONNECT_SOCKET = 5;

    int AWAIT_TERMINATION_IN_SECONDS_TO_CHECK_RECONNECT_SCHEDULER = 1;

    String HTTP_HEADER_FILELUG_SESSION_ID_NAME = "fsi";

    String HTTP_HEADER_AUTHORIZATION_NAME = "Authorization";

    String HTTP_HEADER_NAME_UPLOAD_KEY = "upkey";

    /* SO_TIMEOUT is the timeout for waiting for data or, put differently,
     * a maximum period inactivity between two consecutive data packets
     */
    int SO_TIMEOUT_IN_SECONDS_DEFAULT = 60;

    // for file transfer from desktop to device
    int SO_TIMEOUT_IN_SECONDS_TO_FILE_UPLOAD = 60;

    // for file transfer from device to desktop,
    // adding 2 more seconds to wait for data from device
    int SO_TIMEOUT_IN_SECONDS_TO_FILE_DOWNLOAD = 62;

    int CONNECT_TIMEOUT_IN_SECONDS_DEFAULT = 5;

    // The server value Constants.DEFAULT_CLIENT_SESSION_IDLE_TIMEOUT_IN_SECONDS minus 5 minutes:
    // 55 minutes
    int LESS_OF_CLIENT_SESSION_IDLE_TIMEOUT_IN_SECONDS = 3600 - (5 * 60);

    // seconds
    int KEEP_ALIVE_TIMEOUT = 60;
    int MAX_KEEP_ALIVE_REQUESTS = 5;

    // default wait timeout in seconds for Future.get()
    Integer FUTURE_WAIT_TIMEOUT_IN_SECONDS_DEFAULT = 60; // 60 seconds

    /* connect socket: socket timeout in seconds */
    Integer IDLE_TIMEOUT_IN_SECONDS_TO_CONNECT_SOCKET = 3600; // 1 hour

    int DEFAULT_DELAY_TO_REMOVE_ALL_SOCKETS_IN_SECONDS = 30;

    /* buffer size to download/upload file */
//    int DEFAULT_TRANSFER_FILE_BUFFER_SIZE_IN_BYTES = 8192;
    int DEFAULT_TRANSFER_FILE_BUFFER_SIZE_IN_BYTES = 4096;
//    int DEFAULT_TRANSFER_FILE_BUFFER_SIZE_IN_BYTES = 32767;

    int INITIAL_CONNECT_MAX_TEXT_MESSAGE_BUFFER_SIZE = 20000;

    String CONTENT_TYPE_JSON_UTF8 = "application/json; charset=utf-8";

    String CONTENT_TYPE_TEXT_PLAIN_UTF8 = "text/plain; charset=utf-8";

    String DEFAULT_RESPONSE_ENTITY_CHARSET = "UTF-8";

    String DEFAULT_FILE_READ_WRITE_CHARSET = "UTF-8";

    String DEFAULT_FILENAME_CHARSET = "UTF-8";

    int SPLASH_SCREEN_WINDOW_WIDTH = 560;

    int SPLASH_SCREEN_WINDOW_HEIGHT = 420;

    long K = 1024;

    long M = K * K;

    long G = M * K;

    long T = G * K;

    String ENCRYPTED_USER_COMPUTER_ID_PREFIX = "@";

    String ENCRYPTED_USER_COMPUTER_ID_SUFFIX = "#";

    String ENCRYPTED_USER_ID_PREFIX = ")@";

    String ENCRYPTED_USER_ID_SUFFIX = "#(";

    String USER_ACCOUNT_DELIMITERS = "-";

    String COMPUTER_DELIMITERS = "|";

    // Replaced by value of key: 'user.home.directory.label'
//    String DEFAULT_HOME_DIRECTORY_NAME = "Home";

    String DESKTOP_VERSION_REG_EXP = "^\\d+(\\.\\d+)+";

    String DEFAULT_DESKTOP_VERSION = "2.0.3";

    String TSF_FILENAME = "filelug-mk.data";

    String DEFAULT_DATA_DIRECTORY_NAME_FOR_PRODUCTION = "Filelug2";

    String DEFAULT_DATA_DIRECTORY_NAME_FOR_PRODUCTION_V1 = "Filelug";

    String DEFAULT_DATA_DIRECTORY_NAME_FOR_TESTING = "Filelug-TESTING2";

    String DEFAULT_DATA_DIRECTORY_NAME_FOR_TESTING_V1 = "Filelug-TESTING";

    String DOWNLOADED_SOFTWARE_TEMP_FILE_PREFIX = "_filelug-desktop-";

    String SOFTWARE_PATCH_FILE_NAME = "filelug-desktop.patch";

    int EXIT_CODE_ON_SOFTWARE_UPDATE = 999;

//    String PROPERTY_KEY_DESKTOP_LOCALE = "desktop.locale";
//
//    String PROPERTY_KEY_FILELUG_DESKTOP_CURRENT_VERSION = "desktop.version";

    String AA_SERVER_ID_AS_LUG_SERVER = "aa";

    String VERSION_REG_EXP = "^\\d+(\\.\\d+)+";

    String DEFAULT_WEB_SITE_URL = "filelug.com";

    String DEFAULT_WEB_SITE_DOWNLOAD_PAGE_URL = "http://www.filelug.com/get/index.html";

    // CUSTOMIZED HTTP HEADER

    String HTTP_HEADER_NAME_FILE_CONTENT_RANGE = "File-Content-Range";

    String HTTP_HEADER_NAME_FILE_LAST_MODIFIED = "File-Last-Modified";

    // CUSTOMIZED HTTP RESPONSE STATUS

    /*
     * 430: User already registered
     */
    int HTTP_STATUS_USER_ALREADY_REGISTERED = 430;

    /*
     * 431: Incorrect security code for such as confirming registration or reset password.
     */
    int HTTP_STATUS_INCORRECT_SECURITY_CODE = 431;

    /*
     * 432: User not registered
     */
    int HTTP_STATUS_USER_NOT_REGISTERED = 432;

    /*
     *  460: computer not found
     */
    int HTTP_STATUS_COMPUTER_NOT_FOUND = 460;

    /*
     *  461: the specified user is not the administrator of the computer
     */
    int HTTP_STATUS_USER_NOT_ADMIN = 461;

    /*
     *  462: the specified user not apply connection to the computer yet
     */
    int HTTP_STATUS_USER_NOT_APPLY_CONNECTION_YET = 462;

    /*
     *  463: the specified user have't been approved to access the computer
     */
    int HTTP_STATUS_APPLY_CONNECTION_NOT_APPROVED_YET = 463;

    /*
     *  464: the specified country is not supported yet
     */
    int HTTP_STATUS_COUNTRY_NOT_SUPPORTED = 464;

    /*
     *  465: the version of the desktop is too old and the desktop application needs upgrade
     */
    int HTTP_STATUS_DESKTOP_VERSION_TOO_OLD = 465;

    /*
     *  466: the version of the device is too old and the device app needs upgrade
     */
    int HTTP_STATUS_DEVICE_VERSION_TOO_OLD = 466;

    /*
     *  467: the phone number has been taken and need to change a new one
     */
    int HTTP_STATUS_SHOULD_UPDATE_PHONE_NUMBER = 467;

    /*
     *  468: user's email address is empty.
     */
    int HTTP_STATUS_EMPTY_USER_EMAIL = 468;

    /*
     *  469: user is an administrator of a computer and
     *  there's at least one non-administrator user allowed to connect to this computer.
     */
    int HTTP_STATUS_USER_ALLOWED_NON_ADMIN_USERS = 469;

    /*
     *  470: file size is larger than the download/upload file size limit.
     */
    Integer HTTP_STATUS_FILE_SIZE_LIMIT_EXCEEDED = 470;

    /*
     * 499: request closed by user
     */
    int HTTP_STATUS_CLIENT_CLOSE_REQUEST = 499;
}
