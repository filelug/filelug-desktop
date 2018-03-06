package com.filelug.desktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class PopupTester extends JFrame {



    private static class MessagePopup extends Popup implements WindowFocusListener {
        private final JWindow window;

//        private final JDialog dialog;

        public MessagePopup() {
            super();

            GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

            window = new JWindow(graphicsConfiguration);

            JPopupMenu popupMenu = new JPopupMenu("Tasks");
            popupMenu.add(new JMenuItem("First Menu Item"));
            popupMenu.add(new JMenuItem("Second Menu Item"));
            popupMenu.addSeparator();
            popupMenu.add(new JMenuItem("Third Menu Item"));
            popupMenu.addSeparator();
            popupMenu.add(new JMenuItem(new AbstractAction("Exit") {
                private static final long serialVersionUID = 4182135873507242272L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            }));

            popupMenu.setInvoker(window);

            window.setOpacity(1.0f);
            window.setContentPane(popupMenu);
            window.pack();

//            dialog = new JOptionPane().createDialog(base, "Message");
//            dialog.setModal(false);
//            dialog.setContentPane(new JLabel(message));
        }

        public void showAt(Point location) {
            window.addWindowFocusListener(this);
            window.setLocation(location);

            window.setVisible(true);
        }

        @Override
        public void show() {
            Point mouseLocation = MouseInfo.getPointerInfo().getLocation();

            showAt(new Point(mouseLocation));

//            dialog.addWindowFocusListener(this);
//            dialog.setVisible(true);
        }

        @Override
        public void hide() {
            window.setVisible(false);
            window.removeWindowFocusListener(this);
//            dialog.setVisible(false);
//            dialog.removeWindowFocusListener(this);
        }

        public void windowGainedFocus(WindowEvent e) {
            // NO-OP
        }

        public void windowLostFocus(WindowEvent e) {
            hide();
        }
    }

    public static void main(String[] args) {
        final PopupTester popupTester = new PopupTester();
        popupTester.setLayout(new FlowLayout());
        popupTester.setSize(300, 100);
        popupTester.add(new JButton("Click Me") {
            @Override
            protected void fireActionPerformed(ActionEvent event) {
//                Point location = getLocationOnScreen();
                MessagePopup popup = new MessagePopup();
                popup.showAt(popupTester.getLocation());
            }
        });
        popupTester.add(new JButton("No Click Me"));
        popupTester.setVisible(true);
        popupTester.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
