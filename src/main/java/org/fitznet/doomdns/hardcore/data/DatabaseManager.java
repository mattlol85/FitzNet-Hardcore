package org.fitznet.doomdns.hardcore.data;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.fitznet.doomdns.hardcore.FitzNetHardcore;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DatabaseManager - Handles Bukkit-native YAML player data persistence.
 */
@Slf4j
public class DatabaseManager {
    private static DatabaseManager instance;
    private final FitzNetHardcore plugin;
    private final File folder;
    private final Map<UUID, Object> playerLocks = new ConcurrentHashMap<>();

    public DatabaseManager(FitzNetHardcore plugin) {
        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "PlayerData");
        instance = this;

        // Ensure PlayerData folder exists
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    private static DatabaseManager requireInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseManager is not initialized yet");
        }
        return instance;
    }

    private Object lockFor(UUID uuid) {
        return playerLocks.computeIfAbsent(uuid, ignored -> new Object());
    }

    private static Object[] orderedLocks(DatabaseManager manager, UUID first, UUID second) {
        if (first.compareTo(second) <= 0) {
            return new Object[] { manager.lockFor(first), manager.lockFor(second) };
        }
        return new Object[] { manager.lockFor(second), manager.lockFor(first) };
    }

    /** Create a new player data file with default values. */
    public static void createPlayerData(Player player) {
        DatabaseManager manager = requireInstance();
        int startingLives = manager.plugin.getConfig().getInt("StartingLives", 1);
        PlayerData data = new PlayerData(
            player.getUniqueId(),
            player.getName(),
            startingLives
        );
        savePlayerData(data);
        log.info("Created new player data for {}", player.getName());
    }

    /** Check if a player data file exists. */
    public static boolean exists(Player player) {
        return getPlayerFile(player).exists();
    }

    /** Check if a player data file exists by UUID. */
    public static boolean exists(UUID uuid) {
        return requireInstance().getPlayerFile(uuid).exists();
    }

    /** Load player data from YAML by player instance. */
    public static PlayerData loadPlayerData(Player player) {
        return loadPlayerData(player.getUniqueId());
    }

    /** Load player data from YAML by UUID. */
    public static PlayerData loadPlayerData(UUID uuid) {
        DatabaseManager manager = requireInstance();
        File file = manager.getPlayerFile(uuid);
        if (!file.exists()) {
            log.warn("Attempted to load non-existent player data: {}", uuid);
            return null;
        }

        synchronized (manager.lockFor(uuid)) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            String uuidText = yaml.getString("uuid");
            if (uuidText == null || uuidText.isBlank()) {
                log.warn("Player data for {} is missing 'uuid'", uuid);
                return null;
            }

            return new PlayerData(
                UUID.fromString(uuidText),
                yaml.getString("name", "unknown"),
                yaml.getInt("lives", 0),
                yaml.getInt("daysAlive", 0),
                yaml.getBoolean("isSpectator", false),
                yaml.getLong("nextLifeTimestamp", System.currentTimeMillis()),
                yaml.getLong("lastDeathTime", 0L)
            );
        }
    }

    /** Save player data to YAML. */
    public static void savePlayerData(PlayerData data) {
        if (data == null || data.getUuid() == null) {
            log.warn("Attempted to save null player data or data without UUID");
            return;
        }

        DatabaseManager manager = requireInstance();
        File file = manager.getPlayerFile(data.getUuid());

        synchronized (manager.lockFor(data.getUuid())) {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.set("uuid", data.getUuid().toString());
            yaml.set("name", data.getName());
            yaml.set("lives", data.getLives());
            yaml.set("daysAlive", data.getDaysAlive());
            yaml.set("isSpectator", data.isSpectator());
            yaml.set("nextLifeTimestamp", data.getNextLifeTimestamp());
            yaml.set("lastDeathTime", data.getLastDeathTime());

            try {
                yaml.save(file);
            } catch (IOException e) {
                log.error("Error saving player data for {}", data.getName(), e);
            }
        }
    }

    /** Transfer exactly one life from sender to target atomically. */
    public static boolean transferOneLife(Player from, Player to, int maxLives) {
        DatabaseManager manager = requireInstance();
        UUID fromId = from.getUniqueId();
        UUID toId = to.getUniqueId();

        Object[] locks = orderedLocks(manager, fromId, toId);

        synchronized (locks[0]) {
            synchronized (locks[1]) {
                PlayerData fromData = loadPlayerData(fromId);
                PlayerData toData = loadPlayerData(toId);

                if (fromData == null || toData == null) {
                    return false;
                }
                if (fromData.getLives() <= 1) {
                    return false;
                }
                if (toData.getLives() >= maxLives) {
                    return false;
                }

                fromData.setLives(Math.max(0, fromData.getLives() - 1));
                toData.setLives(toData.getLives() + 1);

                savePlayerData(fromData);
                savePlayerData(toData);
                return true;
            }
        }
    }

    /** Get the number of lives for a player. */
    public static int getLives(Player player) {
        PlayerData data = loadPlayerData(player);
        return data != null ? data.getLives() : 0;
    }

    /** Get the number of lives for a player by UUID. */
    public static int getLives(UUID uuid) {
        PlayerData data = loadPlayerData(uuid);
        return data != null ? data.getLives() : 0;
    }

    /** Set the number of lives for a player. */
    public static void setLives(Player player, int lives) {
        PlayerData data = loadPlayerData(player);
        if (data != null) {
            data.setLives(lives);
            savePlayerData(data);
        }
    }

    /** Get player data file path. */
    private static File getPlayerFile(Player player) {
        return requireInstance().getPlayerFile(player.getUniqueId());
    }

    /** Get player data file path by UUID. */
    private File getPlayerFile(UUID uuid) {
        return new File(folder, uuid.toString() + ".yml");
    }

    /** Legacy helper retained for old call sites. */
    public static int getInt(Player p, String query) {
        if ("Lives".equals(query)) {
            return getLives(p);
        }
        return 0;
    }

    /** Legacy helper retained for old call sites. */
    public static void createCustomConfig(Player player) {
        createPlayerData(player);
    }
}
