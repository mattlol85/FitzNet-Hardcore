package org.fitznet.doomdns.fitznethardcore;

import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * ScoreboardManager - Manages player list display and life countdown sidebar
 * Shows color-coded lives in tab menu and time until next life
 */
@Slf4j
public class ScoreboardManager {
    private final FitzNetHardcore plugin;

    public ScoreboardManager(FitzNetHardcore plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize scoreboard for a player
     */
    public void setupPlayer(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        player.setScoreboard(scoreboard);
        updatePlayerSidebar(player);
    }

    /**
     * Update all players' scoreboards
     */
    public void updateAllPlayers() {
        if (!plugin.getConfig().getBoolean("ShowTabLives", true)) {
            return;
        }

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            updatePlayer(player);
        }
    }

    /**
     * Update a specific player's display
     */
    public void updatePlayer(Player player) {
        updatePlayerListName(player);
        updatePlayerSidebar(player);
    }

    /**
     * Update player's name in the tab list with color-coded lives
     */
    private void updatePlayerListName(Player player) {
        if (!plugin.getConfig().getBoolean("ShowTabLives", true)) {
            return;
        }

        int lives = DatabaseManager.getLives(player);
        TextColor color = getLifeColor(lives);

        String livesDisplay = lives > 0 ? String.valueOf(lives) : "☠";

        Component displayName = Component.text(player.getName())
            .append(Component.text(" [", NamedTextColor.DARK_GRAY))
            .append(Component.text(livesDisplay, color))
            .append(Component.text("]", NamedTextColor.DARK_GRAY));

        player.playerListName(displayName);
    }

    /**
     * Update player's sidebar showing time until next life
     */
    private void updatePlayerSidebar(Player player) {
        PlayerData data = DatabaseManager.loadPlayerData(player);
        if (data == null) {
            return;
        }

        try {
            Scoreboard scoreboard = player.getScoreboard();
            if (scoreboard == null) {
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                player.setScoreboard(scoreboard);
            }

            // Remove existing objective if present
            Objective objective = scoreboard.getObjective("lives");
            if (objective != null) {
                objective.unregister();
            }

            // Create new objective using modern API with Criteria
            objective = scoreboard.registerNewObjective(
                "lives",
                org.bukkit.scoreboard.Criteria.DUMMY,
                Component.text("FitzNet Hardcore", NamedTextColor.GOLD),
                org.bukkit.scoreboard.RenderType.INTEGER
            );
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Display current lives with color
        int lives = data.getLives();
        TextColor color = getLifeColor(lives);
        String livesText = lives > 0 ? lives + " ❤" : "DEAD ☠";

        objective.getScore("§r").setScore(5); // Blank line
        objective.getScore("§fLives: " + getColorCode(color) + livesText).setScore(4);

        // Display time until next life if applicable
        if (lives < plugin.getConfig().getInt("MaxLives", 3) && lives > 0) {
            long timeUntilNext = getTimeUntilNextLife(data);
            String timeDisplay = formatTime(timeUntilNext);

            objective.getScore("§r ").setScore(3); // Blank line
            objective.getScore("§fNext Life:").setScore(2);
            objective.getScore("§e" + timeDisplay).setScore(1);
        } else if (lives == 0) {
            objective.getScore("§r ").setScore(3); // Blank line
            objective.getScore("§cWaiting for").setScore(2);
            objective.getScore("§cresurrection...").setScore(1);
        }
        } catch (UnsupportedOperationException e) {
            // Folia may not support custom scoreboards in the same way
            // Silently skip sidebar updates on Folia
            log.debug("Scoreboard sidebar not supported on this server (Folia): {}", e.getMessage());
        } catch (Exception e) {
            log.warn("Error updating player sidebar for {}: {}", player.getName(), e.getMessage());
        }
    }

    /**
     * Calculate time until next life regeneration
     */
    private long getTimeUntilNextLife(PlayerData data) {
        long nextLifeTime = data.getNextLifeTimestamp();
        long currentTime = System.currentTimeMillis();
        return Math.max(0, nextLifeTime - currentTime);
    }

    /**
     * Format milliseconds into MM:SS display
     */
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Get color based on number of lives
     * 3 lives = green, 2 = yellow, 1 = red, 0 = dark gray
     */
    private TextColor getLifeColor(int lives) {
        return switch (lives) {
            case 0 -> NamedTextColor.DARK_GRAY;
            case 1 -> NamedTextColor.RED;
            case 2 -> NamedTextColor.YELLOW;
            default -> NamedTextColor.GREEN; // 3 or more
        };
    }

    /**
     * Get legacy color code for scoreboard formatting
     */
    private String getColorCode(TextColor color) {
        if (color == NamedTextColor.DARK_GRAY) return "§8";
        if (color == NamedTextColor.RED) return "§c";
        if (color == NamedTextColor.YELLOW) return "§e";
        if (color == NamedTextColor.GREEN) return "§a";
        return "§f";
    }

    /**
     * Start periodic scoreboard update task
     * Updates every second to keep the countdown fresh
     * Folia-compatible
     */
    public void startUpdateTask() {
        SchedulerUtil.runTaskTimer(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                updatePlayerSidebar(player);
            }
        }, 20L, 20L); // Run every second (20 ticks)
    }
}

