package com.filelug.desktop;


import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;


/**
 * <code>ZipTest</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ZipTest {

    @Test
    public void testZip() throws Exception {
        Path sourceDirectory = new File("/Users/masonhsieh/Downloads/temp/帳號資訊組件狀態連動表.numbers").toPath();

        Path destZipFile = new File("/Users/masonhsieh/Downloads/temp/帳號資訊組件狀態連動表.numbers.zip").toPath();

        try {
            Utility.zipDirectory(sourceDirectory, destZipFile);
        } catch (IOException e) {
            System.err.println("Error on zip directory: '" + sourceDirectory.getFileName() + "' to file: '" + destZipFile.getFileName() + "'\n" + e.getClass().getName() + "\n" + e.getMessage());
        }
    }
}
