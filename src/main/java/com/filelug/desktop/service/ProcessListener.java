package com.filelug.desktop.service;

import java.util.EventListener;

/**
 * <code>ProcessListener</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public interface ProcessListener extends EventListener {
    void processFinished(Process process);
}
