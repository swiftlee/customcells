package com.phaseos.gangs;

import com.phaseos.customcells.CustomCells;
import com.phaseos.utils.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Gang {

    private static YamlConfiguration gangData = new YamlConfiguration();
    private int size = -1;
    private int power = -1;
    private UUID gangId = null;
    private String[] permissions = null;
    private int[] dimensions = new int[2];
    private Location home;
    private int bankSize = -1;
    private String name = "";
    private int tokens = -1;
    private List<String> members = null;
    private UUID leader;
    private CustomCells plugin;

    /**
     * Use this if creating a new gang.
     *
     * @param plugin the current plugin.
     */
    public Gang(CustomCells plugin) {
        this.plugin = plugin;
    }

    /**
     * Use if plugin access is necessary.
     *
     * @param plugin the current plugin.
     * @param gangId the gang we want to refer to.
     */
    public Gang(CustomCells plugin, UUID gangId) {
        this.plugin = plugin;
        if (exists(gangId)) {
            this.gangId = gangId;
            fillData();
        } else
            this.gangId = null;
    }

    /**
     * Use this if not creating a gang.
     *
     * @param gangId the gang we want to refer to.
     */
    public Gang(UUID gangId) {
        if (exists(gangId)) {
            this.gangId = gangId;
            fillData();
        } else
            this.gangId = null;
    }

    public static boolean contains(UUID gangMember) {
        String id = gangMember.toString();
        for (String gangId : gangData.getKeys(false)) {
            if (gangData.getStringList(gangId + ".members").contains(id))
                return true;
        }

        return false;
    }

    public static Gang getGangFromMember(UUID gangMember) {

        String id = gangMember.toString();
        for (String gangId : gangData.getKeys(false)) {
            if (gangData.getStringList(gangId + ".members").contains(id))
                return new Gang(UUID.fromString(gangId));
        }

        return null;

    }

    public static Gang getGangFromId(UUID gangId) {
        if (exists(gangId))
            return new Gang(gangId);
        else
            return null;
    }

    public static boolean exists(UUID gangId) {

        return gangData.contains(gangId.toString());

    }

    public static boolean exists(String gangName) {
        for (String key : gangData.getKeys(true)) {
            if (key.equals(gangName))
                return true;
        }
        return false;
    }

    public static String[] getPermissionFromGroup(GangMember.Rank rank) {
        String rankStr = rank.toString();
        return (String[]) Arrays.stream(CustomCells.defaultPermissions).filter(perm -> perm.split("\\[")[0].equalsIgnoreCase(rankStr)).toArray();
    }

    /**
     * Creates and fills all member fields.
     *
     * @param name the name of the new gang.
     */
    public void create(String name, UUID leader) {

        if (!name.matches("[^a-zA-Z0-9]") || plugin.getConfig().getStringList("gangs.bannedNames").stream().anyMatch(str -> str.equalsIgnoreCase(name))) {
            Bukkit.getPlayer(leader).sendMessage(StringUtils.fmt("&cInvalid gang name."));
            return;
        }

        String base = UUID.randomUUID().toString() + ".";

        this.name = name;
        gangData.set(base + "name", name);

        this.size = plugin.getConfig().getInt("gangs.minSize");
        gangData.set(base + "size", this.size);

        this.power = 0;
        gangData.set(base + "power", 0);

        this.tokens = 0;
        gangData.set(base + "tokens", 0);

        List<String> list = new ArrayList<>();
        list.add(leader.toString());
        this.members = list;
        gangData.set(base + "members", list);

        this.leader = leader;
        gangData.set(base + "leader", leader.toString());

        List<String> gangPerms = new ArrayList<>(Arrays.asList(CustomCells.defaultPermissions));
        this.permissions = CustomCells.defaultPermissions;
        gangData.set(base + "permissions", gangPerms);

        String[] cellSize = plugin.getConfig().getStringList("cell.sizes").get(0).split("x");
        this.dimensions[0] = Integer.valueOf(cellSize[0]);
        this.dimensions[1] = Integer.valueOf(cellSize[1]);
        gangData.set(base + "cellSize", this.dimensions[0] + "x" + this.dimensions[1]);

        this.bankSize = plugin.getConfig().getIntegerList("bank.sizes").get(0);
        gangData.set(base + "bankSize", this.bankSize);

        GangDatabase db = new GangDatabase();
        db.save();
        db.load();

    }

    private void fillData() {

        String base = gangId.toString() + ".";
        this.name = gangData.getString(base + "name");
        this.size = gangData.getInt(base + "size");
        this.tokens = gangData.getInt(base + "tokens");
        this.members = gangData.getStringList(base + "members");
        this.power = gangData.getInt(base + "power");
        this.permissions = (String[]) gangData.getStringList(base + "permissions").toArray();
        String[] dimensionStr = gangData.getString(base + "cellSize").split("x");
        for (int i = 0; i < dimensionStr.length; i++)
            this.dimensions[i] = Integer.valueOf(dimensionStr[i]);
        this.bankSize = gangData.getInt(base + "bankSize");
        this.leader = UUID.fromString(gangData.getString(base + "leader"));

    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        gangData.set(gangId.toString() + ".permissions", permissions);
        this.permissions = permissions;
        GangDatabase db = new GangDatabase();
        db.save();
        db.load();
    }

    public int getSize() {
        return members.size();
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        gangData.set(gangId.toString() + ".power", power);
        GangDatabase db = new GangDatabase();
        db.save();
        db.load();
        this.power = power;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    public void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
        gangData.set(gangId.toString() + ".cellSize", dimensions[0] + "x" + dimensions[1]);
        GangDatabase db = new GangDatabase();
        db.save();
        db.load();
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public int getBankSize() {
        return bankSize;
    }

    public void setBankSize(int bankSize) {
        this.bankSize = bankSize;
        gangData.set(gangId.toString() + ".bankSize", bankSize);
        GangDatabase db = new GangDatabase();
        db.save();
        db.load();
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
        gangData.set(gangId.toString() + ".leader", leader.toString());
        GangDatabase db = new GangDatabase();
        db.save();
        db.load();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        gangData.set(gangId.toString() + ".name", name);
        GangDatabase db = new GangDatabase();
        db.save();
        db.load();
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
        gangData.set(gangId.toString() + ".members", members);
        GangDatabase db = new GangDatabase();
        db.save();
        db.load();
    }

    public static class GangDatabase {


        public static YamlConfiguration getYml() {
            return gangData;
        }

        public void save() {

            try {
                gangData.save("plugins/customcells/gangs.yml");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private boolean exists() {

            File f = new File("plugins/customcells/gangs.yml");

            return f.exists() && !f.isDirectory();

        }

        public void load() {

            if (gangData == null) {
                gangData = new YamlConfiguration();
            }

            try {
                gangData.load("plugins/customcells/gangs.yml");
            } catch (FileNotFoundException e1) {
                save();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
