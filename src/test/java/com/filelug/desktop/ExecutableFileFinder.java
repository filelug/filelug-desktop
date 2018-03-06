package com.filelug.desktop;

import java.io.File;

/**
 * <code>ExecutableFileFinder</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ExecutableFileFinder {

    private static void listExecutableFiles(File dir) {
        File[] subFiles = dir.listFiles();

        if (subFiles != null && subFiles.length > 0) {
            for (File subFile : subFiles) {
                if (subFile.isDirectory()) {
                    listExecutableFiles(subFile);
                } else {
                    if (subFile.canExecute()) {
                        System.out.println(subFile.getAbsolutePath());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {

        String filePath = "/Users/masonhsieh/projects/clopuccino/codebase/filelug-desktop/filelug-desktop-dev/install/jre/jre1.7.0_60";

        File dir = new File(filePath);

        listExecutableFiles(dir);
    }

}
