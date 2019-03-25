package com.phaseos.island;

import com.boydti.fawe.object.schematic.Schematic;
import com.phaseos.customcells.CustomCells;
import com.phaseos.gangs.Gang;
import com.phaseos.schematic.IslandSchematic;
import com.sk89q.worldedit.bukkit.BukkitWorld;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Objects;
import java.util.UUID;

public class IslandHandler {

    //The water level of the world.
    public static final int SCHEMATIC_BASE = 108;
    private CustomCells plugin;

    public IslandHandler(CustomCells plugin) {
        this.plugin = plugin;
    }

    public Pair getOpenPosition() {

        Pair homePos = new Pair();

        for (String id : Gang.GangDatabase.getYml().getKeys(false)) {
            UUID gangId = UUID.fromString(id);
            Location home = Objects.requireNonNull(Gang.getGangFromId(gangId), "gang must not be null when getting home").getHome();
            if (home == null)
                continue;

            //check pos x
            if (home.getWorld().getBlockAt(home.add(28, 0, 0)).getType() == Material.AIR) {
                homePos.setX((int) (home.getX() + 28));
                homePos.setZ((int) home.getZ());
                //check neg x
            } else if (home.getWorld().getBlockAt(home.subtract(28, 0, 0)).getType() == Material.AIR) {
                homePos.setX((int) (home.getX() - 28));
                homePos.setZ((int) home.getZ());
                //check pos z
            } else if (home.getWorld().getBlockAt(home.add(0, 0, 28)).getType() == Material.AIR) {
                homePos.setX((int) (home.getX()));
                homePos.setZ((int) home.getZ() + 28);
                //check neg z
            } else if (home.getWorld().getBlockAt(home.subtract(0, 0, 28)).getType() == Material.AIR) {
                homePos.setX((int) (home.getX()));
                homePos.setZ((int) home.getZ() - 28);
            }

        }

        if (homePos.x == null && homePos.z == null) {
            homePos.setX(0);
            homePos.setZ(0);
        }

        return homePos;

    }

    public void createIsland(Player player, String schematicName, UUID gangId) {

        IslandSchematic islandSchematic = plugin.getSchematicManager().getSchematic(schematicName);
        if (islandSchematic != null) {
            System.out.println("This schematic was not null!");
            Pair openPosition = getOpenPosition();
            Gang gang = new Gang(plugin, gangId);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Schematic schematic = islandSchematic.getSchematic();
                int chunkWidth = 48;
                // min position of future island
                Location loc = new Location(Bukkit.getWorld("islandWorld"), openPosition.getX(), SCHEMATIC_BASE, openPosition.getZ());
                // real center of future island
                loc.add(chunkWidth / 2, 0, chunkWidth / 2);
                com.sk89q.worldedit.Vector dim = Objects.requireNonNull(schematic.getClipboard(), "clipboard must not be null!").getDimensions();
                // due to schematic
                loc.subtract(dim.getBlockX() / 2, 0, dim.getBlockZ() / 2); // TODO: leftoff here
                // paste the schematic!
                schematic.paste(new BukkitWorld(loc.getWorld()), new com.sk89q.worldedit.Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), true, false, null);

                System.out.println("PASTE >> x: " + loc.getBlockX() + " y: " + loc.getBlockY() + " z: " + loc.getBlockZ());
                // gang home setup
                gang.setHome(loc);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    loc.getBlock().setType(Material.AIR);
                    player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);

                }, 2);
            });
        }
    }

    public enum SchematicName {

        SMALL, MEDIUM, LARGE, XL;

        /**
         * @return The schematic name with corresponding file extension.
         */
        public String getSchematicName() {

            if (this == SMALL) return "15x15_house.schematic";
            else if (this == MEDIUM) return "25x25_house.schematic";
            else if (this == LARGE) return "35x35_house.schematic";
            else if (this == XL) return "45x45_house.schematic";
            else return "";

        }

    }

    private class Pair {

        private Integer x;
        private Integer z;

        private Pair(int x, int y) {
            this.x = x;
            this.z = y;
        }

        private Pair() {
        }

        public int getX() {
            if (x != null)
                return x;
            else
                throw new NullPointerException("The location for x was never set.");
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getZ() {
            if (z != null)
                return z;
            else
                throw new NullPointerException("The location for z was never set.");
        }

        public void setZ(int z) {
            this.z = z;
        }
    }

}
