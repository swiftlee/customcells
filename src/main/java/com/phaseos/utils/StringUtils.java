package com.phaseos.utils;

import org.bukkit.ChatColor;

import java.util.List;

public class StringUtils {

    public static String fmt(String msg) {

        return ChatColor.translateAlternateColorCodes('&', msg);

    }

}
