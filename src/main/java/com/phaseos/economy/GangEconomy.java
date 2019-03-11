package com.phaseos.economy;

import com.phaseos.gangs.GangMember;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileNotFoundException;

public class GangEconomy {

    public void setup() {
        EconomyDatabase db = new EconomyDatabase();
        db.save();
    }

    public void save() {
        setup();
    }

    public static YamlConfiguration economyData = new YamlConfiguration();

    public static class EconomyDatabase {

        public static YamlConfiguration getYml() {
            return economyData;
        }

        public void save() {

            try {
                economyData.save("plugins/customcells/economydata.yml");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private boolean exists() {

            File f = new File("plugins/customcells/economydata.yml");

            return f.exists() && !f.isDirectory();

        }

        public void load() {

            if (economyData == null) {
                economyData = new YamlConfiguration();
            }

            try {
                economyData.load("plugins/customcells/economydata.yml");
            } catch (FileNotFoundException e1) {
                save();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void deposit(GangMember player, int amount) {

        player.setTokens(player.getTokens() + amount);

    }

    public void withdraw(GangMember player, int amount) {

        if (player.getTokens() - amount < 0) {

            player.setTokens(0);

        }

    }

}
