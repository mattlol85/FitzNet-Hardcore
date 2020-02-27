package org.fitznet.doomdns.fitznethardcore.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.fitznet.doomdns.fitznethardcore.FitzNetHardcore;

public class SetLifeCommand implements CommandExecutor{
    private FitzNetHardcore plugin;
    
    public SetLifeCommand(FitzNetHardcore plugin){
        //Save a copy of the plugin, just in case
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        //FIXME Command does not work
        if (command.getName().equals("setlife")){
            //If there is no player specificed, return
            if(args[0].equals("") || args[1].equals("")){
                sender.sendMessage("Please enter a valid user and lives amount.");
                return true;
            }
            
        }
        return false;
    }
}
