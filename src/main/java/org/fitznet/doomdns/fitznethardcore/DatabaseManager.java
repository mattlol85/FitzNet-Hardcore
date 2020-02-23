package org.fitznet.doomdns.fitznethardcore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DatabaseManager {
    FitzNetHardcore plugin;
    static final File folder = new File("plugins/Fitz-NetHardcore/PlayerData//");
    ArrayList<JSONObject> playerJsonFiles = new ArrayList<>();

    public DatabaseManager(FitzNetHardcore plugin) {
        this.plugin = plugin;
    }

    public void writeDatabase() {

    }

    public void printDatabase() {

    }
    public static void setup(){
        //Check for first time start up
        firstRun();
    }

    public static void createCustomConfig(Player player) {
        FileConfiguration configFile = null;
        File playerFile = new File(folder , player.getUniqueId().toString()+ ".yml");
        try {
            Logger.logDebug(folder + player.getUniqueId().toString()+".yml");
            playerFile.createNewFile();
            configFile = YamlConfiguration.loadConfiguration(playerFile);
            configFile.addDefault("Name", player.getDisplayName());
            configFile.addDefault("Lives", 1);
            configFile.addDefault("RegenTimer", "00:00");
            configFile.save(playerFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Logger.logError(e.getMessage());
            // e.printStackTrace();
            Logger.logError("Error opening/writing player config!");
        }
    }

    public static boolean exists(Player player) {
        File playerFile = new File(folder, player.getUniqueId().toString());
        if (playerFile.exists())
            return true;
        else
            return false;
    }

    public static void firstRun() {
        
    }
}
