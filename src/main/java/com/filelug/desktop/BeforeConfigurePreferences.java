package com.filelug.desktop;

/**
 * <code>BeforeConfigurePreferences</code> inserts action before configuring preferences.
 *
 * @author masonhsieh
 * @version 1.0
 */
public interface BeforeConfigurePreferences {

    void actionBeforeConfigurePreferences(boolean needCopyValueFromV1);
    
}
