package org.fitznet.doomdns.fitznethardcore.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.fitznet.doomdns.fitznethardcore.DatabaseManager;
import org.fitznet.doomdns.fitznethardcore.FitzNetHardcore;
import org.fitznet.doomdns.fitznethardcore.Logger;

public class LivesCommand implements CommandExecutor{
    private FitzNetHardcore plugin;
    
    public LivesCommand(FitzNetHardcore plugin){
        //Save a copy of the plugin, just in case
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (command.getName().equals("lives")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                player.sendMessage(
                ChatColor.RED + player.getName() + "... you have " + DatabaseManager.getInt(player, "Lives") + " lives!");
                return true;

            } else {
                Logger.logInfo("This command cannot be used on Console.");
                return true;
            }
        }
        return false;
    }
}
