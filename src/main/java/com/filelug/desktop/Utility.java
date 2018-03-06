package com.filelug.desktop;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.filelug.desktop.model.DeviceToken;
import com.filelug.desktop.service.NSOpenPanelBridge;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.lang3.RandomStringUtils;
import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;
import org.glassfish.jersey.internal.l10n.Localizer;
import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.uri.UriTemplate;
import org.slf4j.LoggerFactory;

import javax.naming.OperationNotSupportedException;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * <code>Utility</code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class Utility {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger("UTILITY");

//    public static final boolean USE_HTTPS = Boolean.parseBoolean(System.getProperty("use.https", String.valueOf(true)));

    private static final ExecutorService executor = Executors.newFixedThreadPool(Constants.N_THREADS);

    private static final LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("Messages");

    private static final Localizer localizer = new Localizer();

    private static final Pattern unicodeCharactersPattern = Pattern.compile("\\w+[\\s?\\w+]*", Pattern.UNICODE_CHARACTER_CLASS);

    private static final ResourceBundle versionBundle = ResourceBundle.getBundle("Version");

    private static final String[] WHO_AM_I_SYSPROPS = {"java.runtime.name",
                                                       "java.vm.name",
                                                       "java.specification.version",
                                                       "java.vm.specification.version",
                                                       "java.runtime.version",
                                                       "java.vm.version",
                                                       "java.version",
                                                       "java.specification.vendor",
                                                       "java.vm.specification.vendor",
                                                       "java.vendor.url",
                                                       "java.vm.vendor",
                                                       "java.vendor",
                                                       "java.home",
                                                       "user.country.format",
                                                       "user.country",
                                                       "user.dir",
                                                       "user.language",
                                                       "user.region",
                                                       "user.timezone",
                                                       "user.name",
                                                       "user.home",
                                                       "os.arch",
                                                       "os.name",
                                                       "os.version",
                                                       "file.encoding"};

//    private static final String WINDOWS_TRAY_ICON_FILENAME = "filelug-tray-32-dark-gray.png";
////    private static final String WINDOWS_TRAY_ICON_FILENAME = "filelug-tray-16-dark-gray.png";
//
//    private static final String OSX_TRAY_ICON_FILENAME = "filelug-tray-30-dark-gray.png";
//
//    private static final String DEFAULT_TRAY_ICON_FILENAME = "filelug-tray-48-dark-gray.png";
//
//    public static final String USER_ACCOUNT_DELIMITERS = "-";
//
//    public static final String COMPUTER_DELIMITERS = "|";

    private static PropertiesConfiguration preferences;

//    private static Map<RenderingHints.Key, Object> hintsMap = null;

    private static String latestFileChooserDirectoy;

    private static Font[] availableFonts;

    public Utility() {
    }

    public static ExecutorService getExecutorService() {
        return executor;
    }

    public static String localizedString(String key, Object... args) {
        return localizer.localize(messageFactory.getMessage(key, args));
    }

    public static String realUrlEncode(final String value) {
        final String encodedValue;

        if (value != null) {
            String valueName = "_value_";

            String valueTemplate = "{" + valueName + "}";

            Map<String, String> templateMap = new HashMap<>();

            templateMap.put(valueName, value);

            encodedValue = UriTemplate.resolveTemplateValues(UriComponent.Type.PATH_SEGMENT, valueTemplate, true, templateMap);
        } else {
            encodedValue = null;
        }

        return encodedValue;
    }

    public static long convertDateToTimeMillis(String dateString, SimpleDateFormat dateFormat) throws Exception {
        return dateFormat.parse(dateString).getTime();
    }

    public static Image createTrayIcon() {
        Image image;

        String tooltip = Utility.localizedString("app.title");

        if (OSUtility.isWindows()) {
            image = createImage(Constants.WINDOWS_TRAY_ICON_FILENAME, tooltip);
        } else if (OSUtility.isOSX()) {
            image = createImage(Constants.OSX_TRAY_ICON_FILENAME, tooltip);
        } else {
            image = createImage(Constants.DEFAULT_TRAY_ICON_FILENAME, tooltip);
        }

        return image;
    }

    public static URL trayIconUrl() {
        String iconName;

        if (OSUtility.isWindows()) {
            iconName = Constants.WINDOWS_TRAY_ICON_FILENAME;
        } else if (OSUtility.isOSX()) {
            iconName = Constants.OSX_TRAY_ICON_FILENAME;
        } else {
            iconName = Constants.DEFAULT_TRAY_ICON_FILENAME;
        }

        return getFileResource(iconName);
    }

    public static List<Image> createWindowIconImages() {
        List<Image> imageList = new ArrayList<>();

        String tooltip = Utility.localizedString("app.title");

//        if (OSUtility.isWindows()) {
            imageList.add(createImage("Filelug_Icon_32Bits_16.png", tooltip));
            imageList.add(createImage("Filelug_Icon_32Bits_24.png", tooltip));
            imageList.add(createImage("Filelug_Icon_32Bits_32.png", tooltip));
            imageList.add(createImage("Filelug_Icon_32Bits_48.png", tooltip));
            imageList.add(createImage("Filelug_Icon_32Bits_60.png", tooltip));
            imageList.add(createImage("Filelug_Icon_32Bits_64.png", tooltip));
            imageList.add(createImage("Filelug_Icon_32Bits_96.png", tooltip));
            imageList.add(createImage("Filelug_Icon_32Bits_128.png", tooltip));
            imageList.add(createImage("Filelug_Icon_32Bits_256.png", tooltip));
            imageList.add(createImage("Filelug_Icon_32Bits_512.png", tooltip));
            imageList.add(createImage("Filelug_Icon_32Bits_768.png", tooltip));
//        } else if (OSUtility.isOSX()) {
//            imageList.add(createImage(Constants.OSX_TRAY_ICON_FILENAME, tooltip));
//        } else {
//            imageList.add(createImage(Constants.DEFAULT_TRAY_ICON_FILENAME, tooltip));
//        }

        return imageList;
    }

    /**
     * create image from path. Search path from user.dir to context class loader and finally class resource.
     *
     * @return null if image not found for the path
     */
    public static Image createImage(String path, String description) {
        ImageIcon imageIcon = createImageIcon(path, description);

        return imageIcon != null ? imageIcon.getImage() : null;
    }

    /**
     * create ImageIcon from path. Search path from user.dir to context class loader and finally class resource.
     *
     * @return null if image not found for the path
     */
    public static ImageIcon createImageIconByBundleKey(String resourceBundleKey, String description) {
        String path = localizedString(resourceBundleKey);

        URL url = null;

        if (path != null) {
            url = getFileResource(path);
        }

        return url != null ? new ImageIcon(url, description) : null;
    }

    /**
     * create ImageIcon from path. Search path from user.dir to context class loader and finally class resource.
     *
     * @return null if image not found for the path
     */
    public static ImageIcon createImageIcon(String path, String description) {
        URL url = getFileResource(path);

        return url != null ? new ImageIcon(url, description) : null;
    }

    public static URL getFileResource(String path) {
        URL url = null;

        File file = new File(System.getProperty("user.dir"), path);

        if (!file.exists()) {
            url = Thread.currentThread().getContextClassLoader().getResource(path);

            if (url == null) {
                url = Class.class.getResource(path);
            }
        } else {
            try {
                url = file.toURI().toURL();
            } catch (Exception e) {
                /* ignored */
            }
        }

        /* DEBUG */
//        LOGGER.info("Path: " + path + ", URL: " + url);

        return url;
    }

    /**
     * All the <code>ObjectMapper</>s should be created from here.
     */
    public static ObjectMapper createObjectMapper() {
        JsonFactory jsonFactory = new JsonFactory();
        jsonFactory.enable(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS);
        jsonFactory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);

        // Instead using this, invoke [NSString escapeIllegalJsonCharacter] in device to solve this.
//        jsonFactory.enable(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER);

        ObjectMapper mapper = new ObjectMapper(jsonFactory);

        // To ignore unknown properties when parsing from string to object
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }

    public static String generateVerificationToLogin(String computerGroup, String computerName, String locale) {
        return DigestUtils.sha256Hex(computerName + "|" + computerGroup + ":" + locale + "_" + computerGroup);
    }

    public static String generateVerificationToLoginApplyUser(String adminId, String applyUserId, long computerId) {
        return DigestUtils.sha256Hex(adminId + "|" + applyUserId + ":" + computerId + "_" + adminId);
    }

    /**
     * Generates encrypted user computer key from user id and computer id.
     */
    public static String generateEncryptedUserComputerIdFrom(String userId, Long computerId) {
        return DigestUtils.sha256Hex(Constants.ENCRYPTED_USER_COMPUTER_ID_PREFIX
                                     + (userId + Constants.COMPUTER_DELIMITERS + String.valueOf(computerId)).toLowerCase()
                                     + Constants.ENCRYPTED_USER_COMPUTER_ID_SUFFIX);
    }

    public static String generateVerificationForExchangeSession(String userId, String countryId, String phoneNumber) {
        return DigestUtils.sha256Hex(userId + "|" + countryId + ":" + phoneNumber) + DigestUtils.md5Hex(phoneNumber + "==" + countryId);
    }

    public static void listSystemProperties() {
        Properties properties = System.getProperties();
        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
        for (Map.Entry<Object, Object> entry : entries) {
            LOGGER.info(entry.getKey() + ":" + entry.getValue());
        }
    }

    public static boolean isUnicodeCharacters(String text) {
        return unicodeCharactersPattern.matcher(text).matches();
    }

    public static String stringFromLocale(Locale locale) {
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    public static String defaultNickname() {
        return RandomStringUtils.random(12, true, true);
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static String encodeUsingBase64(String rawString, String charset) throws UnsupportedEncodingException {
        return Base64.encodeBase64String(rawString.getBytes(charset));
    }

    public static String representationFileSizeFromBytes(final long value) {
        final long[] dividers = new long[]{Constants.T, Constants.G, Constants.M, Constants.K, 1};

        final String[] units = new String[]{"TB", "GB", "MB", "KB", "B"};

        if (value < 1) {
            return "";
        }

        String result = null;

        for (int i = 0; i < dividers.length; i++) {
            final long divider = dividers[i];
            if (value >= divider) {
                result = format(value, divider, units[i]);
                break;
            }
        }
        return result;
    }

    private static String format(final long value, final long divider, final String unit) {
        final double result = divider > 1 ? (double) value / (double) divider : (double) value;
        return String.format("%.2f %s", result, unit);
    }

    public static boolean isUnixSymlink(File file) throws IOException {
        File fileInCanonicalDir;

        if (file.getParent() == null) {
            fileInCanonicalDir = file;
        } else {
            File canonicalDir = file.getParentFile().getCanonicalFile();
            fileInCanonicalDir = new File(canonicalDir, file.getName());
        }

        return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile());
    }

    public static String generateDbPassword(String source) {
        return DigestUtils.md5Hex(DigestUtils.sha256Hex(source));
    }


    /**
     * 取得指定json中的sid。若找不到或者sid值不是整數，回傳null。
     */
    public static Integer findSidFromJson(String json) {
        Integer sid = null;

        try {
            ObjectMapper mapper = createObjectMapper();
            
            JsonNode jsonObject = mapper.readTree(json);

            JsonNode sidNode = jsonObject.findValue("sid");

            if (sidNode != null && sidNode.isNumber()) {
                sid = sidNode.intValue();
            }
        } catch (Exception e) {
            LOGGER.error(String.format("Failed to find sid from JSON:%n%s", json), e);
        }

        return sid;
    }

    // ---- Application preferences ----

//    /**
//     *
//     * @return false if computer id and recovery key exists; otherwise return true.
//     */
//    public static boolean checkIfNeedUpgradingFromV1() {
//        boolean needUpgrading = true;
//
//        File configurationFile = new File(OSUtility.getApplicationDataDirectoryFile(), Constants.PREFS_FILE_NAME);
//
//        if (configurationFile.exists()) {
//            long NEGATIVE_COMPUTER_ID = -1L;
//
//            long computerId = Utility.getPreferenceLong(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, NEGATIVE_COMPUTER_ID);
//
//            String recoveryKey = Utility.getPreference(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, "");
//
//            if (computerId != NEGATIVE_COMPUTER_ID && recoveryKey.trim().length() > 0) {
//                needUpgrading = false;
//            }
//        }
//
//        return needUpgrading;
//    }

    public static void configurePreferences(BeforeConfigurePreferences beforeConfigurePreferences) {
        if (preferences == null) {
            boolean needCopyValuesFromV1 = false;

            File configurationFileV1 = new File(OSUtility.getApplicationDataDirectoryFile_V1(), Constants.PREFS_FILE_NAME);

            File configurationFile = new File(OSUtility.getApplicationDataDirectoryFile(), Constants.PREFS_FILE_NAME);

            if (!configurationFile.exists()) {
                needCopyValuesFromV1 = configurationFileV1.exists() && configurationFileV1.isFile() && configurationFileV1.canRead();

                try {
                    configurationFile.createNewFile();
                } catch (Exception e) {
                    needCopyValuesFromV1 = false;

                    LOGGER.error("Error on creating preferences file: " + configurationFile.getAbsolutePath(), e);
                }
            }

            PropertiesConfiguration.setDefaultListDelimiter((char) 12); // Line Feed

            try {
                preferences = new PropertiesConfiguration(configurationFile);
                preferences.setAutoSave(true);
                preferences.setReloadingStrategy(new FileChangedReloadingStrategy());
            } catch (Exception e) {
                LOGGER.error("Error on configure preferences.", e);
            }

            beforeConfigurePreferences.actionBeforeConfigurePreferences(needCopyValuesFromV1);

            // Copy values from V1

            if (needCopyValuesFromV1) {
                PropertiesConfiguration preferencesV1 = null;

                try {
                    preferencesV1 = new PropertiesConfiguration(configurationFileV1);

                    // copy values if user not requested to reset in V1

                    String appResetValue = preferencesV1.getString(PropertyConstants.PROPERTY_NAME_V1_APP_RESET, "");

                    if (appResetValue.length() == 0 || appResetValue.trim().equals("0")) {
                        long NEGATIVE_COMPUTER_ID = -1L;

                        long computerIdV1 = preferencesV1.getLong(PropertyConstants.PROPERTY_NAME_V1_COMPUTER_ID, NEGATIVE_COMPUTER_ID);

                        if (computerIdV1 != NEGATIVE_COMPUTER_ID) {
                            String recoveryKeyV1 = preferencesV1.getString(PropertyConstants.PROPERTY_NAME_V1_RECOVERY_KEY, "");

                            if (recoveryKeyV1.trim().length() > 0) {
                                LOGGER.info("Start migrating from old version ...");

                                // copy computer id
                                Utility.putPreferenceLong(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, computerIdV1);

                                // copy recovery key
                                Utility.putPreference(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, recoveryKeyV1);

                                // copy computer group, if exists

                                String computerGroupV1 = preferencesV1.getString(PropertyConstants.PROPERTY_NAME_V1_COMPUTER_GROUP, "");

                                if (computerGroupV1.length() > 0) {
                                    Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, computerGroupV1);
                                }

                                // copy computer name, if exists

                                String computerNameV1 = preferencesV1.getString(PropertyConstants.PROPERTY_NAME_V1_COMPUTER_NAME, "");

                                if (computerNameV1.length() > 0) {
                                    Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, computerNameV1);
                                }

                                LOGGER.info("Successfuly migrated.");
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Error on migrating from old version:\n" + e.getMessage(), e);
                } finally {
                    // close preferences for old version

                    preferencesV1 = null;
                }
            }
        }
    }

    // Before Lambda
//    public static void configurePreferences() {
//        if (preferences == null) {
//            boolean needCopyValuesFromV1 = false;
//
//            File configurationFileV1 = new File(OSUtility.getApplicationDataDirectoryFile_V1(), Constants.PREFS_FILE_NAME);
//
//            File configurationFile = new File(OSUtility.getApplicationDataDirectoryFile(), Constants.PREFS_FILE_NAME);
//
//            if (!configurationFile.exists()) {
//                needCopyValuesFromV1 = configurationFileV1.exists() && configurationFileV1.isFile() && configurationFileV1.canRead();
//
//                try {
//                    configurationFile.createNewFile();
//                } catch (Exception e) {
//                    needCopyValuesFromV1 = false;
//
//                    LOGGER.error("Error on creating preferences file: " + configurationFile.getAbsolutePath(), e);
//                }
//            }
//
//            PropertiesConfiguration.setDefaultListDelimiter((char) 12); // Line Feed
//
//            try {
//                preferences = new PropertiesConfiguration(configurationFile);
//                preferences.setAutoSave(true);
//                preferences.setReloadingStrategy(new FileChangedReloadingStrategy());
//            } catch (Exception e) {
//                LOGGER.error("Error on configure preferences.", e);
//            }
//
//            // Copy values from V1
//
//            if (needCopyValuesFromV1) {
//                PropertiesConfiguration preferencesV1 = null;
//
//                try {
//                    preferencesV1 = new PropertiesConfiguration(configurationFileV1);
//
//                    // copy values if user not requested to reset in V1
//
//                    String appResetValue = preferencesV1.getString(PropertyConstants.PROPERTY_NAME_V1_APP_RESET, "");
//
//                    if (appResetValue.length() == 0 || appResetValue.trim().equals("0")) {
//                        long NEGATIVE_COMPUTER_ID = -1L;
//
//                        long computerIdV1 = preferencesV1.getLong(PropertyConstants.PROPERTY_NAME_V1_COMPUTER_ID, NEGATIVE_COMPUTER_ID);
//
//                        if (computerIdV1 != NEGATIVE_COMPUTER_ID) {
//                            String recoveryKeyV1 = preferencesV1.getString(PropertyConstants.PROPERTY_NAME_V1_RECOVERY_KEY, "");
//
//                            if (recoveryKeyV1.trim().length() > 0) {
//                                LOGGER.info("Start migrating from old version ...");
//
//                                // copy computer id
//                                Utility.putPreferenceLong(PropertyConstants.PROPERTY_NAME_COMPUTER_ID, computerIdV1);
//
//                                // copy recovery key
//                                Utility.putPreference(PropertyConstants.PROPERTY_NAME_RECOVERY_KEY, recoveryKeyV1);
//
//                                // copy computer group, if exists
//
//                                String computerGroupV1 = preferencesV1.getString(PropertyConstants.PROPERTY_NAME_V1_COMPUTER_GROUP, "");
//
//                                if (computerGroupV1.length() > 0) {
//                                    Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP, computerGroupV1);
//                                }
//
//                                // copy computer name, if exists
//
//                                String computerNameV1 = preferencesV1.getString(PropertyConstants.PROPERTY_NAME_V1_COMPUTER_NAME, "");
//
//                                if (computerNameV1.length() > 0) {
//                                    Utility.putPreference(PropertyConstants.PROPERTY_NAME_COMPUTER_NAME, computerNameV1);
//                                }
//
//                                LOGGER.info("Successfuly migrated.");
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    LOGGER.error("Error on migrating from old version:\n" + e.getMessage(), e);
//                } finally {
//                    // close preferences for old version
//
//                    preferencesV1 = null;
//                }
//            }
//        }
//    }

    public static String getPreference(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }

    public static void putPreference(String key, String value) {
        preferences.setProperty(key, value);
    }

    public static boolean getPreferenceBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public static void putPreferenceBoolean(String key, boolean value) {
        preferences.setProperty(key, value ? Boolean.TRUE : Boolean.FALSE);
    }

    public static double getPreferenceDouble(String key, double defaultValue) {
        return preferences.getDouble(key, defaultValue);
    }

    public static void putPreferenceDouble(String key, double value) {
        preferences.setProperty(key, value);
    }

    public static float getPreferenceFloat(String key, float defaultValue) {
        return preferences.getFloat(key, defaultValue);
    }

    public static void putPreferenceFloat(String key, float value) {
        preferences.setProperty(key, value);
    }

    public static long getPreferenceLong(String key, long defaultValue) {
        return preferences.getLong(key, defaultValue);
    }

    public static void putPreferenceLong(String key, long value) {
        preferences.setProperty(key, value);
    }

    public static int getPreferenceInt(String key, int defaultValue) {
        return preferences.getInt(key, defaultValue);
    }

    public static void putPreferenceInt(String key, int value) {
        preferences.setProperty(key, value);
    }

    public static void removePreference(String key) {
        preferences.clearProperty(key);
    }

    public static void clearPreferences() {
        preferences.clear();

        LOGGER.info("All preferences cleared.");
    }

    public static void removeNonComputerRelatedPreferences() {
        // computer-related preferences

        /*
            // computer id
            PropertyConstants.PROPERTY_NAME_COMPUTER_ID

            // computer name
            PropertyConstants.PROPERTY_NAME_COMPUTER_NAME

            // computer group
            PropertyConstants.PROPERTY_NAME_COMPUTER_GROUP

            // recovery key
            PropertyConstants.PROPERTY_NAME_RECOVERY_KEY

            // device token
            PropertyConstants.PROPERTY_NAME_DEVICE_TOKEN

        */

        // Non computer-related preferences

        /*
            // QR code
            PropertyConstants.PROPERTY_NAME_QR_CODE
        */

        removePreference(PropertyConstants.PROPERTY_NAME_QR_CODE);

        LOGGER.info("All preferences but computer-related cleared.");
    }

//    public static void removeV1Preferences() {
//        removePreference(PropertyConstants.PROPERTY_NAME_V1_COMPUTER_ID);
//        removePreference(PropertyConstants.PROPERTY_NAME_V1_COMPUTER_GROUP);
//        removePreference(PropertyConstants.PROPERTY_NAME_V1_COMPUTER_NAME);
//        removePreference(PropertyConstants.PROPERTY_NAME_V1_RECOVERY_KEY);
//        removePreference(PropertyConstants.PROPERTY_NAME_V1_APP_RESET);
//    }

    public static void addPreferenceChangeListener(ConfigurationListener listener) {
        Collection<ConfigurationListener> listeners = preferences.getConfigurationListeners();

        if (!listeners.contains(listener)) {
            preferences.addConfigurationListener(listener);
        }
    }

    public static void removePreferenceChangeListener(ConfigurationListener listener) {
        preferences.removeConfigurationListener(listener);
    }

    public static void unzip(File zipFile, File destDirectory) throws Exception {
        LOGGER.info("Start unzipping file: \"" + zipFile.getAbsolutePath() + "\" to \"" + destDirectory.getAbsolutePath() + "\"");

        ZipInputStream zis = null;
        FileOutputStream fos = null;

        try {
            zis = new ZipInputStream(new FileInputStream(zipFile));

            ZipEntry ze = zis.getNextEntry();

            for (int index = 1; ze != null; index++) {
                String entryName = ze.getName();

                File f = new File(destDirectory, entryName);

                //create all folder needed to store in correct relative path.
                f.getParentFile().mkdirs();

                fos = new FileOutputStream(f);
                int len;
                byte buffer[] = new byte[1024];
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();

                LOGGER.info("\"" + entryName + "\" extracted to \"" + destDirectory.getAbsolutePath() + File.separator + entryName + "\"");

                if (index % 5 == 0) {
                    System.gc();
                }

                ze = zis.getNextEntry();
            }

            LOGGER.info("Successfully unzipping file: \"" + zipFile.getAbsolutePath() + "\"");
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                // ignored
            }

            try {
                if (zis != null) {
                    zis.closeEntry();
                }
            } catch (Exception e) {
                // ignored
            }

            try {
                if (zis != null) {
                    zis.close();
                }
            } catch (Exception e) {
                // ignored
            }
        }
    }

    public static void zipDirectory(final Path srcDirectoryPath, final Path destZipFilePath) throws IOException {
        try (
                FileOutputStream fos = new FileOutputStream(destZipFilePath.toFile());
                ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            Files.walkFileTree(srcDirectoryPath, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String relativePath = srcDirectoryPath.relativize(file).toString();

                    if (File.separatorChar != '/') {
                        relativePath = relativePath.replace(File.separatorChar, '/');
                    }

                    zos.putNextEntry(new ZipEntry(relativePath));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    String relativePath = srcDirectoryPath.relativize(dir).toString();

                    if (File.separatorChar != '/') {
                        relativePath = relativePath.replace(File.separatorChar, '/');
                    }

                    zos.putNextEntry(new ZipEntry(relativePath + "/"));
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public static String getApplicationLocale() {
        String locale = System.getProperty(PropertyConstants.PROPERTY_NAME_LOCALE);

        return locale != null ? locale : ClopuccinoMessages.localeToString(Locale.getDefault());
    }

    public static String defaultRootDirectoryPath() {
        return System.getProperty("user.home");
    }

    public static String getLatestFileChooserDirectoy() {
        if (latestFileChooserDirectoy == null) {
            setLatestFileChooserDirectoy(defaultRootDirectoryPath());
        }

        return latestFileChooserDirectoy;
    }

    public static void setLatestFileChooserDirectoy(String latestFileChooserDirectoy) {
        Utility.latestFileChooserDirectoy = latestFileChooserDirectoy;
    }

    public static File openAndSelectRootDirectoryWithFileChooser(Component parent) {
        if (OSUtility.isOSX()) {
            NSOpenPanelBridge openPanelBridge = new NSOpenPanelBridge();

            String selectedPath = openPanelBridge.chooseFile();

            if (selectedPath != null) {
                return new File(selectedPath);
            } else {
                return null;
            }
        } else {
            JFileChooser fileChooser = new JFileChooser(Utility.getLatestFileChooserDirectoy());

            fileChooser.setDialogTitle(Utility.localizedString("root.directory.chooser.title"));
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory();
                }

                @Override
                public String getDescription() {
                    return Utility.localizedString("root.directory.chooser.filter.description");
                }
            });
            fileChooser.setFileHidingEnabled(false);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int option = fileChooser.showDialog(parent, Utility.localizedString("root.directory.chooser.approve.button.title"));

            File selectedFile;

            if (option == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
            } else {
                selectedFile = null;
            }

            return selectedFile;
        }
    }

    public static boolean containsIllegalCharacterForComputerName(String computerName) {
        return computerName.contains("|");
    }

    public static String findIllegalCharacterForComputerName(String computerName) {
        return computerName.contains("|") ? "|(Pipe)" : "";
    }

    public static String correctDirectoryPath(String rootDirectoryPath) {
        if (rootDirectoryPath == null) {
            rootDirectoryPath = "";
        }

        rootDirectoryPath = rootDirectoryPath.trim();

        // delete last file separator, if any
        // exclude the root directory for Linux/Unix: '/'
        // loop to clear all the file separators at the end of the file name

        for (; rootDirectoryPath.length() > 1 && rootDirectoryPath.endsWith(File.separator); ) {
            rootDirectoryPath = rootDirectoryPath.substring(0, rootDirectoryPath.length() - 1);
        }

        // Do not add this becasuse the correct name of system root should be like 'C:' instead of 'C:\'
//        // For Windows, only names of the system roots end with ':'
//        if (OSUtility.isWindows() && rootDirectoryPath.endsWith(":")) {
//            rootDirectoryPath = rootDirectoryPath + File.separator;
//        }

        return rootDirectoryPath;
    }

    public static byte[] shortToByte(short[] in) {
        byte[] out = new byte[in.length];

        for (int i = in.length - 1; i >= 0; i--) {
            out[i] = (byte) (in[i]);
        }

        return out;
    }

    public static String suffixLocaleParameterWithURL(String urlString) {
        StringBuilder builder = new StringBuilder(urlString);

        if (urlString.contains("?")) {
            builder.append("&locale=");
        } else {
            builder.append("?locale=");
        }

        String locale = System.getProperty(PropertyConstants.PROPERTY_NAME_LOCALE, ClopuccinoMessages.localeToString(Locale.getDefault()));

        builder.append(locale);

        return builder.toString();
    }

    public static String findCurrentDesktopVersion() {
        if (versionBundle.containsKey("app.version")) {
            return versionBundle.getString("app.version");
        } else {
            return Constants.DEFAULT_DESKTOP_VERSION;
        }
    }

    /**
     * Wrap message in text area to wrap text in <code></code>JOptionPane.showMessageDialog(...)</code>.
     */
    public static void showMessageDialogWithMessageInTextArea(Component parentComponent, String message, String title, int messageType, Icon icon) {
        showMessageDialogWithMessageInTextArea(parentComponent, message, title, messageType, icon, 480, 200);
    }

    /**
     * Wrap message in text area to wrap text in <code></code>JOptionPane.showMessageDialog(...)</code>.
     */
    public static void showMessageDialogWithMessageInTextArea(Component parentComponent, String message, String title, int messageType, Icon icon, final int preferredWidth, final int preferredHeight) {
        JTextArea textArea = new JTextArea(message != null ? message : "");
        JScrollPane scrollPane = new JScrollPane(textArea){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(preferredWidth, preferredHeight);
            }
        };

        showMessageDialog(parentComponent, scrollPane, title, messageType, icon);
//        JOptionPane.showMessageDialog(parentComponent, scrollPane, title, messageType, icon);
    }

    /**
     * Wrap message in text area to wrap text in <code></code>JOptionPane.showMessageDialog(...)</code>.
     */
    public static int showConfirmDialogWithMessageInTextArea(Component parentComponent, String message, String title, int optionType, int messageType, Icon icon) {
        return showConfirmDialogWithMessageInTextArea(parentComponent, message, title, optionType, messageType, icon, 480, 200);
    }

    /**
     * Wrap message in text area to wrap text in <code></code>JOptionPane.showMessageDialog(...)</code>.
     */
    public static int showConfirmDialogWithMessageInTextArea(Component parentComponent, String message, String title, int optionType, int messageType, Icon icon, final int preferredWidth, final int preferredHeight) {
        JTextArea textArea = new JTextArea(message != null ? message : "");
        JScrollPane scrollPane = new JScrollPane(textArea){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(preferredWidth, preferredHeight);
            }
        };

        return showConfirmDialog(parentComponent, scrollPane, title, optionType, messageType, icon);
//        return JOptionPane.showConfirmDialog(parentComponent, scrollPane, title, optionType, messageType, icon);
    }

    public static Properties prepareSystemProperties() {
        Properties properties = System.getProperties();

        properties.remove("java.class.path");
        properties.remove("java.library.path");
        properties.remove("sun.boot.class.path");
        properties.remove("com.apple.mrj.application.apple.menu.about.name");
        properties.remove("http.nonProxyHosts");
        properties.remove("socksNonProxyHosts");
        properties.remove("ftp.nonProxyHosts");
        properties.setProperty(PropertyConstants.PROPERTY_NAME_FILELUG_DESKTOP_CURRENT_VERSION, System.getProperty(PropertyConstants.PROPERTY_NAME_FILELUG_DESKTOP_CURRENT_VERSION, Constants.DEFAULT_DESKTOP_VERSION));
        properties.setProperty(PropertyConstants.PROPERTY_NAME_LOCALE, System.getProperty(PropertyConstants.PROPERTY_NAME_LOCALE));

        if (OSUtility.isLinux()) {
            try {
                LinuxOSVersion linuxOSVersion = new LinuxOSVersion();

                // Not working for  the first three, which are the same with the property without 'linux' prefix
//                properties.setProperty("linux.os.name", linuxOSVersion.getName());
//                properties.setProperty("linux.os.version", linuxOSVersion.getVersion());
//                properties.setProperty("linux.os.arch", linuxOSVersion.getArch());
                properties.setProperty("linux.platform.name", linuxOSVersion.getPlatformName());
            } catch (OperationNotSupportedException e) {
                // ignored
            }
        }

        return properties;
    }

    public static JsonNode prepareJsonNodeFromSystemProperties(ObjectMapper mapper) {
        if (mapper == null) {
            mapper = Utility.createObjectMapper();
        }

        Properties properties = Utility.prepareSystemProperties();

        ObjectNode syspropsNode = mapper.createObjectNode();

        Set<String> propertyNames = properties.stringPropertyNames();

        for (String propertyName : propertyNames) {
            syspropsNode.put(propertyName, properties.getProperty(propertyName, ""));
        }

        return syspropsNode;
    }

    public static String generateDeviceTokenString() {
        String currentDeviceToken = Utility.getPreference(PropertyConstants.PROPERTY_NAME_DEVICE_TOKEN, "");

        if (currentDeviceToken.trim().length() < 1) {
            currentDeviceToken = DigestUtils.sha256Hex(UUID.randomUUID().toString() + System.currentTimeMillis());

            Utility.putPreference(PropertyConstants.PROPERTY_NAME_DEVICE_TOKEN, currentDeviceToken);
        }

        return currentDeviceToken;
    }

    public static DeviceToken prepareDeviceTokenObject() {
        String deviceTokenString = Utility.generateDeviceTokenString();

        String notificationType = "NONE";

        String deviceType = prepareDeviceType();

        String deviceVersion = System.getProperty(PropertyConstants.PROPERTY_NAME_OS_VERSION);

        // Not working, the value is the same with system property 'os.version'
//        if (OSUtility.isLinux()) {
//            try {
//                LinuxOSVersion linuxOSVersion = new LinuxOSVersion();
//
//                deviceVersion = linuxOSVersion.getVersion();
//            } catch (OperationNotSupportedException e) {
//                // ignored
//            }
//        }

        String filelugVersion = System.getProperty(PropertyConstants.PROPERTY_NAME_FILELUG_DESKTOP_CURRENT_VERSION);

        String filelugBuild = "0";

        return new DeviceToken(null, deviceTokenString, notificationType, deviceType, deviceVersion, filelugVersion, filelugBuild, 0, 0, null);
    }

    public static String createNewComputerName() {
        String computerName = null;

        OSUtility.OSType osType = OSUtility.getOSType();

        computerName = OSUtility.computerNameFromScript(osType);

        if (computerName == null || computerName.length() < 1) {
            computerName = OSUtility.computerNameFromInternetAdapter();

            if (computerName == null || computerName.length() < 1) {
                computerName = OSUtility.computerNameFromMXBean();
            }
        }

        if (computerName == null || computerName.trim().length() < 0 || computerName.trim().toLowerCase().equals("localhost")) {
            String userName = System.getProperty("user.name");

            computerName = String.format("%s%s", userName, ClopuccinoMessages.getMessage("s.computer"));
        }

        return computerName;
    }

    public static String prepareDeviceType() {
        DeviceToken.DeviceType type;

        OSUtility.OSType osType = OSUtility.getOSType();

        switch (osType) {
            case WINDOWS:
                type = DeviceToken.DeviceType.WIN_DESKTOP;

                break;
            case OS_X:
                type = DeviceToken.DeviceType.OSX;

                break;
            case LINUX:
                type = DeviceToken.DeviceType.LINUX_DESKTOP;

                break;
            default:
                type = DeviceToken.DeviceType.DESKTOP;
        }

        return type.name();
    }

    public static String generateVerificationToDeleteComputer(String userId, Long computerId) {
        String hash = DigestUtils.md5Hex(userId + "==" + computerId);

        return DigestUtils.sha256Hex(userId + "|" + hash + ":" + computerId + "_" + hash);
    }

    public static String generateVerificationToCheckComputerExists(String userId, Long computerId, String recoveryKey) {
        return DigestUtils.sha256Hex(recoveryKey + "|" + userId + ":" + computerId + "_" + userId);
    }

    public static String generateRemoveAdminVerification(String userId, Long computerId, String sessionId) {
        return DigestUtils.sha256Hex(sessionId + "|" + userId + ":" + computerId + "_" + userId);
    }

    /**
     * Create zero-sized JFrame, at the center of the screen, but not show up.
     * You need to invoke setVisible(true) (use Swing thread) to make it visible.
     * <br/>
     * If you want to dispose it, invoke setVisible(false) and dispose().
     *
     * @return a newly created JFrame, but not visible.
     */
    public static JFrame createZeroSizeFrame() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();

        Point centerPoint = pointerInfo.getLocation();

        GraphicsDevice graphicsDevice = pointerInfo.getDevice();

        GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();

        JFrame zeroSizeFrame = new JFrame(graphicsConfiguration);

        zeroSizeFrame.setSize(new Dimension(0, 0));

        zeroSizeFrame.setLocation(centerPoint);
//        zeroSizeFrame.setLocationRelativeTo(null);

        zeroSizeFrame.setUndecorated(true);
        zeroSizeFrame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        zeroSizeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        zeroSizeFrame.setAlwaysOnTop(true);

        return zeroSizeFrame;
    }

    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon) {
        boolean useZeroFrameAsParentComponent = false;

        int selectedOption;

        Component realParentComponent;

        if (parentComponent == null || !parentComponent.isVisible()) {
            useZeroFrameAsParentComponent = true;

            realParentComponent = Utility.createZeroSizeFrame();
            realParentComponent.setVisible(true);
        } else {
            realParentComponent = parentComponent;
        }

        try {
            selectedOption = JOptionPane.showConfirmDialog(realParentComponent, message, title, optionType, messageType, icon);
        } finally {
            if (useZeroFrameAsParentComponent) {
                realParentComponent.setVisible(false);
                ((JFrame) realParentComponent).dispose();
            }
        }

        return selectedOption;
    }

    public static void showMessageDialog(Component parentComponent, Object message, String title, int messageType, Icon icon) {
        boolean useZeroFrameAsParentComponent = false;

        Component realParentComponent;

        if (parentComponent == null || !parentComponent.isVisible()) {
            useZeroFrameAsParentComponent = true;

            realParentComponent = Utility.createZeroSizeFrame();
            realParentComponent.setVisible(true);
        } else {
            realParentComponent = parentComponent;
        }

        try {
            JOptionPane.showMessageDialog(realParentComponent, message, title, messageType, icon);
        } finally {
            if (useZeroFrameAsParentComponent) {
                realParentComponent.setVisible(false);
                ((JFrame) realParentComponent).dispose();
            }
        }
    }

    public static void printAllAvailableFontFamilyNames(final Logger logger) {
        if (logger != null) {
            Font[] allFonts = getAvailableFonts();

            StringBuilder buffer = new StringBuilder("\n--- START Available Font Family Names ---\n");

            for (Font font : allFonts) {
                buffer.append("Font name: ");
                buffer.append(font.getFontName(Locale.ENGLISH));
                buffer.append(", Family: ");
                buffer.append(font.getFamily(Locale.ENGLISH));
                buffer.append("\n");
            }
            buffer.append("--- END Available Font Family Names ---\n");

            logger.info(buffer.toString());
        }
    }

    public static Font[] getAvailableFonts() {
        if (availableFonts == null) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

            availableFonts = ge.getAllFonts();
        }

        return availableFonts;
    }

    public static Font findFontByFamilyName(String fontFamilyName, Locale fontFamilyNameLocale) {
        Font foundFont = null;

        Font[] availableFonts = getAvailableFonts();

        for (Font font : availableFonts) {
            String foundFamilyName = font.getFamily(fontFamilyNameLocale);

            if (foundFamilyName != null && foundFamilyName.toLowerCase().equals(fontFamilyName.toLowerCase())) {
                foundFont = font;

                LOGGER.info("Use Default Font: " + font.getFamily(fontFamilyNameLocale));

                break;
            }
        }

        return foundFont;
    }

    public static Font prepareFontForMenuItem(MenuItem menuItem, String nameToDisplay) {
        Font foundFont = null;

        Font currentFont = menuItem.getFont();

        if (currentFont == null) {
            currentFont = UIManager.getFont("MenuItem.font");
        }

        if (currentFont != null && canDisplayString(currentFont, nameToDisplay)) {
            foundFont = currentFont;
        } else {
            // search the first font of all other fonts that can display the string

            Font[] availableFonts = getAvailableFonts();

            for (Font otherFont : availableFonts) {
                if (canDisplayString(otherFont, nameToDisplay)) {
                    if (currentFont != null) {
                        foundFont = otherFont.deriveFont(Font.PLAIN, currentFont.getSize2D());
                    } else {
                        foundFont = otherFont.deriveFont(Font.PLAIN, Constants.DEFAULT_FONT_SIZE);
                    }
                }
            }
        }

        return foundFont;
    }

    private static boolean canDisplayString(Font font, String display) {
        return font.canDisplayUpTo(display) == -1;
    }

    public static void showAboutDialog(Component parentComponent) {
        String title = Utility.localizedString("menu.about");

        String applicationVersion = System.getProperty(PropertyConstants.PROPERTY_NAME_FILELUG_DESKTOP_CURRENT_VERSION, Constants.DEFAULT_DESKTOP_VERSION);

        String message = Utility.localizedString("app.version", applicationVersion) + "\n" + Constants.DEFAULT_WEB_SITE_URL;

        Utility.showMessageDialog(parentComponent, message, title, JOptionPane.INFORMATION_MESSAGE, Utility.createImageIcon("Filelug_Icon_32Bits_60.png", ""));
    }
}