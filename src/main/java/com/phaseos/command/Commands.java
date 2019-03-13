package com.phaseos.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by Dennis Heckmann on 13.05.17
 * Copyright (c) 2017 Dennis Heckmann
 */
public class Commands implements Listener {

    private JavaPlugin plugin;
    private List<CustomCommand> customCommands;
    private List<Injection<?>> injections;
    private String prefix;
    String playerNotFound, noNumber, noBoolean, noPermissions;

    public Commands(JavaPlugin plugin) {
        this(plugin, "[PREFIX] ");
    }

    public Commands(JavaPlugin plugin, String prefix) {
        this.plugin = plugin;
        customCommands = new ArrayList<>();
        injections = new ArrayList<>();
        this.prefix = prefix;
        this.playerNotFound = prefix + ChatColor.RED + "The player '%player%' is not online!";
        this.noNumber = prefix + ChatColor.RED + "Entered number '%num%' is invalid!";
        this.noBoolean = prefix + ChatColor.RED + "Entered truth value '%value%' is invalid!";
        this.noPermissions = prefix + ChatColor.RED + "You are not permitted to use this command!";
    }

    public <T> Commands addDependencyInjector(Class<T> clazz, Function<String, ? extends T> function, String condition) {
        Objects.requireNonNull(clazz, "clazz may not be null");
        Objects.requireNonNull(function, "function may not be null");
        injections.add(new Injection<>(clazz, function, condition));
        return this;
    }

    public <T> Commands addStaticDependencyInjector(Class<T> clazz, T o, String condition) {
        return addDependencyInjector(clazz, s -> o, condition);
    }

    public <T> Commands addForcedDependencyInjector(Class<T> clazz, Function<String, ? extends T> function) {
        injections.add(new ForcedInjection<>(clazz, function));
        return this;
    }

    public <T> Commands addStaticForcedDependencyInjector(Class<T> clazz, T o) {
        return addForcedDependencyInjector(clazz, s -> o);
    }

    private void injectDependencies(Object o) {
        Class<?> clazz = o.getClass();
        Field[] fields = clazz.getDeclaredFields();
        outer: for (Field field : fields) {
            for (Injection<?> injection : injections) {
                if (injection.shallInject(field)) {
                    injection.inject(o, field);
                    continue outer;
                }
            }
        }
    }

    public Commands registerCommand(Command command) {
        injectDependencies(command);
        command.commands = this;
        String name = command.getName();
        PluginCommand pluginCommand = plugin.getCommand(name);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        } else {
            if (command instanceof CustomCommand) {
                if (customCommands.size() == 0) {
                    // only register if necessary
                    Bukkit.getPluginManager().registerEvents(this, plugin);
                }
                customCommands.add((CustomCommand) command);
            } else {
                throw new RuntimeException("Could not find command named " + command.getName());
            }
        }
        return this;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        for (CustomCommand command : customCommands) {
            if (command.isCustomCommand(message)) {
                command.handleCustomCommand(e);
                e.setCancelled(true);
            }
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public String prefixedMessage(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        return prefix + (prefix.charAt(prefix.length() - 1) == ' ' ?  message : " " + message);
    }

    private class Injection<T> {
        Class<T> clazz;
        Function<String, ? extends T> function;
        private String condition;

        public Injection(Class<T> clazz, Function<String, ? extends T> function, String condition) {
            this.clazz = clazz;
            this.function = function;
            this.condition = condition;
        }

        boolean shallInject(Field field) {
            if (field.isAnnotationPresent(Inject.class)) {
                String condition = field.getAnnotation(Inject.class).value();
                return field.getType() == clazz && (this.condition == null || condition.equals(this.condition));
            } else {
                return false;
            }
        }

        void inject(Object o, Field field) {
            String argument = field.getAnnotation(Inject.class).value();
            field.setAccessible(true);
            try {
                field.set(o, function.apply(argument));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(false);
        }
    }

    private class ForcedInjection<T> extends Injection<T> {

        public ForcedInjection(Class<T> clazz, Function<String, ? extends T> function) {
            super(clazz, function, null);
        }

        @Override
        boolean shallInject(Field field) {
            return field.getType() == clazz;
        }

        @Override
        void inject(Object o, Field field) {
            field.setAccessible(true);
            try {
                field.set(o, function.apply(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(false);
        }
    }

    public Commands setErrorMessages(String playerNotFound, String noNumber, String noPermissions) {
        this.playerNotFound = playerNotFound;
        this.noNumber = noNumber;
        this.noPermissions = noPermissions;
        return this;
    }

}
