package com.filelug.desktop.service;

import org.apache.http.HttpResponse;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public interface SystemService {

//    boolean ping();

//    void startServer() throws Exception;

//    void shutdownServer() throws Exception;

//    boolean serverStarted(int pingTimes);

    HttpResponse whoami() throws Exception;

//    List<CountryModel> findAvailableContries() throws SocketException, Exception;

    /**
     * Download software and save to temp
     *
     * @param destination the path of the file to save the downloaded software
     *
     * @return the status of response
     */
    int downloadSoftware(String destination);

//    /**
//     * Reset the application without prompting.
//     */
//    void resetApplicationWithoutExistsApplication() throws Exception;
//
//    /**
//     * When application finds the computer no longer exists,
//     * it invokes this method to automatically reset the application
//     * and then prompt that the application is about to close.
//     */
//    void resetApplicationAndPromptForComputerNotFound(final Window parent);
//
//    /**
//     * When application finds the computer no longer exists,
//     * it invokes this method to automatically reset the application
//     * and then prompt that the application is about to close.
//     */
//    void resetApplicationAndPromptForComputerNotFound();
}
