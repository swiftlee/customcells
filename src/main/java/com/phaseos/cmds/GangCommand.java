package com.phaseos.cmds;

import com.phaseos.customcells.CustomCells;
import com.phaseos.command.ArgumentParser;
import com.phaseos.command.Command;
import com.phaseos.command.PlayerCommand;
import com.phaseos.gangs.Gang;
import com.phaseos.gangs.GangMember;
import com.phaseos.utils.Color;
import com.phaseos.utils.Gui;
import com.phaseos.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GangCommand extends Command {

    private CustomCells plugin;
    public static final String commandsHeader = StringUtils.fmt("&8&m----------------&r&8[ &6Gang Commands &8]&m----------------");
    public static final String listHeader = StringUtils.fmt("&8&m----------------&r&8[ &6Gangs List &8]&m----------------");
    public static final String topHeader = StringUtils.fmt("&8&m----------------&r&8[ &6Top Gangs &8]&m----------------");

    public GangCommand(CustomCells plugin) {
        super("gang", "g");
        this.plugin = plugin;
        addSubCommand(new HelpCommand());
        addSubCommand(new ListCommand());
        addSubCommand(new TopCommand());

    }

    @Override
    protected void execute(CommandSender sender, ArgumentParser ap) {
        if (sender instanceof Player) {

            Player p = (Player) sender;

            if (!GangMember.hasGang(p.getUniqueId())) {
                p.sendMessage(StringUtils.fmt("&cYou are not in a gang!"));
                return;
            }

            p.openInventory(gui);

        } else
            sender.sendMessage(StringUtils.fmt("&cYou cannot execute this command."));
    }

    private class HelpCommand extends PlayerCommand {

        private final String perm = Permission.HELP.toString();

        public HelpCommand() {
            super("help");
        }

        @Override
        protected void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

            player.sendMessage(commandsHeader);
            for (String subCommandName : getParent().getSubCommandNames())
                player.sendMessage("§e/g " + subCommandName);

        }

    }

    private class ListCommand extends PlayerCommand {

        private final String perm = Permission.LIST.toString();

        public ListCommand() {
            super("list");
        }

        @Override
        protected void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

            SortedSet<String> names = new TreeSet<>();
            int page = ap.getInt(0);
            int counter = 1;
            final int pageLength = 10;

            for (String key : Gang.GangDatabase.getYml().getKeys(false)) {

                if (counter >= (page * pageLength) - (pageLength + 1))
                    names.add(Gang.GangDatabase.getYml().getString(key + ".name"));
                counter++;

            }

            if (names.isEmpty()) {
                player.sendMessage(listHeader + "\n\n" + StringUtils.fmt("&6This page is empty!"));
                return;
            }

            StringBuilder pageContent = new StringBuilder();
            pageContent.append(listHeader).append("\n\n");
            for (String line : names) {

                pageContent.append(StringUtils.fmt("&8- &6")).append(line).append("\n");

            }
            pageContent.append(StringUtils.fmt("\n&8Page number: &6" + page));
            player.sendMessage(String.valueOf(pageContent));

        }

    }

    private class TopCommand extends PlayerCommand {

        private final String perm = Permission.TOP.toString();

        public TopCommand() {
            super("top");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

            Map<Integer, String> unsortedGangs = new HashMap<>();
            YamlConfiguration db = Gang.GangDatabase.getYml();

            for (String gang : db.getKeys(false)) {
                String base = gang + ".";
                unsortedGangs.put(db.getInt(base + "power"), db.getString(base + "name"));
            }

            Map<Integer, String> sortedGangs = new TreeMap<>(Comparator.reverseOrder());
            sortedGangs.putAll(unsortedGangs);

            StringBuilder message = new StringBuilder(topHeader + "\n\n");
            int counter = 1;

            for (Map.Entry entry : sortedGangs.entrySet()) {
                message.append(counter).append(". ").append(entry.getValue()).append("\n");

                if (counter == 10)
                    break;

                counter++;

            }

            player.sendMessage(String.valueOf(message));

        }

    }

    private class InfoCommand extends PlayerCommand {

        private final String perm = Permission.INFO.toString();

        public InfoCommand() {
            super("info");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

            YamlConfiguration db = Gang.GangDatabase.getYml();
            Gang gang = null;
            for (String gangId : db.getKeys(false)) {

                if (db.getString(gangId + ".name").equalsIgnoreCase(ap.get(0))) {
                    gang = new Gang(UUID.fromString(gangId));
                    break;
                }

            }

            if (gang == null)
                player.sendMessage(StringUtils.fmt("&cThere was no gang listed under the name \"" + ap.get(0) + "\"."));
            else {
                String info = "";
            }

        }

    }

    private class CreateCommand extends PlayerCommand {

        private final String perm = Permission.CREATE.toString();

        public CreateCommand() {
            super("create");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

    private class DisbandCommand extends PlayerCommand {

        private final String perm = Permission.DISBAND.toString();

        public DisbandCommand() {
            super("disband");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

    private class InviteCommand extends PlayerCommand {

        private final String perm = Permission.INVITE.toString();

        public InviteCommand() {
            super("invite");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

    private class JoinCommand extends PlayerCommand {

        private final String perm = Permission.JOIN.toString();

        public JoinCommand() {
            super("join", "accept");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

    private class UninviteCommand extends PlayerCommand {

        private final String perm = Permission.UNINVITE.toString();

        public UninviteCommand() {
            super("uninvite");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

    private class KickCommand extends PlayerCommand {

        private final String perm = Permission.KICK.toString();

        public KickCommand() {
            super("kick");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

    private class LeaveCommand extends PlayerCommand {

        private final String perm = Permission.LEAVE.toString();

        public LeaveCommand() {
            super("leave");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

    private class PromoteCommand extends PlayerCommand {

        private final String perm = Permission.PROMOTE.toString();

        public PromoteCommand() {
            super("promote");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

    private class DemoteCommand extends PlayerCommand {

        private final String perm = Permission.DEMOTE.toString();

        public DemoteCommand() {
            super("demote");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

    private class LeaderCommand extends PlayerCommand {

        private final String perm = Permission.LEADER.toString();

        public LeaderCommand() {
            super("leader");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

    private class DepositCommand extends PlayerCommand {

        private final String perm = Permission.DEPOSIT.toString();

        public DepositCommand() {
            super("deposit");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

    private class WithdrawCommand extends PlayerCommand {

        private final String perm = Permission.WITHDRAW.toString();

        public WithdrawCommand() {
            super("withdraw");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

    private class HomeCommand extends PlayerCommand {

        private final String perm = Permission.HOME.toString();

        public HomeCommand() {
            super("home");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

    private class ChatCommand extends PlayerCommand {

        private final String perm = Permission.GANGCHAT.toString();

        public ChatCommand() {
            super("gc");
        }

        @Override
        public void execute(Player player, ArgumentParser ap) {

            if (!player.hasPermission(perm)) {
                player.sendMessage(StringUtils.fmt("&cYou do not have permission to use this command!"));
                return;
            }

        }

    }

}

enum Permission {
    HELP, LIST, TOP, INFO,
    CREATE, DISBAND, INVITE,
    JOIN, UNINVITE, KICK, LEAVE,
    PROMOTE, DEMOTE, LEADER, DEPOSIT,
    WITHDRAW, HOME, REDEEM, LEVELUP,
    GANGCHAT, ADMIN_BANK, ADMIN_DISBAND,
    ADMIN_RESET, ADMIN_RELOAD, ADMIN_SOCIALSPY;

    @Override
    public String toString() {
        if (this == HELP) return "cellgangs.gang.help";
        else if (this == LIST) return "cellgangs.gang.list";
        else if (this == TOP) return "cellgangs.gang.top";
        else if (this == INFO) return "cellgangs.gang.info";
        else if (this == CREATE) return "cellgangs.gang.create";
        else if (this == DISBAND) return "cellgangs.gang.disband";
        else if (this == INVITE) return "cellgangs.gang.invite";
        else if (this == JOIN) return "cellgangs.gang.join";
        else if (this == UNINVITE) return "cellgangs.gang.uninvite";
        else if (this == KICK) return "cellgangs.gang.kick";
        else if (this == LEAVE) return "cellgangs.gang.leave";
        else if (this == PROMOTE) return "cellgangs.gang.promote";
        else if (this == DEMOTE) return "cellgangs.gang.demote";
        else if (this == LEADER) return "cellgangs.gang.leader";
        else if (this == DEPOSIT) return "cellgangs.gang.deposit";
        else if (this == WITHDRAW) return "cellgangs.gang.withdraw";
        else if (this == HOME) return "cellgangs.gang.home";
        else if (this == REDEEM) return "cellgangs.gang.redeem";
        else if (this == LEVELUP) return "cellgangs.gang.levelup";
        else if (this == GANGCHAT) return "cellgangs.gangchat";
        else if (this == ADMIN_BANK) return "cellgangs.gangadmin.bank";
        else if (this == ADMIN_DISBAND) return "cellgangs.gangadmin.disband";
        else if (this == ADMIN_RESET) return "cellgangs.gangadmin.reset";
        else if (this == ADMIN_RELOAD) return "cellgangs.gangadmin.reload";
        else if (this == ADMIN_SOCIALSPY) return "cellgangs.gangadmin.socialspy";
        else return "null";
    }

}
