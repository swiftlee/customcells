package com.phaseos.listener;

import com.phaseos.customcells.CustomCells;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryClickListener implements Listener {

    private CustomCells plugin;

    public InventoryClickListener(CustomCells plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        String name = e.getClickedInventory().getName();
        if (name.contains("§")) {

            if (name.equalsIgnoreCase(plugin.getConfig().getString("gang_gui.title"))) {

            } else if (name.equalsIgnoreCase(plugin.getConfig().getString("upgrades_gui.title"))) {

            } else if (name.equalsIgnoreCase(plugin.getConfig().getString("cell_size.title"))) {

            } else if (name.equalsIgnoreCase(plugin.getConfig().getString("commands_gui.title"))) {

            } else if (name.equalsIgnoreCase(plugin.getConfig().getString("permissions_gui.title"))) {

            }

            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

        String name = e.getInventory().getName();

        if (name.contains("§")) {

            if (name.equalsIgnoreCase(plugin.getConfig().getString("gang_gui.title"))) {

            } else if (name.equalsIgnoreCase(plugin.getConfig().getString("upgrades_gui.title"))) {

            } else if (name.equalsIgnoreCase(plugin.getConfig().getString("cell_size.title"))) {

            } else if (name.equalsIgnoreCase(plugin.getConfig().getString("commands_gui.title"))) {

            } else if (name.equalsIgnoreCase(plugin.getConfig().getString("permissions_gui.title"))) {

            }

            // TODO: handle going back a page

        }

    }

    private void openGui() {

    }

}
