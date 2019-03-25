package com.phaseos.listener;

import com.phaseos.customcells.CustomCells;
import com.phaseos.gangs.GangMember;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private CustomCells plugin;

    public PlayerConnectionListener(CustomCells plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        System.out.println("Has gang: " + GangMember.hasGang(e.getPlayer().getUniqueId()));

        if (!GangMember.hasGang(e.getPlayer().getUniqueId()))
            GangMember.addMember(e.getPlayer().getUniqueId());
        else
            GangMember.setLastJoinTime(e.getPlayer().getUniqueId());

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {

        GangMember.addPlayTime(e.getPlayer().getUniqueId());

    }

}
