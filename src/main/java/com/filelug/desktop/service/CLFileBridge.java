package com.filelug.desktop.service;

import ca.weblite.objc.Client;
import ca.weblite.objc.NSObject;
import ca.weblite.objc.Proxy;
import ch.qos.logback.classic.Logger;
import com.filelug.desktop.OSUtility;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * <code>CLFileBridge</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class CLFileBridge extends NSObject {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("CLFILE");

    private static boolean bundleLoaded = false;

    private String absFilePath;

    public CLFileBridge(String absFilePath) {
        super();

        if (!bundleLoaded) {
            loadBundle();

            bundleLoaded = true;
        }

        init("CLFile");

        this.absFilePath = absFilePath;
    }

    private synchronized void loadBundle() {
        String bundleParentPath = System.getProperty("bundle.resources");

        if (bundleParentPath == null || bundleParentPath.trim().length() < 1) {
            bundleParentPath = "install";

            // DEBUG
//            LOGGER.info("Use developer bundle parent path: " + bundleParentPath);
        } else {
            // DEBUG
//            LOGGER.info("Bundle parent path: " + bundleParentPath);
        }

        // Use ca path to prevent symlink
        try {
            String bundlePath = new File(bundleParentPath, "fileinfobundle.bundle").getCanonicalPath();

            Proxy clFileBundle = Client.getInstance().sendProxy("NSBundle", "bundleWithPath:", bundlePath);

            clFileBundle.send("load");
        } catch (Exception e) {
            LOGGER.error("Failed to load bundle. Make sure your application is not in an alias directory.\n", e);
        }
    }

    public boolean isAliasFile() {
        return sendBoolean("isAliasFile:", absFilePath);
    }

    public String resolveAliasFile() {
        return sendString("resolveAliasFile:", absFilePath);
    }

//    public static void main(String[] args) {
//        String testFilePath = "/Users/masonhsieh/test/clopuccino alias";
//
//        CLFileBridge clFile = new CLFileBridge(testFilePath);
//
//        boolean isAlias = clFile.isAliasFile();
//
//        System.out.println("File '" + testFilePath + "' is " + (isAlias ? "an " : "not an ") + "alias file");
//
//        if (isAlias) {
//            System.out.println("'" + clFile.resolveAliasFile() + "' is the real path of file: '" + testFilePath + "'");
//        }
//
//    }

}
