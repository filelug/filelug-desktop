package com.filelug.desktop.service.websocket;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelug.desktop.ClopuccinoMessages;
import com.filelug.desktop.OSUtility;
import com.filelug.desktop.Utility;
import com.filelug.desktop.model.HierarchicalModelType;
import com.filelug.desktop.model.RequestListRootsModel;
import com.filelug.desktop.model.ResponseListRootsModel;
import com.filelug.desktop.model.RootDirectory;
import com.filelug.desktop.service.Sid;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <code>ListRootsWebSocketService</code> receives and process the web socket message from server with service id: LIST_ROOT_V2
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ListRootsWebSocketService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ListRootsWebSocketService.class.getSimpleName());

    private final Session session;

    private final String message;

    private final ConnectSocket connectSocket;

    public ListRootsWebSocketService(final Session session, final String message, final ConnectSocket connectSocket) {
        this.session = session;
        this.message = message;
        this.connectSocket = connectSocket;
    }

    public void onListRootsWebSocket() {
        /* requested to find all roots of file system */
        List<RootDirectory> roots = new ArrayList<>();

        String operatorId = null;
        String clientLocale = null;
//        boolean showHidden = true;

        ObjectMapper mapper = Utility.createObjectMapper();

        try {
            RequestListRootsModel requestModel = mapper.readValue(message, RequestListRootsModel.class);

            operatorId = requestModel.getOperatorId();
            clientLocale = requestModel.getLocale();
//            showHidden = requestModel.isShowHidden();

            if (!connectSocket.validate(true)) {
                ResponseListRootsModel responseModel = new ResponseListRootsModel(Sid.LIST_ALL_ROOT_DIRECTORIES_V2, HttpServletResponse.SC_UNAUTHORIZED, ClopuccinoMessages.localizedMessage(clientLocale, "invalid.session"), operatorId, System.currentTimeMillis(), null);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else {
                // Make it final to be accessed in Iterable foreach block
                final String[] rootSlashFileStoreNames = new String[1];
                rootSlashFileStoreNames[0] = null;

                String finalLocale = clientLocale;

                Iterable<Path> paths = FileSystems.getDefault().getRootDirectories();

                paths.forEach(path -> {
                    String realPath = Utility.correctDirectoryPath(path.toString());

                    // type

                    HierarchicalModelType type = HierarchicalModelType.LOCAL_DISK;

                    try {
                        FileStore fileStore = Files.getFileStore(path);

                        if (fileStore.type() != null && (fileStore.type().trim().contains("cdfs") || fileStore.type().trim().contains("9660"))) {
                            type = HierarchicalModelType.DVD_PLAYER;
                        }

                        LOGGER.debug(path.toString() + " : " + fileStore.name() + "(" + fileStore.type() + ")");
                    } catch (Exception e) {
                        type = HierarchicalModelType.DVD_PLAYER;

                        LOGGER.error(path.toString() + " : " + e.getMessage());
                    }

                    // label

                    String label;

                    if (realPath.equals("/")) {
                        label = String.format("%s(%s)", ClopuccinoMessages.localizedMessage(finalLocale, "system.root.directory.label"), realPath);

                        try {
                            rootSlashFileStoreNames[0] = Files.getFileStore(path).name();
                        } catch (Exception e) {
                            // igonored
                        }
                    } else {
                        if (path.getFileName() == null) {
                            label = realPath;
                        } else {
                            label = path.getFileName().toString();
                        }

                        String typeRepresentation;

                        if (type == HierarchicalModelType.DVD_PLAYER) {
                            typeRepresentation = ClopuccinoMessages.localizedMessage(finalLocale, "dvd.or.cd");
                        } else {
                            typeRepresentation = ClopuccinoMessages.localizedMessage(finalLocale, "local.disk");
                        }

                        label = String.format("%s(%s)", typeRepresentation, label);
                    }

                    roots.add(new RootDirectory(realPath, realPath, label, type));

                    // DEBUG
//                    try {
//                        FileStore fileStore = Files.getFileStore(path);
//                        LOGGER.info(path.toString() + " : " + fileStore.name() + "(" + fileStore.type() + ")");
//                    } catch (Exception e) {
//                        LOGGER.info(path.toString() + " : " + e.getMessage());
//                    }
                });

                // External Disk on Mac
                if (OSUtility.isOSX()) {
                    try {
                        File volumesDirectory = new File("/Volumes");

                        findAndAddExternalDisksFrom(volumesDirectory, roots, clientLocale, rootSlashFileStoreNames[0]);
                    } catch (Exception e) {
                        LOGGER.error("Error on finding external disks on mac.\n" + e.getMessage(), e);
                    }
                }

                String userHomePath = Utility.correctDirectoryPath(System.getProperty("user.home"));

                // External Disks on Linux
                if (OSUtility.isLinux()) {
                    try {
                        String userName = FilenameUtils.getName(userHomePath);

                        if (userName != null && userName.length() > 0) {
                            // For Ubuntu, Debian, and Elementary,
                            // the external disk/cd/dvd is under /media/<File name of the user.home>

                            File ubuntuExternalDiskParent = new File("/media/" + userName);

                            findAndAddExternalDisksFrom(ubuntuExternalDiskParent, roots, clientLocale, null);

                            // For Fedora, CentOS, and Suse
                            // the external disk/cd/dvd is under /run/media/<File name of the user.home>

                            File fedoraExternalDiskParent = new File("/run/media/" + userName);

                            findAndAddExternalDisksFrom(fedoraExternalDiskParent, roots, clientLocale, null);
                        }

                    } catch (Exception e) {
                        LOGGER.error("Error on finding external disks on Linux\n" + e.getMessage(), e);
                    }
                }

                // See RootDirectory.compareTo(Object) for more information
                Collections.sort(roots);

                // Add the user home to the first of the list

                if (userHomePath != null && new File(userHomePath).exists() && new File(userHomePath).isDirectory()) {
                    RootDirectory userHome = new RootDirectory(userHomePath, userHomePath, ClopuccinoMessages.localizedMessage(clientLocale, "user.home.directory.label"), HierarchicalModelType.USER_HOME);

                    roots.add(0, userHome);
                }
                
                // DEBUG: REMOVED IN PRODUCTION -- Add two folder to test the displaying order in devices
//
//                File downloadFolder = new File(userHomePath, "Downloads");
//                roots.add(new RootDirectory(downloadFolder.getAbsolutePath(), downloadFolder.getAbsolutePath(), downloadFolder.getName(), HierarchicalModelType.DIRECTORY));
//
//                File projectFolder = new File(userHomePath, "projects");
//                roots.add(new RootDirectory(projectFolder.getCanonicalPath(), projectFolder.getAbsolutePath(), projectFolder.getName(), HierarchicalModelType.MAC_ALIAS_DIRECTORY));

                ResponseListRootsModel responseModel = new ResponseListRootsModel(Sid.LIST_ALL_ROOT_DIRECTORIES_V2, HttpServletResponse.SC_OK, null, operatorId, System.currentTimeMillis(), roots);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            }
        } catch (Exception e) {
            // websocket sent internal error

            try {
                String errorMessage = ClopuccinoMessages.localizedMessage(clientLocale, "error.invoke.service", "listRoots", e.getMessage() != null ? e.getMessage() : "");

                LOGGER.error(errorMessage, e);

                ResponseListRootsModel responseModel = new ResponseListRootsModel(Sid.LIST_ALL_ROOT_DIRECTORIES_V2, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage, operatorId, System.currentTimeMillis(), null);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } catch (Exception e1) {
                LOGGER.error("Error on sent internal error message! Class: " + e1.getClass().getName() + ", Reason: " + e.getMessage());
            }
        }
    }

    private void findAndAddExternalDisksFrom(File externalDiskParent, List<RootDirectory> rootDirectories, String clientLocale, String fileStoreNameToExclude) {
        if (externalDiskParent.exists() && externalDiskParent.isDirectory()) {
            File[] externalDiskRoots = externalDiskParent.listFiles();

            if (externalDiskRoots != null) {
                for (File externalDiskRoot : externalDiskRoots) {
                    if (externalDiskRoot.exists()) {
                        Path path = externalDiskRoot.toPath();

                        String realPath = Utility.correctDirectoryPath(path.toString());

                        // type

                        HierarchicalModelType type = HierarchicalModelType.EXTERNAL_DISK;
                        
                        try {
                            FileStore fileStore = Files.getFileStore(path);

                            if (fileStoreNameToExclude != null && fileStoreNameToExclude.trim().length() > 0 && fileStoreNameToExclude.equals(fileStore.name())) {
                                continue;
                            }

                            if (fileStore.type() != null) {
                                String lowercaseType = fileStore.type().toLowerCase();

                                if ((lowercaseType.contains("9660") || lowercaseType.contains("iso"))) {
                                    type = HierarchicalModelType.DVD_PLAYER;
                                } else if (lowercaseType.contains("mtmfs")) {
                                    // Mobile Time Machine File System
                                    type = HierarchicalModelType.TIME_MACHINE;
                                }
                            }

//                            LOGGER.info(path.toString() + " : " + fileStore.name() + "(" + fileStore.type() + ")");
                        } catch (Exception e) {
                            LOGGER.error(path.toString() + " : " + e.getMessage());
                        }

                        // label

                        String label = externalDiskLabelFrom(path, type, clientLocale);

                        rootDirectories.add(new RootDirectory(realPath, realPath, label, type));
                    }
                }
            }
        }
    }

    private String externalDiskLabelFrom(Path path, HierarchicalModelType type, String clientLocale) {
        String label;

        if (path.getFileName() == null) {
            // remove the '/' in the end of the file path
            label = Utility.correctDirectoryPath(path.toString());
        } else {
            label = path.getFileName().toString();
        }

        // Set to true to skip label because it is too long
        boolean labelWithoutPath = false;

        String typeRepresentation;

        if (type == HierarchicalModelType.DVD_PLAYER) {
            typeRepresentation = ClopuccinoMessages.localizedMessage(clientLocale, "dvd.or.cd");
        } else if (type == HierarchicalModelType.TIME_MACHINE) {
            typeRepresentation = ClopuccinoMessages.localizedMessage(clientLocale, "time.machine");

            labelWithoutPath = true;
        } else {
            // exception for /Volumes/Recovery HD
            if (OSUtility.isOSX() && label.equals("Recovery HD")) {
                typeRepresentation = ClopuccinoMessages.localizedMessage(clientLocale, "system.recovery.hd");;

                labelWithoutPath = true;
            } else {
                typeRepresentation = ClopuccinoMessages.localizedMessage(clientLocale, "external.disk");
            }
        }

        if (labelWithoutPath) {
            return typeRepresentation;
        } else {
            return String.format("%s(%s)", typeRepresentation, label);
        }
    }
}
