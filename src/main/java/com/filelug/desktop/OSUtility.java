package com.filelug.desktop;

import com.filelug.desktop.model.DeviceToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;

/**
 * Utilities for OS-related
 */
public class OSUtility {

    /* Logger class is FilelugLogAppender, which use the method getApplicationDataDirectoryFile,
     * so DO NOT USE LOGGER in this class
     */
//    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("OS_UTIL");

    public static final boolean USE_HTTPS = Boolean.parseBoolean(System.getProperty("use.https", String.valueOf(true)));

    // shell script for computer name
    private static final String scriptForMac = "scutil --get ComputerName";
    private static final String scriptForWindows = "echo %COMPUTERNAME%";
    private static final String scriptForLinux = "uname -n";

    public static File getApplicationDataDirectoryFile() {
        File directory;
        String dataDirectoryName;

        if (USE_HTTPS) {
            dataDirectoryName = Constants.DEFAULT_DATA_DIRECTORY_NAME_FOR_PRODUCTION;
        } else {
            dataDirectoryName = Constants.DEFAULT_DATA_DIRECTORY_NAME_FOR_TESTING;
        }

        // data directory name prefix with '.'
        directory = new File(System.getProperty("user.home"), "." + dataDirectoryName);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        return directory;
    }

    public static File getApplicationDataDirectoryFile_V1() {
        File directory;
        String dataDirectoryName;

        if (USE_HTTPS) {
            dataDirectoryName = System.getProperty("bundle.name", Constants.DEFAULT_DATA_DIRECTORY_NAME_FOR_PRODUCTION_V1);
        } else {
            dataDirectoryName = Constants.DEFAULT_DATA_DIRECTORY_NAME_FOR_TESTING_V1;
        }

        // data directory name prefix with '.'
        directory = new File(System.getProperty("user.home"), "." + dataDirectoryName);

        // Do not create directory if not exists
//        if (!directory.exists()) {
//            directory.mkdirs();
//        }

        return directory;
    }

    public enum OSType {
        WINDOWS, LINUX, OS_X, SOLARIS, HP_UX, BSD, OTHERS
    }

    private static final String lowerCaseOsName = System.getProperty("os.name").toLowerCase();

    public OSUtility() {
    }

    public static String computerNameFromInternetAdapter() {
        String computerName = "";

        try {
            computerName = InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            // ignored
        }

        return computerName;
    }

    public static String computerNameFromMXBean() {
        String computerName = "";

        try {
            computerName = ManagementFactory.getRuntimeMXBean().getName();
        } catch (Exception e) {
            // ignored
        }

        return computerName;
    }

    public static String computerNameFromScript(OSType osType) {
        String computerName;

        BufferedReader reader = null;

        try {
            String script = "";

            switch (osType) {
                case WINDOWS:
                    script = scriptForWindows;

                    break;
                case LINUX:
                    script = scriptForLinux;

                    break;
                case OS_X:
                    script = scriptForMac;

                    break;
                default:
                    script = scriptForLinux;
            }

            Process process = Runtime.getRuntime().exec(script);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Constants.DEFAULT_FILE_READ_WRITE_CHARSET));
            computerName = reader.readLine();
        } catch (Exception e) {
            computerName = "";
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e1) {
                    // ignored
                }
            }
        }

        return computerName;
    }

    public static OSType getOSType() {
        OSType osType;

        if (isWindows()) {
            osType = OSType.WINDOWS;
        } else if (isLinux()) {
            osType = OSType.LINUX;
        } else if (isOSX()) {
            osType = OSType.OS_X;
        } else if (isSolaris()) {
            osType = OSType.SOLARIS;
        } else if (isHpUnix()) {
            osType = OSType.HP_UX;
        } else if (isBSD()) {
            osType = OSType.BSD;
        } else {
            osType = OSType.OTHERS;
        }

        return osType;
    }

    public static boolean isBSD() {
        return lowerCaseOsName.contains("bsd");
    }

    public static boolean isHpUnix() {
        return lowerCaseOsName.contains("hp-ux");
    }

    public static boolean isSolaris() {
        return lowerCaseOsName.contains("solaris");
    }

    public static boolean isOSX() {
        return lowerCaseOsName.contains("mac os x");
    }

    public static boolean isLinux() {
        return lowerCaseOsName.contains("linux");
    }

    public static boolean isWindows() {
        return lowerCaseOsName.contains("windows");
    }
}
