package com.phaseos.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArgumentParser {

    private String[] args;
    private org.bukkit.command.Command command;
    private CommandSender sender;

    public ArgumentParser(String[] args) {
        this.args = args.clone();
    }

    public ArgumentParser(String[] args, org.bukkit.command.Command command, CommandSender sender) {
        this(args);
        this.command = command;
        this.sender = sender;
    }

    public int size() {
        return args.length;
    }

    /**
     * @param index starts at 1
     * @return the string at index index-1
     */
    public String get(int index) {
        return args[index - 1];
    }

    public String getFrom(int index) {
        return getFrom(index, " ");
    }

    public String getFrom(int index, String splitter) {
        if (index > size()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (int i = index; i < args.length; i++) {
            if (!first) {
                sb.append(splitter);
            } else {
                first = false;
            }
            sb.append(args[i]);
        }
        return sb.toString();
    }

    public double getDouble(int index) {
        try {
            return Double.parseDouble(get(index).replace(",", "."));
        } catch (Exception ex) {
            throw new NotANumberException(get(index));
        }
    }

    public long getLong(int index) {
        try {
            return Long.parseLong(get(index));
        } catch (Exception e) {
            throw new NotANumberException(get(index));
        }
    }

    public int getInt(int index) {
        try {
            return Integer.parseInt(get(index));
        } catch (Exception ex) {
            throw new NotANumberException(get(index));
        }
    }

    public boolean getBoolean(int index) {
        try {
            return Boolean.parseBoolean(get(index));
        } catch (Exception ex) {
            throw new NotABooleanException(get(index));
        }
    }

    public Player getPlayer(int index) {
        String playerName = get(index);
        Player player = Bukkit.getPlayer(playerName);
        if (player == null)
            throw new PlayerNotFoundException(playerName);
        return player;
    }

    public boolean hasNoArguments() {
        return size() == 0;
    }

    public boolean hasExactly(int arguments) {
        return size() == arguments;
    }

    public boolean hasAtLeast(int arguments) {
        return size() >= arguments;
    }

    public boolean hasLessThan(int arguments) {
        return size() < arguments;
    }

    public String[] getArgs() {
        return this.args;
    }

    public org.bukkit.command.Command getCommand() {
        return command;
    }

    public CommandSender getSender() {
        return sender;
    }

    public class ParameterException extends RuntimeException {

        public ParameterException(String message) {
            super(message, null, false, false);
        }

    }

    public class NotANumberException extends ParameterException {
        public NotANumberException(String message) {
            super(message);
        }
    }

    public class PlayerNotFoundException extends ParameterException {
        public PlayerNotFoundException(String message) {
            super(message);
        }
    }

    public class NotABooleanException extends ParameterException {
        public NotABooleanException(String message) {
            super(message);
        }
    }

}
