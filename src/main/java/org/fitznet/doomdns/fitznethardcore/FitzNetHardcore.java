/**
 * FITZ NET HARDCORE MODE
 * ON first world join, 1 life only.
 * <p>
 * Every 8 in game hours, one life added.
 * LifeCap total 3.
 * <p>
 * <p>
 * To-Do List
 * -Add scoreboard lives.
 * -When 3 lives, the '3' is green, then '2' is yellow, and '1' red
 * -Detect no more lives and kick
 * -Test and ensure database works well.
 * -Future add afk check.
 */
//Matthew Fitzgerald Jan 19 2020
package org.fitznet.doomdns.fitznethardcore;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.fitznet.doomdns.fitznethardcore.command.LivesCommand;
import org.fitznet.doomdns.util.BasicUtil;

import java.io.File;

public final class FitzNetHardcore extends JavaPlugin {

    private final DatabaseManager dbm = new DatabaseManager(this);
    // Declare all event listeners


    // ******************************************************************************

    /**
     *
     */
    @Override   
    public void onEnable() {

        // Plugin startup logic
        Logger.logInfo("FitzNet starting up.");
        // Load file and copy on every reload
        getConfig().options().copyDefaults();
        // Saves file from above
        saveDefaultConfig();
        // getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new EventManager(this), this);
        //Ensure playerData folder is created
        createFolders();
        registerCommands();
    }

    private void createFolders() {
        File userFiles = new File(getDataFolder(), "PlayerData");
        if (!userFiles.exists())
            userFiles.mkdirs();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Logger.logInfo("Fitz-Net Shutting down gracefully.");
    }

    // **********************METHODS*************************************************
    public void registerCommands(){
        getCommand("lives").setExecutor(new LivesCommand(this));
    }
    @Override
    /**
     * onCommand - Sets commands for the server
     *
     */
    public boolean onCommand(final CommandSender sender, final Command command, final String label,
            final String[] args) {
        // COMMANDS
        // ***************************
        // Returns the number of lives the player has
        // Add one life to player
        if (command.getName().equals("addlife")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                // Add one life
                BasicUtil.addLife(player);
                return true;
            }
        }
        
        // Remove one life from player
        if (command.getName().equals("sublife")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                // Subtract one life
                BasicUtil.removeLife(player);
                return true;
            }
        }
        if (command.getName().equals("setlife")){
            //If there is no player specificed, return
            if(args[0].equals("") || args[1].equals("")){
                sender.sendMessage("Please enter a valid user and lives amount.");
                return true;
            }
            
        }
        return false;
    }

    // Ban from server. Blow them up, do something.
    // private void fitzNetSmiteThisFoolForDying(final Player player) {
    // Do something grate mate :)

    // }


}
