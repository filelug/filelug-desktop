package com.filelug.desktop.exception;

/**
 * <code>FilePermissionDeniedException</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class FilePermissionDeniedException extends RuntimeException {
    private static final long serialVersionUID = 777560639095547181L;

    private String filePath;

    private String privilege;

    public FilePermissionDeniedException() {
    }

    public FilePermissionDeniedException(String filePath, String privilege) {
        super();

        this.filePath = filePath;
        this.privilege = privilege;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }
}
