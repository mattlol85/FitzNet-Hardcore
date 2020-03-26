package org.fitznet.doomdns.fitznethardcore.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.fitznet.doomdns.fitznethardcore.DatabaseManager;
import org.fitznet.doomdns.fitznethardcore.FitzNetHardcore;
import org.fitznet.doomdns.fitznethardcore.LivesScheduler;
import org.fitznet.doomdns.fitznethardcore.Logger;

public class FNLoginListener implements Listener {
    private FitzNetHardcore plugin;
    private static HashMap<Player, BukkitTask> timerMap = new HashMap<>();


    public FNLoginListener(FitzNetHardcore plugin){
        this.plugin = plugin;
    } 
    /**
     * onFirstJoin Catches all new players joining the server.
     * @param p
     */
    @EventHandler
    public void onFirstJoin(PlayerJoinEvent p){
        Player player = p.getPlayer();
        //Check if new
        if(!DatabaseManager.exists(player)){
        //Create config file for player
        DatabaseManager.createCustomConfig(player);
        }
    }
        /**
     * onJoin will preform multiple functions.
     * <p>
     *  - Checks if a player is the the database, if not create them
     *  - Adds a timer for each player into a map that triggers to add a life
     * @param p - Player join event.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent p) {
        Player player = p.getPlayer();
        
        // One Second = 20 Ticks
        // One Min = 1200 Ticks
        // Every 24 Mins (Every Minecraft Day)
        // Amount of Mins Until New life  * One Min in ticks
        timerMap.put(p.getPlayer(), new LivesScheduler(plugin,player).runTaskTimerAsynchronously(plugin, (plugin.getConfig().getInt("LifeRegenTime") * 1200L), (plugin.getConfig().getInt("LifeRegenTime") * 1200L)));
        //timerMap.put(p.getPlayer(), new LivesScheduler(plugin,player).runTaskTimerAsynchronously(plugin, 2L, 99999999999999L));
    }
    @EventHandler
    //Stop the timer on disconnect
    public void onPlayerLeave(PlayerQuitEvent p){
        timerMap.get(p.getPlayer()).cancel();
    }
}