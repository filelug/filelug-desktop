package com.filelug.desktop.service;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.filelug.desktop.Constants;
import com.filelug.desktop.OSUtility;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.eclipse.jetty.websocket.jsr356.ClientContainer;
import org.slf4j.LoggerFactory;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * <code>ServiceUtilities</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ServiceUtilities {

    private static final String CONTENT_TYPE_UNKNOWN = "application/octet-stream";

    private static final ResourceBundle mimeTypeBundle = ResourceBundle.getBundle("MimeType");

    private static final String PREFIX_SCHEMA = OSUtility.USE_HTTPS ? "https://" : "http://";

    private static final String PREFIX_WEB_SOCKET_SCHEMA = OSUtility.USE_HTTPS ? "wss://" : "ws://";

    private static final String CREPO_ADDRESS_FOR_TESTING = "127.0.0.1";

    private static final String CREPO_DOMAIN_ZONE_NAME = "filelug.com";

    private static final String CREPO_ADDRESS = OSUtility.USE_HTTPS ? ("repo." + CREPO_DOMAIN_ZONE_NAME) : CREPO_ADDRESS_FOR_TESTING;

    private static final String SERVER_PORT = OSUtility.USE_HTTPS ? "443" : "8080";

    private static final String CONTEXT_PATH = "crepo";

    private static boolean keystored;

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("SERVICE_USER");


    public ServiceUtilities() {}

    public Session connectWebSocket(Class<?> annotatedEndpointClass, URI path) throws Exception {
        Session session = null;
        if (OSUtility.USE_HTTPS) {
            ClientContainer jsr356Client = createClientContainer();

            session = jsr356Client.connectToServer(annotatedEndpointClass, path);
        } else {
            WebSocketContainer container = createWebSocketContainer();

            session = container.connectToServer(annotatedEndpointClass, path);
        }

        return session;
    }

    public Session connectWebSocket(Object endpoint, URI path) throws Exception {
        Session session;

        if (OSUtility.USE_HTTPS) {
            ClientContainer jsr356Client = createClientContainer();

            session = jsr356Client.connectToServer(endpoint, path);
        } else {
            WebSocketContainer container = createWebSocketContainer();

            session = container.connectToServer(endpoint, path);
        }

        return session;
    }

    // Creating non-ssl web socket container
    private WebSocketContainer createWebSocketContainer() {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        // 如果idle timeout 設太少，則每隔一下 ConnectSocket 就會收到 onClose 通知， repository的ConnectSocket也會收到onClose

        container.setDefaultMaxSessionIdleTimeout(Constants.IDLE_TIMEOUT_IN_SECONDS_TO_CONNECT_SOCKET * 1000);
        container.setAsyncSendTimeout(Constants.IDLE_TIMEOUT_IN_SECONDS_TO_CONNECT_SOCKET * 1000);

        container.setDefaultMaxBinaryMessageBufferSize(Integer.MAX_VALUE);
        container.setDefaultMaxTextMessageBufferSize(Integer.MAX_VALUE);
        return container;
    }

    // Create ssl web socket client
    private ClientContainer createClientContainer() throws Exception {
        // set system property "org.eclipse.jetty.websocket.jsr356.ssl-trust-all" to true
        System.setProperty("org.eclipse.jetty.websocket.jsr356.ssl-trust-all", "true");

        ClientContainer clientContainer = new ClientContainer();

        clientContainer.getClient().start();

        return clientContainer;
    }

//    // Create ssl web socket client
//    private ClientContainer createClientContainer(File trustStoreFile) throws Exception {
//        String trustStoreAbsPath = trustStoreFile.getAbsolutePath();
//
//        SslContextFactory sslContextFactory = new SslContextFactory();
//
//        sslContextFactory.setKeyStorePath(trustStoreAbsPath);
//        sslContextFactory.setKeyStorePassword("Lug123@World");
//
//        sslContextFactory.setTrustStorePath(trustStoreAbsPath);
//        sslContextFactory.setTrustStorePassword("Lug123@World");
//
//        // set system property "org.eclipse.jetty.websocket.jsr356.ssl-trust-all" to true
//
////        System.setProperty("org.eclipse.jetty.websocket.jsr356.ssl-trust-all", "true");
//
//        WebSocketClient jettyClient = new WebSocketClient(sslContextFactory);
//
//        // 如果idle timeout 設太少，則每隔一下 ConnectSocket 就會收到 onClose 通知， repository的ConnectSocket也會收到onClose
//
//        jettyClient.getPolicy().setIdleTimeout(Constants.IDLE_TIMEOUT_IN_SECONDS_TO_CONNECT_SOCKET * 1000);
//        jettyClient.getPolicy().setAsyncWriteTimeout(Constants.IDLE_TIMEOUT_IN_SECONDS_TO_CONNECT_SOCKET * 1000);
//
//        jettyClient.getPolicy().setMaxTextMessageSize(Integer.MAX_VALUE);
//        jettyClient.getPolicy().setMaxBinaryMessageSize(Integer.MAX_VALUE);
//
//        jettyClient.start();
//
//        return new ClientContainer(jettyClient);
//    }

//    public WebSocketContainer getWebSocketContainer() {
//        if (webSocketContainer == null) {
//            WebSocketContainer container = createWebSocketContainer();
//
//            webSocketContainer = container;
//        }
//
//        return webSocketContainer;
//    }

    /**
     * @param lugServerId The sub domain name of connecting lug server. Set to null if you want to connect to the AA server.
     * @param suffixPath the path after context path. DO NOT start with a slash ('/')
     * @return composed full address
     */
    public String composeFullAddressWithLugServerId(String lugServerId, String suffixPath) {
        if (lugServerId == null || lugServerId.trim().length() < 1 || lugServerId.equals(Constants.AA_SERVER_ID_AS_LUG_SERVER)) {
            return PREFIX_SCHEMA + CREPO_ADDRESS + ":" + SERVER_PORT + "/" + CONTEXT_PATH + "/" + suffixPath;
        } else {
            return PREFIX_SCHEMA + lugServerId + "." + CREPO_DOMAIN_ZONE_NAME + ":" + SERVER_PORT + "/" + CONTEXT_PATH + "/" + suffixPath;
        }
    }

    /**
     * @param suffixPath the path after context path. DO NOT start with a slash ('/')
     * @return composed full address
     */
    public String composeFullAddress(String suffixPath) {
        return composeFullAddressWithLugServerId(null, suffixPath);
    }

    public String composeFullWebSocketAddress(String lugServerId, String suffixPath) {
        if (lugServerId == null || lugServerId.trim().length() < 1 || lugServerId.equals(Constants.AA_SERVER_ID_AS_LUG_SERVER)) {
            return PREFIX_WEB_SOCKET_SCHEMA + CREPO_ADDRESS + ":" + SERVER_PORT + "/" + CONTEXT_PATH + "/" + suffixPath;
        } else {
            return PREFIX_WEB_SOCKET_SCHEMA + lugServerId + "." + CREPO_DOMAIN_ZONE_NAME + ":" + SERVER_PORT + "/" + CONTEXT_PATH + "/" + suffixPath;
        }
    }

    public String composeInitialConnectFullWebSocketAddress(String suffixPath) {
        return PREFIX_WEB_SOCKET_SCHEMA + CREPO_ADDRESS + ":" + SERVER_PORT + "/" + CONTEXT_PATH + "/" + suffixPath;
    }

    public String composeLugServerPingAddress(String lugServerId) {
        String lugServerPingAddress;

        if (lugServerId == null || lugServerId.trim().length() < 1 || lugServerId.equals(Constants.AA_SERVER_ID_AS_LUG_SERVER)) {
            lugServerPingAddress = PREFIX_SCHEMA + lugServerId + "." + CREPO_DOMAIN_ZONE_NAME + ":" + SERVER_PORT + "/" + CONTEXT_PATH + "/index.jsp";
        } else {
            lugServerPingAddress = PREFIX_SCHEMA + lugServerId + "." + CREPO_DOMAIN_ZONE_NAME + ":" + SERVER_PORT + "/" + CONTEXT_PATH + "/.index.jsp";
        }

        // DEBUG
        LOGGER.debug(lugServerPingAddress);

        return lugServerPingAddress;
    }

    public HttpResponse doGet(String path) throws Exception {
        return doGet(path, null, null);
    }

    public HttpResponse doGet(String path, List<NameValuePair> params) throws Exception {
        return doGet(path, null, params);
    }

    public HttpResponse doGet(String path, Set<Header> headers, List<NameValuePair> params) throws Exception {
        HttpResponse response;

        RequestConfig config = RequestConfig.custom().setSocketTimeout(Constants.SO_TIMEOUT_IN_SECONDS_DEFAULT * 1000).setConnectTimeout(Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000).build();

        HttpClientBuilder clientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(config);

        Set<Header> newHeaders = null;

        if (headers != null && headers.size() > 0) {
            newHeaders = new HashSet<>(headers);
        } else {
            newHeaders = new HashSet<>();
        }

        if (newHeaders.size() > 0) {
            clientBuilder.setDefaultHeaders(newHeaders);
        }

        HttpClient httpClient = clientBuilder.build();

        String fullPath = composeFullAddress(path);

        if (params != null && params.size() > 0) {
            if (!fullPath.endsWith("?")) {
                fullPath += "?";
            }

            String paramString = URLEncodedUtils.format(params, Constants.DEFAULT_RESPONSE_ENTITY_CHARSET);

            fullPath += paramString;
        }

        HttpGet httpGet = new HttpGet(fullPath);

        LOGGER.debug("Execute request GET " + httpGet.getURI().toString());

        response = httpClient.execute(httpGet);

        return response;
    }

    public HttpResponse doGetWebPage(String urlString, Set<Header> headers, List<NameValuePair> params) throws Exception {
        HttpResponse response;

        RequestConfig config = RequestConfig.custom().setSocketTimeout(Constants.SO_TIMEOUT_IN_SECONDS_DEFAULT * 1000).setConnectTimeout(Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000).build();

        HttpClientBuilder clientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(config);

        Set<Header> newHeaders = null;

        if (headers != null && headers.size() > 0) {
            newHeaders = new HashSet<>(headers);
        } else {
            newHeaders = new HashSet<>();
        }

        if (newHeaders.size() > 0) {
            clientBuilder.setDefaultHeaders(newHeaders);
        }

        HttpClient httpClient = clientBuilder.build();

        String fullPath = urlString;

        if (params != null && params.size() > 0) {
            if (!fullPath.endsWith("?")) {
                fullPath += "?";
            }

            String paramString = URLEncodedUtils.format(params, Constants.DEFAULT_RESPONSE_ENTITY_CHARSET);

            fullPath += paramString;
        }

        HttpGet httpGet = new HttpGet(fullPath);

        LOGGER.debug("Execute request GET " + httpGet.getURI().toString());

        response = httpClient.execute(httpGet);

        return response;
    }

    public HttpResponse doPutJson(String path, String jsonString) throws Exception {
        RequestConfig config = RequestConfig.custom().setSocketTimeout(Constants.SO_TIMEOUT_IN_SECONDS_DEFAULT * 1000).setConnectTimeout(Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000).build();

        HttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

        HttpPut httpPut = new HttpPut(composeFullAddress(path));

        httpPut.setEntity(new StringEntity(jsonString, ContentType.APPLICATION_JSON));

        LOGGER.debug("Execute request PUT " + httpPut.getURI().toString());

        return httpClient.execute(httpPut);
    }

    public HttpResponse doPostJsonAndSaveCertificate(String path, Set<Header> headers, String jsonString, boolean updateTruststore) throws Exception {
        RequestConfig config = RequestConfig.custom().setSocketTimeout(Constants.SO_TIMEOUT_IN_SECONDS_DEFAULT * 1000).setConnectTimeout(Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000).build();

        HttpClientBuilder clientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(config);

        // headers

        Set<Header> newHeaders;

        if (headers != null && headers.size() > 0) {
            newHeaders = new HashSet<>(headers);
        } else {
            newHeaders = new HashSet<>();
        }

        if (newHeaders.size() > 0) {
            clientBuilder.setDefaultHeaders(newHeaders);
        }

        HttpClient httpClient;

        if (OSUtility.USE_HTTPS && (!keystored || updateTruststore)) {
            httpClient = clientBuilder.addInterceptorLast(new HttpResponseInterceptor() {
                @Override
                public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
                    ManagedHttpClientConnection connection = (ManagedHttpClientConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION);

                    try {
                        Certificate[] certificates = connection.getSSLSession().getPeerCertificates();

                        int certCount = certificates.length;

                        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

                        trustStore.load(null, "Lug123@World".toCharArray());

                        /* import by reversing sequence */
                        for (int index = certCount - 1; index > -1; index--) {
                            trustStore.setCertificateEntry("Cert" + index, certificates[index]);
                        }

                        Path trustStorePath = FileSystems.getDefault().getPath(OSUtility.getApplicationDataDirectoryFile().getAbsolutePath(), Constants.TSF_FILENAME);

                        try {
                            Files.deleteIfExists(trustStorePath);
                        } catch (Exception e) {
                            LOGGER.error("Error on deleting file: " + trustStorePath.toAbsolutePath() + "\n" + e.getMessage());
                        }

                        FileOutputStream trustStoreFileOutputStream = new FileOutputStream(trustStorePath.toFile());

//                        File trustStoreFile = new File(OSUtility.getApplicationDataDirectoryFile(), Constants.TSF_FILENAME);
//
//                        if (trustStoreFile.exists()) {
//                            boolean deleted = trustStoreFile.delete();
//
//                            if (!deleted) {
//                                LOGGER.error("File not deleted: " + trustStoreFile.getAbsolutePath());
//                            }
//                        }
//
//                        FileOutputStream trustStoreFileOutputStream = new FileOutputStream(trustStoreFile);

                        trustStore.store(trustStoreFileOutputStream, "Lug123@World".toCharArray());

                        keystored = true;
                    } catch (Exception e) {
                        LOGGER.error("Failed to get certificates.", e);
                    }
                }
            }).build();
        } else {
            httpClient = clientBuilder.build();
        }

        HttpPost httpPost = new HttpPost(composeFullAddress(path));

        if (jsonString != null && jsonString.trim().length() > 0) {
            httpPost.setEntity(new StringEntity(jsonString, ContentType.APPLICATION_JSON));
        }

        LOGGER.debug("Execute request POST " + httpPost.getURI().toString());

        return httpClient.execute(httpPost);
    }

    public HttpResponse doPostJson(String lugServerId, String path, Set<Header> headers) throws Exception {
        return doPostJson(lugServerId, path, headers, null, null, Constants.SO_TIMEOUT_IN_SECONDS_DEFAULT * 1000, Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000);
    }

    public HttpResponse doPostJson(String lugServerId, String path, String jsonString) throws Exception {
        return doPostJson(lugServerId, path, null, null, jsonString, Constants.SO_TIMEOUT_IN_SECONDS_DEFAULT * 1000, Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000);
    }

    public HttpResponse doPostJson(String lugServerId, String path, String jsonString, int soTimeoutInMillis, int connecTimeoutInMillis) throws Exception {
        return doPostJson(lugServerId, path, null, null, jsonString, soTimeoutInMillis, connecTimeoutInMillis);
    }

    public HttpResponse doPost(String path, Set<Header> headers, List<NameValuePair> params) throws Exception {
        return doPostJson(null, path, headers, params, null, Constants.SO_TIMEOUT_IN_SECONDS_DEFAULT * 1000, Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000);
    }

    public HttpResponse doPostJson(String lugServerId, String path, Set<Header> headers, List<NameValuePair> params, String jsonString) throws Exception {
        return doPostJson(lugServerId, path, headers, params, jsonString, Constants.SO_TIMEOUT_IN_SECONDS_DEFAULT * 1000, Constants.CONNECT_TIMEOUT_IN_SECONDS_DEFAULT * 1000);
    }

    public HttpResponse doPostJson(String lugServerId, String path, Set<Header> headers, List<NameValuePair> params, String jsonString, int soTimeoutInMillis, int connecTimeoutInMillis) throws Exception {
        RequestConfig config = RequestConfig.custom().setSocketTimeout(soTimeoutInMillis).setConnectTimeout(connecTimeoutInMillis).build();

        HttpClientBuilder clientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(config);

        // headers

        Set<Header> newHeaders;

        if (headers != null && headers.size() > 0) {
            newHeaders = new HashSet<>(headers);
        } else {
            newHeaders = new HashSet<>();
        }

        if (newHeaders.size() > 0) {
            clientBuilder.setDefaultHeaders(newHeaders);
        }

        HttpClient httpClient = clientBuilder.build();

        // path with/without lug server id

        String fullPath;

        if (lugServerId != null && lugServerId.trim().length() > 0) {
            fullPath = composeFullAddressWithLugServerId(lugServerId, path);
        } else {
            fullPath = composeFullAddress(path);
        }

        // params

        if (params != null && params.size() > 0) {
            if (!fullPath.endsWith("?")) {
                fullPath += "?";
            }

            String paramString = URLEncodedUtils.format(params, Constants.DEFAULT_RESPONSE_ENTITY_CHARSET);

            fullPath += paramString;
        }

        HttpPost httpPost = new HttpPost(fullPath);

        // json

        if (jsonString != null && jsonString.trim().length() > 0) {
            httpPost.setEntity(new StringEntity(jsonString, ContentType.APPLICATION_JSON));
        }

        LOGGER.debug("Execute request POST " + httpPost.getURI().toString());

        return httpClient.execute(httpPost);
    }

    public void doAsyncPost(String lugServerId, String path, Set<Header> headers, String jsonString, final FutureCallback<HttpResponse> callback, final int socketTimeoutInMillis, final int connectTimeoutInMillis) throws Exception {
        RequestConfig config = RequestConfig.custom().setSocketTimeout(socketTimeoutInMillis).setConnectTimeout(connectTimeoutInMillis).build();

        HttpAsyncClientBuilder clientBuilder = HttpAsyncClientBuilder.create().setDefaultRequestConfig(config);

        // header

        if (headers != null && headers.size() > 0) {
            clientBuilder.setDefaultHeaders(headers);
        }

        String fullPath = composeFullAddressWithLugServerId(lugServerId, path);

        HttpPost httpPost = new HttpPost(fullPath);

        // body

        if (jsonString != null) {
            httpPost.setEntity(new StringEntity(jsonString, ContentType.APPLICATION_JSON));
        }

        CloseableHttpAsyncClient httpClient = clientBuilder.build();

        httpClient.start();

        LOGGER.debug("Execute async request POST " + fullPath);

        Future<HttpResponse> future = httpClient.execute(httpPost, callback);

        try {
            future.get();
        } catch (Exception fex) {
            // Even if execution exception occurred for server is down,
            // reconnect checking continures
        } finally {
            closeHttpClient(httpClient);
        }
    }

    private void closeHttpClient(CloseableHttpAsyncClient httpClient) {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (Exception e) {
                // ignored
            }
        }
    }

    public static String findErrorMessageFromResponseNode(JsonNode responseNode, String defaultMessage) {
        JsonNode errorNode = responseNode.get("error");

        String errorMessage = errorNode != null ? errorNode.asText() : defaultMessage;

        return errorMessage.trim().length() > 0 ? errorMessage : defaultMessage;
    }
}
