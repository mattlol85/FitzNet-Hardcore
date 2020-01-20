//Matthew Fitzgerald Jan 19 2020
package org.fitznet.doomdns.fitznethardcore;

import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static org.fitznet.doomdns.fitznethardcore.Logging.*;

public final class FitzNetHardcore extends JavaPlugin implements Listener {

    /**
     * ON first world join, 1 life only.
     * <p>
     * Every 8 in game hours, one life added.
     * <p>
     * LifeCap total 10?
     * <p>
     * -Future add afk check.
     */

    @Override
    public void onEnable() {
        // Plugin startup logic
        logInfo("FitzNet starting up.");
        //getConfig().options().copyDefaults();
        //saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        //verifyFiles();
        getConfig();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logInfo("FitzNet Shutting down gracefully...");
    }

    /**
     * Check the plugins own directory for files
     */
    private void verifyFiles() {
        //Check if folder exists
        File rootPluginDir = new File("plugins\\Fitz-NetHardcore");
        //If the directory doesnt exist, create it and the config file.
        if (!rootPluginDir.isDirectory()) {
            if (rootPluginDir.mkdirs())
                logInfo("Created Directory");
        }
    }


    public void onFirstJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().hasPlayedBefore()) {
            Player newPlayer = e.getPlayer();
            newPlayer.sendMessage(ChatColor.RED + "Welcome to Fitz-Net Hardcore!");
            newPlayer.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.RED + getPlayerLives(newPlayer) + ChatColor.YELLOW + "lives.");
            newPlayer.sendMessage(ChatColor.YELLOW + "Every 8 INGAME hours, you will gain 1 life.");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //COMMANDS
        //***************************
        /**
         * /fitznet Testing method only.
         */
        if (command.getName().equals("fitznet")) {
            //Must be a player only
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage("FitzNet Commands");
                //   player.giveExp(10);
            } else
                //User is on the console (ADMIN ONLY)
                logInfo("Hello Server Master.");
        }

        /**
         * Returns the number of lives the player has
         */
        if (command.getName().equals("lives"))
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage(ChatColor.RED + player.getName() + "... you have " + getPlayerLives(player) + " lives!");

            } else {
                logInfo("This command cannot be used on Console.");
            }

        return false;
    }

    private int getPlayerLives(Player player) {
        return 5;
    }
}
