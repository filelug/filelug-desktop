package com.filelug.desktop;

import ch.qos.logback.classic.Logger;
import com.filelug.desktop.db.DatabaseAccess;
import com.filelug.desktop.db.HyperSQLDatabaseAccess;
import com.filelug.desktop.model.*;
import com.filelug.desktop.service.*;
import com.filelug.desktop.service.websocket.ConnectSocket;
import com.filelug.desktop.view.*;
import com.filelug.desktop.view.SplashScreen;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <code>Application</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class Application implements Observer, ConfigurationListener {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("APP");

    private static Application defaultApplication;

    private Thread mainThread;

    private final UserService userService;

    private final ComputerService computerService;

    private final SplashScreen splashScreen;

    private UserConnectionService userConnectionService;

    // always null for Linux
//    private JPopupMenu popupMenu;
    private PopupMenu popupMenu;

    // Either one of connectionStatusAWTMenu or connectionStatusJMenu exists, but not both
    private Menu connectionStatusAWTMenu;
    private JMenu connectionStatusJMenu;

    // Either one of trayIcon or applicationFrame exists, but not both
    private TrayIcon trayIcon;
    private ApplicationFrame applicationFrame;

    private boolean popupMenuNowVisible;

    public static Application getDefaultApplication() {
        return defaultApplication;
    }

    private Application() {
        DatabaseAccess dbAccess = new HyperSQLDatabaseAccess();

        this.userService = new DefaultUserService(dbAccess);

        this.computerService = new DefaultComputerService(dbAccess);

        // show splash screen
        splashScreen = new SplashScreen();
        splashScreen.show();

        PopupMenuItemClickNotificationService.getInstance().addObserver(this);

        UserChangedService.getInstance().addObserver(this);

        userConnectionService = new UserConnectionService();

        userConnectionService.addObserver(this);
        
        ResetApplicationNotificationService.getInstance().addObserver(this);

        NewVersionAvailableState.getInstance().addObserver(this);
    }

    private void setMainThread(Thread mainThread) {
        if (this.mainThread == null) {
            this.mainThread = mainThread;
        }
    }

    private SplashScreen getSplashScreen() {
        return splashScreen;
    }

    private Menu getConnectionStatusAWTMenu() {
        return connectionStatusAWTMenu;
    }

    private void setConnectionStatusAWTMenu(Menu connectionStatusAWTMenu) {
        this.connectionStatusAWTMenu = connectionStatusAWTMenu;
    }

    private JMenu getConnectionStatusJMenu() {
        return connectionStatusJMenu;
    }

    private void setConnectionStatusJMenu(JMenu connectionStatusJMenu) {
        this.connectionStatusJMenu = connectionStatusJMenu;
    }

//    public JPopupMenu getPopupMenu() {
//        return popupMenu;
//    }
    public PopupMenu getPopupMenu() {
        return popupMenu;
    }

    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    public void setTrayIcon(TrayIcon trayIcon) {
        this.trayIcon = trayIcon;
    }

    public ApplicationFrame getApplicationFrame() {
        return applicationFrame;
    }

    public void setApplicationFrame(ApplicationFrame applicationFrame) {
        this.applicationFrame = applicationFrame;
    }

    public boolean isPopupMenuNowVisible() {
        return popupMenuNowVisible;
    }

    public void setPopupMenuNowVisible(boolean popupMenuNowVisible) {
        this.popupMenuNowVisible = popupMenuNowVisible;
    }

    public static void main(String[] args) {
        // DEBUG
//        Utility.listSystemProperties();

        LOGGER.info("Startup application.");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable t) {
            // ignored
        }

        if (!OSUtility.USE_HTTPS) {
            LOGGER.warn("NO Https used. The application starts under testing environment.");
        }

        defaultApplication = new Application();

        defaultApplication.setMainThread(Thread.currentThread());

        defaultApplication.getSplashScreen().setDescription(Utility.localizedString("text.splash.load.system.properties"));

        System.setProperty(PropertyConstants.PROPERTY_NAME_FILELUG_DESKTOP_CURRENT_VERSION, Utility.findCurrentDesktopVersion());

        LOGGER.info("filelug version: " + System.getProperty(PropertyConstants.PROPERTY_NAME_FILELUG_DESKTOP_CURRENT_VERSION));

        System.setProperty(PropertyConstants.PROPERTY_NAME_LOCALE, ClopuccinoMessages.localeToString(Locale.getDefault()));

        LOGGER.info("filelug locale: " + System.getProperty(PropertyConstants.PROPERTY_NAME_LOCALE));

        defaultApplication.getSplashScreen().setDescription(Utility.localizedString("text.splash.load.configuration"));

        // load preferences, migrate from old version if necessary

        BeforeConfigurePreferences beforeConfigurePreferences = needCopyValueFromV1 -> {
            if (needCopyValueFromV1) {
                defaultApplication.getSplashScreen().setDescription(Utility.localizedString("text.splash.upgrading"));
            } else {
                defaultApplication.getSplashScreen().setDescription(Utility.localizedString("text.splash.prepare.qrcode"));
            }
        };

        Utility.configurePreferences(beforeConfigurePreferences);

        // create device token if not exists
        Utility.generateDeviceTokenString();

        defaultApplication.getSplashScreen().setDescription(Utility.localizedString("text.splash.load.database"));

        // setup database

        try {
            DatabaseAccess dbAccess = new HyperSQLDatabaseAccess();
            dbAccess.initDatabase();
        } catch (Exception e) {
            // TODO: prompt that the application may already be launched.

            String message = Utility.localizedString("text.splash.load.database.failed");

            LOGGER.error(message + "\n" + e.getClass().getName() + "\n" + e.getMessage(), e);

            defaultApplication.getSplashScreen().setDescription(message);

            String menuBar;

            if (OSUtility.isWindows()) {
                menuBar = Utility.localizedString("application.menubar.windows");
            } else if (OSUtility.isOSX()) {
                menuBar = Utility.localizedString("application.menubar.mac");
            } else {
                menuBar = Utility.localizedString("application.menubar.linux");
            }

            String displayMessage = Utility.localizedString("application.exists", menuBar);

            Component parentComponent = null;

            if (defaultApplication.getSplashScreen() != null && defaultApplication.getSplashScreen().getWindow() != null) {
                parentComponent = defaultApplication.getSplashScreen().getWindow();
            }

            int option = Utility.showConfirmDialog(parentComponent, displayMessage, Utility.localizedString("error"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));

            if (option == JOptionPane.OK_OPTION) {
                System.exit(1);
            }
        }

        // Application Menu for mac

        if (OSUtility.isOSX()) {
            MacApplication.setupHandlers();
        }

        defaultApplication.getSplashScreen().setDescription(Utility.localizedString("text.splash.check.network"));


        // Show system tray for Windows and OSX; show application frame for Linux.
        // Must be before "defaultApplication.prepareForAllUsers()" to react the change of user data, such as nickname and AccountState.State.

//        defaultApplication.setConnectionStatusJMenu(MenuUtility.createJMenu(Utility.localizedString("menu.connection.status")));

        if (OSUtility.isWindows() || OSUtility.isOSX()) {
            defaultApplication.setConnectionStatusAWTMenu(MenuUtility.createAWTMenu(Utility.localizedString("menu.connection.status")));

            defaultApplication.setTrayIcon(defaultApplication.createTrayIconWithConnectionStatusMenu());
        } else {
            defaultApplication.setConnectionStatusJMenu(MenuUtility.createJMenu(Utility.localizedString("menu.connection.status")));

            ApplicationFrame applicationFrame = new ApplicationFrame(defaultApplication.getConnectionStatusJMenu(), defaultApplication.createChangeAdministratorListener());

            defaultApplication.setApplicationFrame(applicationFrame);
        }

        boolean shouldShowQRCode = defaultApplication.shouldShowQRCode();

        if (shouldShowQRCode) {
            // listen to the QR code string saved in preference.
            Utility.addPreferenceChangeListener(defaultApplication);

            // login user or login with QR code
            defaultApplication.loginUserOrGetQRCode();
        } else {
            defaultApplication.prepareForAllUsers();
        }

        // Show system tray or application frame after QRCode displayed or connect to user successfully.

        TrayIcon trayIcon = defaultApplication.getTrayIcon();

        if (trayIcon != null) {
            defaultApplication.showSystemTrayWithTrayIcon(trayIcon);
        } else {
            final ApplicationFrame applicationFrame = defaultApplication.getApplicationFrame();

            if (applicationFrame != null) {
                SwingUtilities.invokeLater(() -> applicationFrame.show());
            }
        }

        // enabled/disabled change-user menu after the menu shows
        defaultApplication.enableChangeUserMenu(!shouldShowQRCode);

        // Start check-reconnect even if login or connect all users failed
        // The method must be invoked after loginUserOrGetQRCode() or prepareForAllUsers() so
        // if the application needs to be reset, it should be invoked after all local user are deleted
        defaultApplication.initSchedulers();

        defaultApplication.addShutdownHook();
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (PopupMenuItemClickNotificationService.class.isInstance(observable)) {
            if (arg != null && ActionEvent.class.isInstance(arg)) {
                Object source = ((ActionEvent) arg).getSource();

                if (source != null && JMenuItem.class.isInstance(source)) {
                    setPopupMenuNowVisible(false);

                    // DEBUG
//                    System.out.println("setPopupMenuNowVisible(false)");
                }
            }
        } else if (UserChangedService.class.isInstance(observable)) {
            if (arg != null && UserChangedEvent.class.isInstance(arg)) {
                UserChangedEvent userChanged = (UserChangedEvent) arg;

                UserChangedEvent.ChangeType type = userChanged.getChangeType();

                if (type.equals(UserChangedEvent.ChangeType.USER_ADDED)) {
                    User user = userChanged.getNewUser();

                    String userId = user.getAccount();
                    String nickname = user.getNickname();
                    AccountState.State connectionState = user.getState();

                    userConnectionService.addOrUpdateUserConnectionModel(new UserConnectionModel(userId, nickname, connectionState));

                    enableChangeUserMenu(true);

                    if (getSplashScreen() != null && getSplashScreen().isOpen()) {
                        String description;

                        if (connectionState == AccountState.State.ACTIVATED) {
                            description = Utility.localizedString("text.splash.try.active.user.success", nickname);
                        } else {
                            description = Utility.localizedString("text.splash.try.active.user.failure", nickname);
                        }

                        getSplashScreen().setDescription(description);
                    }
                } else if (type.equals(UserChangedEvent.ChangeType.USER_UPDATED)) {
                    User user = userChanged.getNewUser();

                    String userId = user.getAccount();
                    String nickname = user.getNickname();
                    AccountState.State connectionState = user.getState();

                    userConnectionService.addOrUpdateUserConnectionModel(new UserConnectionModel(userId, nickname, connectionState));

                    // When upgrade from 1.x, the initial status of change-user menu set to disabled because shouldShowQRCode == true,
                    // and here is the only place that the change-user menu can change to enabled after the user is updated after login successfull.
                    if (user.getAdmin() != null && user.getAdmin()) {
                        enableChangeUserMenu(true);
                    }

                    if (getSplashScreen() != null && getSplashScreen().isOpen()) {
                        if (connectionState == AccountState.State.ACTIVATED) {
                            String description = Utility.localizedString("text.splash.try.active.user.success", nickname);
                            getSplashScreen().setDescription(description);

                        }
                    }
                } else if (type.equals(UserChangedEvent.ChangeType.USER_REMOVED)) {
                    User user = userChanged.getOldUser();

                    if (user != null) {
                        String userId = user.getAccount();

                        int menuIndex = userConnectionService.indexOfUserConnectionModelsByUserId(userId);

                        if (menuIndex > -1) {
                            if (getConnectionStatusAWTMenu() != null) {
                                Menu menu = getConnectionStatusAWTMenu();

                                SwingUtilities.invokeLater(() -> {
                                    try {
                                        menu.remove(menuIndex);
                                    } catch (Exception e) {
                                        // ignored. --> menuItem could not found.
                                    }
                                });
                            } else if (getConnectionStatusJMenu() != null) {
                                JMenu menu = getConnectionStatusJMenu();

                                SwingUtilities.invokeLater(() -> {
                                    try {
                                        menu.remove(menuIndex);
                                    } catch (Exception e) {
                                        // ignored. --> menuItem could not found.
                                    }
                                });
                            }
                        }

                        userConnectionService.removeUserConnectionModelByUserId(user.getAccount());

                        if (userService.findAdministrator() == null) {
                            enableChangeUserMenu(false);
                        }
                    }
                }
            }
        } else if (UserConnectionService.class.isInstance(observable)) {
            if (arg != null && UserConnectionEvent.class.isInstance(arg)) {
                UserConnectionEvent userConnectionEvent = (UserConnectionEvent) arg;

                UserConnectionEvent.ChangeType type = userConnectionEvent.getChangeType();

                if (type == UserConnectionEvent.ChangeType.USER_CONNECTION_UPDATED) {
                    // update menu item

                    UserConnectionModel newModel = userConnectionEvent.getNewUserConnectionModel();

                    String userId = newModel.getUserId();

                    String nickname = newModel.getNickname();

                    AccountState.State connectionState = newModel.getConnectionState();

                    String newMenuLabel = prepareMenuLabel(userId, nickname, connectionState);

                    int menuIndex = userConnectionService.indexOfUserConnectionModelsByUserId(userId);

                    if (menuIndex > -1) {
                        if (getConnectionStatusAWTMenu() != null) {
                            Menu menu = getConnectionStatusAWTMenu();

                            try {
                                MenuItem menuItem = menu.getItem(menuIndex);

                                if (menuItem != null) {
                                    SwingUtilities.invokeLater(() -> {
                                        menuItem.setLabel(newMenuLabel);
                                    });
                                }
                            } catch (Exception e) {
                                // ignored. --> menuItem could not found.
                            }
                        } else if (getConnectionStatusJMenu() != null) {
                            JMenu menu = getConnectionStatusJMenu();

                            try {
                                JMenuItem menuItem = menu.getItem(menuIndex);

                                if (menuItem != null) {
                                    SwingUtilities.invokeLater(() -> menuItem.setText(newMenuLabel));
                                }
                            } catch (Exception e) {
                                // ignored. --> menuItem could not found.
                            }
                        }
                    } else {
                        LOGGER.warn(String.format("Update menu item failed because the menu item for user: '%s' not found.", nickname));
                    }
                } else if (type == UserConnectionEvent.ChangeType.USER_CONNECTION_ADDED) {
                    // Add menu item

                    UserConnectionModel newModel = userConnectionEvent.getNewUserConnectionModel();

                    String userId = newModel.getUserId();

                    String nickname = newModel.getNickname();

                    AccountState.State connectionState = newModel.getConnectionState();

                    String menuLabel = prepareMenuLabel(userId, nickname, connectionState);

                    if (getConnectionStatusAWTMenu() != null) {
                        MenuItem menuItem = new MenuItem(menuLabel);

                        menuItem.addActionListener(e -> {
                            PopupMenuItemClickNotificationService.getInstance().menuItemClicked(e);

                            onClickMenuItemForUser(userId);
                        });

                        SwingUtilities.invokeLater(() -> getConnectionStatusAWTMenu().add(menuItem));
                    } else if (getConnectionStatusJMenu() != null) {
                        JMenuItem menuItem = new JMenuItem(menuLabel);

                        menuItem.addActionListener(e -> {
                            PopupMenuItemClickNotificationService.getInstance().menuItemClicked(e);

                            onClickMenuItemForUser(userId);
                        });

                        SwingUtilities.invokeLater(() -> getConnectionStatusJMenu().add(menuItem));
                    }
                }
            }
        } else if (NewVersionAvailableState.class.isInstance(observable)) {
            String applicationVersion = System.getProperty(PropertyConstants.PROPERTY_NAME_FILELUG_DESKTOP_CURRENT_VERSION, Constants.DEFAULT_DESKTOP_VERSION);

            NewVersionAvailableState state = (NewVersionAvailableState) observable;

            String latestVersion = state.getNewVersion();
            String downloadUrl = state.getDownloadUrl();

            prepareVersion(applicationVersion, latestVersion, downloadUrl);
        } else if (ResetApplicationNotificationService.class.isInstance(observable)) {
            ResetApplicationNotificationService resetApplicationNotificationService = (ResetApplicationNotificationService) observable;

            if (arg != null && ResetApplicationNotificationService.REASON.class.isInstance(arg)) {
                ResetApplicationNotificationService.REASON reason = (ResetApplicationNotificationService.REASON) arg;

                LOGGER.warn("Application is going to reset. Reason:\n" + resetApplicationNotificationService.representationStringFromReason(reason));

                if (reason == ResetApplicationNotificationService.REASON.REMOVE_ALL_USERS) {
                    removeAllUsersAndShowQRCode();
                } else if (reason == ResetApplicationNotificationService.REASON.REFRESH_QR_CODE) {
                    loginUserOrGetQRCode();
                } else {
                    resetApplicationAndShowQRCode();
                }
            }
        }
    }

    @Override
    public void configurationChanged(ConfigurationEvent event) {
        int type = event.getType();

        // There're twice set-event fired before AND after the property is updated.
        boolean beforeUpdate = event.isBeforeUpdate();

        if (type == AbstractConfiguration.EVENT_SET_PROPERTY && !beforeUpdate) { // Not EVENT_ADD_PROPERTY, which triggered when preferences.addProperty(key, value)
            String propertyName = event.getPropertyName();

            if (propertyName.equals(PropertyConstants.PROPERTY_NAME_QR_CODE)) {
                // When QR code received, prompt the QR code UI.

                String qrCode = (String) event.getPropertyValue();

                if (qrCode != null && qrCode.trim().length() > 0) {
                    try {
                        Dimension dimension = getSplashScreen().getWindow().getSize();

                        QRCodeWindow qrCodeWindow = new QRCodeWindow(qrCode, dimension);

                        qrCodeWindow.show();
                    } catch (Exception e) {
                        LOGGER.error("Error on preparing for QR code window", e);

                        String errorMessage = ClopuccinoMessages.getMessage("error.launch.qrcode.window.try.again");

                        Component parentComponent = null;

                        if (getSplashScreen() != null && getSplashScreen().getWindow() != null) {
                            parentComponent = getSplashScreen().getWindow();
                        }

                        int option = Utility.showConfirmDialog(parentComponent, errorMessage, ClopuccinoMessages.getMessage("error"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));
//                        int option = JOptionPane.showConfirmDialog(getSplashScreen().getWindow(), errorMessage, ClopuccinoMessages.getMessage("error"), JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));

                        if (option == JOptionPane.OK_OPTION) {
                            loginUserOrGetQRCode();
                        }
                    } finally {
                        // Close splash screen when QR code window shows

                        closeSplashScreen();
                    }
                }
            } else if (propertyName.equals(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME)) {
                // change computer name in AWT menu or ApplicationFrame title

                String newComputerName = (String) event.getPropertyValue();

                changeMenuItemOrFrameTitleToNewComputerName(newComputerName);
            }
        }
    }

    private boolean shouldShowQRCode() {
        // Check if the table record of the admin user contains a value in column session_id,
        // or if app_reset in preferences is true

        boolean shouldShowQRCode = false;

        String appResetValue = Utility.getPreference(PropertyConstants.PROPERTY_NAME_APP_RESET, "Empty");

        if (appResetValue.trim().equals("1")) {
            shouldShowQRCode = true;
        } else {
            User adminUser = userService.findAdministrator();

            if (adminUser == null) {
                shouldShowQRCode = true;
            } else if (adminUser.getSessionId() == null || adminUser.getLugServerId() == null
                       || adminUser.getSessionId().trim().length() < 0 || adminUser.getLugServerId().trim().length() < 0) {
                shouldShowQRCode = true;
            }
        }

        return shouldShowQRCode;
    }

    private TrayIcon createTrayIconWithConnectionStatusMenu() {
        String tooltip = Utility.localizedString("app.title");

        Image icon = Utility.createTrayIcon();

        if (icon == null) {
            LOGGER.error("Tray icon not found.");

            System.exit(2);
        }

        final TrayIcon trayIcon = new TrayIcon(icon, tooltip);

        // Add menus

        final String computerName = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, "");

        final PopupMenu popupMenu = MenuUtility.createAWTMenu(computerName, getConnectionStatusAWTMenu(), createChangeAdministratorListener());
        
//        popupMenu.addPopupMenuListener(new PopupMenuListener() {
//            @Override
//            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
////                System.out.println("popupMenuWillBecomeVisible");
//                setPopupMenuNowVisible(true);
//            }
//
//            @Override
//            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
////                System.out.println("popupMenuWillBecomeInvisible");
//            }
//
//            @Override
//            public void popupMenuCanceled(PopupMenuEvent e) {
////                System.out.println("popupMenuCanceled");
//            }
//        });

        this.popupMenu = popupMenu;

        trayIcon.setPopupMenu(popupMenu);

        trayIcon.setImageAutoSize(true);

//        trayIcon.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (!isPopupMenuNowVisible()) {
////                    int screenX = e.getXOnScreen();
////                    int screenY = e.getYOnScreen();
////
////                    // DEBUG
////                    System.out.println("Screen: [x=" + screenX + ", y=" + screenY + "]");
////
////                    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
////                    GraphicsDevice[] lstGDs = ge.getScreenDevices();
////                    GraphicsDevice defaultScreenDevice = ge.getDefaultScreenDevice();
////
////                    for (GraphicsDevice gd : lstGDs) {
////                        GraphicsConfiguration gc = gd.getDefaultConfiguration();
////                        Rectangle screenBounds = gc.getBounds();
////
////                        boolean isDefaultScreenDevice = (gd.equals(defaultScreenDevice));
////
////                        System.out.println("Device: " + screenBounds + (isDefaultScreenDevice ? ("(DEFAULT)") : ""));
////                    }
//
//                    double mouseX = e.getX();
//                    double mouseY = e.getY();
//
////                    // DEBUG
////                    System.out.println("Before: [x=" + mouseX + ", y=" + mouseY + "]");
//
//                    if (OSUtility.isWindows()) {
//                        PopupMenuLocationDetector mouseLocationDetector = new PopupMenuLocationDetector(popupMenu, e.getPoint());
//
//                        Point newPoint = mouseLocationDetector.prepareLocation();
//
//                        mouseX = newPoint.getX();
//                        mouseY = newPoint.getY();
//                    }
//
////                    // DEBUG
////                    System.out.println("After: [x=" + mouseX + ", y=" + mouseY + "]");
////
////                    Point mousePoint = new Point((int) mouseX, (int) mouseY);
////
////                    popupMenu.setLocation(mousePoint);
//
//                    popupMenu.setLocation((int) mouseX, (int) mouseY);
//                    popupMenu.setInvoker(popupMenu);
//                    popupMenu.setVisible(true);
//
////                    System.out.println("Popup location: "+ popupMenu.getLocation());
////                    System.out.println("Popup Screen: " + popupMenu.getLocationOnScreen());
//                } else {
//                    setPopupMenuNowVisible(false);
//                }
//            }
//        });

//        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
//            public void eventDispatched(AWTEvent event) {
//                if( event.getID() == ActionEvent.ACTION_PERFORMED) {
//                    System.out.println("[" + event.getSource().getClass().getSimpleName() + "] : " + event);
//                }
//            }
//        }, AWTEvent.ACTION_EVENT_MASK);
//        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
//            public void eventDispatched(AWTEvent event) {
//                if(event instanceof MouseEvent){
//                    MouseEvent mouseEvent = (MouseEvent)event;
//
//                    System.out.println("Mouse Event ID: " + mouseEvent.getID());
//
//                    if(mouseEvent.getID() == MouseEvent.MOUSE_CLICKED && popupMenu.getMousePosition(true) == null){
//                        // mouse point not in popupMenu --> hide popupMenu
//                        popupMenu.setVisible(false);
//                    }
//                }
//            }
//        }, AWTEvent.MOUSE_EVENT_MASK);

        return trayIcon;
    }

    public ActionListener createChangeAdministratorListener() {
        return e -> {
            PopupMenuItemClickNotificationService.getInstance().menuItemClicked(e);

            Component parentComponent = (getApplicationFrame() != null) ? getApplicationFrame().getFrame() : null;

            String message = ClopuccinoMessages.getMessage("dialog.confirm.change.admin");

            int option = Utility.showConfirmDialog(parentComponent, message, ClopuccinoMessages.getMessage("title.confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, Utility.createImageIcon("question.png", ""));

            if (option == JOptionPane.YES_OPTION) {
                ResetApplicationNotificationService.getInstance().applicationShouldReset(ResetApplicationNotificationService.REASON.REMOVE_ALL_USERS);
            }
        };
    }

    private void showSystemTrayWithTrayIcon(TrayIcon trayIcon) {
        SwingUtilities.invokeLater(() -> {
            final SystemTray tray = SystemTray.getSystemTray();

            try {
                tray.add(trayIcon);
            } catch (Exception e) {
                LOGGER.error("Failed to add tray icon", e);

                System.exit(3);
            }
        });
    }

    private void initSchedulers() {
        // Check if connect sockets need reconnect
        CheckReconnectService checkReconnectService = CheckReconnectService.getInstance();

        checkReconnectService.start();
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // stop Preferences change listener
            try {
                Utility.removePreferenceChangeListener(this);
            } catch (Throwable t) {
                // ignored
            }

            // stop listeners of all the observables

            try {
                userConnectionService.deleteObservers();
            } catch (Throwable t) {
                // ignored
            }

            try {
                PopupMenuItemClickNotificationService.getInstance().deleteObservers();
            } catch (Throwable t) {
                // ignored
            }

            try {
                UserChangedService.getInstance().deleteObservers();
            } catch (Throwable t) {
                // ignored
            }

            // stop checking reconnect

            CheckReconnectService checkReconnectService = CheckReconnectService.getInstance();

            checkReconnectService.stop();

            // Close all sockets to desktops

            try {
                ConnectSocket.closeAllConnectSockets();
            } catch (Exception e) {
                // ignored
            }

            try {
                DatabaseAccess dbAccess = new HyperSQLDatabaseAccess();
                dbAccess.closeDataSource();

                LOGGER.debug("DB shutdown");
            } catch (Exception e) {
                // ignored
            } finally {
                /* shutdown executor only after db shutdown */
                try {
                    ExecutorService executor = Utility.getExecutorService();

                    /* This will make the executor accept no new threads and finish all existing threads in the queue */
                    executor.shutdown();

                    /* Wait until all threads are finish */
                    executor.awaitTermination(3, TimeUnit.SECONDS);
                } catch (Throwable t) {
                    // ignored
                }
            }

            if (mainThread != null) {
                try {
                    mainThread.join();
                } catch (Exception e) {
                    // ignored
                }
            }
        }));
    }

    private void onClickMenuItemForUser(String userId) {
        User user = userService.findLocalUserById(userId);

        AccountState.State currentStatus = user.getState();

        if (currentStatus == AccountState.State.ACTIVATED) {
            onClickActiveUserMenuItem(userId);
        } else {
            onClickInactiveUserMenuItem();
        }
    }

    private void onClickInactiveUserMenuItem() {
        Component parentComponent = (getApplicationFrame() != null) ? getApplicationFrame().getFrame() : null;

        String computerName = Utility.getPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, "");

        String message = ClopuccinoMessages.getMessage("connect.computer.from.device", computerName);

        Utility.showMessageDialog(parentComponent, message, ClopuccinoMessages.getMessage("title.connect"), JOptionPane.INFORMATION_MESSAGE, Utility.createImageIcon("iphone5-active.png", ""));
    }

    private void onClickActiveUserMenuItem(String userId) {
        Component parentComponent = (getApplicationFrame() != null) ? getApplicationFrame().getFrame() : null;

        String message = ClopuccinoMessages.getMessage("if.reconnect.computer");

        int option = Utility.showConfirmDialog(parentComponent, message, ClopuccinoMessages.getMessage("title.reconnect"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, Utility.createImageIcon("question.png", ""));

        if (option == JOptionPane.YES_OPTION) {
            User user = userService.findLocalUserById(userId);

            userService.connectUser(user, true);
        }
    }

    private String prepareMenuLabel(String userId, String nickname, AccountState.State connectionState) {
        User user = userService.findLocalUserById(userId);

        String newMenuLabel;

        List<String> allUserIds = userService.findAllUserIds();

        if (allUserIds.size() > 1 && user.getAdmin() != null && user.getAdmin()) {
            String adminWithNickname = Utility.localizedString("label.admin.with.nickname", nickname);

            newMenuLabel = MenuUtility.prepareConnectionStatusMenuLabel(adminWithNickname, AccountState.localizedDisplayNameForState(connectionState));
        } else {
            newMenuLabel = MenuUtility.prepareConnectionStatusMenuLabel(nickname, AccountState.localizedDisplayNameForState(connectionState));
        }

        return newMenuLabel;
    }

//    private void changeMenuItemOrFrameTitleToNewComputerName(String newComputerName) {
//        if (getPopupMenu() != null) {
//            JMenuItem computerNameMenuItem = (JMenuItem) getPopupMenu().getComponent(MenuUtility.INDEX_OF_COMPUTER_NAME_MENU_ITEM_OF_AWT_MENU);
//
//            SwingUtilities.invokeLater(() -> {
//                computerNameMenuItem.setText(newComputerName);
//            });
//        } else {
//            final ApplicationFrame applicationFrame = getApplicationFrame();
//
//            if (applicationFrame != null) {
//                SwingUtilities.invokeLater(() -> applicationFrame.changeFrameTitleTo(newComputerName));
//            }
//        }
//    }
    private void changeMenuItemOrFrameTitleToNewComputerName(String newComputerName) {
        if (getPopupMenu() != null) {
            MenuItem computerNameMenuItem = getPopupMenu().getItem(MenuUtility.INDEX_OF_COMPUTER_NAME_MENU_ITEM_OF_AWT_MENU);

            SwingUtilities.invokeLater(() -> {
                computerNameMenuItem.setLabel(newComputerName);
            });
        } else {
            final ApplicationFrame applicationFrame = getApplicationFrame();

            if (applicationFrame != null) {
                SwingUtilities.invokeLater(() -> applicationFrame.changeFrameTitleTo(newComputerName));
            }
        }
    }

//    private void enableChangeUserMenu(boolean enabled) {
//        if (getPopupMenu() != null) {
//            JMenuItem changeUserMenuItem = (JMenuItem) getPopupMenu().getComponent(MenuUtility.INDEX_OF_CHANGE_USER_MENU_ITEM_OF_AWT_MENU);
//
//            SwingUtilities.invokeLater(() -> changeUserMenuItem.setEnabled(enabled));
//        } else {
//            final ApplicationFrame applicationFrame = getApplicationFrame();
//
//            if (applicationFrame != null) {
//                applicationFrame.enableChangeUserMenu(enabled);
//            }
//        }
//    }
    private void enableChangeUserMenu(boolean enabled) {
        if (getPopupMenu() != null) {
            MenuItem changeUserMenuItem = getPopupMenu().getItem(MenuUtility.INDEX_OF_CHANGE_USER_MENU_ITEM_OF_AWT_MENU);

            SwingUtilities.invokeLater(() -> changeUserMenuItem.setEnabled(enabled));
        } else {
            final ApplicationFrame applicationFrame = getApplicationFrame();

            if (applicationFrame != null) {
                applicationFrame.enableChangeUserMenu(enabled);
            }
        }
    }

    private void prepareForAllUsers() {
        if (getSplashScreen() != null && getSplashScreen().isOpen()) {
            getSplashScreen().setDescription(Utility.localizedString("text.splash.test.connection"));
        }

        try {
            // listen to the QR code string saved in preference.
            Utility.addPreferenceChangeListener(this);

            // Update the approved users first before connect local users

            userService.syncUsersWithApprovedConnectionUsers();

            // Prepre menu items for user connection status and active all users
            prepareUserConnectionStatusMenuItemsAndConnectAllUsers();
        } catch (SessionNotFoundException e) {
            // session of the admin not found, or the user of this session not found -> reset application (but not restart) and show QRCode

            ResetApplicationNotificationService.getInstance().applicationShouldReset(ResetApplicationNotificationService.REASON.SESSION_OR_USER_NOT_FOUND);
//            LOGGER.warn("User or session has been deleted. Application will reset and user have to re-login using new QR code.");
//
//            resetApplicationAndShowQRCode();
        } catch (ComputerNotFoundException e) {
            // computer not found -> reset application (but not restart) and show QRCode

            ResetApplicationNotificationService.getInstance().applicationShouldReset(ResetApplicationNotificationService.REASON.COMPUTER_NOT_FOUND);
//            LOGGER.warn("The computer has been deleted. Application will reset and user have to re-login using new QR code.");
//
//            resetApplicationAndShowQRCode();
        } catch (Exception e) {
            LOGGER.error("Failed to synchronized approved users list from server.", e);

            promptToConnectAllUsersAgain();
        } finally {
            closeSplashScreen();
        }
    }

    private void promptToConnectAllUsersAgain() {
        Component parentComponent = null;

        if (getSplashScreen() != null && getSplashScreen().getWindow() != null && getSplashScreen().getWindow().isVisible()) {
            parentComponent = getSplashScreen().getWindow();
        }

        String errorMessage = ClopuccinoMessages.getMessage("error.connect.to.server.try.again");

        int option = Utility.showConfirmDialog(parentComponent, errorMessage, ClopuccinoMessages.getMessage("error"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));

        if (option == JOptionPane.YES_OPTION) {
            prepareForAllUsers();
        } else {
            closeSplashScreen();
        }
    }

    private void removeAllUsersAndShowQRCode() {
        FutureCallback<HttpResponse> callback = new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                int statusCode = result.getStatusLine().getStatusCode();

                if (statusCode == HttpServletResponse.SC_OK) {
                    // stop preferences change listener
                    Utility.removePreferenceChangeListener(Application.this);

                    // stop reconnect checking
                    try {
                        CheckReconnectService checkReconnectService = CheckReconnectService.getInstance();

                        checkReconnectService.stop();
                    } catch (Throwable t) {
                        LOGGER.error("Failed to stop check reconnect service.", t);
                    }

                    // reset administrator account and its users
                    userService.removeAllUsers();

                    // add-back the preferences change listener,
                    // must be added before login in order to listen to the QR code string saved in preference.
                    Utility.addPreferenceChangeListener(Application.this);

                    // add-back reconnect checking
                    try {
                        CheckReconnectService checkReconnectService = CheckReconnectService.getInstance();

                        checkReconnectService.start();
                    } catch (Throwable t) {
                        LOGGER.error("Failed to start check reconnect service.", t);
                    }

                    // login user or login with QR code

                    loginUserOrGetQRCode();
                } else {
                    if (statusCode == HttpServletResponse.SC_FORBIDDEN) {
                        // if statusCode is 403, meaning that the session of the admin user not found --> reset the application

                        ResetApplicationNotificationService.getInstance().applicationShouldReset(ResetApplicationNotificationService.REASON.SESSION_OR_USER_NOT_FOUND);
                    } else if (statusCode == Constants.HTTP_STATUS_COMPUTER_NOT_FOUND) {
                        // if statusCode is 460, meaning that the computer not found --> reset the application

                        ResetApplicationNotificationService.getInstance().applicationShouldReset(ResetApplicationNotificationService.REASON.COMPUTER_NOT_FOUND);
                    } else {
                        Component parentComponent = (getApplicationFrame() != null) ? getApplicationFrame().getFrame() : null;

                        String message = ClopuccinoMessages.getMessage("error.change.user.and.try.later");

                        Utility.showMessageDialog(parentComponent, message, ClopuccinoMessages.getMessage("error"), JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));
                    }
                }
            }

            @Override
            public void failed(Exception e) {
                Component parentComponent = (getApplicationFrame() != null) ? getApplicationFrame().getFrame() : null;

                if (HttpHostConnectException.class.isInstance(e) || ConnectTimeoutException.class.isInstance(e) || UnknownHostException.class.isInstance(e) || ConnectException.class.isInstance(e)) {
                    LOGGER.debug("Network Failure.\nClass: " + e.getClass().getName() + "\nMessage: " + e.getMessage());

                    String message = ClopuccinoMessages.getMessage("no.network");

                    Utility.showMessageDialog(parentComponent, message, ClopuccinoMessages.getMessage("error"), JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));
                } else {
                    LOGGER.debug("Failed to remove all users from computer!\nClass: " + e.getClass().getName() + "\nMessage: " + e.getMessage());

                    String message = ClopuccinoMessages.getMessage("error.change.user.and.try.later");

                    Utility.showMessageDialog(parentComponent, message, ClopuccinoMessages.getMessage("error"), JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));
                }
            }

            @Override
            public void cancelled() {
                LOGGER.debug("User cancelled invoking service to remove all users of this computer.");
            }
        };

        try {
            computerService.removeAllUsersFromComputer(callback);
        } catch (Exception e) {
            LOGGER.debug("Failed to remove all users from computer!\nClass: " + e.getClass().getName() + "\nMessage: " + e.getMessage());

            Component parentComponent = (getApplicationFrame() != null) ? getApplicationFrame().getFrame() : null;

            String message = ClopuccinoMessages.getMessage("error.change.user.and.try.later");

            Utility.showMessageDialog(parentComponent, message, ClopuccinoMessages.getMessage("error"), JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));
        }
    }

    private void resetApplicationAndShowQRCode() {
        // set the first menu item of the tray icon or the title of the application frame to 'Filelug'
        changeMenuItemOrFrameTitleToNewComputerName(Utility.localizedString("app.title"));

        // stop preferences change listener
        Utility.removePreferenceChangeListener(Application.getDefaultApplication());

        // stop reconnect checking
        try {
            CheckReconnectService checkReconnectService = CheckReconnectService.getInstance();

            checkReconnectService.stop();
        } catch (Throwable t) {
            LOGGER.error("Failed to stop check reconnect service.", t);
        }

        // reset application
        userService.resetApplication();

        // add-back the preferences change listener,
        // must be added before login in order to listen to the QR code string saved in preference.
        Utility.addPreferenceChangeListener(this);

        // add-back reconnect checking
        try {
            CheckReconnectService checkReconnectService = CheckReconnectService.getInstance();

            checkReconnectService.start();
        } catch (Throwable t) {
            LOGGER.error("Failed to start check reconnect service.", t);
        }

        // login user or login with QR code

        loginUserOrGetQRCode();
    }

    private void prepareUserConnectionStatusMenuItemsAndConnectAllUsers() {
        List<User> allUsers = userService.findAllLocalUsers();

        if (allUsers != null && allUsers.size() > 0) {
            getSplashScreen().setDescription(Utility.localizedString("text.splash.try.active.user"));

            for (User user : allUsers) {
                String userId = user.getAccount();

                String nickname = user.getNickname();

                AccountState.State connectionState = user.getState();

                // add UserConnectionModel if not found, or update it if found.

                userConnectionService.addOrUpdateUserConnectionModel(new UserConnectionModel(userId, nickname, connectionState));

                userService.connectUser(user, false);
            }
        }
    }

//    private void connectUser(User user) {
//        String userId = user.getAccount();
//        String sessionId = user.getSessionId();
//
//        try {
//            // Get new lug server id and save to User before connecting
//
//            String lugServerId = userService.dispatchConnection(userId);
//
//            if (lugServerId == null || lugServerId.trim().length() < 1) {
//                // use the current lug server id
//
//                lugServerId = user.getLugServerId();
//            } else if (!lugServerId.equals(user.getLugServerId())) {
//                // update to db only if changed
//
//                user.setLugServerId(lugServerId);
//
//                userService.updateLocalUser(user);
//            }
//
//            ConnectModel connectModel = new ConnectModel();
//
//            // computerId is not used, but userId IS USED when connecting from computer because we need use it to disconnect current connection, if any.
//
//            connectModel.setSid(Sid.CONNECT_V2);
//            connectModel.setAccount(userId);
//            connectModel.setSessionId(sessionId);
//            connectModel.setLugServerId(lugServerId);
//            connectModel.setLocale(Utility.getApplicationLocale());
//
//            // system properties
//            Properties properties = Utility.prepareSystemProperties();
//
//            connectModel.setProperties(properties);
//
//            ConnectResponseState connectResponseState = new ConnectResponseState(connectModel);
//
//            userService.connectFromComputer(connectResponseState);
//        } catch (Throwable t) {
//            User oldUser = user.copy();
//
//            user.setState(AccountState.State.INACTIVATED);
//
//            userService.updateLocalUser(user);
//
//            User newUser = userService.findLocalUserById(userId);
//
//            if (newUser != null) {
//                UserChangedService.getInstance().userUpdated(oldUser, newUser);
//            }
//
//            LOGGER.error("Error on connecting to server for user: " + user.getAccount(), t);
//        }
//    }

    private void closeSplashScreen() {
        SplashScreen splashScreen = getSplashScreen();

        if (splashScreen != null && splashScreen.isOpen()) {
            SwingUtilities.invokeLater(() -> getSplashScreen().close());
        }
    }

    private void loginUserOrGetQRCode() {
        try {
            userService.loginUserOrGetQRCode();
        } catch (Exception e) {
            LOGGER.error("Failed to login or get QR code.", e);

            // prompt to try again.

            Component parentComponent = null;

            if (getSplashScreen() != null && getSplashScreen().getWindow() != null && getSplashScreen().getWindow().isVisible()) {
                parentComponent = getSplashScreen().getWindow();
            }

            String errorMessage = ClopuccinoMessages.getMessage("error.connect.to.server.try.again");

            int option = Utility.showConfirmDialog(parentComponent, errorMessage, ClopuccinoMessages.getMessage("error"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, Utility.createImageIcon("failure.png", ""));

            if (option == JOptionPane.YES_OPTION) {
                loginUserOrGetQRCode();
            }
        } finally {
            closeSplashScreen();
        }
    }

    private void prepareVersion(String currentVersion, String latestVersion, String downloadUrl) {
        String textForMenuItem;
        ImageIcon icon;
        String textForTrayIcon;

        if (latestVersion == null
            || latestVersion.trim().length() < 1
            || downloadUrl == null
            || downloadUrl.trim().length() < 1
            || new Version(currentVersion).compareTo(new Version(latestVersion)) >= 0) {
            textForMenuItem = Utility.localizedString("menu.download.latest.version");
            icon = null;
            textForTrayIcon = null;
        } else {
            textForMenuItem = Utility.localizedString("menu.download.latest.version2", latestVersion);
            icon = Utility.createImageIcon("download-from-cloud.png", "");
            textForTrayIcon = Utility.localizedString("menu.download.latest.version3", latestVersion);
        }

        changeMenuItemLabelOfShowDownloadPage(textForMenuItem, icon, textForTrayIcon);
    }

    private void changeMenuItemLabelOfShowDownloadPage(String textForMenuItem, ImageIcon icon, String textForTrayIcon) {
        if (getPopupMenu() != null) {
            MenuItem downloadMenuItem = getPopupMenu().getItem(MenuUtility.INDEX_OF_SHOW_DOWNLOAD_PAGE_MENU_ITME_OF_AWT_MENU);

            SwingUtilities.invokeLater(() -> {
                // Change the text in menu item, either new version found or not
                downloadMenuItem.setLabel(textForMenuItem);

                // Show message only when new message found, only works for Windows.
                // The message hides in Mac, so we don't provide here for now.
                if (OSUtility.isWindows() && textForTrayIcon != null) {
                    getTrayIcon().displayMessage("", textForTrayIcon, TrayIcon.MessageType.INFO);
                }
            });
        }
    }

//    private void changeMenuItemLabelOfShowDownloadPage(String textForMenuItem, ImageIcon icon, String textForTrayIcon) {
//        if (getPopupMenu() != null) {
//            JMenuItem downloadMenuItem = (JMenuItem) getPopupMenu().getComponent(MenuUtility.INDEX_OF_SHOW_DOWNLOAD_PAGE_MENU_ITME_OF_AWT_MENU);
//
//            SwingUtilities.invokeLater(() -> {
//                // Change the text in menu item, either new version found or not
//                downloadMenuItem.setText(textForMenuItem);
//                downloadMenuItem.setIcon(icon);
//
//                getPopupMenu().revalidate();
//                getPopupMenu().repaint();
//
//                // Show message only when new message found, only works for Windows.
//                // The message hides in Mac, so we don't provide here for now.
//                if (OSUtility.isWindows() && textForTrayIcon != null) {
//                    getTrayIcon().displayMessage("", textForTrayIcon, TrayIcon.MessageType.INFO);
//                }
//            });
//        }
//    }
}
