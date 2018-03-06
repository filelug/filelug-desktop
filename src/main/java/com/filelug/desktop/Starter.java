package com.filelug.desktop;

import ch.qos.logback.classic.Logger;
import com.filelug.desktop.service.ProcessExitDetector;
import com.filelug.desktop.service.ProcessListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class Starter implements ProcessListener {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("START");

    public Starter(Process applicationProcess) {
        ProcessExitDetector processExitDetector = new ProcessExitDetector(applicationProcess);

        processExitDetector.addProcessListener(this);
    }

    @Override
    public void processFinished(Process process) {
        if (process.exitValue() == Constants.EXIT_CODE_ON_SOFTWARE_UPDATE) {
            // find patch file
            File patchFile = new File(OSUtility.getApplicationDataDirectoryFile(), Constants.SOFTWARE_PATCH_FILE_NAME);

            if (patchFile.exists()) {
                File extractDirectory = createTempPatchDirectory();

                // extract to tmp
                try {
                    Utility.unzip(patchFile, extractDirectory);
                } catch (Exception e) {
                    LOGGER.error("Failed to unizp path file: " + patchFile.getAbsolutePath() + "\nUpdate failed.", e);
                }

                // TODO: find instruction file and run

            } else {
                LOGGER.error("No patch file found. Update failed.");
            }

        }
    }

    private File createTempPatchDirectory() {
        File tempDirectory = null;
        boolean created = false;

        do {
            tempDirectory = new File(System.getProperty("java.io.tmpdir"), Constants.SOFTWARE_PATCH_FILE_NAME + "-" + String.valueOf(System.currentTimeMillis()));

            created = tempDirectory.mkdir();

            try {
                Thread.sleep(1);
            } catch (Exception e) {
                // ignored
            }
        } while (!created);

        return tempDirectory;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        List<String> commands = new ArrayList<>();

        // 1. javaw/java
        String jreHome = System.getProperty("bundle.jre");

        if (OSUtility.isWindows()) {
            // JRE_HOME has been set in script, so we don't have to specified the full path of javaw.exe
            String javaExecutableFilePath = "javaw.exe";
            commands.add(javaExecutableFilePath);
        } else {
            String javaExecutableFilePath = null;

            if (jreHome != null) {
                String applicationName = System.getProperty("bundle.name", "Filelug");

                File javaExecutableFile = new File(new File(jreHome, "bin"), applicationName);

                if (!javaExecutableFile.exists() || !javaExecutableFile.canRead()) {
                    javaExecutableFile = new File(new File(jreHome, "bin"), "java");

                    // DEBUG
//                    System.out.print("executable file 'Filelug' not found, use 'java' instead.");
                } else {
                    // DEBUG
//                    System.out.print("Use executable file 'Filelug'.");
                }

                if (javaExecutableFile.exists() && javaExecutableFile.canRead()) {
                    javaExecutableFilePath = javaExecutableFile.getAbsolutePath();
                }
            }

//            if (jreHome != null) {
//                File javaExecutableFile = new File(new File(jreHome, "bin"), "java");
//
//                if (javaExecutableFile.exists() && javaExecutableFile.isFile()) {
//                    javaExecutableFilePath = javaExecutableFile.getAbsolutePath();
//                }
//            }

            if (javaExecutableFilePath == null) {
                javaExecutableFilePath = "java";
            }

            commands.add(javaExecutableFilePath);
        }

        LOGGER.debug("java path: " + commands.get(0));

        // 2. Virtual Machine specific options

        List<String> vmOptions = ManagementFactory.getRuntimeMXBean().getInputArguments();

        for (String vmOption : vmOptions) {
            if (vmOption.toUpperCase().startsWith("-X")) {
                commands.add(vmOption);

                LOGGER.debug("added vm option: '" + vmOption + "'");
            }
        }

        // 3. system properties

        LOGGER.debug("Start adding filelugd properties:------------------\n");

        Map<String, String> systemProperties = ManagementFactory.getRuntimeMXBean().getSystemProperties();

        if (systemProperties.size() > 0) {
            Set<Map.Entry<String, String>> entries = systemProperties.entrySet();

            String filelugdPrefix = "filelugd.";
            String filelugxPrefix = "filelugx.";

            if (OSUtility.isWindows()) {
                for (Map.Entry<String, String> entry : entries) {
                    String key = entry.getKey();

                    if (key.startsWith(filelugdPrefix)) {
                        String newKey = StringUtils.substringAfter(key, filelugdPrefix);
                        commands.add("-D\"" + newKey + "\"=\"" + entry.getValue() + "\"");

                        LOGGER.debug("-D\"" + newKey + "\"=\"" + entry.getValue() + "\"");
                    } else if (key.startsWith(filelugxPrefix)) {
                        String newKey = StringUtils.substringAfter(key, filelugxPrefix);
                        commands.add("-X\"" + newKey + "\"=\"" + entry.getValue() + "\"");

                        LOGGER.debug("-X\"" + newKey + "\"=\"" + entry.getValue() + "\"");
                    }
                }
            } else {
                for (Map.Entry<String, String> entry : entries) {
                    String key = entry.getKey();

                    if (key.startsWith(filelugdPrefix)) {
                        String newKey = StringUtils.substringAfter(key, filelugdPrefix);

                        String propertyValue = entry.getValue();

                        commands.add("-D" + newKey + "=" + propertyValue);

                        LOGGER.debug("-D" + newKey + "=" + propertyValue);
                    } else if (key.startsWith(filelugxPrefix)) {
                        String newKey = StringUtils.substringAfter(key, filelugxPrefix);

                        String propertyValue = entry.getValue();

                        commands.add("-X" + newKey + "=" + propertyValue);

                        LOGGER.debug("-X" + newKey + "=" + propertyValue);
                    }
                }
            }
        }

        LOGGER.debug("End adding filelugd properties:------------------\n");

        /* 4. classpath */
        String bundleLibraryPath = System.getProperty("bundle.lib");

        if (bundleLibraryPath == null) {
            throw new IOException("Value of system property 'bundle.lib' not specified.");
        }

        File directory = new File(bundleLibraryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            directory = new File(System.getProperty("user.dir"), bundleLibraryPath);

            if (!directory.exists() || !directory.isDirectory()) {
                throw new IOException("The library directory not exists: " + directory.getAbsolutePath());
            }
        }

        List<String> filenamesInLib = prepareFilenamesInLib();

        File[] libFiles = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.canRead() && filenamesInLib.contains(pathname.getName());
//                return pathname.isFile() && (pathname.getName().endsWith(".jar") || pathname.getName().endsWith(".zip"));
            }
        });

        if (libFiles != null && libFiles.length > 0) {
            commands.add("-classpath");

            if (OSUtility.isWindows()) {
                StringBuilder classPaths = new StringBuilder("\".");

                for (File libFile : libFiles) {
                    classPaths.append(File.pathSeparator + libFile.getAbsolutePath());
                }

                classPaths.append("\"");
                
//                for (File libFile : libFiles) {
//                    classPaths.append(File.pathSeparator + "\"" + libFile.getAbsolutePath() + "\"");
//                }

                LOGGER.debug("Added classpath\n" + classPaths.toString());

                commands.add(classPaths.toString());
            } else {
                StringBuilder classPaths = new StringBuilder(".");

                for (File libFile : libFiles) {
                    String absPath = libFile.getAbsolutePath();

                    classPaths.append(File.pathSeparator + absPath);
                }

                commands.add(classPaths.toString());

                LOGGER.debug("Added classpath\n" + classPaths.toString());
            }
        }

        /* 5. main class */
        commands.add(Application.class.getName());

        for (String arg : args) {
            commands.add(arg);
        }

        try {
            File startErrorFile = new File(new File(OSUtility.getApplicationDataDirectoryFile(), "logs"), "start-error.log");

            if (startErrorFile.exists()) {
                startErrorFile.delete();
            }

            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.to(startErrorFile));
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectError(ProcessBuilder.Redirect.to(startErrorFile));

            if (!OSUtility.isLinux()) {
                processBuilder.directory(new File(jreHome, "bin"));
            }

            Process applicationProcess = processBuilder.start();

            /* watching application process on exit */
            new Starter(applicationProcess);

            String[] commandStringArray = commands.toArray(new String[commands.size()]);

            LOGGER.debug("Script to be executed:\n" + Arrays.toString(commandStringArray));
        } catch (Exception e) {
            LOGGER.error("Error on starting application.", e);
        }
    }

    private static List<String> prepareFilenamesInLib() {
        String[] filenames = new String[] {
                "appframework-1.0.3.jar",
                "asm-all-repackaged-2.2.0-b14.jar",
                "cglib-2.2.0-b14.jar",
                "classworlds-1.1-alpha-2.jar",
                "commons-beanutils-1.8.1.jar",
                "commons-codec-1.6.jar",
                "commons-collections-3.2.1.jar",
                "commons-configuration-1.10.jar",
                "commons-dbcp2-2.1.1.jar",
                "commons-digester-1.8.1.jar",
                "commons-io-2.4.jar",
                "commons-jexl-1.1.jar",
                "commons-jxpath-1.3.jar",
                "commons-lang-2.6.jar",
                "commons-lang3-3.1.jar",
                "commons-logging-1.1.1.jar",
                "commons-pool2-2.4.2.jar",
                "commons-vfs2-2.0.jar",
                "core-3.3.0.jar",
                "fdesktop.jar",
                "guava-18.0.jar",
                "hk2-api-2.2.0-b14.jar",
                "hk2-locator-2.2.0-b14.jar",
                "hk2-utils-2.2.0-b14.jar",
                "hsqldb-2.3.2.jar",
                "httpasyncclient-4.1.jar",
                "httpclient-4.5.1.jar",
                "httpcore-4.4.3.jar",
                "httpcore-nio-4.4.1.jar",
                "jackson-annotations-2.8.6.jar",
                "jackson-core-2.8.6.jar",
                "jackson-databind-2.8.6.jar",
                "jackson-dataformat-xml-2.8.6.jar",
                "jackson-module-jaxb-annotations-2.8.6.jar",
                "java-objc-bridge-1.0.0.jar",
                "javax-websocket-client-impl-9.3.14.v20161028.jar",
                "javax.annotation-api-1.2.jar",
                "javax.inject-2.2.0-b14.jar",
                "javax.servlet-api-3.1.0.jar",
                "javax.websocket-api-1.1.jar",
                "javax.websocket-client-api-1.1.jar",
                "javax.ws.rs-api-2.0.jar",
                "jersey-common-2.2.jar",
                "jetty-io-9.3.14.v20161028.jar",
                "jetty-util-9.3.14.v20161028.jar",
                "jna-4.1.0.jar",
                "jna-platform-4.1.0.jar",
                "logback-classic-1.1.3.jar",
                "logback-core-1.1.3.jar",
                "osgi-resource-locator-1.0.1.jar",
                "plexus-container-default-1.0-alpha-9-stable-1.jar",
                "plexus-utils-1.5.6.jar",
                "properties-maven-plugin-1.0-alpha-2.jar",
                "regexp-1.3.jar",
                "serializer-2.7.1.jar",
                "slf4j-api-1.7.13.jar",
                "stax-api-1.0-2.jar",
                "stax2-api-3.1.1.jar",
                "swing-worker-1.1.jar",
                "swingx-all-1.6.5-1.jar",
                "swingx-mavensupport-1.6.5-1.jar",
                "tika-core-1.3.jar",
                "wagon-provider-api-1.0-beta-2.jar",
                "websocket-api-9.3.14.v20161028.jar",
                "websocket-client-9.3.14.v20161028.jar",
                "websocket-common-9.3.14.v20161028.jar",
                "woodstox-core-5.0.3.jar",
                "woodstox-core-asl-4.1.4.jar",
                "xalan-2.7.1.jar",
                "xml-apis-1.3.04.jar",
                "xml-resolver-1.2.jar"
        };

        return Arrays.asList(filenames);
    }
}
