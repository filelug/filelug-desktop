package com.filelug.desktop.model;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.filelug.desktop.Constants;
import com.filelug.desktop.OSUtility;
import com.filelug.desktop.Utility;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ResourceBundle;

/**
 * <code>HierarchicalModel</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class HierarchicalModel {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("MODEL_HIERARCHICAL");

    private static final String CONTENT_TYPE_UNKNOWN = "application/octet-stream";

    private static final ResourceBundle mimeTypeBundle = ResourceBundle.getBundle("MimeType");

    private static FileSystemManager fileSystemManager;

    // including *nix symlink, windows shortcut, and mac alias
    @JsonProperty("symlink")
    protected Boolean symlink;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("parent")
    protected String parent;

    @JsonProperty("realName")
    protected String realName;

    @JsonProperty("realParent")
    protected String realParent;

    @JsonProperty("readable")
    protected Boolean readable;

    /* owner only */
    @JsonProperty("writable")
    protected Boolean writable;

    /* owner only */
    @JsonProperty("executable")
    protected Boolean executable;

    /* The size of a directory recursively (sum of the length of all files), calculated on demand */
    @JsonProperty("displaySize")
    protected String displaySize;

    @JsonProperty("sizeInBytes")
    protected Long sizeInBytes;

    @JsonProperty("hidden")
    protected Boolean hidden;

    @JsonProperty("lastModified")
    protected String lastModified;

    /* default use name() as the json value, such as FILE and DIRECTORY */
    @JsonProperty("type")
    protected HierarchicalModelType type;

    @JsonProperty("contentType")
    protected String contentType;

    public static FileSystemManager getFileSystemManager() {
        if (fileSystemManager == null) {
            try {
                fileSystemManager = VFS.getManager();
            } catch (Exception e) {
                LOGGER.error("Failed to create file system manager", e);
            }
        }

        return fileSystemManager;
    }

    public File toFile() throws Exception {
        File newFile = new File(parent, name);

        newFile.setExecutable(executable);
        newFile.setWritable(writable);
        newFile.setReadable(readable);
        newFile.setLastModified(Constants.DEFAULT_DATE_FORMAT.parse(lastModified).getTime());

        return newFile;
    }

    public static String prepareContentType(File file) {
        return prepareContentTypeFromFileExtension(FilenameUtils.getExtension(file.getName()));
    }

    public static String prepareContentType(String filename) {
        return prepareContentTypeFromFileExtension(FilenameUtils.getExtension(filename));
    }

    public static String prepareContentTypeFromFileExtension(String fileExtension) {
        String mimeType = null;

        if (fileExtension != null && fileExtension.trim().length() > 0) {
            String extensionLowerCase = fileExtension.toLowerCase();
            if (mimeTypeBundle.containsKey(extensionLowerCase)) {
                mimeType = mimeTypeBundle.getString(extensionLowerCase);
            }
        }

        return mimeType != null ? mimeType : CONTENT_TYPE_UNKNOWN;
    }

//    public static String prepareContentType(File file) {
//        String mimeType;
//
//        String fileExtension = FilenameUtils.getExtension(file.getName());
//
//        if (fileExtension != null && fileExtension.trim().length() > 0 && mimeTypeBundle.containsKey(fileExtension)) {
//            mimeType = mimeTypeBundle.getString(fileExtension);
//        } else {
//            mimeType = CONTENT_TYPE_UNKNOWN;
//        }
//
//        return mimeType;
//    } // end prepareContenttype()

    public static String prepareContentTypeUsingTika(File file) {
        String mimeType = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);

            BodyContentHandler contentHandler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());

            AutoDetectParser parser = new AutoDetectParser();
            parser.parse(fileInputStream, contentHandler, metadata);

            mimeType = metadata.get(Metadata.CONTENT_TYPE);
        } catch (Throwable t) {
            LOGGER.warn("Failed to find content type of file: " + file.getAbsolutePath(), t);
            mimeType = CONTENT_TYPE_UNKNOWN;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Throwable t) {
                    /* ignored */
                }
            }
        }

        return mimeType;
    } // end prepareContentTypeUsingTika()

    // WindowsShortcut can not find real path if any Chinese characters in the real path.
//    public static HierarchicalModelType prepareHierarchicalModelType(File file) {
//        HierarchicalModelType type;
//
//        if (OSUtility.isWindows()) {
//            try {
//                FileObject fileObject = getFileSystemManager().toFileObject(file);
//
//                if (WindowsShortcut.isPotentialValidLink(fileObject)) {
//                    WindowsShortcut shortcut = new WindowsShortcut(file);
//
//                    if (shortcut.isDirectory()) {
//                        type = HierarchicalModelType.WINDOWS_SHORTCUT_DIRECTORY;
//                    } else {
//                        type = HierarchicalModelType.WINDOWS_SHORTCUT_FILE;
//                    }
//                } else {
//                    if (file.isDirectory()) {
//                        type = HierarchicalModelType.DIRECTORY;
//                    } else {
//                        type = HierarchicalModelType.FILE;
//                    }
//                }
//            } catch (Exception e) {
//                if (file.isDirectory()) {
//                    type = HierarchicalModelType.DIRECTORY;
//                } else {
//                    type = HierarchicalModelType.FILE;
//                }
//
//                LOGGER.debug("Failed to parse file '" + file.getAbsolutePath() + "' to file object. Use general rule to file hierarchical model type to: " + type.name(), e);
//            }
//        } else {
//            try {
//                if (Utility.isUnixSymlink(file)) {
//                    /* symbolic link */
//                    if (new File(file.getCanonicalPath()).isDirectory()) {
//                        type = HierarchicalModelType.UNIX_SYMBOLIC_LINK_DIRECTORY;
//                    } else {
//                        type = HierarchicalModelType.UNIX_SYMBOLIC_LINK_FILE;
//                    }
//                } else {
//                    if (OSUtility.isOSX()) {
//                        // Check mac alias
//
//                        CLFileBridge destFileBridge = new CLFileBridge(file.getAbsolutePath());
//
//                        if (destFileBridge.isAliasFile()) {
//                            // find real path
//                            String realFilePath = destFileBridge.resolveAliasFile();
//
//                            if (realFilePath != null) {
//                                if (new File(realFilePath).isDirectory()) {
//                                    type = HierarchicalModelType.MAC_ALIAS_DIRECTORY;
//                                } else {
//                                    type = HierarchicalModelType.MAC_ALIAS_FILE;
//                                }
//                            } else {
//                                throw new Exception("Can't find real file path from alias file: " + file.getAbsolutePath());
//                            }
//                        } else {
//                            // not an alias
//
//                            if (file.isDirectory()) {
//                                type = HierarchicalModelType.DIRECTORY;
//                            } else {
//                                type = HierarchicalModelType.FILE;
//                            }
//                        }
//
//                        System.gc();
//                    } else {
//                        // not symbolic link, not a mac
//                        if (file.isDirectory()) {
//                            type = HierarchicalModelType.DIRECTORY;
//                        } else {
//                            type = HierarchicalModelType.FILE;
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                if (file.isDirectory()) {
//                    type = HierarchicalModelType.DIRECTORY;
//                } else {
//                    type = HierarchicalModelType.FILE;
//                }
//
//                LOGGER.debug("Failed to find type of file '" + file.getAbsolutePath() + "'.  Use general rule to file hierarchical model type to: " + type.name(), e);
//            }
//        }
//
//        return type;
//    }

//    public String calcuateDisplaySize() {
//        String size;
//
//        switch (getType()) {
//            case FILE:
//                size = FileUtils.byteCountToDisplaySize(new File(parent, name).length());
//
//                break;
//            case DIRECTORY:
//                size = Utility.representationFileSizeFromBytes(FileUtils.sizeOfDirectory(new File(parent, name)));
//
//                break;
//            case WINDOWS_SHORTCUT_FILE:
//            case UNIX_SYMBOLIC_LINK_FILE:
//                size = FileUtils.byteCountToDisplaySize(new File(realParent, realName).length());
//
//                break;
//            case WINDOWS_SHORTCUT_DIRECTORY:
//            case UNIX_SYMBOLIC_LINK_DIRECTORY:
//                size = Utility.representationFileSizeFromBytes(FileUtils.sizeOfDirectory(new File(realParent, realName)));
//
//                break;
//            default:
//                size = "";
//        }
//
//        return size;
//    }

    public String calcuateDisplaySize(long sizeInBytes) {
        String size;

        switch (getType()) {
            case FILE:
            case WINDOWS_SHORTCUT_FILE:
            case UNIX_SYMBOLIC_LINK_FILE:
                size = FileUtils.byteCountToDisplaySize(sizeInBytes);

                break;
            case DIRECTORY:
            case WINDOWS_SHORTCUT_DIRECTORY:
            case UNIX_SYMBOLIC_LINK_DIRECTORY:
                size = Utility.representationFileSizeFromBytes(sizeInBytes);

                break;
            default:
                size = "";
        }

        return size;
    }

    public long calcuateSizeInBytes() {
        long size;

        try {
            switch (getType()) {
                case FILE:
                    if (parent != null && parent.length() > 0) {
                        size = new File(parent, name).length();
                    } else {
                        String modifiedFilename;

                        if (OSUtility.isWindows() && name.endsWith(":")) {
                            modifiedFilename = name + File.separator;
                        } else {
                            modifiedFilename = name;
                        }

                        size = new File(modifiedFilename).length();
                    }

                    break;
                case DIRECTORY:
                    if (parent != null && parent.length() > 0) {
                        size = FileUtils.sizeOfDirectory(new File(parent, name));
                    } else {
                        String modifiedFilename;

                        if (OSUtility.isWindows() && name.endsWith(":")) {
                            modifiedFilename = name + File.separator;
                        } else {
                            modifiedFilename = name;
                        }

                        size = FileUtils.sizeOfDirectory(new File(modifiedFilename));
                    }

                    break;
                case WINDOWS_SHORTCUT_FILE:
                case UNIX_SYMBOLIC_LINK_FILE:
                    if (realParent != null && realParent.length() > 0) {
                        size = new File(realParent, realName).length();
                    } else {
                        String modifiedFilename;

                        if (OSUtility.isWindows() && realName.endsWith(":")) {
                            modifiedFilename = realName + File.separator;
                        } else {
                            modifiedFilename = realName;
                        }

                        size = new File(modifiedFilename).length();
                    }

                    break;
                case WINDOWS_SHORTCUT_DIRECTORY:
                case UNIX_SYMBOLIC_LINK_DIRECTORY:
                    if (realParent != null && realParent.length() > 0) {
                        size = FileUtils.sizeOfDirectory(new File(realParent, realName));
                    } else {
                        String modifiedFilename;

                        if (OSUtility.isWindows() && realName.endsWith(":")) {
                            modifiedFilename = realName + File.separator;
                        } else {
                            modifiedFilename = realName;
                        }

                        size = FileUtils.sizeOfDirectory(new File(modifiedFilename));
                    }

                    break;
                default:
                    size = 0;
            }
        } catch (Exception e) {
            size = 0;

            LOGGER.error("Error on calculate file/directory size for:\n" + this, e);
        }

        return size;
    }

    public Boolean getExecutable() {
        return executable;
    }

    public void setExecutable(Boolean executable) {
        this.executable = executable;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getLastModified() {
        return lastModified;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRealParent() {
        return realParent;
    }

    public void setRealParent(String realParent) {
        this.realParent = realParent;
    }

    public Boolean getReadable() {
        return readable;
    }

    public void setReadable(Boolean readable) {
        this.readable = readable;
    }

    public Boolean getSymlink() {
        return symlink;
    }

    public void setSymlink(Boolean symlink) {
        this.symlink = symlink;
    }

    public Boolean getWritable() {
        return writable;
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    public String getDisplaySize() {
        return displaySize;
    }

    public void setDisplaySize(String displaySize) {
        this.displaySize = displaySize;
    }

    public Long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(Long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public HierarchicalModelType getType() {
        return type;
    }

    public void setType(HierarchicalModelType type) {
        this.type = type;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HierarchicalModel{");
        sb.append("contentType='").append(contentType).append('\'');
        sb.append(", symlink=").append(symlink);
        sb.append(", name='").append(name).append('\'');
        sb.append(", parent='").append(parent).append('\'');
        sb.append(", realName='").append(realName).append('\'');
        sb.append(", realParent='").append(realParent).append('\'');
        sb.append(", readable=").append(readable);
        sb.append(", writable=").append(writable);
        sb.append(", executable=").append(executable);
        sb.append(", displaySize='").append(displaySize).append('\'');
        sb.append(", sizeInBytes=").append(sizeInBytes);
        sb.append(", hidden=").append(hidden);
        sb.append(", lastModified='").append(lastModified).append('\'');
        sb.append(", type=").append(type);
        sb.append('}');
        return sb.toString();
    }
}
