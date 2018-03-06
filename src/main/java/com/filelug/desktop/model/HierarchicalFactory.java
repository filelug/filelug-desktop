package com.filelug.desktop.model;

import ch.qos.logback.classic.Logger;
import com.filelug.desktop.Constants;
import com.filelug.desktop.OSUtility;
import com.filelug.desktop.Utility;
import com.filelug.desktop.service.BundleDirectoryService;
import com.filelug.desktop.service.CLFileBridge;
import com.filelug.desktop.service.WindowsShortcut;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * <code>HierarchicalFactory</code> is the factory to create classes extends {@code HierarchicalModel}.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class HierarchicalFactory {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("HIERARCHY");


    // calculate size only when it is a file, not a directory.
    // prepare content type only when it is a directory, not a file.
    public static HierarchicalModel createHierarchical(File originalFile, File destinationFile) throws IOException {
        HierarchicalModel model;

        boolean keepWalking = true;

        try {
            if (originalFile == null || destinationFile == null || !destinationFile.canRead()) {
                keepWalking = false;
            }
        } catch (Exception e) {
            keepWalking = false;
        }

        if (keepWalking) {
            if (OSUtility.isWindows()) {
                // check if file is a windows shortcut
                // use try-catch to prevent the destinationFile is a directory,
                // and it will throw FileNotFoundException.

                boolean candidate;

                try {
                    candidate = WindowsShortcut.isPotentialValidLink(destinationFile);
                } catch (Exception e) {
                    candidate = false;
                }

                if (candidate) {
                    try {
                        WindowsShortcut shortcut = new WindowsShortcut(destinationFile);

                        String realFilePath = shortcut.getRealFilename();

                        if (realFilePath != null) {
                            File realFile = new File(realFilePath);

                            if (shortcut.isDirectory()) {
                                boolean calculateSize = BundleDirectoryService.isBundleDirectory(realFile);

                                model = createModel(originalFile, realFilePath, realFile, HierarchicalModelType.WINDOWS_SHORTCUT_DIRECTORY, true, calculateSize, true);
                            } else {
                                model = createModel(originalFile, realFilePath, realFile, HierarchicalModelType.WINDOWS_SHORTCUT_FILE, true, true, false);
                            }
                        } else {
                            // not a link, should be a normal file or directory

                            String absolutePath = destinationFile.getAbsolutePath();

                            if (destinationFile.isDirectory()) {
                                boolean calculateSize = BundleDirectoryService.isBundleDirectory(originalFile);

                                model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, calculateSize, true);
                            } else {
                                model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, true, false);
                            }
                        }

//                        if (!shortcut.isLocal()) {
//                            // shortcuts to network drive are not supported now because
//                            // if network connection failed, warning dialog shows on the desktop and will block the program
//                            // util someone click on the button of this dialog
//
//                            model = null;
//                        } else {
//                            String realFilePath = shortcut.getRealFilename();
//
//                            if (realFilePath != null) {
//                                File realFile = new File(realFilePath);
//
//                                if (realFile.isDirectory()) {
//                                    model = createModel(originalFile, realFilePath, realFile, HierarchicalModelType.WINDOWS_SHORTCUT_DIRECTORY, true, false, true);
//                                } else {
//                                    model = createModel(originalFile, realFilePath, realFile, HierarchicalModelType.WINDOWS_SHORTCUT_FILE, true, true, false);
//                                }
//                            } else {
//                                // not a link, should be a normal file or directory
//
//                                String absolutePath = destinationFile.getAbsolutePath();
//
//                                if (destinationFile.isDirectory()) {
//                                    model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, false, true);
//                                } else {
//                                    model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, true, false);
//                                }
//                            }
//                        }
                    } catch (Exception e) {
                        // Not a shortcut, view as a normal file or directory

                        String absolutePath = destinationFile.getAbsolutePath();

                        if (destinationFile.isDirectory()) {
                            model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, false, true);
                        } else {
                            model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, true, false);
                        }
                    }
                } else {
                    String absolutePath = destinationFile.getAbsolutePath();

                    if (destinationFile.isDirectory()) {
                        boolean calculateSize = BundleDirectoryService.isBundleDirectory(originalFile);

                        model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, calculateSize, true);
                    } else {
                        model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, true, false);
                    }
                }
            } else {
                try {
                    if (Utility.isUnixSymlink(destinationFile)) {
                        // symbolic link
                        String realFilePath = destinationFile.getCanonicalPath();

                        File realFile = new File(realFilePath);

                        if (Utility.isUnixSymlink(realFile)) {
                            // recursive

                            return createHierarchical(originalFile, realFile);
                        } else {
                            if (realFile.isDirectory()) {
                                boolean calculateSize = BundleDirectoryService.isBundleDirectory(realFile);

                                model = createModel(originalFile, realFilePath, realFile, HierarchicalModelType.UNIX_SYMBOLIC_LINK_DIRECTORY, true, calculateSize, true);
                            } else {
                                model = createModel(originalFile, realFilePath, realFile, HierarchicalModelType.UNIX_SYMBOLIC_LINK_FILE, true, true, false);
                            }
                        }
                    } else {
                        if (OSUtility.isOSX()) {
                            // Check mac alias

                            CLFileBridge destFileBridge = new CLFileBridge(destinationFile.getAbsolutePath());

                            if (destFileBridge.isAliasFile()) {
                                // find real path
                                String realFilePath = destFileBridge.resolveAliasFile();

                                if (realFilePath != null) {
                                    CLFileBridge realFileBridge = new CLFileBridge(realFilePath);

                                    File realFile = new File(realFilePath);

                                    if (realFileBridge.isAliasFile()) {
                                        // recursive

                                        return createHierarchical(originalFile, realFile);
                                    } else {
                                        if (realFile.isDirectory()) {
                                            boolean calculateSize = BundleDirectoryService.isBundleDirectory(realFile);

                                            model = createModel(originalFile, realFilePath, realFile, HierarchicalModelType.MAC_ALIAS_DIRECTORY, true, calculateSize, true);
                                        } else {
                                            model = createModel(originalFile, realFilePath, realFile, HierarchicalModelType.MAC_ALIAS_FILE, true, true, false);
                                        }
                                    }
                                } else {
                                    // View as a normal file for mac

                                    String absolutePath = destinationFile.getAbsolutePath();

                                    model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, true, false);

//                                    throw new Exception("Can't find real file path from alias file: " + destinationFile.getAbsolutePath());
                                }
                            } else {
                                String absolutePath = destinationFile.getAbsolutePath();

                                if (destinationFile.isDirectory()) {
                                    boolean calculateSize = BundleDirectoryService.isBundleDirectory(originalFile);

                                    model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, calculateSize, true);
                                } else {
                                    model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, true, false);
                                }
                            }

                            System.gc();
                        } else {
                            // not symbolic link, not a mac
                            String absolutePath = destinationFile.getAbsolutePath();

                            if (destinationFile.isDirectory()) {
                                boolean calculateSize = BundleDirectoryService.isBundleDirectory(originalFile);

                                model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, calculateSize, true);
                            } else {
                                model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, true, false);
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Error on creating file model. Path='" + originalFile.getAbsolutePath() + "'", e);

                    String absolutePath = destinationFile.getAbsolutePath();

                    if (destinationFile.isDirectory()) {
                        boolean calculateSize = BundleDirectoryService.isBundleDirectory(originalFile);

                        model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, calculateSize, true);
                    } else {
                        model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, true, false);
                    }
                }
            }
        } else {
            model = null;
        }

        return model;
    }

    // calculate size only when it is a file, not a directory.
    // prepare content type only when it is a directory, not a file.
    public static HierarchicalModel createNonAliasHierarchical(File originalFile, File destinationFile) throws IOException {
        HierarchicalModel model = null;

        if (OSUtility.isWindows()) {
            // check if file is a windows shortcut
            // use try-catch to prevent the destinationFile is a directory,
            // and it will throw FileNotFoundException.

            boolean candidate;

            try {
                candidate = WindowsShortcut.isPotentialValidLink(destinationFile);
            } catch (Exception e) {
                candidate = false;
            }

            if (!candidate) {
                String absolutePath = destinationFile.getAbsolutePath();

                if (destinationFile.isDirectory()) {
                    boolean calculateSize = BundleDirectoryService.isBundleDirectory(originalFile);

                    model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, calculateSize, true);
                } else {
                    model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, true, false);
                }
            }
        } else {
            try {
                if (!Utility.isUnixSymlink(destinationFile)) {
                    if (OSUtility.isOSX()) {
                        // Check mac alias

                        CLFileBridge destFileBridge = new CLFileBridge(destinationFile.getAbsolutePath());

                        if (!destFileBridge.isAliasFile()) {
                            String absolutePath = destinationFile.getAbsolutePath();

                            if (destinationFile.isDirectory()) {
                                boolean calculateSize = BundleDirectoryService.isBundleDirectory(originalFile);

                                model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, calculateSize, true);
                            } else {
                                model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, true, false);
                            }
                        }

                        System.gc();
                    } else {
                        // not symbolic link, not a mac
                        String absolutePath = destinationFile.getAbsolutePath();

                        if (destinationFile.isDirectory()) {
                            boolean calculateSize = BundleDirectoryService.isBundleDirectory(originalFile);

                            model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, calculateSize, true);
                        } else {
                            model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, true, false);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error on creating file model. Path='" + originalFile.getAbsolutePath() + "'", e);

                String absolutePath = destinationFile.getAbsolutePath();

                if (destinationFile.isDirectory()) {
                    boolean calculateSize = BundleDirectoryService.isBundleDirectory(originalFile);

                    model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, calculateSize, true);
                } else {
                    model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, true, false);
                }
            }
        }

        return model;
    }

    public static HierarchicalModel createHierarchical(File originalFile, File destinationFile, boolean calculateSize, boolean prepareContentType) throws IOException {
        HierarchicalModel model = null;

        if (OSUtility.isWindows()) {
            String absolutePath = destinationFile.getAbsolutePath();

            if (destinationFile.isDirectory()) {
                model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, calculateSize, prepareContentType);
            } else {
                model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, calculateSize, prepareContentType);
            }
        } else {
            try {
                if (Utility.isUnixSymlink(destinationFile)) {
                    /* symbolic link */
                    String realFilePath = destinationFile.getCanonicalPath();
                    File realFile = new File(realFilePath);

                    if (Utility.isUnixSymlink(realFile)) {
                        // recursive

                        return createHierarchical(originalFile, realFile, calculateSize, prepareContentType);
                    } else {
                        if (realFile.isDirectory()) {
                            model = createModel(originalFile, realFilePath, realFile, HierarchicalModelType.UNIX_SYMBOLIC_LINK_DIRECTORY, true, calculateSize, prepareContentType);
                        } else {
                            model = createModel(originalFile, realFilePath, realFile, HierarchicalModelType.UNIX_SYMBOLIC_LINK_FILE, true, calculateSize, prepareContentType);
                        }
                    }
                } else {
                    if (OSUtility.isOSX()) {
                        // Check mac alias

                        CLFileBridge destFileBridge = new CLFileBridge(destinationFile.getAbsolutePath());

                        if (destFileBridge.isAliasFile()) {
                            // find real path
                            String realFilePath = destFileBridge.resolveAliasFile();

                            if (realFilePath != null) {
                                CLFileBridge realFileBridge = new CLFileBridge(realFilePath);

                                File realFile = new File(realFilePath);

                                if (realFileBridge.isAliasFile()) {
                                    // recursive

                                    return createHierarchical(originalFile, realFile, calculateSize, prepareContentType);
                                } else {
                                    if (realFile.isDirectory()) {
                                        model = createModel(originalFile, realFilePath, realFile, HierarchicalModelType.MAC_ALIAS_DIRECTORY, true, calculateSize, prepareContentType);
                                    } else {
                                        model = createModel(originalFile, realFilePath, realFile, HierarchicalModelType.MAC_ALIAS_FILE, true, calculateSize, prepareContentType);
                                    }
                                }
                            } else {
                                LOGGER.warn("Can't find real file path from alias file: " + destinationFile.getAbsolutePath());
                            }
                        } else {
                            // not symbolic link
                            String absolutePath = destinationFile.getAbsolutePath();

                            if (destinationFile.isDirectory()) {
                                model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, calculateSize, prepareContentType);
                            } else {
                                model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, calculateSize, prepareContentType);
                            }
                        }

                        System.gc();
                    } else  {
                        // not symbolic link, not a mac
                        String absolutePath = destinationFile.getAbsolutePath();

                        if (destinationFile.isDirectory()) {
                            model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, calculateSize, prepareContentType);
                        } else {
                            model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, calculateSize, prepareContentType);
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error on creating file model. Path='" + originalFile.getAbsolutePath() + "'", e);

                String absolutePath = destinationFile.getAbsolutePath();

                if (destinationFile.isDirectory()) {
                    model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.DIRECTORY, false, calculateSize, prepareContentType);
                } else {
                    model = createModel(originalFile, absolutePath, destinationFile, HierarchicalModelType.FILE, false, calculateSize, prepareContentType);
                }
            }
        }

        return model;
    }

    /**
     * If file or directory is a symbolic link or a windows shortcut, the original file refers to the link/shortcut file and
     * real file refers to the real file or directory that the link/shortcut points to.
     * <br>
     * If file or directory is a symbolic link, a windows shortcut, or an mac alias, the properties of the created HierarchicalModel will generated from
     * the real file of directory that the link/shortcut points to.
     * <br>
     * If file or directory is not a link or shortcut, the real name is equals to name, and the real parent is equals to parent.
     */
    private static HierarchicalModel createModel(File originalFile, String realFilePath, File realFile, HierarchicalModelType type, boolean symlink, boolean calculateSize, boolean prepareContentType) {
        HierarchicalModel model = new HierarchicalModel();

        if (type.name().endsWith("DIRECTORY")) {
            if (prepareContentType) {
                model.setContentType("application/directory");
            } else {
                model.setContentType("");
            }
        } else {
            if (prepareContentType) {
                model.setContentType(HierarchicalModel.prepareContentType(realFile));
            } else {
                model.setContentType("");
            }
        }

        model.setType(type);
        model.setSymlink(symlink);

        // delete the last file separator: '/' or '\'
        // consider the root directory, sucha as
        // For Linux/Unix: '/' --> must be excluded from substring
        // For Windowns:   'C:\'

        String originalFilePath = Utility.correctDirectoryPath(originalFile.getAbsolutePath());

        realFilePath = Utility.correctDirectoryPath(realFilePath);

        model.setName(FilenameUtils.getName(originalFilePath));
        model.setParent(FilenameUtils.getFullPathNoEndSeparator(originalFilePath));

        if (type != null && HierarchicalModelType.isShortcutOrLink(type)) {
            if (!OSUtility.isWindows() && realFilePath.equals("/")) {
                model.setRealName("/");
                model.setRealParent("");
            } else if (OSUtility.isWindows() && realFilePath.endsWith(":")) {
                model.setRealName(realFilePath);
                model.setRealParent("");
            } else {
                model.setRealName(FilenameUtils.getName(realFilePath));
                model.setRealParent(FilenameUtils.getFullPathNoEndSeparator(realFilePath));
            }

            model.setReadable(realFile.canRead());
            model.setWritable(realFile.canWrite());
            model.setExecutable(realFile.canExecute());

            model.setHidden(realFile.isHidden());

            model.setLastModified(Constants.DEFAULT_DATE_FORMAT.format(new Date(realFile.lastModified())));
        } else {
            model.setRealName(model.getName());
            model.setRealParent(model.getParent());

            model.setReadable(originalFile.canRead());
            model.setWritable(originalFile.canWrite());
            model.setExecutable(originalFile.canExecute());

            model.setHidden(originalFile.isHidden());

            model.setLastModified(Constants.DEFAULT_DATE_FORMAT.format(new Date(originalFile.lastModified())));
        }

        if (calculateSize) {
            long sizeInBytes = model.calcuateSizeInBytes();

            model.setSizeInBytes(sizeInBytes);
            model.setDisplaySize(model.calcuateDisplaySize(sizeInBytes));
        } else {
            model.setSizeInBytes(0L);
            model.setDisplaySize("");
        }

        // DEBUG:
//        LOGGER.info("Model created: " + model.toString());

        return model;
    }

    /**
     * Creates {@code HierarchicalModel} instance from the map object. The map object contains the following key and value type:
     * <ol>
     * <li>type: {@link String}, mandatory</li>
     * <li>symlink: {@link String}, mandatory</li>
     * <li>parent: {@link String}, mandatory</li>
     * <li>name: {@link String}, mandatory</li>
     * <li>realParent: {@link String}, mandatory</li>
     * <li>realName: {@link String}, mandatory</li>
     * <li>executable: {@link Boolean}, optional</li>
     * <li>writable: {@link Boolean}, optional</li>
     * <li>readable: {@link Boolean}, optional</li>
     * <li>displaySize: {@link String}, optional</li>
     * <li>sizeInBytes: {@link Long}, optional</li>
     * <li>lastModified: {@link String}, optional. Must be formatted as: <pre>yyyy/MM/dd HH:mm:ss 'GMT'Z</pre></li>
     * </ol>
     *
     * @param map Used to create {@code HierarchicalModel} instance. See above for more details.
     * @return The newly created {@code HierarchicalModel} instance.
     */
    public static HierarchicalModel createHierarchical(Map map) throws IllegalArgumentException {
        Boolean symlink = (Boolean) map.get("symlink");

        if (symlink == null) {
            throw new IllegalArgumentException("Empty value for property 'symlink'");
        }

        String typeString = (String) map.get("type");

        HierarchicalModelType type = HierarchicalModelType.fromString(typeString);
        if (type == null) {
            throw new IllegalArgumentException("Illegal type value: " + typeString);
        }

        String parent = (String) map.get("parent");

        if (parent == null || parent.trim().length() < 1) {
            throw new IllegalArgumentException("Empty value for property 'parent'");
        }

        String name = (String) map.get("name");

        if (name == null || name.trim().length() < 1) {
            throw new IllegalArgumentException("Empty value for property 'name'");
        }

        String realParent = (String) map.get("realParent");

        if (realParent == null || realParent.trim().length() < 1) {
            throw new IllegalArgumentException("Empty value for property 'realParent'");
        }

        String realName = (String) map.get("realName");

        if (realName == null || realName.trim().length() < 1) {
            throw new IllegalArgumentException("Empty value for property 'realName'");
        }

        HierarchicalModel model = new HierarchicalModel();
        model.setSymlink(symlink);
        model.setType(type);
        model.setParent(parent);
        model.setName(name);
        model.setRealParent(realParent);
        model.setRealName(realName);

        Boolean executable = (Boolean) map.get("executable");
        if (executable != null) {
            model.setExecutable(executable);
        }

        Boolean writable = (Boolean) map.get("writable");
        if (writable != null) {
            model.setWritable(writable);
        }

        Boolean readable = (Boolean) map.get("readable");
        if (readable != null) {
            model.setReadable(readable);
        }

        String displaySize = (String) map.get("displaySize");
        if (displaySize != null) {
            model.setDisplaySize(displaySize);
        }

        Long sizeInBytes = (Long) map.get("sizeInBytes");
        if (sizeInBytes != null) {
            model.setSizeInBytes(sizeInBytes);
        }

        Boolean hidden = (Boolean) map.get("hidden");
        if (hidden != null) {
            model.setHidden(hidden);
        }

        String lastModified = (String) map.get("lastModified");
        if (lastModified != null) {
            model.setLastModified(lastModified);
        }

        String contentType = (String) map.get("contentType");
        if (contentType != null) {
            model.setContentType(contentType);
        }

        return model;
    } // end createInstance(Map)

}
