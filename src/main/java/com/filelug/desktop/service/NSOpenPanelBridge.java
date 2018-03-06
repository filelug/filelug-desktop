package com.filelug.desktop.service;

import ca.weblite.objc.NSObject;
import ca.weblite.objc.Proxy;
import ca.weblite.objc.annotations.Msg;

import static ca.weblite.objc.RuntimeUtils.sel;

/**
 * @author shannah
 */
public class NSOpenPanelBridge extends NSObject {

    private String selectedFilePath;

    public NSOpenPanelBridge() {
        super();
        init("NSObject");
    }

//    @Msg(selector = "panelSelectionDidChange:", signature = "v@:@")
//    public void panelSelectionDidChange(Proxy sender) {
//        System.out.println("---------In panel selection did change---------");
//    }


    @Msg(selector = "start", signature = "v@:")
    public void start() {
        Proxy openPanel = getClient().sendProxy("NSOpenPanel", "openPanel");
//        openPanel.send("setDelegate:", this);

        // can only select directories
        openPanel.send("setCanChooseDirectories:", true);
        openPanel.send("setCanChooseFiles:", false);

        // resolve alias
        openPanel.send("setResolvesAliases:", true);

        // multiple selection not allowed
        openPanel.send("setAllowsMultipleSelection:", false);

        int result = openPanel.sendInt("runModal");
        if ( result == 1 ) {
            // File was selected
            // Use the -[URLs] message on NSOpen panel to get an NSArray
            // of the selected files
            Proxy selectedUrls = openPanel.sendProxy("URLs");

            Proxy selectedUrl = selectedUrls.sendProxy("firstObject");

            if (selectedUrl != null) {
                selectedFilePath = selectedUrl.sendString("path");
            }
        }

    }

    public String chooseFile() {
        this.send("performSelectorOnMainThread:withObject:waitUntilDone:", sel("start"), this, true);

        return selectedFilePath;
    }

//    public static void main(String[] args) {
//        NSOpenPanelBridge sample = new NSOpenPanelBridge();
//
//        System.out.println("Selected file: " + sample.chooseFile());
//
////        // Any interaction with the GUI must happen on the main thread
////        // for cocoa, so we'll use NSObject's performSelectorOnMainThread:
////        // message to
////        sample.send("performSelectorOnMainThread:withObject:waitUntilDone:", sel("start"), sample, true);
//    }


}
