package com.phaseos.customcells;

import com.phaseos.economy.GangEconomy;
import com.phaseos.gangs.Gang;
import com.phaseos.gangs.GangMember;
import com.phaseos.listener.PlayerConnectionListener;
import com.phaseos.utils.Gui;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class CustomCells extends JavaPlugin {

    private GangEconomy economy = null;
    public static String[] defaultPermissions;
    private static Inventory mainGui;
    private static Inventory upgradesGui;
    private static Inventory commandsGui;

    @Override
    public void onEnable() {
        System.out.println("\033[0;32m\n\n ______   __  __     ______     ______     ______     ______     ______   \n" +
                "/\\  == \\ /\\ \\_\\ \\   /\\  __ \\   /\\  ___\\   /\\  ___\\   /\\  __ \\   /\\  ___\\  \n" +
                "\\ \\  _-/ \\ \\  __ \\  \\ \\  __ \\  \\ \\___  \\  \\ \\  __\\   \\ \\ \\/\\ \\  \\ \\___  \\ \n" +
                " \\ \\_\\    \\ \\_\\ \\_\\  \\ \\_\\ \\_\\  \\/\\_____\\  \\ \\_____\\  \\ \\_____\\  \\/\\_____\\\n" +
                "  \\/_/     \\/_/\\/_/   \\/_/\\/_/   \\/_____/   \\/_____/   \\/_____/   \\/_____/\n" +
                "\n\033[0;37m[\033[0;31mCustomCells v" + getClass().getPackage().getImplementationVersion() + " written by Jonathan Conlin. Software and source code are licensed and require permission for editing.\033[0;37m]\n\n\033[0m");

        getEconomy().setup();
        Gang.GangDatabase gangDb = new Gang.GangDatabase();
        gangDb.save();
        gangDb.load();
        GangMember.MemberDatabase memberDb = new GangMember.MemberDatabase();
        memberDb.save();
        memberDb.load();

        File file = new File("plugins/customcells/config.yml");

        if (file.exists())
            reloadConfig();
        else
            saveDefaultConfig();

        CustomCells.defaultPermissions = new String[] {
            "RECRUIT[]", "COMMANDER[invite,redeem]", "COLEADER[kick,invite,redeem,promote,demote]", "LEADER[kick,invite,level,redeem,promote,demote]"
        };

        Bukkit.getPluginManager().registerEvents(new PlayerConnectionListener(this), this);

        Gui gui = new Gui(this);
        mainGui = gui.mainGui();
        upgradesGui = gui.upgradesGui();
        commandsGui = gui.commandsGui();

    }

    @Override
    public void onDisable() {
        getEconomy().save();
        Gang.GangDatabase db = new Gang.GangDatabase();
        db.save();
    }

    public static Inventory getMainGui() {
        return mainGui;
    }

    public static Inventory getUpgradesGui() {
        return upgradesGui;
    }

    public static Inventory getCommandsGui() {
        return commandsGui;
    }

    public GangEconomy getEconomy() {
        return economy == null ? economy = new GangEconomy() : economy;
    }
}
