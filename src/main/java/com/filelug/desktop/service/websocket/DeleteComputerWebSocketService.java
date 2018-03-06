package com.filelug.desktop.service.websocket;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.filelug.desktop.PropertyConstants;
import com.filelug.desktop.Utility;
import com.filelug.desktop.model.RequestDeleteComputerModel;
import com.filelug.desktop.model.User;
import com.filelug.desktop.service.ResetApplicationNotificationService;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;

/**
 * <code>DeleteComputerWebSocketService</code> receives and process the web socket message from server with service id: DELETE_COMPUTER_V2
 *
 * @author masonhsieh
 * @version 1.0
 */
public class DeleteComputerWebSocketService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(DeleteComputerWebSocketService.class.getSimpleName());

    private final Session session;

    private final String message;

    private final ConnectSocket connectSocket;

    public DeleteComputerWebSocketService(final Session session, final String message, final ConnectSocket connectSocket) {
        this.session = session;
        this.message = message;
        this.connectSocket = connectSocket;
    }

    public void deleteComputer() {
        ObjectMapper mapper = Utility.createObjectMapper();

        try {
            RequestDeleteComputerModel requestModel = mapper.readValue(message, RequestDeleteComputerModel.class);

            String operatorId = requestModel.getOperatorId();
            String clientLocale = requestModel.getLocale();
            String verification = requestModel.getVerification();

            User user = connectSocket.getUserService().findAdministrator();
            Long computerId = Utility.getPreferenceLong(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, -1);
            String computerGroup = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, "");

            if (verification == null || verification.trim().length() < 1
                || user == null || user.getAdmin() == null
                || computerId < 0
                || computerGroup.trim().length() < 1) {
                String errorMessage = Utility.localizedString("failed.delete.computer.data.not.integrity");

                LOGGER.error(errorMessage);
            } else {
                String adminId = user.getAccount();

                String expectedVerification = Utility.generateVerificationToDeleteComputer(adminId, computerId);

                if (!verification.equals(expectedVerification)) {
                    LOGGER.warn("Be careful that user: '" + operatorId + "' is trying to hack the verification code to delete this computer!");
                } else {
                    ResetApplicationNotificationService.getInstance().applicationShouldReset(ResetApplicationNotificationService.REASON.COMPUTER_NOT_FOUND);
//                    connectSocket.getUserService().resetAndExitQuietly();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error on deleting computer.", e);
        }
    }
}
