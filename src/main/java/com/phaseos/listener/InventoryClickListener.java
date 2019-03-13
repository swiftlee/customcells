package com.phaseos.listener;

import com.phaseos.customcells.CustomCells;
import com.phaseos.gangs.Gang;
import com.phaseos.gangs.GangMember;
import com.phaseos.utils.StringUtils;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class InventoryClickListener implements Listener {

    private CustomCells plugin;

    public InventoryClickListener(CustomCells plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        if (e.getInventory() == null || e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || e.getRawSlot() != e.getSlot()) {
            e.setCancelled(true);
            return;
        }

        String inventoryName = e.getClickedInventory().getName();
        if (inventoryName.contains("§")) {

            ItemStack currItem = e.getCurrentItem();

            if (currItem.hasItemMeta() && currItem.getItemMeta().hasDisplayName()) {

                if (inventoryName.equalsIgnoreCase(plugin.getConfig().getString("gang_gui.title"))) {

                    String itemName = currItem.getItemMeta().getDisplayName();
                    final String toCell = StringUtils.fmt(plugin.getConfig().getString("gang_gui.to_cell.name"));
                    final String upgradeCell = StringUtils.fmt(plugin.getConfig().getString("gang_gui.upgrade_cell.name"));
                    final String listCell = StringUtils.fmt(plugin.getConfig().getString("gang_gui.list_cell.name"));
                    final String permsCell = StringUtils.fmt(plugin.getConfig().getString("gang_gui.perms_cell.name"));

                    if (itemName.equals(toCell)) {
                        Gang gang = GangMember.getGang(e.getWhoClicked().getUniqueId());
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().teleport(Objects.requireNonNull(gang, "gang must not be null!").getHome(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    } else if (itemName.equals(upgradeCell)) {
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().openInventory(CustomCells.getUpgradesGui());
                    } else if (itemName.equals(listCell)) {
                        e.getWhoClicked().closeInventory();
                        Gang gang = GangMember.getGang(e.getWhoClicked().getUniqueId());
                        Inventory gui = Bukkit.createInventory(null, Objects.requireNonNull(gang, "gang must not be null when referencing gang size!").getSize() > 9 ? 18 : 9, listCell);
                        Economy econ = plugin.getEcon();

                        int counter = 0;
                        for (String uuid : Objects.requireNonNull(gang, "gang must not be null in loop!").getMembers()) {

                            UUID memberId = UUID.fromString(uuid);
                            GangMember gangMember = new GangMember(memberId);
                            ItemStack playerHead = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
                            ItemMeta playerHeadMeta = playerHead.getItemMeta();
                            playerHeadMeta.setDisplayName(Bukkit.getPlayer(memberId).getName());
                            List<String> lore = new ArrayList<>();
                            lore.add(StringUtils.fmt("&7Gang rank: &6" + StringUtils.capitalize(gangMember.getRank().toString())));
                            lore.add(StringUtils.fmt("&7Donator rank: &6" + StringUtils.capitalize(gangMember.getRank().toString())));
                            lore.add(StringUtils.fmt("&7Money: &6" + econ.getBalance((OfflinePlayer) e.getWhoClicked())));
                            lore.add(StringUtils.fmt("&7Tokens: &6" + gangMember.getTokens()));
                            lore.add(StringUtils.fmt("&7Time played: &6" + (gangMember.getTimePlayed() > 60 ? gangMember.getTimePlayed() / 3600 + "h:" + ((gangMember.getTimePlayed() % 3600) / 60) + "m" : gangMember.getTimePlayed() / 60 + "m")));
                            playerHeadMeta.setLore(lore);
                            playerHead.setItemMeta(playerHeadMeta);
                            gui.setItem(counter++, playerHead);

                        }

                        openGui((Player) e.getWhoClicked(), gui);

                    } else if (itemName.equals(permsCell)) {

                        e.getWhoClicked().closeInventory();
                        openGui((Player) e.getWhoClicked(), CustomCells.getCommandsGui());

                    }

                } else if (inventoryName.equalsIgnoreCase(plugin.getConfig().getString("upgrades_gui.title"))) {

                } else if (inventoryName.equalsIgnoreCase(plugin.getConfig().getString("cell_size.title"))) {

                } else if (inventoryName.equalsIgnoreCase(plugin.getConfig().getString("commands_gui.title"))) {

                } else if (inventoryName.equalsIgnoreCase(plugin.getConfig().getString("permissions_gui.title"))) {

                }

            }

            e.setCancelled(true);
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {

        String name = e.getInventory().getName();

        if (name.contains("§")) {

            if (name.equalsIgnoreCase(plugin.getConfig().getString("upgrades_gui.title"))) {
                openGui((Player) e.getPlayer(), CustomCells.getMainGui());
            } else if (name.equalsIgnoreCase(plugin.getConfig().getString("commands_gui.title"))) {
                openGui((Player) e.getPlayer(), CustomCells.getMainGui());
            } else if (name.matches("§eKick|§eInvite|§eLevel-ups|§ePromote|§eDemote")) {
                openGui((Player) e.getPlayer(), CustomCells.getCommandsGui());
            }

            // TODO: handle going back a page

        }

    }

    private void openGui(Player player, Inventory inventory) {
        new BukkitRunnable() {

            @Override
            public void run() {
                player.openInventory(inventory);
            }

        }.runTaskLater(plugin, 0);
    }

}
