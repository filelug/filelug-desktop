package com.filelug.desktop.service.websocket;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelug.desktop.PropertyConstants;
import com.filelug.desktop.Utility;
import com.filelug.desktop.model.RequestChangeComputerNameModel;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;

/**
 * <code>ChangeComputerNameWebSocketService</code> receives and process the web socket message from server with service id: CHANGE_COMPUTER_NAME_V2
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ChangeComputerNameWebSocketService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(ChangeComputerNameWebSocketService.class.getSimpleName());

    private final Session session;

    private final String message;

    private final ConnectSocket connectSocket;

    public ChangeComputerNameWebSocketService(final Session session, final String message, final ConnectSocket connectSocket) {
        this.session = session;
        this.message = message;
        this.connectSocket = connectSocket;
    }

    public void changeComputerName() {
        ObjectMapper mapper = Utility.createObjectMapper();

        try {
            RequestChangeComputerNameModel requestModel = mapper.readValue(message, RequestChangeComputerNameModel.class);

            String newComputerGroup = requestModel.getNewComputerGroup();
            String newComputerName = requestModel.getNewComputerName();

            Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, newComputerGroup);
            Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, newComputerName);
        } catch (Exception e) {
            LOGGER.error("Error on deleting computer.", e);
        }
    }
}
