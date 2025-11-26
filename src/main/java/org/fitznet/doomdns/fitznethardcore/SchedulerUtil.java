package org.fitznet.doomdns.fitznethardcore;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

/**
 * SchedulerUtil - Abstraction layer for Paper/Folia scheduler compatibility
 * Automatically detects Folia and uses region-based scheduling when available
 */
@Slf4j
public class SchedulerUtil {

    private static final boolean IS_FOLIA;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
            log.info("Folia detected - using region-based scheduling");
        } catch (ClassNotFoundException e) {
            folia = false;
            log.info("Paper detected - using legacy scheduling");
        }
        IS_FOLIA = folia;
    }

    /**
     * Check if running on Folia
     */
    public static boolean isFolia() {
        return IS_FOLIA;
    }

    /**
     * Run a task on the main/global region thread
     */
    public static void runTask(Plugin plugin, Runnable task) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().run(plugin, (scheduledTask) -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Run a delayed task on the main/global region thread
     */
    public static void runTaskLater(Plugin plugin, Runnable task, long delayTicks) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    /**
     * Run a repeating task on the main/global region thread
     */
    public static void runTaskTimer(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        if (IS_FOLIA) {
            Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, (scheduledTask) -> task.run(),
                delayTicks, periodTicks);
        } else {
            Bukkit.getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
        }
    }

    /**
     * Run an async task
     */
    public static void runTaskAsync(Plugin plugin, Runnable task) {
        if (IS_FOLIA) {
            Bukkit.getAsyncScheduler().runNow(plugin, (scheduledTask) -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    /**
     * Run a delayed async task
     */
    public static void runTaskLaterAsync(Plugin plugin, Runnable task, long delay, TimeUnit unit) {
        if (IS_FOLIA) {
            Bukkit.getAsyncScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), delay, unit);
        } else {
            long ticks = unit.toMillis(delay) / 50; // Convert to ticks (50ms = 1 tick)
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, ticks);
        }
    }

    /**
     * Run a repeating async task
     */
    public static void runTaskTimerAsync(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        if (IS_FOLIA) {
            long delayMs = delayTicks * 50;
            long periodMs = periodTicks * 50;
            Bukkit.getAsyncScheduler().runAtFixedRate(plugin, (scheduledTask) -> task.run(),
                delayMs, periodMs, TimeUnit.MILLISECONDS);
        } else {
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
        }
    }

    /**
     * Run a task for a specific entity (region-aware on Folia)
     */
    public static void runEntityTask(Plugin plugin, Entity entity, Runnable task) {
        if (IS_FOLIA) {
            entity.getScheduler().run(plugin, (scheduledTask) -> task.run(), null);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Run a delayed task for a specific entity (region-aware on Folia)
     */
    public static void runEntityTaskLater(Plugin plugin, Entity entity, Runnable task, long delayTicks) {
        if (IS_FOLIA) {
            entity.getScheduler().runDelayed(plugin, (scheduledTask) -> task.run(), null, delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    /**
     * Run a task at a specific location (region-aware on Folia)
     */
    public static void runTaskAtLocation(Plugin plugin, Location location, Runnable task) {
        if (IS_FOLIA) {
            Bukkit.getRegionScheduler().run(plugin, location, (scheduledTask) -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    /**
     * Run a delayed task at a specific location (region-aware on Folia)
     */
    public static void runTaskLaterAtLocation(Plugin plugin, Location location, Runnable task, long delayTicks) {
        if (IS_FOLIA) {
            Bukkit.getRegionScheduler().runDelayed(plugin, location, (scheduledTask) -> task.run(), delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    /**
     * Cancel all tasks for this plugin
     */
    public static void cancelAllTasks(Plugin plugin) {
        if (IS_FOLIA) {
            // Folia handles task cleanup automatically when plugin disables
            log.debug("Folia handles task cleanup automatically");
        } else {
            Bukkit.getScheduler().cancelTasks(plugin);
        }
    }
}

