package com.filelug.desktop.service;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.filelug.desktop.ClopuccinoMessages;
import com.filelug.desktop.Utility;
import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Set;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public final class DefaultSystemService implements SystemService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("SERVICE_SYSTEM");

    private final ServiceUtilities serviceUtilities;

    public DefaultSystemService() {
        serviceUtilities = new ServiceUtilities();
    }

    /**
     * This is the first request to server every time the application lauches, it is used to get the SSL information.
     */
    @Override
    public HttpResponse whoami() throws Exception {
        HttpResponse response = null;

        String path = "computer/whoami";

        try {
            ObjectMapper mapper = Utility.createObjectMapper();

            // system properties

            JsonNode syspropsNode = Utility.prepareJsonNodeFromSystemProperties(mapper);

//            ObjectNode syspropsNode = mapper.createObjectNode();
//
//            Properties properties = Utility.prepareSystemProperties();
//
//            Set<String> propertyNames = properties.stringPropertyNames();
//
//            for (String propertyName : propertyNames) {
//                syspropsNode.put(propertyName, properties.getProperty(propertyName, ""));
//            }

            String requestJson = mapper.writeValueAsString(syspropsNode);

            response = serviceUtilities.doPostJsonAndSaveCertificate(path, null, requestJson, false);
        } catch (UnknownHostException | HttpHostConnectException | ConnectTimeoutException e) {
            String message = ClopuccinoMessages.getMessage("no.network");

            LOGGER.error(message, e);

            throw new SocketException(message);
        } catch (Exception e) {
            String message = Utility.localizedString("failed.test.connection");

            LOGGER.error(message, e);

            throw new Exception(message);
        }

        return response;
    }

    @Override
    public int downloadSoftware(String destination) {
        return 0;
    }
}
