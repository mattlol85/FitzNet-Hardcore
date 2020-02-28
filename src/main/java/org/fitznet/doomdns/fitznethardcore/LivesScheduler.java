package org.fitznet.doomdns.fitznethardcore;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.fitznet.doomdns.util.BasicUtil;

public class LivesScheduler extends BukkitRunnable {

    FitzNetHardcore plugin;
    Player player;

    public LivesScheduler(FitzNetHardcore plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        Logger.logDebug("Scheduler: Adding Life for " + player.getName());
        BasicUtil.addLife(player);
    }
}