package org.fitznet.doomdns.util;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.fitznet.doomdns.fitznethardcore.DatabaseManager;

/**
 * Basic Utilities Class
 */
@Slf4j
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
            log.error(e.getMessage());
            log.error("Issue adding player life.");
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
            log.error("Issue setting player life.", e);
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
            log.error("Issue removing player life.", e);
        }
    }
    public static int getPlayerLives(Player p){
        return DatabaseManager.getInt(p, "Lives");
    }

}