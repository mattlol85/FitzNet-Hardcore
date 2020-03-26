package org.fitznet.doomdns.fitznethardcore.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fitznet.doomdns.fitznethardcore.FitzNetHardcore;
import org.fitznet.doomdns.fitznethardcore.Logger;
import org.fitznet.doomdns.util.BasicUtil;

public class RemoveLifeCommand implements CommandExecutor{
    private FitzNetHardcore plugin;
    
    public RemoveLifeCommand(FitzNetHardcore plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        // Remove one life from the player
        if (command.getName().equals("removelife")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                if(player.hasPermission("FitzNetHardcore.removelife")){

                // Remove one life
                sender.sendMessage("Removing 1 life from player: " + sender.getName());
                BasicUtil.removeLife(player);
                return true;
                }else{
                    player.sendMessage(ChatColor.RED + "You dont have permission to run this command!");
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
