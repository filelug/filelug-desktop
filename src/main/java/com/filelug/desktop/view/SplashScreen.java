package com.filelug.desktop.view;

import com.filelug.desktop.Constants;
import com.filelug.desktop.Utility;

import javax.swing.*;
import java.awt.*;

/**
 * <code>SplashScreen</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class SplashScreen {

    private JFrame window;

    private JLabel descriptionLabel;

    public SplashScreen() {
        window = new JFrame();

        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        window.setContentPane(contentPane);

        JLabel logoLabel = new JLabel(Utility.createImageIconByBundleKey("wizard-logo.png", ""));

        descriptionLabel = new JLabel();
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
        setDescription(Utility.localizedString("text.splash.default.description"));

        GridBagLayout layout = new GridBagLayout();
        contentPane.setLayout(layout);

        /* row 1 */
        Component glueBox = Box.createGlue();
        layout.setConstraints(glueBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 0));
        contentPane.add(glueBox);

        /* row 2 */
        layout.setConstraints(logoLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        contentPane.add(logoLabel);

        /* row 3 */
        Component glueBox2 = Box.createGlue();
        layout.setConstraints(glueBox2, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 0));
        contentPane.add(glueBox2);

        /* row 4 */
        layout.setConstraints(descriptionLabel, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 50, 5), 5, 0));
        contentPane.add(descriptionLabel);

        /* row 5 */
        Component glueBox3 = Box.createGlue();
        layout.setConstraints(glueBox3, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 5, 0));
        contentPane.add(glueBox3);

        Dimension frameSize = new Dimension(Constants.SPLASH_SCREEN_WINDOW_WIDTH, Constants.SPLASH_SCREEN_WINDOW_HEIGHT);
//        Dimension frameSize = new Dimension(560, 420);
        window.setPreferredSize(frameSize);
        window.setMinimumSize(frameSize);
        window.setSize(frameSize);

        // To prevent dialog shows up but hidden in the back of the splash screen.
//        window.setAlwaysOnTop(true);
        window.setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);
        window.setUndecorated(true);
        window.getRootPane().setWindowDecorationStyle(JRootPane.NONE);

    }

    public JFrame getWindow() {
        return window;
    }

    public void show() {
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public void setDescription(final String description) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                descriptionLabel.setText(description);
            }
//                descriptionLabel.setText("<html><H3>" + description + "</H3></html>");
//            }
        });
    }

    public boolean isOpen() {
        return window != null && window.isVisible();
    }

    public void close() {
        window.setVisible(false);
        window.dispose();
    }
}
