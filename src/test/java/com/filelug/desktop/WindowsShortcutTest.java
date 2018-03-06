package com.filelug.desktop;

import org.junit.Test;
import sun.awt.shell.ShellFolder;

import java.io.File;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class WindowsShortcutTest {

    @Test
    public void testShellFolderWithDirectory() throws Exception {
        String filePath = "C:\\shortcut 測試的許功蓋";
//        String filePath = "C:\\test";
//        String filePath = "C:\\test\\捷徑 -  shortcut 測試的許功蓋";

        File file = new File(filePath);

        // ShellFolder not working for Mac alias
        if (OSUtility.isWindows()) {
            ShellFolder parentShellFolder = ShellFolder.getShellFolder(file);

            File[] children = parentShellFolder.listFiles();

            if (children != null && children.length > 0) {
                for (File child : children) {
                    ShellFolder childShellFolder = ShellFolder.getShellFolder(child);

                    System.out.println("-------\nAbsolute path: " + childShellFolder.getAbsolutePath()
                                       + "\nCanonical path: " + childShellFolder.getCanonicalPath()
                                       + "\nIs Directory: " + childShellFolder.isDirectory()
                                       + "\nDisplay name: " + childShellFolder.getDisplayName()
                                       + "\nLink location: " + (childShellFolder.isLink() ? childShellFolder.getLinkLocation().getAbsolutePath() : "(Not a link)")
                                       + "\nFolder type: " + childShellFolder.getFolderType()
                                       + "\nExecutable type: " + childShellFolder.getExecutableType()
                                       + "\n");
                }
            }
        }
    }

//    @Test // Not working
//     public void testShellFolderWithFile() throws Exception {
//        String filePath = "C:\\shortcut 測試的許功蓋\\捷徑 -  Pirate ship 海盜船.jpg";
//
//        File file = new File(filePath);
//
//        // ShellFolder not working for Mac alias
//        if (OSUtility.isWindows()) {
//            ShellFolder shellFolder = ShellFolder.getShellFolder(file);
//
//
//            System.out.println("-------\nAbsolute path: " + shellFolder.getAbsolutePath()
//                               + "\nCanonical path: " + shellFolder.getCanonicalPath()
//                               + "\nIs Directory: " + shellFolder.isDirectory()
//                               + "\nDisplay name: " + shellFolder.getDisplayName()
//                               + "\nLink location: " + (shellFolder.isLink() ? shellFolder.getLinkLocation().getAbsolutePath() : "(Not a link)")
//                               + "\nFolder type: " + shellFolder.getFolderType()
//                               + "\nExecutable type: " + shellFolder.getExecutableType()
//                               + "\n");
//        }
//    }
}
