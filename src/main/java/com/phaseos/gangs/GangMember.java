package com.phaseos.gangs;

import com.phaseos.economy.GangEconomy;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.UUID;

public class GangMember {

    private Rank rank = null;
    private UUID memberId = null;
    private UUID gangId = null;
    private int tokens = -1;
    private int kills = -1;
    private int deaths = -1;
    private int assists = -1;
    private GangMember.MemberDatabase db;

    public GangMember(UUID memberId) {

        if (!hasGang(memberId))
            addMember(memberId);
        else
            fillFields(memberId);

        db = new MemberDatabase();

    }

    private String[] getPermissions() {
        if (hasGang(memberId)) {
            Gang gang = Gang.getGangFromId(gangId);
            if (gang != null)
                return gang.getPermissions();

        }

        return null;
    }

    public boolean hasPermission(String perm) {
        return Arrays.stream(Gang.getPermissionFromGroup(rank)).anyMatch(current -> current.equalsIgnoreCase(perm));
    }

    public static boolean hasGang(UUID memberId) {

        String id = memberId.toString();
        return memberData.contains(id) && !memberData.getString(id + ".gangId").trim().equals("");

    }

    public static void setLastJoinTime(UUID memberId) {
        memberData.set(memberId.toString() + ".lastJoin", timeToSeconds(System.currentTimeMillis()));
        MemberDatabase database = new MemberDatabase();
        database.save();
        database.load();
    }

    public static long getLastJoinTime(UUID memberId) {

        return memberData.getLong(memberId.toString() + ".lastJoin");

    }

    public static void addPlayTime(UUID memberId) {

        int totalTime = memberData.getInt(memberId.toString() + ".playTime");
        memberData.set(memberId.toString() + ".playTime", totalTime + (timeToSeconds(System.currentTimeMillis()) - getLastJoinTime(memberId)));

    }

    public boolean inGang(UUID gangId) {

        return memberData.contains(memberId.toString()) && gangId.toString().equals(memberData.getString(memberId.toString() + ".gangId"));

    }

    public static Gang getGang(UUID playerId) {

        GangMember gangMember = new GangMember(playerId);

        if (gangMember.gangId != null)
            return new Gang(gangMember.gangId);

        return null;

    }

    /**
     * This method is called when a new player joins the server.
     *
     * @param uuid is the UUID of the player.
     */
    public static void addMember(UUID uuid) {

        String id = uuid.toString();
        String base = id + ".";

        memberData.set(base + "lastJoin", timeToSeconds(System.currentTimeMillis()));
        memberData.set(base + "timePlayed", 0L);
        memberData.set(base + "rank", Rank.RECRUIT.toString());
        memberData.set(base + "gangId", "");
        memberData.set(base + "tokens", -1);
        memberData.set(base + "combat.kills", -1);
        memberData.set(base + "combat.deaths", -1);
        memberData.set(base + "combat.assists", -1);
        MemberDatabase database = new MemberDatabase();
        database.save();
        database.load();

    }

    private static long timeToSeconds(long millis) {

        return millis / 1000L;

    }

    public boolean isLeader() {
        return Rank.LEADER == rank;
    }

    /**
     * Fills all of the local fields to match the data in the YML file.
     *
     * @param uuid is the UUID of the player.
     */
    public void fillFields(UUID uuid) {

        if (!hasGang(uuid))
            return;

        String id = uuid.toString();

        this.rank = Rank.valueOf(memberData.getString(id + ".rank"));
        this.memberId = uuid;
        this.gangId = UUID.fromString(memberData.getString(id + ".gangId"));
        this.tokens = memberData.getInt(id + ".tokens");
        this.kills = memberData.getInt(id + ".combat.kills");
        this.deaths = memberData.getInt(id + ".combat.deaths");
        this.assists = memberData.getInt(id + ".combat.assists");

    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
        memberData.set(memberId.toString() + ".rank", rank.toString());
        db.save();
        db.load();
    }

    public UUID getMemberId() {
        return memberId;
    }

    public void setMemberId(UUID memberId) {
        this.memberId = memberId;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
        GangEconomy.economyData.set(memberId.toString() + ".tokens", tokens);
        GangEconomy.EconomyDatabase economyDatabase = new GangEconomy.EconomyDatabase();
        economyDatabase.save();
        economyDatabase.load();

    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        memberData.set(memberId.toString() + ".combat.kills", kills);
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        memberData.set(memberId.toString() + ".combat.deaths", deaths);
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
        memberData.set(memberId.toString() + ".combat.assists", assists);
    }

    public UUID getGangId() {
        return gangId;
    }

    public void setGangId(UUID gangId) {
        this.gangId = gangId;
        memberData.set(memberId.toString() + ".gangId", gangId.toString());
    }

    enum Rank {
        RECRUIT, COMMANDER, COLEADER, LEADER;

        @Override
        public String toString() {
            if (this == RECRUIT) return "recruit";
            else if (this == COMMANDER) return "commander";
            else if (this == COLEADER) return "coleader";
            else if (this == LEADER) return "leader";
            else return "";
        }
    }

    private static YamlConfiguration memberData = new YamlConfiguration();

    public static class MemberDatabase {


        public static YamlConfiguration getYml() {
            return memberData;
        }

        public void save() {

            try {
                memberData.save("plugins/customcells/members.yml");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private boolean exists() {

            File f = new File("plugins/customcells/members.yml");

            return f.exists() && !f.isDirectory();

        }

        public void load() {

            if (memberData == null) {
                memberData = new YamlConfiguration();
            }

            try {
                memberData.load("plugins/customcells/members.yml");
            } catch (FileNotFoundException e1) {
                save();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}
