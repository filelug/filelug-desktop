package com.filelug.desktop.view;

import com.filelug.desktop.Constants;
import com.filelug.desktop.FilelugDesktop;
import com.filelug.desktop.PropertyConstants;
import com.filelug.desktop.Utility;
import com.filelug.desktop.model.NewVersionAvailableState;
import com.filelug.desktop.model.Version;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URI;
import java.util.Observable;
import java.util.Observer;

/**
 * <code>ApplicationFrame</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class ApplicationFrame implements WindowListener, Observer, ConfigurationListener {

    private JFrame frame;

    private AbstractButton versionLabel;

//    private JButton closeButton;

    public ApplicationFrame(JMenu connectionStatusMenu, ActionListener changeAdministratorListener) {
        JPanel contentPane = new JPanel();

        JLabel logoLabel = new JLabel(Utility.createImageIconByBundleKey("wizard-logo.png", Utility.localizedString("app.title")));

        String applicationVersion = System.getProperty(PropertyConstants.PROPERTY_NAME_FILELUG_DESKTOP_CURRENT_VERSION, Constants.DEFAULT_DESKTOP_VERSION);

//        prepareVersion(applicationVersion, null, null);

        // Use computer name as the title
        String computerName = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, "");

        if (computerName.trim().length() < 1) {
            frame = new JFrame(Utility.localizedString("app.title"));
        } else {
            frame = new JFrame(computerName);
        }

        frame.setContentPane(contentPane);

        frame.addWindowListener(this);

        Dimension frameSize = new Dimension(560, 420);
        frame.setPreferredSize(frameSize);
        frame.setMinimumSize(frameSize);
        frame.setSize(frameSize);
        frame.setIconImages(Utility.createWindowIconImages());

        // Will handled by WindowListener
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // menu

        JMenu actionMenu = MenuUtility.createSwingMenu(connectionStatusMenu, changeAdministratorListener);

        JMenuBar menuBar = new JMenuBar();

        menuBar.add(actionMenu);

        frame.setJMenuBar(menuBar);

        // version prepared after menu prepared, but before sub components of the content pane created
        prepareVersion(applicationVersion, null, null);

        // layout

        GridBagLayout layout = new GridBagLayout();
        contentPane.setLayout(layout);

        // row 1
        Component glueBox = Box.createVerticalGlue();
        layout.setConstraints(glueBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(5, 5, 5, 5), 5, 0));
        contentPane.add(glueBox);

        // row 2
        layout.setConstraints(logoLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 5, 0));
        contentPane.add(logoLabel);

        // row 3
        layout.setConstraints(versionLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 5, 0));
        contentPane.add(versionLabel);

        // row 4
        Component glueBox2 = Box.createVerticalGlue();
        layout.setConstraints(glueBox2, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(5, 5, 5, 5), 5, 0));
        contentPane.add(glueBox2);

//        // row 5
//        layout.setConstraints(buttonPane, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 0));
//        contentPane.add(buttonPane);

        // row 5
        Component glueBox3 = Box.createVerticalGlue();
        layout.setConstraints(glueBox3, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(5, 5, 5, 5), 5, 0));
        contentPane.add(glueBox3);

        // listeners

        NewVersionAvailableState.getInstance().addObserver(this);

        Utility.addPreferenceChangeListener(this);
    }

    public JFrame getFrame() {
        return frame;
    }

    public void changeFrameTitleTo(String newFrameTitle) {
        frame.setTitle(newFrameTitle);

        SwingUtilities.invokeLater(() -> {
            frame.revalidate();
            frame.repaint();
        });
    }

    public void show() {
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);

        /* get focus */
        frame.setAlwaysOnTop(true);
        frame.setAlwaysOnTop(false);

//        if (closeButton != null) {
//            closeButton.requestFocusInWindow();
//        }
    }

    public void enableChangeUserMenu(boolean enabled) {
        JMenuBar menuBar = frame.getJMenuBar();

        JMenu changeUserMenu = menuBar.getMenu(MenuUtility.INDEX_OF_CHANGE_USER_MENU_ITEM_OF_SWING_MENU);

        if (changeUserMenu != null) {
            SwingUtilities.invokeLater(() -> changeUserMenu.setEnabled(enabled));
        }
    }

    private void changeMenuItemLabelOfShowDownloadPage(String text, ImageIcon icon) {
        JMenuBar menuBar = frame.getJMenuBar();

        JMenu downloadMenuItem = menuBar.getMenu(MenuUtility.INDEX_OF_SHOW_DOWNLOAD_PAGE_MENU_ITME_OF_SWING_MENU);

        if (downloadMenuItem != null) {
            SwingUtilities.invokeLater(() -> {
                downloadMenuItem.setText(text);

                downloadMenuItem.setIcon(icon);

                menuBar.revalidate();
                menuBar.repaint();
            });
        }
    }

    /* window listener */

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        SwingUtilities.invokeLater(this::promptAndCloseApplication);
    }

    private void promptAndCloseApplication() {
        int selectedOption = JOptionPane.showConfirmDialog(frame, Utility.localizedString("dialog.confirm.exist"), Utility.localizedString("dialog.title.confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Utility.createImageIcon("question.png", ""));

        if (selectedOption == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void update(final Observable observable, Object arg) {
        SwingUtilities.invokeLater(() -> {
            if (NewVersionAvailableState.class.isInstance(observable)) {
                String applicationVersion = System.getProperty(PropertyConstants.PROPERTY_NAME_FILELUG_DESKTOP_CURRENT_VERSION, Constants.DEFAULT_DESKTOP_VERSION);

                NewVersionAvailableState state = (NewVersionAvailableState) observable;

                String latestVersion = state.getNewVersion();
                String downloadUrl = state.getDownloadUrl();

                prepareVersion(applicationVersion, latestVersion, downloadUrl);
            }

            SwingUtilities.invokeLater(() -> {
                frame.revalidate();
                frame.repaint();
            });
        });
    }

    @Override
    public void configurationChanged(ConfigurationEvent event) {
        int type = event.getType();

        // There're twice set-event fired before AND after the property is updated.
        boolean beforeUpdate = event.isBeforeUpdate();

        if (type == AbstractConfiguration.EVENT_SET_PROPERTY && !beforeUpdate) { // Not EVENT_ADD_PROPERTY, which triggered when preferences.addProperty(key, value)
            String propertyName = event.getPropertyName();

            if (propertyName.equals(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME)) {
                // update the title of the ApplicationFrame

                changeFrameTitleTo((String) event.getPropertyValue());
            }
        }
    }

    private void prepareVersion(String currentVersion, String latestVersion, String downloadUrl) {
        String text;
        URI uri;
        String menuItemText;
        ImageIcon menuItemIcon;

        if (latestVersion == null
            || latestVersion.trim().length() < 1
            || downloadUrl == null
            || downloadUrl.trim().length() < 1
            || new Version(currentVersion).compareTo(new Version(latestVersion)) >= 0) {
            text = Utility.localizedString("app.version", currentVersion);

            uri = null;

            menuItemText = Utility.localizedString("menu.download.latest.version");
            menuItemIcon = null;
        } else {
            text = Utility.localizedString("app.version.with.latest", currentVersion, latestVersion);

            menuItemText = Utility.localizedString("menu.download.latest.version2", latestVersion);
            menuItemIcon = Utility.createImageIcon("download-from-cloud.png", "");

            try {
                uri = URI.create(Utility.suffixLocaleParameterWithURL(downloadUrl));
            } catch (Exception e) {
                // ignored
                uri = URI.create(Constants.DEFAULT_WEB_SITE_URL);
            }
        }

        if (versionLabel == null) {
            versionLabel = FilelugDesktop.createNavigationButton(text, uri);
        } else {
            versionLabel = FilelugDesktop.updateHyperlinkButton(versionLabel, text, uri);

//            // when the frame initiates, the versionLabel is empty and it's not time to update the text of the download page menu item.
//            // The initialization process could halt when the menu item changes on frame initialization.
//            changeMenuItemLabelOfShowDownloadPage(menuItemText, menuItemIcon);
        }
        
        changeMenuItemLabelOfShowDownloadPage(menuItemText, menuItemIcon);
    }
}
