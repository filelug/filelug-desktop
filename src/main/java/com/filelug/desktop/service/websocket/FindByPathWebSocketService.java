package com.filelug.desktop.service.websocket;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelug.desktop.ClopuccinoMessages;
import com.filelug.desktop.Utility;
import com.filelug.desktop.model.HierarchicalFactory;
import com.filelug.desktop.model.HierarchicalModel;
import com.filelug.desktop.model.RequestFindFileByPathModel;
import com.filelug.desktop.model.ResponseFindFileByPathModel;
import com.filelug.desktop.service.Sid;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.Session;
import java.io.File;

/**
 * <code>FindByPathWebSocketService</code> receives and process the web socket message from server with service id: FIND_BY_PATH
 *
 * @author masonhsieh
 * @version 1.0
 */
public class FindByPathWebSocketService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(FindByPathWebSocketService.class.getSimpleName());

    private final Session session;

    private final String message;

    private final ConnectSocket connectSocket;

    public FindByPathWebSocketService(final Session session, final String message, final ConnectSocket connectSocket) {
        this.session = session;
        this.message = message;
        this.connectSocket = connectSocket;
    }

    public void onFindByPathWebSocket() {
        // requested to find the information of the specified file

        String operatorId = null;
        String clientLocale = null;

        ObjectMapper mapper = Utility.createObjectMapper();

        try {
            RequestFindFileByPathModel requestModel = mapper.readValue(message, RequestFindFileByPathModel.class);

            operatorId = requestModel.getOperatorId();
            clientLocale = requestModel.getLocale();

            if (!connectSocket.validate(true)) {
                ResponseFindFileByPathModel responseModel = new ResponseFindFileByPathModel(Sid.FIND_BY_PATH, HttpServletResponse.SC_UNAUTHORIZED, ClopuccinoMessages.localizedMessage(clientLocale, "invalid.session"), operatorId, System.currentTimeMillis(), null);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } else {
                String filePath = requestModel.getPath();

                if (filePath == null || !new File(filePath).exists()) {
                    ResponseFindFileByPathModel responseModel = new ResponseFindFileByPathModel(Sid.FIND_BY_PATH, HttpServletResponse.SC_BAD_REQUEST, ClopuccinoMessages.localizedMessage(clientLocale, "directory.or.file.not.found", filePath != null ? filePath : ""), operatorId, System.currentTimeMillis(), null);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                } else {
                    Boolean calculateSize = requestModel.getCalculateSize();

                    File file = new File(filePath);

                    HierarchicalModel model = HierarchicalFactory.createHierarchical(file, file, (calculateSize != null ? calculateSize : false), true);

                    ResponseFindFileByPathModel responseModel = new ResponseFindFileByPathModel(Sid.FIND_BY_PATH, HttpServletResponse.SC_OK, null, operatorId, System.currentTimeMillis(), model);

                    session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
                }
            }
        } catch (Exception e) {
            /* websocket sent internal error */
            try {
                String errorMessage = ClopuccinoMessages.localizedMessage(clientLocale, "error.invoke.service", "findFileByPath", e.getMessage() != null ? e.getMessage() : "");

                LOGGER.error(errorMessage, e);

                ResponseFindFileByPathModel responseModel = new ResponseFindFileByPathModel(Sid.FIND_BY_PATH, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage, operatorId, System.currentTimeMillis(), null);

                session.getAsyncRemote().sendText(mapper.writeValueAsString(responseModel));
            } catch (Exception e1) {
                LOGGER.error("Error on sent internal error message! Class: " + e1.getClass().getName() + ", Reason: " + e.getMessage());
            }
        }
    }
}
