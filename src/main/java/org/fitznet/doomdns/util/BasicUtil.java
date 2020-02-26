package org.fitznet.doomdns.util;

import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.fitznet.doomdns.fitznethardcore.DatabaseManager;
import org.fitznet.doomdns.fitznethardcore.Logger;

/**
 * Basic Utilities Class
 */
public class BasicUtil {
    /**
     * Adds a singluar life to a player
     * 
     * @param p Player to add life to
     */
    public static void addLife(Player p) {
        FileConfiguration config = DatabaseManager.getPlayerFileConfiguration(p);
        config.set("Lives", config.getInt("Lives") + 1);
        try {
            config.save(DatabaseManager.getPlayerFile(p));
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logError(e.getMessage());
            Logger.logError("Issue adding player life.");
        }
    }
    /**
     * Sets the players lives to the given argument
     * 
     * @param p Player to set lives for
     * @param lives Amount of lives to set to
     */
    public static void setLives(Player p, int lives){
        FileConfiguration config = DatabaseManager.getPlayerFileConfiguration(p);
        config.set("Lives", lives);
        try {
            config.save(DatabaseManager.getPlayerFile(p));
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logError(e.getMessage());
            Logger.logError("Issue setting player life.");
        }  
    }
    /**
     * Removes a singular life from a player
     * 
     * @param p Player to remove one life from
     */
    public static void removeLife(Player p){
        FileConfiguration config = DatabaseManager.getPlayerFileConfiguration(p);
        config.set("Lives", config.getInt("Lives") - 1);
        try {
            config.save(DatabaseManager.getPlayerFile(p));
        } catch (IOException e) {
            e.printStackTrace();
            Logger.logError(e.getMessage());
            Logger.logError("Issue setting player life.");
        }  
    }
    public static int getPlayerLives(Player p){
        return DatabaseManager.getInt(p, "Lives");
    }

    // public static String getIngameTime(FitzNetHardcore plugin){
        
    // }
}