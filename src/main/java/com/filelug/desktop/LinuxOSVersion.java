package com.filelug.desktop;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.OperationNotSupportedException;
import java.io.*;

/**
 * <code>LinuxOSVersion</code> finds the name, version, and arch for Linux OSs
 * PS. It works for platform name only, and the rest of them are the same with related java system properties.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class LinuxOSVersion {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(LinuxOSVersion.class.getSimpleName());

    private OsInfo osInfo;

    public LinuxOSVersion() throws OperationNotSupportedException {
        if (!OSUtility.isLinux()) {
            throw new OperationNotSupportedException("The service is for Linux only");
        }

        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");

        initLinuxOsInfo(osName, osVersion, osArch);
    }

    public String getName() {
        return osInfo.getName();
    }

    public String getArch() {
        return osInfo.getArch();
    }

    public String getVersion() {
        return osInfo.getVersion();
    }

    public String getPlatformName() {
        return osInfo.getPlatformName();
    }

    private void initLinuxOsInfo(final String name, final String version, final String arch) {
        OsInfo osInfo;
        // The most likely is to have a LSB compliant distro
        osInfo = getPlatformNameFromLsbRelease(name, version, arch);

        // Generic Linux platform name
        if (osInfo == null)
            osInfo = getPlatformNameFromFile(name, version, arch, "/etc/system-release");

        File dir = new File("/etc/");
        if (dir.exists()) {
            // if generic 'system-release' file is not present, then try to find another one
            if (osInfo == null)
                osInfo = getPlatformNameFromFile(name, version, arch, getFileEndingWith(dir, "-release"));

            // if generic 'system-release' file is not present, then try to find '_version'
            if (osInfo == null)
                osInfo = getPlatformNameFromFile(name, version, arch, getFileEndingWith(dir, "_version"));

            // try with /etc/issue file
            if (osInfo == null)
                osInfo = getPlatformNameFromFile(name, version, arch, "/etc/issue");

        }

        // if nothing found yet, looks for the version info
        File fileVersion = new File("/proc/version");
        if (fileVersion.exists()) {
            if (osInfo == null)
                osInfo = getPlatformNameFromFile(name, version, arch, fileVersion.getAbsolutePath());
        }

        // if nothing found, well...
        if (osInfo == null)
            osInfo = new OsInfo(name, version, arch, name);

        this.osInfo = osInfo;

        // DEBUG
//        LOGGER.info(toString());
    }

    private String getFileEndingWith(final File dir, final String fileEndingWith) {
        File[] fileList = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(fileEndingWith);
            }
        });

        if (fileList != null && fileList.length > 0) {
            return fileList[0].getAbsolutePath();
        } else {
            return null;
        }
    }

    private OsInfo getPlatformNameFromFile(final String name, final String version, final String arch, final String filename) {
        if (filename == null)
            return null;
        File f = new File(filename);
        if (f.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(filename));
                return readPlatformName(name, version, arch, br);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private OsInfo readPlatformName(final String name, final String version, final String arch, final BufferedReader br) throws IOException {
        String line;
        String lineToReturn = null;
        int lineNb = 0;
        while ((line = br.readLine()) != null) {
            if (lineNb++ == 0) {
                lineToReturn = line;
            }
            if (line.startsWith("PRETTY_NAME"))
                return new OsInfo(name, version, arch, line.substring(13, line.length() - 1));
        }
        return new OsInfo(name, version, arch, lineToReturn);
    }

    private OsInfo getPlatformNameFromLsbRelease(final String name, final String version, final String arch) {
        String fileName = "/etc/lsb-release";
        File f = new File(fileName);
        if (f.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                return readPlatformNameFromLsb(name, version, arch, br);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private OsInfo readPlatformNameFromLsb(final String name, final String version, final String arch, final BufferedReader br) throws IOException {
        String distribDescription = null;
        String distribCodename = null;

        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("DISTRIB_DESCRIPTION"))
                distribDescription = line.replace("DISTRIB_DESCRIPTION=", "").replace("\"", "");
            if (line.startsWith("DISTRIB_CODENAME"))
                distribCodename = line.replace("DISTRIB_CODENAME=", "");
        }
        if (distribDescription != null && distribCodename != null) {
            return new OsInfo(name, version, arch, distribDescription + " (" + distribCodename + ")");
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LinuxOSVersion\n{");
        sb.append("name=").append(getName() + ",\n");
        sb.append("version=").append(getVersion() + ",\n");
        sb.append("arch=").append(getArch() + ",\n");
        sb.append("platform name=").append(getPlatformName() + "\n");
        sb.append('}');
        return sb.toString();
    }

    class OsInfo {
        private String name;

        private String arch;

        private String version;

        private String platformName;

        private OsInfo(final String name, final String version, final String arch, final String platformName) {
            this.name = name;
            this.arch = arch;
            this.version = version;
            this.platformName = platformName;
        }

        public String getName() {
            return name;
        }

        public String getArch() {
            return arch;
        }

        public String getVersion() {
            return version;
        }

        public String getPlatformName() {
            return platformName;
        }
    }
}
