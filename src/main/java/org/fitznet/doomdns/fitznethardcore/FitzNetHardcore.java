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

import org.bukkit.plugin.java.JavaPlugin;
import org.fitznet.doomdns.fitznethardcore.command.AddLifeCommand;
import org.fitznet.doomdns.fitznethardcore.command.LivesCommand;
import org.fitznet.doomdns.fitznethardcore.command.RemoveLifeCommand;
import org.fitznet.doomdns.fitznethardcore.command.SetLifeCommand;
import org.fitznet.doomdns.fitznethardcore.listeners.FNLoginListener;
import org.fitznet.doomdns.fitznethardcore.listeners.FNPlayerDeathListener;

import java.io.File;

public final class FitzNetHardcore extends JavaPlugin {

    private final DatabaseManager dbm = new DatabaseManager(this);
    // Declare all listeners
    private final FNPlayerDeathListener  deathListener = new FNPlayerDeathListener(this);
    private final FNLoginListener loginLogoutListener = new FNLoginListener(this);

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
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Logger.logInfo("Fitz-Net Shutting down gracefully.");
    }

    // **********************METHODS*************************************************

    /**
     * Registers commands to server. Add any new commands here
     */
    public void registerCommands(){
        getCommand("lives").setExecutor(new LivesCommand(this));
        getCommand("setlife").setExecutor(new SetLifeCommand(this));
        getCommand("removelife").setExecutor(new RemoveLifeCommand(this));
        getCommand("addlife").setExecutor(new AddLifeCommand(this));
    }
      
    /**
     * Create playerData folder if it does not exist
     */
    private void createFolders() {
        File userFiles = new File(getDataFolder(), "PlayerData");
        if (!userFiles.exists())
            userFiles.mkdirs();
    }
}