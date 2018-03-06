package com.filelug.desktop.model;

import ch.qos.logback.classic.Logger;
import com.filelug.desktop.Constants;
import com.filelug.desktop.Utility;
import org.slf4j.LoggerFactory;

import java.util.Observable;

/**
 * <code>NewVersionAvailableState</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class NewVersionAvailableState extends Observable {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("STATE_NEW_VERSION");

    private static NewVersionAvailableState theInstance;

    private String newVersion;

    private String downloadUrl;

    public static NewVersionAvailableState getInstance() {
        if (theInstance == null) {
            theInstance = new NewVersionAvailableState();
        }

        return theInstance;
    }

    private NewVersionAvailableState() {
    }


    public String getNewVersion() {
        return newVersion;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setNewVersion(String newVersion, String downloadUrl) {
        String oldVersion = this.newVersion;

        this.downloadUrl = downloadUrl;

        if (oldVersion == null || !oldVersion.equals(newVersion)) {
            this.newVersion = newVersion;

//            Utility.putPreference(PropertyConstants.PREFS_KEY_FILELUG_DESKTOP_LATEST_VERSION, newVersion);

            LOGGER.debug("New version available: " + newVersion);

            setChanged();
            notifyObservers();
        }
    }
}
