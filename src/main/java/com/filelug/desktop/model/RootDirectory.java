package com.filelug.desktop.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.filelug.desktop.exception.NotDirectoryException;

/**
 * <code>RootDirectory</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class RootDirectory implements Cloneable, Comparable {

    @JsonProperty("path")
    private String path;

    /* label to display for this root directory */
    @JsonProperty("label")
    private String label;

    @JsonProperty("realPath")
    private String realPath;

    /* default use name() as the json value, such as FILE and DIRECTORY */
    @JsonProperty("type")
    private HierarchicalModelType type;

    public RootDirectory() {
    }

    public RootDirectory(String path, String realPath, String label, HierarchicalModelType type) throws NotDirectoryException {
        // Do not check if the absolute path is directory, for Windows roots, such as 'D:\' is not a directory

        this.path = path;
        this.realPath = realPath;
        this.label = label;
        this.type = type;
    }

    @Override
    public RootDirectory clone() throws CloneNotSupportedException {
        return (RootDirectory) super.clone();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public HierarchicalModelType getType() {
        return type;
    }

    public void setType(HierarchicalModelType type) {
        this.type = type;
    }

    @Override
    public int compareTo(Object anotherObject) {
        // home directory always the first one, then the local disk
        // use label, path, realPath in sequence for the rest of the conditions.

        if (RootDirectory.class.isInstance(anotherObject)) {
            RootDirectory anotherRootDirectory = (RootDirectory) anotherObject;

            if (type == HierarchicalModelType.USER_HOME) {
                return 1;
            } else if (anotherRootDirectory.getType() == HierarchicalModelType.USER_HOME) {
                return -1;
            } else if (type == HierarchicalModelType.LOCAL_DISK && anotherRootDirectory.getType() != HierarchicalModelType.LOCAL_DISK) {
                return 1;
            } else if (anotherRootDirectory.getType() == HierarchicalModelType.LOCAL_DISK && type != HierarchicalModelType.LOCAL_DISK) {
                return -1;
            } else {
                if (label != null && anotherRootDirectory.getLabel() != null) {
                    return label.compareToIgnoreCase(anotherRootDirectory.getLabel());
                } else if (path != null && anotherRootDirectory.getPath() != null) {
                    return path.compareToIgnoreCase(anotherRootDirectory.getPath());
                } else if (realPath != null && anotherRootDirectory.getRealPath() != null) {
                    return realPath.compareToIgnoreCase(anotherRootDirectory.getRealPath());
                } else {
                    return 0;
                }
            }
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RootDirectory{");
        sb.append("path='").append(path).append('\'');
        sb.append(", label='").append(label).append('\'');
        sb.append(", realPath='").append(realPath).append('\'');
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
