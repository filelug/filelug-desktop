package com.filelug.desktop.view;

import com.filelug.desktop.OSUtility;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * <code>PopupMenuLocationDetector</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class PopupMenuLocationDetector {

    private JPopupMenu popupMenu;

    private Point originalPoint;

    public PopupMenuLocationDetector(JPopupMenu popupMenu, Point mousePoint) {
        this.popupMenu = popupMenu;
        originalPoint = mousePoint;
    }

    public Point prepareLocation() {
        Rectangle bounds = getSafeScreenBounds();

        int x = originalPoint.x;
        int y = originalPoint.y;
        
        if (OSUtility.isWindows()) {
            if (x < bounds.x) {
                x = bounds.x;
            } else if (x > bounds.x + bounds.width) {
                x = bounds.x + bounds.width;
            }

            if (x + popupMenu.getPreferredSize().width > bounds.x + bounds.width) {
                x = (bounds.x + bounds.width) - popupMenu.getPreferredSize().width;
            }
            if (y < bounds.y) {
                y = bounds.y;
            } else if (y > bounds.y + bounds.height) {
                y = bounds.y + bounds.height;
            }

            if (y + popupMenu.getPreferredSize().height > bounds.y + bounds.height) {
                y = (bounds.y + bounds.height) - popupMenu.getPreferredSize().height;
            }
        }

        return new Point(x, y);
    }

//    public Point prepareLocation() {
//        Rectangle bounds = getSafeScreenBounds();
//
//        int x = originalPoint.x;
//        int y = originalPoint.y;
//
//        if (y < bounds.y) {
//            y = bounds.y;
//        } else if (y > bounds.y + bounds.height) {
//            y = bounds.y + bounds.height;
//        }
//
//        if (x < bounds.x) {
//            x = bounds.x;
//        } else if (x > bounds.x + bounds.width) {
//            x = bounds.x + bounds.width;
//        }
//
//        if (x + popupMenu.getPreferredSize().width > bounds.x + bounds.width) {
//            x = (bounds.x + bounds.width) - popupMenu.getPreferredSize().width;
//        }
//
//        if (y + popupMenu.getPreferredSize().height > bounds.y + bounds.height) {
//            y = (bounds.y + bounds.height) - popupMenu.getPreferredSize().height;
//        }
//
//        return new Point(x, y);
//    }

    private Rectangle getSafeScreenBounds() {

        Rectangle bounds = getScreenBoundsAt(originalPoint);
        Insets insets = getScreenInsetsAt(originalPoint);

        bounds.x += insets.left;
        bounds.y += insets.top;
        bounds.width -= (insets.left + insets.right);
        bounds.height -= (insets.top + insets.bottom);

        return bounds;
    }

    private Insets getScreenInsetsAt(Point pos) {
        GraphicsDevice gd = getGraphicsDeviceAt(pos);
        Insets insets = null;
        if (gd != null) {
            insets = Toolkit.getDefaultToolkit().getScreenInsets(gd.getDefaultConfiguration());
        }
        return insets;
    }

    private Rectangle getScreenBoundsAt(Point pos) {
        GraphicsDevice gd = getGraphicsDeviceAt(pos);
        Rectangle bounds = null;
        if (gd != null) {
            bounds = gd.getDefaultConfiguration().getBounds();
        }
        return bounds;
    }

    private GraphicsDevice getGraphicsDeviceAt(Point pos) {

        GraphicsDevice device = null;

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice lstGDs[] = ge.getScreenDevices();

        ArrayList<GraphicsDevice> lstDevices = new ArrayList<GraphicsDevice>(lstGDs.length);

        for (GraphicsDevice gd : lstGDs) {

            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            Rectangle screenBounds = gc.getBounds();

            if (screenBounds.contains(pos)) {
                lstDevices.add(gd);
            }
        }

        if (lstDevices.size() > 0) {
            device = lstDevices.get(0);
        } else {
            device = ge.getDefaultScreenDevice();
        }

        return device;
    }
}
