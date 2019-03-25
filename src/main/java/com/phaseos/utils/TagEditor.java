package com.phaseos.utils;

/*************************************************************************
 *
 * J&M CONFIDENTIAL - @author Jon - 03/23/2019 | 11:10
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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TagEditor {

    enum TeamAction {
        CREATE, DESTROY, UPDATE
    }

    private static Team team;
    private static Scoreboard scoreboard;

    public static void setPlayerName(Player player, String prefix, String suffix, TeamAction action) {
        if (player.getScoreboard() == null || prefix == null || suffix == null || action == null)
            return;

        scoreboard = player.getScoreboard();

        if (scoreboard.getTeam(player.getName()) == null) {
            scoreboard.registerNewTeam(player.getName());
        }

        team = scoreboard.getTeam(player.getName());
        team.setPrefix(Color(prefix));
        team.setSuffix(Color(suffix));
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);

        switch (action) {
            case CREATE:
                team.addEntry(player.getName());
                break;
            case UPDATE:
                team.unregister();
                scoreboard.registerNewTeam(player.getName());
                team = scoreboard.getTeam(player.getName());
                team.setPrefix(Color(prefix));
                team.setSuffix(Color(suffix));
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
                team.addEntry(player.getName());
                break;
            case DESTROY:
                team.unregister();
                break;
        }
    }

    private static String Color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

}
