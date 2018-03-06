package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <code>FileRenameModel</code> is the model for renaming directory or file.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class FileRenameModel {

    @JsonProperty("oldPath")
    private String oldPath;

    @JsonProperty("newPath")
    private String newPath;

    @JsonProperty("oldFilename")
    private String oldFilename;

    @JsonProperty("newFilename")
    private String newFilename;

    public FileRenameModel() {
    }

    public FileRenameModel(String oldPath, String newPath, String oldFilename, String newFilename) {
        this.oldPath = oldPath;
        this.newPath = newPath;
        this.oldFilename = oldFilename;
        this.newFilename = newFilename;
    }

    public String getOldPath() {
        return oldPath;
    }

    public void setOldPath(String oldPath) {
        this.oldPath = oldPath;
    }

    public String getNewPath() {
        return newPath;
    }

    public void setNewPath(String newPath) {
        this.newPath = newPath;
    }

    public String getOldFilename() {
        return oldFilename;
    }

    public void setOldFilename(String oldFilename) {
        this.oldFilename = oldFilename;
    }

    public String getNewFilename() {
        return newFilename;
    }

    public void setNewFilename(String newFilename) {
        this.newFilename = newFilename;
    }
}
