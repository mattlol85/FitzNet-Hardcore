package org.fitznet.doomdns.fitznethardcore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fitznet.doomdns.fitznethardcore.FitzNetHardcore;
import org.fitznet.doomdns.fitznethardcore.Logger;
import org.fitznet.doomdns.util.BasicUtil;

import net.md_5.bungee.api.ChatColor;

public class ToggleInvincibilityCommand implements CommandExecutor{
    private FitzNetHardcore plugin;
    
    public ToggleInvincibilityCommand(FitzNetHardcore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        // Add one life to player
        if (command.getName().equals("fngod")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                if(player.hasPermission("FitzNetHardcore.fngod")){
                // Toggle God Mode
                player.sendMessage("Toggling Invincibility for: " + player.getName());
                BasicUtil.setInvincible(player);
                return true;
                }else{
                    player.sendMessage(ChatColor.RED + "You dont have permission to use this command!");
                    return false;
                }
            }else{
                Logger.logError("This command cannot be used on console.");
                return true;
            }
        }
        return false;
    }
}
