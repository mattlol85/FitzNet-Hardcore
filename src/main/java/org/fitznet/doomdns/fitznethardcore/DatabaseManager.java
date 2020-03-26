package org.fitznet.doomdns.fitznethardcore;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class DatabaseManager {
    static private FitzNetHardcore plugin;
    static private final File folder = new File("plugins/Fitz-NetHardcore/PlayerData//");

    public DatabaseManager(FitzNetHardcore plugin) {
        DatabaseManager.plugin = plugin;
    }


    /**
     * Create a new playerdata.yml file that will store
     * information about the player to the plugin and
     * store it over time.
     * <p>
     * Default values that are first given to player can be changed here.
     */
    public static void createCustomConfig(Player player) {
        FileConfiguration configFile = null;
        File playerFile = new File(folder , player.getUniqueId().toString()+ ".yml");
        try {
            Logger.logDebug(folder + player.getUniqueId().toString()+".yml");
            playerFile.createNewFile();
            configFile = YamlConfiguration.loadConfiguration(playerFile);
            configFile.addDefault("Name", player.getDisplayName());
            configFile.addDefault("Lives", plugin.getConfig().get("StartingLives"));
            configFile.addDefault("RegenTimer", "00:00");
            configFile.addDefault("Invincible", false);
            configFile.options().copyDefaults(true);
            configFile.save(playerFile);
        } catch (IOException e) {
            Logger.logError(e.getMessage());
            // e.printStackTrace();
            Logger.logError("Error opening/writing player config!");
        }
    }
    /**
     * Check if a playerfile was already created
     */
    public static boolean exists(Player player) {
        File playerFile = new File(folder, player.getUniqueId().toString());
        if (playerFile.exists())
            return true;
        else
            return false;
    }

    //          Getters & Setters
    public static String getString(Player p, String query){
        FileConfiguration config = getPlayerFileConfiguration(p);
        return config.getString(query);

    }
    public static int getInt(Player p, String query){
        FileConfiguration config = getPlayerFileConfiguration(p);
        return config.getInt(query);
    }
    public static boolean getBoolean(Player p, String query){
        FileConfiguration config = getPlayerFileConfiguration(p);
        return config.getBoolean(query);
    }
    /**
     * returns the player config file
     * @param p Player to get config file from
     * @return requested player configuration file
     */
    public static FileConfiguration getPlayerFileConfiguration(Player p) {
        File playerFile = getPlayerFile(p);
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        return config;
    }
    /**
     * 
     */
    public static File getPlayerFile(Player p){
        return new File(folder, p.getUniqueId().toString() + ".yml");
    }
}
