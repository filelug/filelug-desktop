package com.filelug.desktop.view;

import com.filelug.desktop.Constants;
import com.filelug.desktop.FilelugDesktop;
import com.filelug.desktop.Utility;
import com.filelug.desktop.model.AccountState;
import com.filelug.desktop.service.PopupMenuItemClickNotificationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class MenuUtility {

    public static final int INDEX_OF_COMPUTER_NAME_MENU_ITEM_OF_AWT_MENU = 0;

    public static final int INDEX_OF_CHANGE_USER_MENU_ITEM_OF_AWT_MENU = 3;

    public static final int INDEX_OF_CHANGE_USER_MENU_ITEM_OF_SWING_MENU = INDEX_OF_CHANGE_USER_MENU_ITEM_OF_AWT_MENU - 2; // Swing menu has no computer name title menu item and the first separator

    public static final int INDEX_OF_SHOW_DOWNLOAD_PAGE_MENU_ITME_OF_AWT_MENU = 5;

    public static final int INDEX_OF_SHOW_DOWNLOAD_PAGE_MENU_ITME_OF_SWING_MENU = 3;

//    public static JPopupMenu createPopupMenu(String computerName, JMenu connectionStatusMenu, ActionListener changeAdministratorListener) {
//        JPopupMenu popupMenu = new JPopupMenu(computerName);
//
//        popupMenu.add(createComputerNameMenu(computerName));
//        popupMenu.addSeparator();
//        popupMenu.add(connectionStatusMenu);
//        popupMenu.add(createSwingChangeUserMenu(changeAdministratorListener));
//        popupMenu.addSeparator();
//        popupMenu.add(createCloseMenuMenu());
//        popupMenu.addSeparator();
//        popupMenu.add(createSwingShowDownloadPageMenu());
//        popupMenu.add(createSwingAboutMenu());
//        popupMenu.addSeparator();
//        popupMenu.add(createSwingExitMenu());
//
////        // TODO: Test if it's better to enable it.
//        popupMenu.setLightWeightPopupEnabled(true);
//
//        return popupMenu;
//    }
    public static PopupMenu createAWTMenu(String computerName, Menu connectionStatusMenu, ActionListener changeAdministratorListener) {
        PopupMenu popupMenu = new PopupMenu();

        popupMenu.add(createAWTComputerNameMenu(computerName));
        popupMenu.addSeparator();
        popupMenu.add(connectionStatusMenu);
        popupMenu.add(createAWTChangeUserMenu(changeAdministratorListener));
        popupMenu.addSeparator();
        popupMenu.add(createAWTShowDownloadPageMenu());
        popupMenu.add(createAWTWebSiteMenu());
        popupMenu.addSeparator();
        popupMenu.add(createAWTExitMenu());

        return popupMenu;
    }

    public static JMenu createSwingMenu(JMenu connectionStatusMenu, ActionListener changeAdministratorListener) {
        JMenu actionMenu = new JMenu(Utility.localizedString("menu.task"));

        actionMenu.add(connectionStatusMenu);
        actionMenu.add(createSwingChangeUserMenu(changeAdministratorListener));
        actionMenu.add(new JSeparator(SwingConstants.HORIZONTAL));
        actionMenu.add(createSwingShowDownloadPageMenu());
        actionMenu.add(createSwingAboutMenu());
        actionMenu.add(new JSeparator(SwingConstants.HORIZONTAL));
        actionMenu.add(createSwingExitMenu());

        return actionMenu;
    }

    public static MenuItem createAWTWebSiteMenu() {
        MenuItem webSiteMenu = new MenuItem(Utility.localizedString("menu.about"));

        webSiteMenu.addActionListener(createAboutActionListener());
//        webSiteMenu.addActionListener(createWebSiteActionListener());

        return webSiteMenu;
    }

    public static JMenuItem createSwingAboutMenu() {
        JMenuItem webSiteMenu = new JMenuItem(Utility.localizedString("menu.about"));

        webSiteMenu.addActionListener(createAboutActionListener());

        return webSiteMenu;
    }

    private static ActionListener createAboutActionListener() {
        return e -> {
            PopupMenuItemClickNotificationService.getInstance().menuItemClicked(e);

            Utility.showAboutDialog(null);

//            FilelugDesktop.navigateTo(Utility.suffixLocaleParameterWithURL(Constants.DEFAULT_WEB_SITE_URL));
        };
    }

//    public static JMenuItem createSwingWebSiteMenu() {
//        JMenuItem webSiteMenu = new JMenuItem(Utility.localizedString("menu.about"));
//
//        webSiteMenu.addActionListener(createWebSiteActionListener());
//
//        return webSiteMenu;
//    }
//
//    private static ActionListener createWebSiteActionListener() {
//        return e -> {
//            PopupMenuItemClickNotificationService.getInstance().menuItemClicked(e);
//
//            FilelugDesktop.navigateTo(Utility.suffixLocaleParameterWithURL(Constants.DEFAULT_WEB_SITE_URL));
//        };
//    }
//
//    public static JMenuItem createComputerNameMenu(String computerName) {
//        String nonNullComputerName = computerName;
//        if (computerName == null || computerName.trim().length() < 1) {
//            nonNullComputerName = Utility.localizedString("app.title");
//        }
//
//        JMenuItem computerNameMenu = new JMenuItem(nonNullComputerName);
//
//        computerNameMenu.setEnabled(false);
//
//        return computerNameMenu;
//    }

    public static MenuItem createAWTComputerNameMenu(String computerName) {
        String nonNullComputerName = computerName;
        if (computerName == null || computerName.trim().length() < 1) {
            nonNullComputerName = Utility.localizedString("app.title");
        }

        MenuItem computerNameMenu = new MenuItem(nonNullComputerName);

        computerNameMenu.setEnabled(false);

        return computerNameMenu;
    }

    public static MenuItem createAWTChangeUserMenu(ActionListener changeAdministratorListener) {
        MenuItem changeUserMenu = new MenuItem(Utility.localizedString("menu.change.user"));

        changeUserMenu.addActionListener(changeAdministratorListener);

        return changeUserMenu;
    }

    public static MenuItem createAWTShowDownloadPageMenu() {
        MenuItem exitMenu = new MenuItem(Utility.localizedString("menu.download.latest.version"));

        exitMenu.addActionListener(createShowDownloadPageListener());

        return exitMenu;
    }

    public static JMenuItem createSwingChangeUserMenu(ActionListener changeAdministratorListener) {
        JMenuItem changeUserMenu = new JMenuItem(Utility.localizedString("menu.change.user"));

        changeUserMenu.addActionListener(changeAdministratorListener);

        return changeUserMenu;
    }

    public static JMenuItem createSwingShowDownloadPageMenu() {
        JMenuItem exitMenu = new JMenuItem(Utility.localizedString("menu.download.latest.version"));

        // FIX: Show dialog with version information and web site link instead.
        exitMenu.addActionListener(createShowDownloadPageListener());

        return exitMenu;
    }

    private static ActionListener createShowDownloadPageListener() {
        return e -> {
            PopupMenuItemClickNotificationService.getInstance().menuItemClicked(e);

            FilelugDesktop.navigateTo(Utility.suffixLocaleParameterWithURL(Constants.DEFAULT_WEB_SITE_DOWNLOAD_PAGE_URL));
        };
    }

    public static MenuItem createAWTExitMenu() {
        MenuItem exitMenu = new MenuItem(Utility.localizedString("menu.exit"));

        exitMenu.addActionListener(createExitActionListener());

        return exitMenu;
    }

    public static JMenuItem createCloseMenuMenu() {
        JMenuItem closeMenu = new JMenuItem(Utility.localizedString("menu.close.current.menu"));

        closeMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PopupMenuItemClickNotificationService.getInstance().menuItemClicked(e);

                Container parent = closeMenu.getParent();

                if (parent != null) {
                    parent.setVisible(false);
                }
            }
        });

        return closeMenu;
    }

    public static JMenuItem createSwingExitMenu() {
        JMenuItem exitMenu = new JMenuItem(Utility.localizedString("menu.exit"));

        exitMenu.addActionListener(createExitActionListener());

        return exitMenu;
    }

    /**
     *
     * @return -1 if not found.
     */
    public static int indexOfMenuItem(Menu menu, MenuItem menuItem) {
        int found = -1;

        int count = menu.getItemCount();

        for (int index = 0; index < count; index++) {
            MenuItem currentMenuItem = menu.getItem(index);

            if (currentMenuItem == menuItem) {
                found = index;

                break;
            }
        }

        return found;
    }

    /**
     *
     * @return -1 if not found.
     */
    public static int indexOfMenuItem(JMenu menu, JMenuItem menuItem) {
        int found = -1;

        int count = menu.getItemCount();

        for (int index = 0; index < count; index++) {
            JMenuItem currentMenuItem = menu.getItem(index);

            if (currentMenuItem == menuItem) {
                found = index;

                break;
            }
        }

        return found;
    }

    public static MenuItem findMenuItemFromAWTMenu(Menu menu, String labelPrefix) {
        MenuItem foundMenuItem = null;

        int count = menu.getItemCount();

        if (count > 0) {
            for (int index = 0; index < count; index++) {
                MenuItem menuItem = menu.getItem(index);

                if (menuItem.getLabel().startsWith(labelPrefix)) {
                    foundMenuItem = menuItem;

                    break;
                }
            }
        }

        return foundMenuItem;
    }

    public static JMenuItem findMenuItemFromJMenu(JMenu menu, String labelPrefix) {
        JMenuItem foundMenuItem = null;

        int count = menu.getItemCount();

        if (count > 0) {
            for (int index = 0; index < count; index++) {
                JMenuItem menuItem = menu.getItem(index);

                if (menuItem.getText().startsWith(labelPrefix)) {
                    foundMenuItem = menuItem;

                    break;
                }
            }
        }

        return foundMenuItem;
    }

    public static String prepareConnectionStatusMenuLabelPrefixForNonAdmin(String nickname) {
        return String.format("%s (", nickname);
    }

    public static String prepareConnectionStatusMenuLabelPrefixForAdmin(String nickname) {
        return String.format("%s (", Utility.localizedString("label.admin.with.nickname", nickname));
    }

    public static String prepareConnectionStatusMenuLabel(String nickname, String displayNameForConnectionStatus) {
        return String.format("%s%s)", prepareConnectionStatusMenuLabelPrefixForNonAdmin(nickname), displayNameForConnectionStatus);
    }

    public static boolean parseToFindIfUserConnectedWithConnectionStatusMenuLabel(String connectionStatusMenuLabel) {
        int startIndex = connectionStatusMenuLabel.lastIndexOf("(") + 1;

        // The last character is ')'
        int endIndex = connectionStatusMenuLabel.length() - 1;

        boolean connected = false;

        try {
            String status = connectionStatusMenuLabel.substring(startIndex, endIndex);

            if (status.equals(AccountState.localizedDisplayNameForState(AccountState.State.ACTIVATED))) {
                connected = true;
            }
        } catch (Exception e) {
            // ignored
        }

        return connected;
    }

    public static ActionListener createExitActionListener() {
        return e -> {
            PopupMenuItemClickNotificationService.getInstance().menuItemClicked(e);

            onApplicationExit();
        };
    }

    public static void onApplicationExit() {
        int selectedOption = Utility.showConfirmDialog(null, Utility.localizedString("dialog.confirm.exist"), Utility.localizedString("dialog.title.confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Utility.createImageIcon("question.png", ""));

        if (selectedOption == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
//    public static void onApplicationExit() {
//        JFrame zeroSizeFrame = new JFrame();
//
//        try {
//            zeroSizeFrame.setSize(0, 0);
//            zeroSizeFrame.setLocationRelativeTo(null);
//            zeroSizeFrame.setUndecorated(true);
//            zeroSizeFrame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
//            zeroSizeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            zeroSizeFrame.setVisible(true);
//            zeroSizeFrame.setAlwaysOnTop(true);
//
//            int selectedOption = JOptionPane.showConfirmDialog(zeroSizeFrame, Utility.localizedString("dialog.confirm.exist"), Utility.localizedString("dialog.title.confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, Utility.createImageIcon("question.png", ""));
//
//            if (selectedOption == JOptionPane.YES_OPTION) {
//                System.exit(0);
//            }
//        } finally {
//            zeroSizeFrame.setVisible(false);
//            zeroSizeFrame.dispose();
//        }
//    }

    public static Menu createAWTMenu(String label) {
        return new Menu(label, false);
    }

    public static JMenu createJMenu(String label) {
        return new JMenu(label, false);
    }
}
