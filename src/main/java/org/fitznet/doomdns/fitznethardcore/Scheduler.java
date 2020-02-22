package org.fitznet.doomdns.fitznethardcore;

import org.bukkit.scheduler.BukkitRunnable;

public class Scheduler extends BukkitRunnable {

    FitzNetHardcore plugin;

    public Scheduler(FitzNetHardcore plugin){
        this.plugin = plugin;
    }

    @Override
    public void run(){
        //TODO Create method to check current player time.
        System.out.println("Task has ran.");
    }
}