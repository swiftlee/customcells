package com.phaseos.utils;

import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.ChatColor;

public class StringUtils {

    public static String fmt(String msg) {

        return ChatColor.translateAlternateColorCodes('&', msg);

    }

    public static String capitalize(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        return new StrBuilder(strLen)
                .append(Character.toTitleCase(str.charAt(0)))
                .append(str.substring(1))
                .toString();
    }

}
