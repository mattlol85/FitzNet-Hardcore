package org.fitznet.doomdns.fitznethardcore;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

public class EventManager implements Listener {
    private FitzNetHardcore plugin;
    private static HashMap<Player, BukkitTask> timerMap = new HashMap<>();

    public EventManager(FitzNetHardcore plugin) {

        this.plugin = plugin;
    }


    // *********Events**************************

    /**
     * onJoin will preform multiple functions.
     * <p>
     * 1 - Check if the player is already in the database. If not, add them and play
     * into prompt. 2 - ...Uhh man i forgot where this one was going
     *
     * @param p - Player join event.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent p) {

        // Get the new player Object
        Player player = p.getPlayer();
        Logger.logInfo(p.getPlayer() + " Join Event Trigger.");
        //Check if new
        if(!DatabaseManager.exists(p.getPlayer())){
        //Create config file for player
        DatabaseManager.createCustomConfig(player);
        }
                // One Second = 20 Ticks
        // One Min = 1200 Ticks
        // Every 24 Mins (Every Minecraft Day)
        // BukkitTask mainSch = new Scheduler(this).runTaskTimer(this, 0L, 28800L);
        // Test call to speed things up Every 5 seconds
        //BukkitTask mainSch = new LivesScheduler(plugin,p.getPlayer()).runTaskTimerAsynchronously(plugin, 0L, 10L);
        timerMap.put(p.getPlayer(), new LivesScheduler(plugin,p.getPlayer()).runTaskTimerAsynchronously(plugin, 0L, 10L));
    }

    @EventHandler
    /**
     * onPlayerDeath() - Detect an instance where a player death occured.
     *
     * @param e - Entity that died
     */
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player deadPlayer = e.getEntity().getPlayer();
        // REMOVE LIFE FROM PLAYER
        // DISRESPECT THE PLAYER
        deadPlayer.getWorld().strikeLightningEffect(deadPlayer.getLocation());
        deadPlayer.sendMessage(ChatColor.YELLOW + "Removing one life !");

    }
    public static void stopTimers(){
        for(Player p : timerMap.keySet()){
            timerMap.get(p).cancel();
        }
    }

}
