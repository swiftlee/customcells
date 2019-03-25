package com.phaseos.schematic;

import com.boydti.fawe.object.schematic.Schematic;
import com.phaseos.customcells.CustomCells;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dennis Heckmann on 06.12.17 Copyright (c) 2017 Dennis Heckmann GitHub:
 * https://github.com/Mylogo Web: http://mylogo.me Mail: contact@mylogo.me
 */
public class SchematicManager {

    private List<IslandSchematic> schematics;
    private CustomCells plugin;

    public SchematicManager(CustomCells plugin) {
        this.plugin = plugin;
        schematics = new ArrayList<>();

        File dir = getSchematicsDir();
        if (!dir.exists())
            dir.mkdir();

        for (File file : dir.listFiles()) {
            System.out.println("FILE:" + file);
            if (!file.getName().startsWith(".")) {
                try {
                    System.out.println("Reading schematic...");
                    Schematic schematic = ClipboardFormat.SCHEMATIC.load(file);
                    schematics.add(new IslandSchematic(file.getName(), schematic));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                System.out.println("Did not read because it starts with a point");

        }
    }

    public IslandSchematic getSchematic(String name) {
        return schematics.stream().filter(s -> s.getName().equalsIgnoreCase(name) || name.equalsIgnoreCase(s + ".schematic")).findFirst().orElse(null);
    }

    private File getSchematicsDir() {
        return new File(plugin.getDataFolder(), "schematics");
    }

}
