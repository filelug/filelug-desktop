package com.filelug.desktop;

import ch.qos.logback.core.joran.spi.NoAutoStart;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
//import org.apache.commons.io.FilenameUtils;

import java.io.File;
//import java.io.FilenameFilter;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;

/**
 * <code>FilelugSizeAndTimeBasedFNATP</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
@NoAutoStart
public class FilelugSizeAndTimeBasedFNATP<E> extends SizeAndTimeBasedFNATP<E> {

//    private static final String LOG_FILENAME_PREFIX = "filelug-desktop";
//
//    private static final int DELETE_INTERVAL_IN_DAYS = 2;
//
//    private static ScheduledThreadPoolExecutor poolExecutor;

    @Override
    public String getCurrentPeriodsFileNameWithoutCompressionSuffix() {
        File parent = OSUtility.getApplicationDataDirectoryFile();

        return new File(parent, super.getCurrentPeriodsFileNameWithoutCompressionSuffix()).getAbsolutePath();
    }

//    @Override
//    public void start() {
//        super.start();
//
//        // Delete files more than value of max history every two days
//
//        if (poolExecutor != null) {
//            shutdownPoolExecutor();
//        }
//
//        poolExecutor = new ScheduledThreadPoolExecutor(1);
//
//        final int maxHistory = tbrp.getMaxHistory();
//
//        final String currentLogFilePath = getCurrentPeriodsFileNameWithoutCompressionSuffix();
//
//        final File parent = new File(FilenameUtils.getFullPath(currentLogFilePath));
//
//        poolExecutor.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                if (parent.exists() && parent.isDirectory()) {
//                    File[] logFiles = parent.listFiles(new FilenameFilter() {
//                        @Override
//                        public boolean accept(File dir, String name) {
//                            return name.startsWith(LOG_FILENAME_PREFIX);
//                        }
//                    });
//
//                    if (logFiles != null && logFiles.length > maxHistory) {
//                        List<File> files = Arrays.asList(logFiles);
//
//                        // reversed so the first one is the latest file.
//                        Collections.sort(files, new Comparator<File>() {
//                            @Override
//                            public int compare(File file1, File file2) {
//                                return file2.getName().compareTo(file1.getName());
//                            }
//                        });
//
//                        List<File> removedList = files.subList(maxHistory, files.size());
//
//                        for (File file : removedList) {
//                            String filename = file.getName();
//                            if (file.delete()) {
//                                System.out.println(filename + " deleted.");
//                            } else {
//                                System.out.println(filename + " deleted failed.");
//                            }
//
//                        }
//                    }
//                }
//            }
//        }, 0, DELETE_INTERVAL_IN_DAYS, TimeUnit.DAYS);
//    }

//    @Override
//    public void stop() {
//        super.stop();
//
//        shutdownPoolExecutor();
//    }
//
//    public static void shutdownPoolExecutor() {
//        try {
//            if (poolExecutor != null && !poolExecutor.isTerminating() && !poolExecutor.isTerminated()) {
//                poolExecutor.shutdown();
//
//                /* wait 1 second to finished previous tasks to finish */
//                poolExecutor.awaitTermination(1, TimeUnit.SECONDS);
//            }
//        } catch (Exception e) {
//            // ignored
//        }
//    }
}
