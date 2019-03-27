package com.phaseos.gangs;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class Member {

    private static YamlConfiguration memberData = new YamlConfiguration();
    private Rank rank = null;
    private UUID memberId = null;
    private UUID gangId = null;
    private int tokens = -1;
    private int kills = -1;
    private int deaths = -1;
    private int assists = -1;
    public boolean gangChat = false;
    private Member.MemberDatabase db;
    private Set<UUID> inviteList;

    public Member(UUID memberId) {

        if (!hasGang(memberId)) {
            addMember(memberId);
            inviteList = new HashSet<>();
        } else
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
        //TODO: fix for ACTUAL permissions, NOT default permissions: use getPermissions()
        return Arrays.stream(Gang.getPermissionFromGroup(rank)).anyMatch(current -> current.equalsIgnoreCase(perm));
    }

    public Set<UUID> getInviteList() {
        return inviteList;
    }

    public void clearInviteList() {
        inviteList.clear();
    }

    public static boolean hasGang(UUID memberId) {

        YamlConfiguration gangDb = Gang.GangDatabase.getYml();
        boolean found = false;
        for (String key : gangDb.getKeys(false)) {
            for (String member : gangDb.getStringList(key + ".members")) {
                if (UUID.fromString(member).equals(memberId)) {
                    found = true;
                    break;
                }
            }
        }
        return found;

    }

    public static void setLastJoinTime(UUID memberId) {
        memberData.set(memberId.toString() + ".lastJoin", timeToSeconds(System.currentTimeMillis()));
        MemberDatabase database = new MemberDatabase();
        database.save();
        database.load();
    }

    public static long getLastJoinTime(UUID memberId) { return memberData.getLong(memberId.toString() + ".lastJoin"); }

    public static void addPlayTime(UUID memberId) {

        long totalTime = memberData.getLong(memberId.toString() + ".playTime");
        memberData.set(memberId.toString() + ".playTime", totalTime + (timeToSeconds(System.currentTimeMillis()) - getLastJoinTime(memberId)));
        MemberDatabase database = new MemberDatabase();
        database.save();
        database.load();
    }

    public int getTimePlayed() {
        return memberData.getInt(memberId.toString() + ".playTime");
    }

    public static Gang getGang(UUID playerId) {

        Member member = new Member(playerId);

        if (member.gangId != null)
            return new Gang(member.gangId);

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
        memberData.set(base + "invites", new ArrayList<String>());
        memberData.set(base + "gangId", " ");
        memberData.set(base + "tokens", 0);
        memberData.set(base + "combat.kills", 0);
        memberData.set(base + "combat.deaths", 0);
        memberData.set(base + "combat.assists", 0);
        MemberDatabase database = new MemberDatabase();
        database.save();
        database.load();

    }

    public void addToInviteList(UUID gangId) {
        inviteList.add(gangId);
        String id = memberId.toString();
        memberData.set(id + ".invites", inviteList.stream().map(UUID::toString).collect(Collectors.toCollection(ArrayList::new)));
        reloadMemberData();
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
        String id = uuid.toString();

        this.rank = Rank.valueOf(memberData.getString(id + ".rank").toUpperCase());
        this.memberId = uuid;
        this.inviteList = memberData.getStringList(id + ".invites").stream().map(UUID::fromString).collect(Collectors.toCollection(HashSet::new));
        this.gangId = memberData.getString(id + ".gangId").trim().equals("") ? null : UUID.fromString(memberData.getString(id + ".gangId"));
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
        reloadMemberData();
    }

    public UUID getMemberId() {
        return memberId;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
        memberData.set(memberId.toString() + ".tokens", tokens);
        reloadMemberData();
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        memberData.set(memberId.toString() + ".combat.kills", kills);
        reloadMemberData();
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        memberData.set(memberId.toString() + ".combat.deaths", deaths);
        reloadMemberData();
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
        memberData.set(memberId.toString() + ".combat.assists", assists);
        reloadMemberData();
    }

    public UUID getGangId() {
        return gangId;
    }

    public void setGangId(UUID gangId) {
        this.gangId = gangId;
        System.out.println("Setting id to: " + gangId);
        memberData.set(memberId.toString() + ".gangId", gangId == null ? " " : gangId.toString());
        reloadMemberData();
    }
    
    private void reloadMemberData() {
        db.save();
        db.load();
    }

    public enum Rank {
        RECRUIT, COMMANDER, COLEADER, LEADER;

        @Override
        public String toString() {
            if (this == RECRUIT) return "recruit";
            else if (this == COMMANDER) return "commander";
            else if (this == COLEADER) return "coleader";
            else if (this == LEADER) return "leader";
            else return "";
        }

        public boolean isLessThan(Rank rank) {
            // comparator
        }
    }

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
