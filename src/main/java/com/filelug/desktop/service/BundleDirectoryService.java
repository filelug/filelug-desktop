package com.filelug.desktop.service;

import com.filelug.desktop.Utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>BundleDirectory</code> is a directory can be read/write by a specified application,
 * such as iWork applications (Pages, Numbers, and Keynote), and OmniGraffile.
 *
 * @author masonhsieh
 * @version 1.0
 */
public class BundleDirectoryService {

    private static List<String> bundleDirectoryEndWiths;


    static {
        if (bundleDirectoryEndWiths == null) {
            bundleDirectoryEndWiths = new ArrayList<>();

            bundleDirectoryEndWiths.add(".pages");
            bundleDirectoryEndWiths.add(".numbers");
            bundleDirectoryEndWiths.add(".key");
            bundleDirectoryEndWiths.add(".graffle");
        }
    }


    public static boolean isBundleDirectory(File directory) {
        boolean passedAllTests = false;

        if (directory != null && directory.exists() && directory.isDirectory()) {
            String directoryNameWithLowerCase = directory.getName().toLowerCase();

            for (String legalExtension : bundleDirectoryEndWiths) {
                if (directoryNameWithLowerCase.endsWith(legalExtension)) {
                    passedAllTests = true;

                    break;
                }
            }
        }

        return passedAllTests;
    }

    /**
     * Zip the specified bundle directory to a zip file at the os-specified temp directory.
     *
     * @param bundleDirectory The bundle directory
     * @param checkIfABundleDirectoryFirst yes if you want to check if the bundle directory is a real one; otherwise set to false.
     *
     * @return The created zip file.
     *
     * @throws IllegalBundleDirectoryException if checkIfABundleDirectoryFirst is true and the bundleDirectory is not a real bundle directory.
     * @throws IOException if error occurred on zipping directory.
     */
    public static File createZipFileFromBundleDirectory(File bundleDirectory, boolean checkIfABundleDirectoryFirst) throws IOException {
        if (checkIfABundleDirectoryFirst && !isBundleDirectory(bundleDirectory)) {
            throw new IllegalBundleDirectoryException(bundleDirectory != null ? bundleDirectory.getAbsolutePath() : "Null bundle directory");
        }

        String baseFilename = bundleDirectory.getName();
        String extension = "zip";

        File createdZipFile = File.createTempFile(baseFilename, "." + extension);

        Utility.zipDirectory(bundleDirectory.toPath(), createdZipFile.toPath());

        return createdZipFile;
    }

//    /**
//     * Zip the specified bundle directory to a zip file.
//     * The name of the zip file is the name of the bundle directory and plus ".zip".
//     *
//     * @param bundleDirectory The bundle directory
//     * @param checkIfABundleDirectoryFirst yes if you want to check if the bundle directory is a real one; otherwise set to false.
//     *
//     * @return The created zip file, already checking to make sure the file not exists at the time of creating.
//     *
//     * @throws IllegalBundleDirectoryException if checkIfABundleDirectoryFirst is true and the bundleDirectory is not a real bundle directory.
//     * @throws IOException if error occurred on zipping directory.
//     */
//    public static File createZipFileFromBundleDirectory(File bundleDirectory, boolean checkIfABundleDirectoryFirst) throws IOException {
//        if (checkIfABundleDirectoryFirst && !isBundleDirectory(bundleDirectory)) {
//            throw new IllegalBundleDirectoryException(bundleDirectory != null ? bundleDirectory.getAbsolutePath() : "Null bundle directory");
//        }
//
//        String parent = bundleDirectory.getParent();
//        String baseFilename = bundleDirectory.getName();
//        String extension = "zip";
//
//        File createdZipFile = new File(parent, baseFilename + "." + extension);
//
//        // To prevent overwriting the existing file
//        if (createdZipFile.exists()) {
//            for (int suffix = 2; createdZipFile.exists(); suffix++) {
//                String newBaseFilename = baseFilename + "_" + suffix;
//
//                createdZipFile = new File(parent, newBaseFilename + "." + extension);
//            }
//        }
//
//        Utility.zipDirectory(bundleDirectory.toPath(), createdZipFile.toPath());
//
//        return createdZipFile;
//    }

}
