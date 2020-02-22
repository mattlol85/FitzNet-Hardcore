package org.fitznet.doomdns.fitznethardcore;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;


public class EventManager {

    @EventHandler
    /**
     * onPlayerDeath() - Detect an instance where a player death occured.
     *
     * @param e - Entity that died
     */
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player deadPlayer = e.getEntity().getPlayer();
        //REMOVE LIFE FROM PLAYER
        //removeLife(deadPlayer);
        //DISRESPECT THE PLAYER
        deadPlayer.getWorld().strikeLightningEffect(deadPlayer.getLocation());
        deadPlayer.sendMessage(ChatColor.YELLOW + "Removing one life !");
    }

}
