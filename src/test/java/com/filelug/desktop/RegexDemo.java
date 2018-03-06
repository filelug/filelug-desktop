package com.filelug.desktop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <code></code>
 *
 * @author masonhsieh
 * @version 1.0
 */
public class RegexDemo {

    public static void main(String[] args) {
        String version1 = "3.10.1";

        Pattern pattern = Pattern.compile(Constants.DESKTOP_VERSION_REG_EXP);

        Matcher matcher = pattern.matcher(version1);

        if (matcher.find()) {
            System.out.println(matcher.group());
        }
    }
}
