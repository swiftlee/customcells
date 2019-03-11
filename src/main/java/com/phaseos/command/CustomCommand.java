package com.phaseos.command;

import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dennis Heckmann on 14.05.17
 * Copyright (c) 2017 Dennis Heckmann
 */
public abstract class CustomCommand extends Command {

    private final String prefix;
    private final Pattern messagePattern;

    public CustomCommand(String prefix, String name, String... aliases) {
        super(name, aliases);
        this.prefix = prefix;
        messagePattern = Pattern.compile(getPrefix() + ".*?\\s?(.*)");
    }

    public boolean isCustomCommand(String message) {
        final String copy = message.toLowerCase();
        return getNameAndAliases().stream().filter(str -> {
            if (copy.length() == (getPrefix() + str).length())
                return copy.equals(getPrefix() + str);
            else
                return copy.length() >= (getPrefix() + str).length() && copy.startsWith(getPrefix() + str + " ");
        }).findFirst().orElse(null) != null;
    }

    public String getPrefix() {
        return prefix;
    }

    public void handleCustomCommand(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        Matcher m = messagePattern.matcher(message);
        if (m.find()) {
            String res = m.group(1);
            String[] p = res.split("\\s+");
            String[] copy = new String[p.length - 1];
            System.arraycopy(p, 1, copy, 0, copy.length);
            onCommand(e.getPlayer(), null, null, copy);
        }
    }

}
