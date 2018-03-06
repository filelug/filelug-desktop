package com.filelug.desktop;


import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Iterator;


/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class OSUtilityTest {

    @Test
    public void testComputerName() throws Exception {
        String finalComputerName;

        OSUtility.OSType osType = OSUtility.getOSType();

        finalComputerName = OSUtility.computerNameFromScript(osType);

        if (finalComputerName == null || finalComputerName.length() < 1) {
            finalComputerName = OSUtility.computerNameFromInternetAdapter();

            if (finalComputerName == null || finalComputerName.length() < 1) {
                finalComputerName = OSUtility.computerNameFromMXBean();
            }
        }

        System.out.println(finalComputerName);
    }

    @Test
    public void testSystemRoot() throws Exception {
        String root = "/";
//        String root = "C:\\";

        String absPath = new File(root).getAbsolutePath();

        System.out.println("Absolute path: '" + absPath + "'");

        String name = FilenameUtils.getName(absPath);

        System.out.println("name: '" + name + "'");

        String parentPath = FilenameUtils.getFullPathNoEndSeparator(absPath);

        System.out.println("parent: '" + parentPath + "'");

    }

    @Test
    public void testSystemRoot2() throws Exception {
        FileSystem fileSystem = FileSystems.getDefault();

        Iterable<Path> pathIterable = fileSystem.getRootDirectories();

        Iterator<Path> pathIterator = pathIterable.iterator();

        for (; pathIterator.hasNext(); ) {
            Path rootPath = pathIterator.next();

            System.out.println("Root: '" + rootPath + "'");

            System.out.println("Root filename: '" + rootPath.getFileName() + "'");

            System.out.println("Root parent: '" + rootPath.getParent() + "'");
        }
    }
}
