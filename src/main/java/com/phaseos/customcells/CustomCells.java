package com.phaseos.customcells;

import com.phaseos.cmds.GangCommand;
import com.phaseos.command.Commands;
import com.phaseos.gangs.Gang;
import com.phaseos.gangs.Member;
import com.phaseos.listener.InventoryClickListener;
import com.phaseos.listener.PlayerConnectionListener;
import com.phaseos.schematic.SchematicManager;
import com.phaseos.utils.Gui;
import com.phaseos.utils.StringUtils;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

public final class CustomCells extends JavaPlugin {

    public static String[] defaultPermissions;
    public static int[] gangLevels;
    private static Inventory mainGui;
    private static Inventory upgradesGui;
    private static Inventory commandsGui;
    private Commands commands;
    private Economy econ = null;
    private SchematicManager schematicManager;

    public static Inventory getMainGui() {
        return mainGui;
    }

    public static Inventory getUpgradesGui() {
        return upgradesGui;
    }

    public static Inventory getCommandsGui() {
        return commandsGui;
    }

    @Override
    public void onEnable() {
        System.out.println("\033[0;32m\n\n ______   __  __     ______     ______     ______     ______     ______   \n" +
                "/\\  == \\ /\\ \\_\\ \\   /\\  __ \\   /\\  ___\\   /\\  ___\\   /\\  __ \\   /\\  ___\\  \n" +
                "\\ \\  _-/ \\ \\  __ \\  \\ \\  __ \\  \\ \\___  \\  \\ \\  __\\   \\ \\ \\/\\ \\  \\ \\___  \\ \n" +
                " \\ \\_\\    \\ \\_\\ \\_\\  \\ \\_\\ \\_\\  \\/\\_____\\  \\ \\_____\\  \\ \\_____\\  \\/\\_____\\\n" +
                "  \\/_/     \\/_/\\/_/   \\/_/\\/_/   \\/_____/   \\/_____/   \\/_____/   \\/_____/\n" +
                "\n\033[0;37m[\033[0;31mCustomCells v" + getClass().getPackage().getImplementationVersion() + " written by Jonathan Conlin. Software and source code are licensed and require permission for editing.\033[0;37m]\n\n\033[0m");

        if (!setupEconomy()) {

            Logger.getLogger("Minecraft").severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;

        }

        /**
         * Save resources and necessary YMLs
         */
        Gang.GangDatabase gangDb = new Gang.GangDatabase();
        gangDb.load();
        Member.MemberDatabase memberDb = new Member.MemberDatabase();
        memberDb.load();


        Arrays.stream(SchematicPath.values()).forEach(value -> {
            File f = new File(value.getPath());
            if (!f.exists())
                this.saveResource(value.getPath(), false);
        });

        File file = new File("plugins/customcells/config.yml");

        if (file.exists())
            reloadConfig();
        else
            saveDefaultConfig();

        gangLevels = new int[]{
                getConfig().getInt("gangs.levels.2"),
                getConfig().getInt("gangs.levels.3"),
                getConfig().getInt("gangs.levels.4"),
                getConfig().getInt("gangs.levels.5")
        };

        /**
         * Setup default permissions for new gangs.
         */
        CustomCells.defaultPermissions = new String[]{
                "RECRUIT[]", "COMMANDER[invite,redeem]", "COLEADER[kick,invite,redeem,promote,demote]", "LEADER[kick,invite,level,redeem,promote,demote]"
        };

        /**
         * Listener registry.
         */
        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);

        /**
         * Setup GUIs for upgrades.
         */
        Gui gui = new Gui(this);
        mainGui = gui.mainGui();
        upgradesGui = gui.upgradesGui();
        commandsGui = gui.commandsGui();

        this.schematicManager = new SchematicManager(this);
        commands = new Commands(this).setErrorMessages(StringUtils.fmt("&6CustomCells &8» &cThat player does not exist!"),
                StringUtils.fmt("&6CustomCells &8» &cThat's not a number"),
                StringUtils.fmt("&6CustomCells &8» &cYou don't have any permissions to this.")).registerCommand(new GangCommand(this)).registerCommand(new GangCommand.KillGangCommand());

    }

    @Override
    public void onDisable() {
        Gang.GangDatabase db = new Gang.GangDatabase();
        Member.MemberDatabase memberDb = new Member.MemberDatabase();
        db.save();
        memberDb.save();

    }

    public Economy getEconomy() {
        return econ;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public SchematicManager getSchematicManager() {
        return schematicManager;
    }

    private enum SchematicPath {
        _15X15, _25X25, _35X35, _45X45;

        public String getPath() {
            if (this == _15X15) return "schematics/15x15_house.schematic";
            else if (this == _25X25) return "schematics/25x25_house.schematic";
            else if (this == _35X35) return "schematics/35x35_house.schematic";
            else if (this == _45X45) return "schematics/45x45_house.schematic";
            else return "";
        }

    }
}
