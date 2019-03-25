package com.phaseos.utils;

import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

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
    
    public static Location parseLocation(String location) {

        World world = Bukkit.getWorld(location.split(",")[0]);
        double x = Integer.parseInt(location.split(",")[1]);
        double y = Integer.parseInt(location.split(",")[2]);
        double z = Integer.parseInt(location.split(",")[3]);
        return new Location(world, x, y ,z);
        
    }

}
