package org.fitznet.doomdns.fitznethardcore;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class EventManager implements Listener {
    private FitzNetHardcore plugin;

    public EventManager(FitzNetHardcore plugin) {

        this.plugin = plugin;
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
        // Check if player is in database
        // if (exists(player)) {
        // //Say welcome back or something, make a nice picture idk.
        // logInfo("PLAYER EXISTS IN DATABASE");
        // } else {
        // logInfo("PLAYER DOESNT EXIST IN DATABASE");
        // initWritePlayer(player);
        // }

    }

}
