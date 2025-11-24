package org.fitznet.doomdns.fitznethardcore;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.fitznet.doomdns.util.BasicUtil;

@Slf4j
public class LivesScheduler extends BukkitRunnable {

    FitzNetHardcore plugin;
    Player player;

    public LivesScheduler(FitzNetHardcore plugin, Player player){
        this.plugin = plugin;
        this.player = player;
    }
    

    @Override
    public void run(){
        log.debug("Scheduler: Adding Life for {}", player.getName());
        BasicUtil.addLife(player);
    }
}