package com.filelug.desktop.view;

import com.apple.eawt.*;
import com.filelug.desktop.Utility;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class MacApplication {

    public static void setupDefaultMenu(JMenu connectionStatusMenu, ActionListener changeAdministratorListener) {
        Application macApp = Application.getApplication();

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(MenuUtility.createSwingMenu(connectionStatusMenu, changeAdministratorListener));

        macApp.setDefaultMenuBar(menuBar);
    }

    public static void setupHandlers() {
        Application macApp = Application.getApplication();

        macApp.setAboutHandler(new AboutHandler() {
            @Override
            public void handleAbout(AppEvent.AboutEvent aboutEvent) {
                Utility.showAboutDialog(null);

//                String title = Utility.localizedString("menu.about");
//
//                String applicationVersion = System.getProperty(PropertyConstants.PROPERTY_NAME_FILELUG_DESKTOP_CURRENT_VERSION, Constants.DEFAULT_DESKTOP_VERSION);
//
//                String message = Utility.localizedString("app.version", applicationVersion) + "\n" + Constants.DEFAULT_WEB_SITE_URL;
//
//                Utility.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE, Utility.createImageIcon("Filelug_Icon_32Bits_60.png", ""));
            }
        });

        macApp.setQuitHandler(new QuitHandler() {
            @Override
            public void handleQuitRequestWith(AppEvent.QuitEvent quitEvent, QuitResponse quitResponse) {
                MenuUtility.onApplicationExit();
            }
        });
    }
}
