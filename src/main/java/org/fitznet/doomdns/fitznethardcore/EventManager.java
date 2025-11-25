package org.fitznet.doomdns.fitznethardcore;

import java.util.HashMap;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.fitznet.doomdns.util.BasicUtil;

/**
 * EventManager - Handles core player events for the lives system
 */
@Slf4j
public class EventManager implements Listener {
    private final FitzNetHardcore plugin;
    private static final HashMap<Player, LivesScheduler> timerMap = new HashMap<>();

    // Storage for player inventory/experience when they die with lives remaining
    private final HashMap<UUID, ItemStack[]> savedInventories = new HashMap<>();
    private final HashMap<UUID, ItemStack[]> savedArmor = new HashMap<>();
    private final HashMap<UUID, Integer> savedExperience = new HashMap<>();
    private final HashMap<UUID, Integer> savedExpLevels = new HashMap<>();

    public EventManager(FitzNetHardcore plugin) {
        this.plugin = plugin;
    }

    /**
     * onJoin - Handle player join events
     * Creates new player data if needed
     * Restores spectator mode for dead players
     * Starts life regeneration timer
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        log.info("{} joined the server", player.getName());

        // Check if player data exists, create if new
        if (!DatabaseManager.exists(player)) {
            DatabaseManager.createPlayerData(player);
            log.info("Created new player data for {}", player.getName());
        }

        // Restore spectator mode if player is dead
        PlayerData data = DatabaseManager.loadPlayerData(player);
        if (data != null && data.isSpectator() && data.getLives() == 0) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Component.text("You are in spectator mode. You have no lives remaining.", NamedTextColor.RED));
            player.sendMessage(Component.text("Another player can resurrect you with a diamond block!", NamedTextColor.YELLOW));
            log.info("Restored spectator mode for dead player {}", player.getName());
        }

        // Start life regeneration scheduler (Folia-compatible via LivesScheduler)
        // Amount of Minutes Until New life * One Min in ticks (1200)
        long regenTicks = plugin.getConfig().getInt("LifeRegenTime", 2) * 1200L;
        LivesScheduler scheduler = new LivesScheduler(plugin, player);
        scheduler.start(regenTicks, regenTicks);
        timerMap.put(player, scheduler);

        // Update scoreboard
        if (plugin.getScoreboardManager() != null) {
            plugin.getScoreboardManager().updatePlayer(player);
        }
    }

    /**
     * onPlayerDeath - Handle player death
     * Removes one life
     * Sets spectator mode if out of lives
     * Broadcasts death message if configured
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getPlayer();

        // Remove one life
        BasicUtil.removeLife(deadPlayer);
        int remainingLives = BasicUtil.getPlayerLives(deadPlayer);

        // Visual effect
        deadPlayer.getWorld().strikeLightningEffect(deadPlayer.getLocation());

        // Message to player
        if (remainingLives > 0) {
            deadPlayer.sendMessage(Component.text("You lost 1 life! ", NamedTextColor.RED)
                .append(Component.text("Lives remaining: " + remainingLives, NamedTextColor.YELLOW)));

            // Save inventory and experience if configured
            if (plugin.getConfig().getBoolean("KeepInventoryOnDeath", true)) {
                // Save inventory contents
                savedInventories.put(deadPlayer.getUniqueId(), deadPlayer.getInventory().getContents().clone());
                savedArmor.put(deadPlayer.getUniqueId(), deadPlayer.getInventory().getArmorContents().clone());

                // Clear drops
                event.getDrops().clear();
                event.setKeepInventory(true);

                log.debug("Saved inventory for {} to restore on respawn", deadPlayer.getName());
            }

            if (plugin.getConfig().getBoolean("KeepExperienceOnDeath", true)) {
                // Save experience
                savedExperience.put(deadPlayer.getUniqueId(), deadPlayer.getTotalExperience());
                savedExpLevels.put(deadPlayer.getUniqueId(), deadPlayer.getLevel());

                // Prevent experience drop
                event.setDroppedExp(0);
                event.setKeepLevel(true);

                log.debug("Saved experience for {} to restore on respawn", deadPlayer.getName());
            }
        } else {
            deadPlayer.sendMessage(Component.text("YOU HAVE NO LIVES REMAINING!", NamedTextColor.DARK_RED, TextDecoration.BOLD));
            deadPlayer.sendMessage(Component.text("You are now in spectator mode until someone resurrects you.", NamedTextColor.RED));
        }

        // Handle running out of lives
        if (remainingLives == 0) {
            // Set spectator mode instead of banning
            BasicUtil.setSpectator(deadPlayer, true);

            // Delay game mode change to after respawn (Folia-compatible)
            SchedulerUtil.runEntityTaskLater(plugin, deadPlayer, () -> {
                deadPlayer.setGameMode(GameMode.SPECTATOR);
                log.info("{} ran out of lives and is now in spectator mode", deadPlayer.getName());
            }, 1L);

            // Broadcast death if enabled
            if (plugin.getConfig().getBoolean("BroadcastDeaths", true)) {
                Component broadcast = Component.text(deadPlayer.getName(), NamedTextColor.RED)
                    .append(Component.text(" has run out of lives and is now a spectator!", NamedTextColor.GRAY));
                plugin.getServer().broadcast(broadcast);
            }
        }
        
        // Update scoreboard for all players
        if (plugin.getScoreboardManager() != null) {
            plugin.getScoreboardManager().updateAllPlayers();
        }
    }

    /**
     * onPlayerRespawn - Handle player respawn
     * Restores inventory and experience if player had lives remaining
     * Teleports to bed spawn location if available
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Check if we have saved data (means player had lives remaining)
        boolean hasSavedData = savedInventories.containsKey(uuid) || savedExperience.containsKey(uuid);

        if (!hasSavedData) {
            // Player ran out of lives - let vanilla respawn handle it
            return;
        }

        // Set respawn location to bed spawn if available
        Location bedSpawn = player.getRespawnLocation();
        if (bedSpawn != null) {
            event.setRespawnLocation(bedSpawn);
            log.debug("Respawning {} at bed location: {}", player.getName(), bedSpawn);
        } else {
            log.debug("No bed spawn found for {}, using world spawn", player.getName());
        }

        // Schedule inventory and experience restoration after respawn
        SchedulerUtil.runEntityTaskLater(plugin, player, () -> {
            // Restore inventory
            if (savedInventories.containsKey(uuid)) {
                ItemStack[] inventory = savedInventories.remove(uuid);
                ItemStack[] armor = savedArmor.remove(uuid);

                if (inventory != null) {
                    player.getInventory().setContents(inventory);
                }
                if (armor != null) {
                    player.getInventory().setArmorContents(armor);
                }

                log.debug("Restored inventory for {}", player.getName());
            }

            // Restore experience
            if (savedExperience.containsKey(uuid)) {
                Integer totalExp = savedExperience.remove(uuid);
                Integer expLevels = savedExpLevels.remove(uuid);

                if (totalExp != null) {
                    player.setTotalExperience(totalExp);
                }
                if (expLevels != null) {
                    player.setLevel(expLevels);
                }

                log.debug("Restored experience for {}", player.getName());
            }

            // Confirm to player
            int lives = BasicUtil.getPlayerLives(player);
            if (bedSpawn != null) {
                player.sendMessage(Component.text("You respawned at your bed with your items! ", NamedTextColor.GREEN)
                    .append(Component.text("Lives: " + lives, NamedTextColor.YELLOW)));
            } else {
                player.sendMessage(Component.text("You respawned with your items! ", NamedTextColor.GREEN)
                    .append(Component.text("Lives: " + lives, NamedTextColor.YELLOW))
                    .append(Component.text(" (Set a bed to respawn there next time!)", NamedTextColor.GRAY)));
            }
        }, 1L);
    }

    /**
     * Stop the timer on disconnect
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        LivesScheduler scheduler = timerMap.remove(player);
        if (scheduler != null) {
            scheduler.cancel();
        }

        // Clean up any saved data (in case player disconnects before respawning)
        savedInventories.remove(uuid);
        savedArmor.remove(uuid);
        savedExperience.remove(uuid);
        savedExpLevels.remove(uuid);

        // Update name in database in case it changed
        PlayerData data = DatabaseManager.loadPlayerData(player);
        if (data != null) {
            data.setName(player.getName());
            DatabaseManager.savePlayerData(data);
        }
    }
}
