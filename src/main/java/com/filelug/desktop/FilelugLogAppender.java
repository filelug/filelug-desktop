package com.filelug.desktop;

import ch.qos.logback.core.recovery.ResilientFileOutputStream;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * <code>FilelugLogAppender</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class FilelugLogAppender extends RollingFileAppender {

    @Override
    public void setFile(String file) {
        if (file == null) {
            fileName = null;
        } else {
            File parent = OSUtility.getApplicationDataDirectoryFile();

            fileName = new File(parent, file.trim()).getAbsolutePath();
        }
    }

//    @Override
//    public void openFile(String file_name) throws IOException {
//        synchronized (lock) {
//            File parent = OSUtility.getApplicationDataDirectoryFile();
//
//            File file = new File(parent, file_name);
//
//            if (FileUtil.isParentDirectoryCreationRequired(file)) {
//                boolean result = FileUtil.createMissingParentDirectories(file);
//                if (!result) {
//                    addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
//                }
//            }
//
//            ResilientFileOutputStream resilientFos = new ResilientFileOutputStream(file, append);
//            resilientFos.setContext(context);
//            setOutputStream(resilientFos);
//        }
//    }
}
