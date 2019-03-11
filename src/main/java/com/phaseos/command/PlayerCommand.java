package com.phaseos.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * Created by Dennis Heckmann on 13.05.17
 * Copyright (c) 2017 Dennis Heckmann
 */
//@Sender(sender = SenderType.PLAYER_ONLY)
public abstract class PlayerCommand extends Command {

    public PlayerCommand(String name, String... aliases) {
        super(name, aliases);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getPlayerOnlyMessage());
            return true;
        } else {
            return super.onCommand(sender, command, s, args);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.singletonList("");
        } else {
            return super.onTabComplete(sender, command, s, args);
        }
    }

    @Override
    protected void execute(CommandSender sender, ArgumentParser ap) {
        execute((Player) sender, ap);
    }

    protected abstract void execute(Player player, ArgumentParser ap);

}
