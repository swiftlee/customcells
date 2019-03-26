package com.phaseos.listener;

import com.phaseos.customcells.CustomCells;
import com.phaseos.gangs.Member;
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

        System.out.println("Has gang: " + Member.hasGang(e.getPlayer().getUniqueId()));

        if (!Member.hasGang(e.getPlayer().getUniqueId()))
            Member.addMember(e.getPlayer().getUniqueId());
        else
            Member.setLastJoinTime(e.getPlayer().getUniqueId());

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {

        Member.addPlayTime(e.getPlayer().getUniqueId());

    }

}
