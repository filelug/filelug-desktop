package com.filelug.desktop.service.websocket;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelug.desktop.ClopuccinoMessages;
import com.filelug.desktop.OSUtility;
import com.filelug.desktop.Utility;
import com.filelug.desktop.model.HierarchicalFactory;
import com.filelug.desktop.model.HierarchicalModel;
import com.filelug.desktop.model.RequestListDirectoryChildrenModel;
import com.filelug.desktop.model.ResponseListDirectoryChildrenModel;
import com.filelug.desktop.service.Sid;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>ListDirectoryChildrenWebSocketService</code> receives and process the web socket message from server with service id: LIST_CHILDREN
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ListDirectoryChildrenWebSocketService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ListDirectoryChildrenWebSocketService.class.getSimpleName());

    private final Session session;

    private final String message;

    private final ConnectSocket connectSocket;

    public ListDirectoryChildrenWebSocketService(final Session session, final String message, final ConnectSocket connectSocket) {
        this.session = session;
        this.message = message;
        this.connectSocket = connectSocket;
    }

    public void onListDirectoryChildrenWebSocket() {
        /* requested to find all directories and files of the specified directory */
        String operatorId = null;
        String clientLocale = null;
        Boolean showHidden = null;

        ObjectMapper mapper = Utility.createObjectMapper();

        try {
            RequestListDirectoryChildrenModel requestModel = mapper.readValue(message, RequestListDirectoryChildrenModel.class);

            operatorId = requestModel.getOperatorId();
            clientLocale = requestModel.getLocale();
            showHidden = requestModel.getShowHidden();

            if (!connectSocket.validate(true)) {
                ResponseListDirectoryChildrenModel responseModel = new ResponseListDirectoryChildrenModel(Sid.LIST_CHILDREN, HttpServletResponse.SC_UNAUTHORIZED, ClopuccinoMessages.localizedMessage(clientLocale, "invalid.session"), operatorId, System.currentTimeMillis(), null);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else {
                String directoryPath = Utility.correctDirectoryPath(requestModel.getPath());

                // For Windows, only names of the system roots end with ':'
                if (OSUtility.isWindows() && directoryPath.endsWith(":")) {
                    directoryPath = directoryPath + File.separator;
                }

                if (directoryPath == null || !new File(directoryPath).exists() || new File(directoryPath).isFile()) {
                    ResponseListDirectoryChildrenModel responseModel = new ResponseListDirectoryChildrenModel(Sid.LIST_CHILDREN, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "directory.not.found", directoryPath != null ? directoryPath : ""), operatorId, System.currentTimeMillis(), null);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else {
                    List<HierarchicalModel> children = new ArrayList<>();

                    File directory = new File(directoryPath);

                    File[] files;

                    if (showHidden != null && showHidden) {
                        files = directory.listFiles();
                    } else {
                        files = directory.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File file) {
                                // FIX: If link is not hidden but the real file is hidden,
                                //      what to do with it?

                                return !file.isHidden();
                            }
                        });
                    }

                    if (files != null && files.length > 0) {
                        boolean allowAlias = connectSocket.getUserDao().findAllowAliasById(connectSocket.getUserId());

                        if (allowAlias) {
                            for (File file : files) {
                                HierarchicalModel hierarchicalModel = HierarchicalFactory.createHierarchical(file, file);
                                if (hierarchicalModel != null) {
                                    children.add(hierarchicalModel);
                                }
                            }
                        } else {
                            for (File file : files) {
                                HierarchicalModel model = HierarchicalFactory.createNonAliasHierarchical(file, file);

                                if (model != null) {
                                    children.add(model);
                                }
                            }
                        }
                    }

                    ResponseListDirectoryChildrenModel responseModel = new ResponseListDirectoryChildrenModel(Sid.LIST_CHILDREN, HttpServletResponse.SC_OK, null, operatorId, System.currentTimeMillis(), children);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                }
            }
        } catch (Exception e) {
            /* websocket sent internal error */
            try {
                String errorMessage = ClopuccinoMessages.localizedMessage(clientLocale, "error.invoke.service", "listRoots", e.getMessage() != null ? e.getMessage() : "");

                LOGGER.error(errorMessage, e);

                ResponseListDirectoryChildrenModel responseModel = new ResponseListDirectoryChildrenModel(Sid.LIST_CHILDREN, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage, operatorId, System.currentTimeMillis(), null);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } catch (Exception e1) {
                LOGGER.error("Error on sent internal error message! Class: " + e1.getClass().getName() + ", Reason: " + e.getMessage());
            }
        }
    }
}
