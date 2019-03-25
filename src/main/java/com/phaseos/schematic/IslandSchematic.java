package com.phaseos.schematic;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.Region;

/**
 * Created by Dennis Heckmann on 06.12.17 Copyright (c) 2017 Dennis Heckmann GitHub:
 * https://github.com/Mylogo Web: http://mylogo.me Mail: contact@mylogo.me
 */
public class IslandSchematic {

    private String name;
    private Schematic schematic;
    private org.bukkit.util.Vector spawnOffset;

    public IslandSchematic(String name, Schematic schematic) {
        this.name = name;
        this.schematic = schematic;
        Region region = schematic.getClipboard().getRegion();
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();

        System.out.println("origin: " + schematic.getClipboard().getOrigin().toString());
        System.out.println("Selection range: x[minX-maxX] y[minY-maxY] z[minZ-maxZ]\n"
                .replace("minX", String.valueOf(min.getBlockX())).replace("maxX", String.valueOf(max.getBlockX()))
                .replace("minY", String.valueOf(min.getBlockY())).replace("maxY", String.valueOf(max.getBlockY()))
                .replace("minZ", String.valueOf(min.getBlockZ())).replace("maxZ", String.valueOf(max.getBlockZ())));
        for (int i = min.getBlockX(); i <= max.getBlockX(); i++) {
            for (int j = min.getBlockY(); j <= max.getBlockY(); j++) {
                for (int k = min.getBlockZ(); k <= max.getBlockZ(); k++) {
                    BaseBlock block = schematic.getClipboard().getBlock(new Vector(i, j, k));
                    // 7 IS BEDROCK
                    if (block != null && block.getId() == 7) {
                        spawnOffset = new org.bukkit.util.Vector(i, j, k);
                        System.out.println("FOUND SPAWN!\n");
                        break;
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public Schematic getSchematic() {
        return schematic;
    }

    public org.bukkit.util.Vector getSpawnOffset() {
        return spawnOffset;
    }
}
