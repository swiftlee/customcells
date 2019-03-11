package com.phaseos.utils;

import com.phaseos.customcells.CustomCells;
import com.phaseos.gangs.GangMember;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Gui {

    private CustomCells plugin;

    public Gui(CustomCells plugin) {
        this.plugin = plugin;
    }

    private static void fill(Inventory inv, ItemStack stack) {

        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR)
                inv.setItem(i, stack);
        }

    }

    /**
     * Generates the main GUI to be opened upon executing: /g
     *
     * @return the main GUI.
     */
    public Inventory mainGui() {

        Inventory gui = Bukkit.createInventory(null, 27, StringUtils.fmt(plugin.getConfig().getString("gang_gui.title")));

        ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) Color.getColor(Color.valueOf(plugin.getConfig().getString("gang_gui.pane_color"))));
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName("\0");
        paneMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        paneMeta.setLore(null);
        pane.setItemMeta(paneMeta);

        ItemStack toCell = new ItemStack(Material.valueOf(plugin.getConfig().getString("gang_gui.to_cell.material")), 1);
        ItemMeta toCellMeta = toCell.getItemMeta();
        toCellMeta.setDisplayName(StringUtils.fmt(plugin.getConfig().getString("gang_gui.to_cell.name")));
        toCell.setItemMeta(toCellMeta);

        ItemStack upgradeCell = new ItemStack(Material.valueOf(plugin.getConfig().getString("gang_gui.upgrade_cell.material")), 1);
        ItemMeta upgradeCellMeta = upgradeCell.getItemMeta();
        upgradeCellMeta.setDisplayName(StringUtils.fmt(plugin.getConfig().getString("gang_gui.upgrade_cell.name")));
        upgradeCell.setItemMeta(upgradeCellMeta);

        ItemStack listCell = new ItemStack(Material.valueOf(plugin.getConfig().getString("gang_gui.list_cell.material")), 1);
        ItemMeta listCellMeta = listCell.getItemMeta();
        listCellMeta.setDisplayName(StringUtils.fmt(plugin.getConfig().getString("gang_gui.list_cell.name")));
        listCell.setItemMeta(listCellMeta);

        ItemStack permsCell = new ItemStack(Material.valueOf(plugin.getConfig().getString("gang_gui.perms_cell.material")), 1);
        ItemMeta permsCellMeta = permsCell.getItemMeta();
        permsCellMeta.setDisplayName(StringUtils.fmt(plugin.getConfig().getString("gang_gui.perms_cell.name")));
        permsCell.setItemMeta(permsCellMeta);

        gui.setItem(plugin.getConfig().getInt("gang_gui.to_cell.slot"), toCell);
        gui.setItem(plugin.getConfig().getInt("gang_gui.upgrade_cell.slot"), upgradeCell);
        gui.setItem(plugin.getConfig().getInt("gang_gui.list_cell.slot"), listCell);
        gui.setItem(plugin.getConfig().getInt("gang_gui.perms_cell.slot"), permsCell);
        fill(gui, pane);
        return gui;
    }

    /**
     * Generates the gang cell upgrades GUI.
     *
     * @return the upgrades GUI.
     */
    public Inventory upgradesGui() {

        Inventory gui = Bukkit.createInventory(null, 27, StringUtils.fmt(plugin.getConfig().getString("upgrades_gui.title")));

        ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) Color.getColor(Color.valueOf(plugin.getConfig().getString("upgrades_gui.pane_color"))));
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName("\0");
        paneMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        paneMeta.setLore(null);
        pane.setItemMeta(paneMeta);

        ItemStack cellUpgrade = new ItemStack(Material.valueOf(plugin.getConfig().getString("upgrades_gui.cell_upgrade.material")), 1);
        ItemMeta cellUpgradeMeta = cellUpgrade.getItemMeta();
        cellUpgradeMeta.setDisplayName(StringUtils.fmt(plugin.getConfig().getString("upgrades_gui.cell_upgrade.name")));
        List<String> cellUpgradeLore = new ArrayList<>();
        plugin.getConfig().getStringList("upgrades_gui.cell_upgrade.lore").forEach(str -> cellUpgradeLore.add(StringUtils.fmt(str)));
        cellUpgradeMeta.setLore(cellUpgradeLore);
        cellUpgrade.setItemMeta(cellUpgradeMeta);

        ItemStack cellSize = new ItemStack(Material.valueOf(plugin.getConfig().getString("upgrades_gui.cell_size.material")), 1);
        ItemMeta cellSizeMeta = cellSize.getItemMeta();
        cellSizeMeta.setDisplayName(StringUtils.fmt(plugin.getConfig().getString("upgrades_gui.cell_size.name")));
        List<String> cellSizeLore = new ArrayList<>();
        plugin.getConfig().getStringList("upgrades_gui.cell_size.lore").forEach(str -> cellSizeLore.add(StringUtils.fmt(str)));
        cellSizeMeta.setLore(cellSizeLore);
        cellSize.setItemMeta(cellSizeMeta);

        ItemStack bankSize = new ItemStack(Material.valueOf(plugin.getConfig().getString("gang_gui.bank_size.material")), 1);
        ItemMeta bankSizeMeta = bankSize.getItemMeta();
        bankSizeMeta.setDisplayName(StringUtils.fmt(plugin.getConfig().getString("gang_gui.bank_size.name")));
        List<String> bankSizeLore = new ArrayList<>();
        plugin.getConfig().getStringList("upgrades_gui.bank_size.lore").forEach(str -> bankSizeLore.add(StringUtils.fmt(str)));
        bankSizeMeta.setLore(bankSizeLore);
        bankSize.setItemMeta(bankSizeMeta);

        gui.setItem(plugin.getConfig().getInt("upgrades_gui.cell_upgrade.slot"), cellUpgrade);
        gui.setItem(plugin.getConfig().getInt("upgrades_gui.cell_size.slot"), cellSize);
        gui.setItem(plugin.getConfig().getInt("upgrades_gui.bank_size.slot"), bankSize);
        fill(gui, pane);
        return gui;

    }

    /**
     * Generates the editable commands GUI.
     *
     * @return the commands GUI.
     */
    public Inventory commandsGui() {

        Inventory gui = Bukkit.createInventory(null, 9, StringUtils.fmt(plugin.getConfig().getString("commands_gui.title")));

        List<String> cmds = plugin.getConfig().getStringList("commands_gui.commands");
        String name;
        String material = plugin.getConfig().getString("commands_gui.item");
        List<String> lore;
        int counter = 0;
        for (String cmd : cmds) {

            ItemStack stack = new ItemStack(Material.valueOf(material), 1);
            name = StringUtils.fmt(cmd.split(":")[1]);

            lore = new ArrayList<>();
            lore.add(StringUtils.fmt(cmd.split(":")[2]));
            lore.add(toInvisibleLine(cmd.split(":")[0]));

            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(name);
            meta.setLore(lore);
            stack.setItemMeta(meta);

            if (counter < 3)
                gui.setItem(counter++, stack);
            else
                gui.setItem(counter++ +3, stack);
        }

        return gui;

    }

    private String toInvisibleLine(String line) {

        StringBuilder invisibleLine = new StringBuilder();

        for (char c : line.toCharArray())
            invisibleLine.append(StringUtils.fmt("&")).append(c);

        return String.valueOf(invisibleLine);
    }

}
