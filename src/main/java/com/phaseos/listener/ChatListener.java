package com.phaseos.listener;

import com.phaseos.customcells.CustomCells;
import com.phaseos.gangs.Member;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Objects;
import java.util.UUID;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 03/23/2019 | 10:42
 * __________________
 *
 *  [2016] J&M Plugin Development 
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of J&M Plugin Development and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to J&M Plugin Development
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from J&M Plugin Development.
 */
public class ChatListener implements Listener {

    private CustomCells plugin;

    public ChatListener(CustomCells plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Member member = new Member(e.getPlayer().getUniqueId());
        if (member.gangChat) {
            if (member.getGangId() == null)
                return;

            for (String uuid : Objects.requireNonNull(Member.getGang(member.getGangId()), "gang must not be null").getMembers()) {
                UUID id = UUID.fromString(uuid);
                Bukkit.getPlayer(id).sendMessage("&7(%cellgangss_name%) %player% » " + e.getMessage());
            }

        }

    }

}
