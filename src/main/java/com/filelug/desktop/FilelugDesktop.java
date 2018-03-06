package com.filelug.desktop;

/**
 * <code>FilelugDesktop</code>
 *
 * @author masonhsieh
 * @version 1.0
 */

import ch.qos.logback.classic.Logger;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.hyperlink.HyperlinkAction;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class FilelugDesktop {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("DESKTOP");

    private static final String HYPERLINK_BUTTON_ACTION_LISTENER_KEY_BROWSE = "action_browse";


    // Got to the web page using the default browser of the OS.
    // the urlString is like: http://www.filelug.com
    public static void navigateTo(String urlString) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(URI.create(urlString));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            FilelugDesktop.browse(URI.create(urlString));
        }
    }

    public static AbstractButton createNavigationButton(String text, URI uri) {
        if (OSUtility.isOSX() || OSUtility.isWindows()) {
            HyperlinkAction action = HyperlinkAction.createHyperlinkAction(uri, Desktop.Action.BROWSE);
            action.putValue(Action.NAME, text);

            JXHyperlink hyperlink = new JXHyperlink(action);

            return updateHyperlinkButton(hyperlink, text, uri);
        } else {
            return createHyperlinkButton(text, uri);
        }
    }

    // uri may be null
    private static AbstractButton createHyperlinkButton(final String text, final URI uri) {
        JButton hyperlink = new JButton();

        hyperlink.setText(text);
        hyperlink.setHorizontalAlignment(SwingConstants.CENTER);
        hyperlink.setBorderPainted(false);
        hyperlink.setOpaque(false);

        hyperlink.setForeground(uri != null ? Color.BLUE : Color.BLACK);
        hyperlink.setBackground(Color.WHITE);

        hyperlink.setCursor(uri != null ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

        if (uri != null) {
            hyperlink.setToolTipText(uri.toString());

            hyperlink.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    FilelugDesktop.browse(uri);
                }

                @Override
                public String toString() {
                    return HYPERLINK_BUTTON_ACTION_LISTENER_KEY_BROWSE;
                }
            });
        }

        return hyperlink;
    }

    public static AbstractButton updateHyperlinkButton(AbstractButton hyperlink, final String text, final URI uri) {
        AbstractButton button = null;

        if (hyperlink != null) {
            if (OSUtility.isOSX() || OSUtility.isWindows()) {
                button = updateNavigationButton((JXHyperlink) hyperlink, text, uri);
            } else {
                hyperlink.setText(text);

                hyperlink.setForeground(uri != null ? Color.BLUE : Color.BLACK);
                hyperlink.setCursor(uri != null ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

                ActionListener[] actionListeners = hyperlink.getActionListeners();
                if (actionListeners != null && actionListeners.length > 0) {
                    for (ActionListener listener : actionListeners) {
                        if (listener.toString().equals(HYPERLINK_BUTTON_ACTION_LISTENER_KEY_BROWSE)) {
                            hyperlink.removeActionListener(listener);
                        }
                    }
                }

                if (uri != null) {
                    hyperlink.setToolTipText(uri.toString());

                    hyperlink.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            FilelugDesktop.browse(uri);
                        }

                        @Override
                        public String toString() {
                            return HYPERLINK_BUTTON_ACTION_LISTENER_KEY_BROWSE;
                        }
                    });
                }
            }

            button = hyperlink;
        }

        return button;
    }

    private static AbstractButton updateNavigationButton(JXHyperlink hyperlink, String text, URI uri) {
        if (hyperlink != null) {
            hyperlink.setContentAreaFilled(false);
            hyperlink.setBorder(BorderFactory.createEmptyBorder());
            hyperlink.setRolloverEnabled(uri != null);
            hyperlink.setFocusable(true);

            HyperlinkAction action = HyperlinkAction.createHyperlinkAction(uri, Desktop.Action.BROWSE);
            action.putValue(Action.NAME, text);

            hyperlink.setAction(action);
        }

        return hyperlink;
    }


    public static boolean browse(URI uri) {
        boolean result;

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);

                result = true;
            } else {
                result = openSystemSpecific(uri.toString());
            }
        } catch (Exception e) {
            result = false;

            LOGGER.error("Failed to browse: " + uri, e);
        }

        return result;
    }

    private static boolean openSystemSpecific(String what) {
        if (OSUtility.isLinux()) {
            if (runCommand("kde-open", "%s", what))
                return true;
            if (runCommand("gnome-open", "%s", what))
                return true;
            if (runCommand("xdg-open", "%s", what))
                return true;
        }

        if (OSUtility.isOSX()) {
            if (runCommand("open", "%s", what))
                return true;
        }

        // This will only open Windows Explorer, not open IE.
        if (OSUtility.isWindows()) {
            if (runCommand("explorer", "%s", what))
                return true;
        }

        return false;
    }

    private static boolean runCommand(String command, String args, String file) {
        String[] parts = prepareCommand(command, args, file);

        try {
            Process p = Runtime.getRuntime().exec(parts);
            if (p == null)
                return false;

            try {
                int retval = p.exitValue();
                if (retval == 0) {
                    return false;
                } else {
                    return false;
                }
            } catch (IllegalThreadStateException itse) {
                String message = "Command='" + command + "', args='" + args + "', file='" + file + "'";
                LOGGER.error("Error on running command: " + message, itse);

                return true;
            }
        } catch (IOException e) {
            String message = "Command='" + command + "', args='" + args + "', file='" + file + "'";
            LOGGER.error("Error on running command: " + message, e);

            return false;
        }
    }


    private static String[] prepareCommand(String command, String args, String file) {
        List<String> parts = new ArrayList<String>();
        parts.add(command);

        if (args != null) {
            for (String s : args.split(" ")) {
                s = String.format(s, file); // put in the filename thing

                parts.add(s.trim());
            }
        }

        return parts.toArray(new String[parts.size()]);
    }
}
