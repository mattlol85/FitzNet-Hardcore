package org.fitznet.doomdns.fitznethardcore.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fitznet.doomdns.fitznethardcore.DatabaseManager;
import org.fitznet.doomdns.fitznethardcore.FitzNetHardcore;
import org.fitznet.doomdns.fitznethardcore.Logger;
import org.fitznet.doomdns.util.BasicUtil;

public class RemoveLifeCommand implements CommandExecutor{
    private FitzNetHardcore plugin;
    
    public RemoveLifeCommand(FitzNetHardcore plugin){
        //Save a copy of the plugin, just in case
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        // Add one life to player
        if (command.getName().equals("removelife")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                // Add one life
                sender.sendMessage("Adding 1 life for player " + sender.getName());
                BasicUtil.removeLife(player);
                return true;
            }else{
                Logger.logError("This command cannot be used on console.");
                return true;
            }
        }
        return false;
    }
}
