package org.fitznet.doomdns.hardcore.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;
import org.fitznet.doomdns.hardcore.FitzNetHardcore;
import org.fitznet.doomdns.hardcore.data.DatabaseManager;
import org.fitznet.doomdns.hardcore.data.PlayerData;
import org.fitznet.doomdns.hardcore.service.ScoreboardManager;
import org.fitznet.doomdns.hardcore.util.BasicUtil;

/**
 * LivesScheduler - Manages life regeneration timer for players
 * Grants lives based on configured time interval
 * Folia-compatible using SchedulerUtil
 */
@Slf4j
public class LivesScheduler {

    private final FitzNetHardcore plugin;
    private final Player player;
    private boolean cancelled = false;
    private SchedulerUtil.TaskHandle taskHandle;

    public LivesScheduler(FitzNetHardcore plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    /**
     * Start the scheduler with specified delay and period
     */
    public void start(long delayTicks, long periodTicks) {
        taskHandle = SchedulerUtil.runTaskTimerAsyncCancellable(plugin, this::run, delayTicks, periodTicks);
    }

    /**
     * Cancel this scheduler
     */
    public void cancel() {
        cancelled = true;
        if (taskHandle != null) {
            taskHandle.cancel();
            taskHandle = null;
        }
    }

    /**
     * Check if cancelled
     */
    public boolean isCancelled() {
        return cancelled;
    }

    private void run() {
        // Check if cancelled
        if (cancelled) {
            return;
        }
        // Check if player is still online
        if (!player.isOnline()) {
            this.cancel();
            return;
        }

        PlayerData data = DatabaseManager.loadPlayerData(player);
        if (data == null) {
            log.warn("Could not load player data for {} in scheduler", player.getName());
            return;
        }

        // Don't give lives to dead players
        if (data.isSpectator() || data.getLives() == 0) {
            log.debug("Skipping life regen for dead player {}", player.getName());
            return;
        }

        int maxLives = plugin.getConfig().getInt("MaxLives", 3);

        // Check if player is already at max lives
        if (data.getLives() >= maxLives) {
            log.debug("Player {} already at max lives ({}), skipping regen", player.getName(), maxLives);
            return;
        }

        // Add one life (with cap enforcement)
        boolean added = BasicUtil.addLife(player, true);

        if (added) {
            log.info("Life regenerated for {} - now has {} lives", player.getName(), data.getLives() + 1);

            // Update next life timestamp
            long regenMinutes = plugin.getConfig().getInt("LifeRegenTime", 2);
            long nextLifeTime = System.currentTimeMillis() + (regenMinutes * 60 * 1000);

            data = DatabaseManager.loadPlayerData(player); // Reload after addLife
            if (data != null) {
                data.setNextLifeTimestamp(nextLifeTime);
                DatabaseManager.savePlayerData(data);
            } else {
                log.warn("Life was added but player data could not be reloaded for {}", player.getName());
                return;
            }

            // Send message to player
            player.sendMessage(net.kyori.adventure.text.Component.text("You gained 1 life! ",
                net.kyori.adventure.text.format.NamedTextColor.GREEN)
                .append(net.kyori.adventure.text.Component.text("Lives: " + (data.getLives()),
                    net.kyori.adventure.text.format.NamedTextColor.YELLOW)));

            // Update scoreboard
            ScoreboardManager scoreboardManager = plugin.getScoreboardManager();
            if (scoreboardManager != null) {
                scoreboardManager.updatePlayer(player);
                scoreboardManager.updateAllPlayers(); // Update tab for everyone
            }
        }
    }
}

