package com.filelug.desktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PopupFactoryDemo {
    private static boolean shouldHidePopup;

    private static Popup popup;

    public static void main(String[] args) {

        try {
            // System Tray and its icon

            SystemTray tray = SystemTray.getSystemTray();

            String tooltip = Utility.localizedString("app.title");

            Image icon = Utility.createTrayIcon();

            final TrayIcon trayIcon = new TrayIcon(icon, tooltip);

            tray.add(trayIcon);

            // component to popup

            GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

//            JWindow window = new JWindow(graphicsConfiguration);

            JPanel panel = new JPanel();
            panel.setBackground(Color.lightGray);

            BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);

            panel.setLayout(boxLayout);

            panel.add(new JMenuItem("Task"));
            panel.add(new JMenuItem("First Menu Item"));
            panel.add(new JMenuItem("Second Menu Item"));

            JSeparator seperator = new JSeparator(SwingConstants.HORIZONTAL);
            seperator.setMaximumSize( new Dimension(Integer.MAX_VALUE, 1) );
            panel.add(seperator);

            panel.add(new JMenuItem("Third Menu Item"));

            JSeparator seperator2 = new JSeparator(SwingConstants.HORIZONTAL);
            seperator2.setMaximumSize( new Dimension(Integer.MAX_VALUE, 1) );
            panel.add(seperator2);

            panel.add(new JMenuItem(new AbstractAction("Exit") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            }));



//            // popup menu
//
//            JPopupMenu popupMenu = new JPopupMenu("Tasks");
//            popupMenu.add(new JMenuItem("First Menu Item"));
//            popupMenu.add(new JMenuItem("Second Menu Item"));
//            popupMenu.addSeparator();
//            popupMenu.add(new JMenuItem("Third Menu Item"));
//            popupMenu.addSeparator();
//            popupMenu.add(new JMenuItem(new AbstractAction("Exit") {
//                private static final long serialVersionUID = 4182135873507242272L;
//
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    System.exit(0);
//                }
//            }));
//
//            popupMenu.setInvoker(popupMenu);

            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    SwingUtilities.invokeLater(() -> {
                        if (shouldHidePopup) {
                            popup.hide();
                        } else {
                            Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
                            Popup newPopup = PopupFactory.getSharedInstance().getPopup(null, panel, (int) mouseLocation.getX(), (int) mouseLocation.getY());

                            popup = newPopup;

                            newPopup.show();
                        }

                        shouldHidePopup = !shouldHidePopup;
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public static void main(String[] args) {
//        final PopupTester popupTester = new PopupTester();
//        popupTester.setLayout(new FlowLayout());
//        popupTester.setSize(300, 100);
//        popupTester.add(new JButton("Click Me") {
//            @Override
//            protected void fireActionPerformed(ActionEvent event) {
////                Point location = getLocationOnScreen();
//                MessagePopup popup = new MessagePopup();
//                popup.showAt(popupTester.getLocation());
//            }
//        });
//        popupTester.add(new JButton("No Click Me"));
//        popupTester.setVisible(true);
//        popupTester.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//    }
}
