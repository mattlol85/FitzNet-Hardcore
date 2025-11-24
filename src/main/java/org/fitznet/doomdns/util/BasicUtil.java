package org.fitznet.doomdns.util;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;
import org.fitznet.doomdns.fitznethardcore.DatabaseManager;
import org.fitznet.doomdns.fitznethardcore.FitzNetHardcore;
import org.fitznet.doomdns.fitznethardcore.PlayerData;

/**
 * Basic Utilities Class - Helper methods for life management
 */
@Slf4j
public class BasicUtil {

    /**
     * Adds a life to a player with cap enforcement
     * Will not exceed MaxLives configured limit
     *
     * @param p Player to add life to
     * @return true if life was added, false if at cap
     */
    public static boolean addLife(Player p) {
        return addLife(p, true);
    }

    /**
     * Adds a life to a player
     *
     * @param p Player to add life to
     * @param enforceLimit If true, respects MaxLives cap; if false, allows unlimited (admin use)
     * @return true if life was added, false if at cap (when enforcing limit)
     */
    public static boolean addLife(Player p, boolean enforceLimit) {
        PlayerData data = DatabaseManager.loadPlayerData(p);
        if (data == null) {
            log.warn("Cannot add life - player data not found for {}", p.getName());
            return false;
        }

        int maxLives = FitzNetHardcore.getInstance().getConfig().getInt("MaxLives", 3);

        if (enforceLimit && data.getLives() >= maxLives) {
            log.debug("Cannot add life to {} - already at max ({}/{})", p.getName(), data.getLives(), maxLives);
            return false;
        }

        data.setLives(data.getLives() + 1);
        DatabaseManager.savePlayerData(data);
        log.debug("Added life to {} - now has {} lives", p.getName(), data.getLives());
        return true;
    }

    /**
     * Sets the player's lives to the given amount
     *
     * @param p Player to set lives for
     * @param lives Amount of lives to set to
     */
    public static void setLives(Player p, int lives) {
        PlayerData data = DatabaseManager.loadPlayerData(p);
        if (data == null) {
            log.warn("Cannot set lives - player data not found for {}", p.getName());
            return;
        }

        data.setLives(Math.max(0, lives)); // Ensure non-negative
        DatabaseManager.savePlayerData(data);
        log.debug("Set lives for {} to {}", p.getName(), lives);
    }

    /**
     * Removes a life from a player
     * Will not go below 0
     *
     * @param p Player to remove one life from
     */
    public static void removeLife(Player p) {
        PlayerData data = DatabaseManager.loadPlayerData(p);
        if (data == null) {
            log.warn("Cannot remove life - player data not found for {}", p.getName());
            return;
        }

        data.setLives(Math.max(0, data.getLives() - 1));
        DatabaseManager.savePlayerData(data);
        log.debug("Removed life from {} - now has {} lives", p.getName(), data.getLives());
    }

    /**
     * Get the number of lives a player has
     *
     * @param p Player to check
     * @return Number of lives
     */
    public static int getPlayerLives(Player p) {
        return DatabaseManager.getLives(p);
    }

    /**
     * Check if a player is in spectator mode (dead)
     *
     * @param p Player to check
     * @return true if player is dead/spectator
     */
    public static boolean isSpectator(Player p) {
        PlayerData data = DatabaseManager.loadPlayerData(p);
        return data != null && data.isSpectator();
    }

    /**
     * Set player spectator status
     *
     * @param p Player to update
     * @param isSpectator true to mark as spectator/dead
     */
    public static void setSpectator(Player p, boolean isSpectator) {
        PlayerData data = DatabaseManager.loadPlayerData(p);
        if (data != null) {
            data.setSpectator(isSpectator);
            DatabaseManager.savePlayerData(data);
        }
    }
}