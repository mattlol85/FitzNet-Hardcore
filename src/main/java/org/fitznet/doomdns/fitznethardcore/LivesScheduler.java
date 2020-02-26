package org.fitznet.doomdns.fitznethardcore;

import java.util.HashMap;
import org.fitznet.doomdns.util.BasicUtil;
import org.bukkit.scheduler.BukkitRunnable;

public class LivesScheduler extends BukkitRunnable {

    FitzNetHardcore plugin;

    public LivesScheduler(FitzNetHardcore plugin){
        this.plugin = plugin;
    }
    

    @Override
    public void run(){
        //TODO Create method to check current player time.
        //BasicUtil.testMethod();
    }
}