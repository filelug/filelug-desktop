package com.filelug.desktop;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;


public class LoggerStartupListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {

    private boolean started = false;

    @Override
    public void start() {
        if (started)
            return;

        String logHome = System.getProperty("log.directory"); // log.file is our custom jvm parameter to change log file name dynamicly if needed

        if (logHome == null) {
            // Copy from Utility.USE_HTTPS
            boolean USE_HTTPS = Boolean.parseBoolean(System.getProperty("use.https", String.valueOf(true)));

            String dataDirectory;
            if (USE_HTTPS) {
                dataDirectory = "." + Constants.DEFAULT_DATA_DIRECTORY_NAME_FOR_PRODUCTION;
            } else {
                dataDirectory = "." + Constants.DEFAULT_DATA_DIRECTORY_NAME_FOR_TESTING;
            }

            logHome = System.getProperty("user.home") + "/" + dataDirectory + "/" + "logs";
        }

        Context context = getContext();

        context.putProperty("LOG_HOME", logHome);

        started = true;
    }

    @Override
    public void stop() {
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isResetResistant() {
        return true;
    }

    @Override
    public void onStart(LoggerContext context) {
    }

    @Override
    public void onReset(LoggerContext context) {
    }

    @Override
    public void onStop(LoggerContext context) {
    }

    @Override
    public void onLevelChange(Logger logger, Level level) {
    }
}
