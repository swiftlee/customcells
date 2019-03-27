package com.phaseos.gangs;

import com.phaseos.customcells.CustomCells;
import com.phaseos.island.IslandHandler;
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
    private int size = 0;
    private int maximumSize = 0;
    private int minimumSize = 0;
    private int level = 1;
    private int power = 0;
    private UUID gangId = null;
    private String[] permissions = null;
    private int[] dimensions = new int[2];
    private Location home;
    private int bankSize = 0;
    private String name = "";
    private int tokens = 0;
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
     * Use this if attempting access an existing gang.
     *
     * @param gangId the gang we want to refer to.
     */
    public Gang(UUID gangId) {
        if (exists(gangId)) {
            this.gangId = gangId;
            fillFields();
        } else
            this.gangId = null;
    }

    private void reloadGangData() {
        GangDatabase db = new GangDatabase();
        db.save();
        db.load();
    }

    public boolean contains(UUID gangMember) {
        if (this.gangId == null)
            return false;
        else {
            String id = gangMember.toString();
            return gangData.getStringList(this.gangId.toString() + ".members").contains(id);
        }
    }

    public static Gang getGangFromMember(UUID gangMember) {
        String id = gangMember.toString();
        for (String gangId : gangData.getKeys(false)) {
            if (gangData.getStringList(gangId + ".members").contains(id))
                return new Gang(UUID.fromString(gangId));
        }

        return null;
    }

    public static Gang getGangFromName(String gangName) {
        for (String gangId : gangData.getKeys(false)) {
            if (gangData.getString(gangId + ".name").equalsIgnoreCase(gangName))
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
            if (gangName.equalsIgnoreCase(gangData.getString(key + ".name")))
                return true;
        }
        return false;
    }

    public static String[] getPermissionFromGroup(Member.Rank rank) {
        String rankStr = rank.toString();
        return (String[]) Arrays.stream(CustomCells.defaultPermissions).filter(perm -> perm.split("\\[")[0].equalsIgnoreCase(rankStr)).toArray();
    }

    /**
     * Creates and fills all member fields.
     *
     * @param name the name of the new gang.
     */
    public void create(String name, UUID leader) {

        if (name.matches("[^a-zA-Z0-9]") || plugin.getConfig().getStringList("gangs.bannedNames").stream().anyMatch(str -> str.equalsIgnoreCase(name))) {
            Bukkit.getPlayer(leader).sendMessage(StringUtils.fmt("&cInvalid gang name."));
            return;
        }

        this.gangId = UUID.randomUUID();
        String base = this.gangId.toString() + ".";

        this.name = name;
        gangData.set(base + "name", name);

        this.size = 1;
        gangData.set(base + "size", this.size);

        this.maximumSize = plugin.getConfig().getInt("gangs.minSize");
        gangData.set(base + "maximumSize", this.maximumSize);

        this.power = 0;
        gangData.set(base + "power", 0);

        gangData.set(base + "level", 1);

        this.minimumSize = maximumSize;
        gangData.set(base + "minimumSize", maximumSize);

        this.tokens = 0;
        gangData.set(base + "tokens", 0);

        List<String> list = new ArrayList<>();
        list.add(leader.toString());
        this.members = list;
        gangData.set(base + "members", list);

        this.leader = leader;
        gangData.set(base + "leader", leader.toString());
        Member member = new Member(leader);
        member.setRank(Member.Rank.LEADER);
        add(leader);

        List<String> gangPerms = new ArrayList<>(Arrays.asList(CustomCells.defaultPermissions));
        this.permissions = CustomCells.defaultPermissions;
        gangData.set(base + "permissions", gangPerms);

        String[] cellSize = plugin.getConfig().getStringList("cell.sizes").get(0).split("x");
        this.dimensions[0] = Integer.valueOf(cellSize[0]);
        this.dimensions[1] = Integer.valueOf(cellSize[1]);
        gangData.set(base + "cellSize", this.dimensions[0] + "x" + this.dimensions[1]);

        this.bankSize = plugin.getConfig().getIntegerList("bank.sizes").get(0);
        gangData.set(base + "bankSize", this.bankSize);

        IslandHandler islandHandler = new IslandHandler(plugin);
        islandHandler.createIsland(Bukkit.getPlayer(leader), IslandHandler.SchematicName.SMALL.getSchematicName(), gangId);

        reloadGangData();

    }

    private void fillFields() {

        String base = gangId.toString() + ".";
        this.name = gangData.getString(base + "name");
        this.size = gangData.getInt(base + "size");
        this.maximumSize = gangData.getInt(base + "maximumSize");
        this.minimumSize = gangData.getInt(base + "minimumSize");
        this.tokens = gangData.getInt(base + "tokens");
        this.members = gangData.getStringList(base + "members");
        this.power = gangData.getInt(base + "power");
        this.level = gangData.getInt(base + "level");
        this.permissions = gangData.getStringList(base + "permissions").toArray(new String[0]);
        this.home = StringUtils.parseLocation(gangData.getString(base + "home"));
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
        reloadGangData();
    }

    /**
     * Sets the players gang.
     */
    public void add(UUID memberId) {
        Member member = new Member(memberId);
        member.setGangId(gangId);
        this.members.add(memberId.toString());
        gangData.set(gangId.toString() + ".members", this.members);
        reloadGangData();
    }

    /**
     * Removes a player from the current gang instance.
     * @param memberId the memberId to remove.
     */
    public void removeMember(UUID memberId) {
        this.members.remove(memberId.toString());
        new Member(memberId).setGangId(null);
        setMembers(members);
    }

    public UUID getGangId() {
        return gangId;
    }

    public int getSize() {
        return members.size();
    }

    public int getMaximumSize() {
        return minimumSize * (level << 1);
    }

    public int getPower() {
        return power;
    }

    public int getLevel() {
        return level;
    }

    public void setPower(int power) {
        gangData.set(gangId.toString() + ".power", power);
        reloadGangData();
        this.power = power;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    public void setDimensions(int[] dimensions) {
        this.dimensions = dimensions;
        gangData.set(gangId.toString() + ".cellSize", dimensions[0] + "x" + dimensions[1]);
        reloadGangData();
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location location) {

        double x = location.getBlockX() + 0.5D;
        double y = location.getBlockY() + 1;
        double z = location.getBlockZ() + 0.5D;
        String worldName = location.getWorld().getName();

        this.home = new Location(Bukkit.getWorld(worldName), x, y, z);

        gangData.set(gangId.toString() + ".home", worldName + "," + x + "," + y + "," + z);
        reloadGangData();

    }

    public int getBankSize() {
        return bankSize;
    }

    public void setBankSize(int bankSize) {
        this.bankSize = bankSize;
        gangData.set(gangId.toString() + ".bankSize", bankSize);
        reloadGangData();
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
        gangData.set(gangId.toString() + ".leader", leader.toString());
        reloadGangData();
    }

    public void levelUp() {
        if (this.level < 5) {
            level++;
            members.stream().map(UUID::fromString).map(Bukkit::getPlayer).filter(p -> p != null && p.isOnline())
                    .forEach(p -> p.sendMessage(StringUtils.fmt("&7Your gang has just leveled up to level: &a" + level + " &7!")));
        }
    }

    public boolean canLevelUp() {
        if (this.level == 5)
            return false;

        return power >= CustomCells.gangLevels[level - 1];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        gangData.set(gangId.toString() + ".name", name);
        reloadGangData();
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
        gangData.set(gangId.toString() + ".members", members);
        reloadGangData();
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
