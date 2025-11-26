package org.fitznet.doomdns.fitznethardcore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * DatabaseManager - Handles JSON-based player data persistence
 * Migrated from YAML to JSON for easier editing and better performance
 */
@Slf4j
public class DatabaseManager {
    private static FitzNetHardcore plugin;
    private static final File folder = new File("plugins/Fitz-NetHardcore/PlayerData/");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public DatabaseManager(FitzNetHardcore plugin) {
        DatabaseManager.plugin = plugin;
        // Ensure PlayerData folder exists
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    /**
     * Create a new player data JSON file with default values
     */
    public static void createPlayerData(Player player) {
        int startingLives = plugin.getConfig().getInt("StartingLives", 1);
        PlayerData data = new PlayerData(
            player.getUniqueId(),
            player.getName(),
            startingLives
        );
        savePlayerData(data);
        log.info("Created new player data for {}", player.getName());
    }

    /**
     * Check if a player data file exists
     */
    public static boolean exists(Player player) {
        return getPlayerFile(player).exists();
    }

    /**
     * Check if a player data file exists by UUID
     */
    public static boolean exists(UUID uuid) {
        return getPlayerFile(uuid).exists();
    }

    /**
     * Load player data from JSON file
     */
    public static PlayerData loadPlayerData(Player player) {
        return loadPlayerData(player.getUniqueId());
    }

    /**
     * Load player data from JSON file by UUID
     */
    public static PlayerData loadPlayerData(UUID uuid) {
        File file = getPlayerFile(uuid);
        if (!file.exists()) {
            log.warn("Attempted to load non-existent player data: {}", uuid);
            return null;
        }

        try (FileReader reader = new FileReader(file)) {
            PlayerData data = gson.fromJson(reader, PlayerData.class);
            return data;
        } catch (IOException e) {
            log.error("Error loading player data for {}", uuid, e);
            return null;
        }
    }

    /**
     * Save player data to JSON file
     */
    public static void savePlayerData(PlayerData data) {
        File file = getPlayerFile(data.getUuid());

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            log.error("Error saving player data for {}", data.getName(), e);
        }
    }

    /**
     * Get the number of lives for a player
     */
    public static int getLives(Player player) {
        PlayerData data = loadPlayerData(player);
        return data != null ? data.getLives() : 0;
    }

    /**
     * Get the number of lives for a player by UUID
     */
    public static int getLives(UUID uuid) {
        PlayerData data = loadPlayerData(uuid);
        return data != null ? data.getLives() : 0;
    }

    /**
     * Set the number of lives for a player
     */
    public static void setLives(Player player, int lives) {
        PlayerData data = loadPlayerData(player);
        if (data != null) {
            data.setLives(lives);
            savePlayerData(data);
        }
    }

    /**
     * Get player data file path
     */
    private static File getPlayerFile(Player player) {
        return getPlayerFile(player.getUniqueId());
    }

    /**
     * Get player data file path by UUID
     */
    private static File getPlayerFile(UUID uuid) {
        return new File(folder, uuid.toString() + ".json");
    }

    /**
     * Legacy method for backward compatibility
     * @deprecated Use getLives(Player) instead
     */
    @Deprecated
    public static int getInt(Player p, String query) {
        if ("Lives".equals(query)) {
            return getLives(p);
        }
        return 0;
    }

    /**
     * Legacy method for backward compatibility
     * @deprecated Use createPlayerData(Player) instead
     */
    @Deprecated
    public static void createCustomConfig(Player player) {
        createPlayerData(player);
    }
}
