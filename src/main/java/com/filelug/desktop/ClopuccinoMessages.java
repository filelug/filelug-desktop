package com.filelug.desktop;

import org.glassfish.jersey.internal.l10n.LocalizableMessageFactory;
import org.glassfish.jersey.internal.l10n.Localizer;

import java.util.Locale;

/**
 * <code>ClopuccinoMessages</code> handles i18n messages for clopuccino.
 *
 * @author masonhsieh
 * @version 1.0
 */
public final class ClopuccinoMessages {

    private static Locale[] allLocales = Locale.getAvailableLocales();

    private final static LocalizableMessageFactory messageFactory = new LocalizableMessageFactory("Messages");
    private final static Localizer localizer = new Localizer();

    /**
     * Use system default locale
     * @param key
     * @param args
     * @return
     */
    public static String getMessage(String key, Object... args) {
        return localizer.localize(messageFactory.getMessage(key, args));
    }

    /**
     * if locale is null or not a valid value, use system default locale
     *
     * @param locale
     * @param key
     * @param args
     * @return
     */
    public static String localizedMessage(String locale, String key, Object... args) {
        Locale foundLocale = locale != null ? getLocaleFromString(locale) : Locale.getDefault();
        if (foundLocale == null) {
            foundLocale = Locale.getDefault();
        }

        return new Localizer(foundLocale).localize(messageFactory.getMessage(key, args));
    }

    /**
     * Convert a string based testLocale into a Locale Object. Assumes the string
     * has form "{language}_{country}_{variant}". Examples: "en", "de_DE",
     * "_GB", "en_US_WIN", "de__POSIX", "fr_MAC"
     * <p/>
     * 若localeString為null、空值、或不合法的Locale，則回傳null
     *
     * @param localeString The String
     * @return the Locale. 若localeString為null、空值、或不合法的Locale，則回傳null
     */
    public static Locale getLocaleFromString(String localeString) {
        if (localeString == null || !validLocale(localeString)) {
            return null;
        }

        localeString = localeString.trim();

        // Extract language
        int languageIndex = localeString.indexOf('_');
        String language = null;
        if (languageIndex == -1) {
            // No further "_" so is "{language}" only
            return new Locale(localeString, "");
        } else {
            language = localeString.substring(0, languageIndex);
        }

        // Extract country
        int countryIndex = localeString.indexOf('_', languageIndex + 1);
        String country = null;
        if (countryIndex == -1) {
            // No further "_" so is "{language}_{country}"
            country = localeString.substring(languageIndex + 1);
            return new Locale(language, country);
        } else {
            // Assume all remaining is the variant so is
            // "{language}_{country}_{variant}"
            country = localeString.substring(languageIndex + 1, countryIndex);
            String variant = localeString.substring(countryIndex + 1);
            return new Locale(language, country, variant);
        }
    } // end getLocaleFromString(String)

    public static boolean validLocale(String locale) {
        boolean valid = false;

        for (Locale currentLocale : allLocales) {
            String localeProgrammingName = currentLocale.toString();

            if (locale.equals(localeProgrammingName)) {
                valid = true;
                break;
            }
        }

        return valid;
    }

    public static String localeToString(Locale locale) {
        StringBuilder builder = new StringBuilder(locale.getLanguage());

        String country = locale.getCountry();
        if (country.length() > 0) {
            builder.append("_");
            builder.append(country);
        }

        String variant = locale.getVariant();
        if (variant.length() > 0) {
            builder.append("_");
            builder.append(variant);
        }

        return builder.toString();
    }
}
