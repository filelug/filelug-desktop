package com.filelug.desktop.view;

import com.filelug.desktop.PropertyConstants;
import com.filelug.desktop.Utility;
import com.filelug.desktop.db.DatabaseAccess;
import com.filelug.desktop.db.HyperSQLDatabaseAccess;
import com.filelug.desktop.model.*;
import com.filelug.desktop.service.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * <code>QRCodeWindow</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class QRCodeWindow implements ConfigurationListener, Observer {

    private static final int BORDER_LEFT = 20;
    private static final int BORDER_RIGHT = 20;

    private static final int BORDER_TOP = 44;
    private static final int BORDER_BOTTOM = 44;
    private static final int BORDER_MIDDLE = 22;

    private static final int COMPONENT_GAP = 44;

    private static final double DESCRIPTION_PANEL_BORDER_TIMES = 5;

    private JFrame window;

    private JButton closeWindowButton;

    private final UserService userService;

    public QRCodeWindow(String qrcode, Dimension windowSize) throws Exception {

        // initial services

        DatabaseAccess dbAccess = new HyperSQLDatabaseAccess();

        this.userService = new DefaultUserService(dbAccess);

        // Initial UI

        Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // Now with zxing version 3.2.1 you could change border size (white border size to just 1)
        hintMap.put(EncodeHintType.MARGIN, 4); /* default = 4 */
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        int windowMinSize = Math.min(windowSize.width, windowSize.height);

        int barcodeSize = (int) (windowMinSize * 0.8);

        BitMatrix bitMatrix = qrCodeWriter.encode(qrcode, BarcodeFormat.QR_CODE, barcodeSize, barcodeSize, hintMap);

        int imageWidth = bitMatrix.getWidth();

        BufferedImage image = new BufferedImage(imageWidth, imageWidth, BufferedImage.TYPE_INT_RGB);

        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, imageWidth, imageWidth);
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageWidth; j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        window = new JFrame(Utility.localizedString("title.add.new.computer"));

        Container contentPane = window.getContentPane();

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

        // QR code

        JPanel qrCodePanel = new JPanel(new BorderLayout());

        Border qrCodePanelBorder = BorderFactory.createEmptyBorder(BORDER_TOP, BORDER_LEFT, BORDER_MIDDLE, BORDER_RIGHT);
        qrCodePanel.setBorder(qrCodePanelBorder);

        // DEBUG: Removed on production
//        qrCodePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED, 2), qrCodePanelBorder));

        final JLabel qrCodeLabel = new JLabel(new ImageIcon(image));

        qrCodePanel.add(qrCodeLabel, BorderLayout.CENTER);

        qrCodePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(qrCodePanel);

        // description

        JPanel descriptionPanel = new JPanel(new BorderLayout(COMPONENT_GAP, COMPONENT_GAP));

        Border descriptionPanelBorder = BorderFactory.createEmptyBorder(BORDER_MIDDLE, (int) (BORDER_LEFT * DESCRIPTION_PANEL_BORDER_TIMES), BORDER_MIDDLE, (int) (BORDER_RIGHT * DESCRIPTION_PANEL_BORDER_TIMES));
        descriptionPanel.setBorder(descriptionPanelBorder);
        
        // DEBUG: Removed on production
//        descriptionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE, 2), descriptionPanelBorder));

        JTextArea textArea = new JTextArea(Utility.localizedString("scan.with.filelug.app.to.login.and.connect"));

        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setOpaque(false);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setBackground(UIManager.getColor("Label.backgroud"));
        textArea.setFont(UIManager.getFont("Label.font"));

        // DEBUG: Remove on production
//        textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        descriptionPanel.add(textArea, BorderLayout.CENTER);

        descriptionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPane.add(descriptionPanel);

        // button to exit

        JButton exitButton = new JButton(Utility.localizedString("menu.exit"), Utility.createImageIcon("cancel.png", ""));

        exitButton.addActionListener(MenuUtility.createExitActionListener());

        // button to close QR code window

        closeWindowButton = new JButton(Utility.localizedString("button.close.qrcode"), Utility.createImageIcon("close-7.png", ""));

        closeWindowButton.addActionListener(createCloseQRCodeActionListener());

        closeWindowButton.setEnabled(userService.hasAdministrator());

        // button to refresh QR code

        JButton refreshQRCodeButton = new JButton(Utility.localizedString("button.refresh.qrcode"), Utility.createImageIcon("refresh.png", ""));

        refreshQRCodeButton.addActionListener(createRefreshQRCodeActionListener());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        Border buttonPanelBorder = BorderFactory.createEmptyBorder(BORDER_MIDDLE, BORDER_LEFT, BORDER_BOTTOM, BORDER_RIGHT);
        buttonPanel.setBorder(buttonPanelBorder);

        // DEBUG: Removed on production
//        buttonPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GREEN, 2), buttonPanelBorder));

        buttonPanel.add(refreshQRCodeButton);
        buttonPanel.add(closeWindowButton);
        buttonPanel.add(exitButton);

        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(buttonPanel);

        Dimension sizeWithBorder = new Dimension(windowSize.width + BORDER_LEFT + BORDER_RIGHT, windowSize.height + BORDER_TOP + BORDER_BOTTOM + (3 * BORDER_MIDDLE) + COMPONENT_GAP);

        window.setPreferredSize(sizeWithBorder);
        window.setMinimumSize(sizeWithBorder);
        window.setSize(sizeWithBorder);

        window.setAlwaysOnTop(true);
        window.setUndecorated(true);
        window.getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        // listeners

        UserChangedService.getInstance().addObserver(this);
    }

    private ActionListener createCloseQRCodeActionListener() {
        return e -> {
            PopupMenuItemClickNotificationService.getInstance().menuItemClicked(e);

            close();
        };
    }

    private ActionListener createRefreshQRCodeActionListener() {
        return e -> {
            PopupMenuItemClickNotificationService.getInstance().menuItemClicked(e);

            close();
            ResetApplicationNotificationService.getInstance().applicationShouldReset(ResetApplicationNotificationService.REASON.REFRESH_QR_CODE);
        };
    }

    public JFrame getWindow() {
        return window;
    }

    public void show() {
        if (window != null) {
            Utility.addPreferenceChangeListener(this);

            SwingUtilities.invokeLater(() -> {
                window.setLocationRelativeTo(null);
                window.setVisible(true);
            });
        }
    }

    public void close() {
        if (window != null) {
            SwingUtilities.invokeLater(() -> {
                window.setVisible(false);
                window.dispose();
            });

            Utility.removePreferenceChangeListener(this);
        }
    }

    @Override
    public void configurationChanged(ConfigurationEvent event) {
        int type = event.getType();

        // There're twice clear-property-event fired before AND after the property is cleared.
        boolean beforeUpdate = event.isBeforeUpdate();

        if (type == AbstractConfiguration.EVENT_CLEAR_PROPERTY && beforeUpdate) {
            String propertyName = event.getPropertyName();

            if (propertyName != null) {
                if (propertyName.equals(PropertyConstants.PROPERTY_NAME_QR_CODE)) {
                    // qr-code removes whenever the result of connecting to server is successful or failed

                    close();
                }
            }
        }
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (UserChangedService.class.isInstance(observable)) {
            if (arg != null && UserChangedEvent.class.isInstance(arg)) {
                UserChangedEvent userChanged = (UserChangedEvent) arg;

                UserChangedEvent.ChangeType type = userChanged.getChangeType();

                if (type.equals(UserChangedEvent.ChangeType.USER_ADDED)) {
                    // enabled close-window button

                    closeWindowButton.setEnabled(true);
                } else if (type.equals(UserChangedEvent.ChangeType.USER_REMOVED)) {
                    // disabled close-window button if no admin exists
                    
                    closeWindowButton.setEnabled(userService.hasAdministrator());
                }
            }
        }
    }
}
