package org.fitznet.doomdns.fitznethardcore.listeners;

import org.bukkit.ChatColor;
import org.bukkit.BanList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.fitznet.doomdns.fitznethardcore.FitzNetHardcore;
import org.fitznet.doomdns.util.BasicUtil;

public class FNPlayerDeathListener implements Listener {
    FitzNetHardcore plugin;

    public FNPlayerDeathListener(FitzNetHardcore plugin){
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
        if(deadPlayer.hasPermission("FitzNetHardcore.Invincible")){
            deadPlayer.sendMessage("You are god !!!!!!!!!!");
            return;
        }
        BasicUtil.removeLife(deadPlayer);
        // DISRESPECT THE PLAYER
        deadPlayer.getWorld().strikeLightningEffect(deadPlayer.getLocation());
        deadPlayer.sendMessage(ChatColor.YELLOW + "Removing one life !");
        if(BasicUtil.getPlayerLives(deadPlayer) == 0){
            plugin.getServer().getBanList(BanList.Type.NAME).addBan(deadPlayer.getName(), ChatColor.RED + "OUT OF LIVES. BANNED!",null, "No Lives.");
            deadPlayer.kickPlayer(ChatColor.RED + "OUT OF LIVES. BANNED");
        }
        

    }
}