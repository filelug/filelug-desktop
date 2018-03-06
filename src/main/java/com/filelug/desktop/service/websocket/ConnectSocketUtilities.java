package com.filelug.desktop.service.websocket;

import javax.websocket.CloseReason;
import javax.websocket.Session;

/**
 * <code>ConnectSocketUtilities</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ConnectSocketUtilities {


    public static void closeSessionWithBadDataStatusCode(final Session session, final String reason) {
        closeSession(session, CloseReason.CloseCodes.UNEXPECTED_CONDITION, reason);
    }

    public static void closeSession(final Session session, final CloseReason.CloseCodes closeCodes, final String reason) {
        try {
            session.close(new CloseReason(closeCodes, reason));
        } catch (Exception e) {
            // ignored
        }
    }
}
