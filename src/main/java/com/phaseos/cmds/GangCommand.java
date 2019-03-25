package com.phaseos.cmds;

import com.phaseos.command.ArgumentParser;
import com.phaseos.command.Command;
import com.phaseos.command.PlayerCommand;
import com.phaseos.customcells.CustomCells;
import com.phaseos.gangs.Gang;
import com.phaseos.gangs.GangMember;
import com.phaseos.utils.MapUtil;
import com.phaseos.utils.StringUtils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

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

public class GangCommand extends Command {

    private static final String commandsHeader = StringUtils.fmt("&8&m----------------&r&8[ &6Gang Commands &8]&m----------------");
    private static final String listHeader = StringUtils.fmt("&8&m----------------&r&8[ &6Gangs List &8]&m----------------");
    private static final String topHeader = StringUtils.fmt("&8&m----------------&r&8[ &6Top Gangs &8]&m----------------");
    private static final String infoHeader = StringUtils.fmt("&8&m----------------&r&8[ &6%gang% &8]&m----------------");
    private CustomCells plugin;

    public GangCommand(CustomCells plugin) {
        super("gang", "g");
        this.plugin = plugin;
        addSubCommand(new HelpCommand());
        addSubCommand(new ListCommand());
        addSubCommand(new TopCommand());
        addSubCommand(new InfoCommand());
        addSubCommand(new CreateCommand());
        addSubCommand(new DisbandCommand());
        addSubCommand(new InviteCommand());
        addSubCommand(new JoinCommand());
        addSubCommand(new UninviteCommand());
        addSubCommand(new KickCommand());
        addSubCommand(new LeaveCommand());
        //TODO: pay command?
        addSubCommand(new PromoteCommand());
        addSubCommand(new DemoteCommand());
        addSubCommand(new LeaderCommand());
        addSubCommand(new DepositCommand());
        addSubCommand(new WithdrawCommand());
        addSubCommand(new HomeCommand());

    }

    // This is used for the disband command, all of the Gang commands are listed below this class
    public static class KillGangCommand extends Command {

        public KillGangCommand() {
            super("killgang", "kg");
        }

        private void doDisand(Gang gang) {

            for (String memberId : gang.getMembers()) {

                UUID id = UUID.fromString(memberId);
                GangMember member = new GangMember(id);
                Player p = Bukkit.getPlayer(id);
                if (p != null && p.isOnline()) {
                    if (member.isLeader())
                        p.sendMessage(StringUtils.fmt("&7You have successfully disbanded &6" + gang.getName() + "&7."));
                    else
                        p.sendMessage(StringUtils.fmt("&7Your gang: &6" + gang.getName() + " &7has been disbanded."));
                }
                member.setGangId(null); // TODO: make sure this is set in the config
                member.setRank(GangMember.Rank.RECRUIT);

            }

            Gang.GangDatabase db = new Gang.GangDatabase();
            Gang.GangDatabase.getYml().set(gang.getGangId().toString(), null);
            db.save();
            db.load();

            // TODO: remove island

        }

        @Override
        protected void execute(CommandSender sender, ArgumentParser ap) {
            if (!(sender instanceof Player)) {
                if (ap.hasExactly(1)) {
                    try {
                        doDisand(Gang.getGangFromId(UUID.fromString(ap.get(1))));
                    } catch (Exception e) {
                        System.err.println("A gang attempted to disband, but was unable to due to an invalid UUID in the argument parser. A valid Gang ID must be passed.");
                        e.printStackTrace();
                    }
                } else
                    System.err.println("No argument passed to KillGangCommand. Must pass an argument in form of UUID.");
            } else {
                Player p = (Player) sender;
                Gang gang = Gang.getGangFromId(UUID.fromString(ap.get(1)));
                if (gang != null) {
                    if (gang.getLeader().equals(p.getUniqueId())) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kg " + ap.get(1));
                        return;
                    }
                }
                p.sendMessage(StringUtils.fmt("&cYou are not the leader of this gang."));

            }
        }
    }

    @Override
    protected void execute(CommandSender sender, ArgumentParser ap) {
        if (sender instanceof Player) {

            Player p = (Player) sender;

            if (!GangMember.hasGang(p.getUniqueId())) {
                p.sendMessage(StringUtils.fmt("&cYou are not in a gang!"));
                return;
            }

            p.openInventory(CustomCells.getMainGui());

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

            if (ap.hasExactly(1)) {

                SortedSet<String> names = new TreeSet<>();
                int page = ap.getInt(1);
                if (page < 1) {
                    player.sendMessage(StringUtils.fmt("&cInvalid arguments, page number must be 1 or greater."));
                    return;
                }
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

            } else
                player.sendMessage(StringUtils.fmt("&cInvalid arguments, try: /list <number>"));

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

            if (ap.hasExactly(0)) {

                Map<String, Integer> unsortedGangs = new HashMap<>();
                YamlConfiguration db = Gang.GangDatabase.getYml();

                System.out.println("names:\n");
                for (String gang : db.getKeys(false)) {
                    String base = gang + ".";
                    System.out.println(db.getString(base + "name") + "\n");
                    unsortedGangs.put(db.getString(base + "name"), db.getInt(base + "power"));
                }

                Map<String, Integer> sortedGangs = MapUtil.sortByValue(unsortedGangs);

                StringBuilder message = new StringBuilder(topHeader).append("\n\n");
                int counter = 1;

                for (Map.Entry<String, Integer> entry : sortedGangs.entrySet()) {
                    message.append(counter == 1 ? ("&e" + counter) : (counter == 2 ? ("&7" + counter) : (counter == 3 ? ("&6" + counter) : "&3" + counter))).append(". ").append(entry.getKey()).append("\n");

                    if (counter == 10)
                        break;

                    counter++;

                }

                player.sendMessage(StringUtils.fmt(String.valueOf(message)));

            } else
                player.sendMessage(StringUtils.fmt("&cInvalid format, try: /g top"));

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

            if (ap.hasExactly(1)) {

                YamlConfiguration db = Gang.GangDatabase.getYml();
                Gang gang = null;
                for (String gangId : db.getKeys(false)) {

                    if (db.getString(gangId + ".name").equalsIgnoreCase(ap.get(1))) {
                        gang = new Gang(UUID.fromString(gangId));
                        break;
                    }

                }

                if (gang == null)
                    player.sendMessage(StringUtils.fmt("&cThere was no gang listed under the name \"" + ap.get(1) + "\"."));
                else {
                    StringBuilder info = new StringBuilder(infoHeader.replace("%gang%", gang.getName()));
                    info.append("\n&8Leader: &6").append(Bukkit.getOfflinePlayer(gang.getLeader()).getName());
                    info.append("\n\n&8Power: &6").append(gang.getPower()).append("\n");
                    info.append("&8Members: ");
                    int counter = 1;
                    for (String member : gang.getMembers()) {
                        info.append("&e").append(Bukkit.getOfflinePlayer(UUID.fromString(member)).getName()).append(", ");
                        if (counter != 1 && counter % 3 == 0)
                            info.append("\n");
                        counter++;
                    }
                    player.sendMessage(StringUtils.fmt(String.valueOf(info.substring(0, info.length() - 2))));
                }

            } else
                player.sendMessage(StringUtils.fmt("&cInvalid arguments, please enter only one gang name."));


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

            if (ap.hasExactly(1)) {

                if (!GangMember.hasGang(player.getUniqueId())) {

                    if (!Gang.exists(ap.get(1))) {
                        Gang gang = new Gang(plugin);
                        gang.create(ap.get(1), player.getUniqueId());
                    } else
                        player.sendMessage(StringUtils.fmt("&cA gang with that name already exists."));

                } else
                    player.sendMessage(StringUtils.fmt("&cYou must leave your current gang in order to do that!"));

            } else
                player.sendMessage(StringUtils.fmt("&cInvalid format, try: /g create <name>"));

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

            if (GangMember.hasGang(player.getUniqueId())) {
                GangMember member = new GangMember(player.getUniqueId());
                if (member.isLeader()) {
                    player.sendMessage(StringUtils.fmt("&cAre you sure you want to disband your current gang?"));
                    TextComponent msg = new TextComponent(StringUtils.fmt("&e&lCONFIRM"));
                    msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/kg " + member.getGangId().toString()));
                    player.spigot().sendMessage(msg);

                } else
                    player.sendMessage(StringUtils.fmt("&cYou must be the leader of the gang to do this."));

            } else
                player.sendMessage(StringUtils.fmt("&cYou are not in a gang!"));

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
            } else if (GangMember.hasGang(player.getUniqueId())) {

                Gang gang = GangMember.getGang(player.getUniqueId());
                player.sendMessage(StringUtils.fmt("&6Teleporting home..."));
                player.teleport(gang.getHome(), PlayerTeleportEvent.TeleportCause.PLUGIN);

            } else
                player.sendMessage(StringUtils.fmt("&cYou are not in a gang!"));

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
