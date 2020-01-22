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
 * -Get a working onDeath listener
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.fitznet.doomdns.fitznethardcore.Logging.*;

public final class FitzNetHardcore extends JavaPlugin implements Listener {

    private File database = new File(getDataFolder().getAbsolutePath() + "\\livesDatabase.txt");
    private ArrayList<HardcorePlayer> hardcorePlayerList = new ArrayList<>();

    //******************************************************************************
    @Override
    public void onEnable() {
        // Plugin startup logic
        logInfo("FitzNet starting up.");
        //Load file and copy on every reload
        getConfig().options().copyDefaults();
        //Saves file from above
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        //Create and check for database
        verifyFiles();
        loadDatabase();
    }

    private void loadDatabase() {
        try {
            Scanner in = new Scanner(database);
            while (in.hasNext()) {
                hardcorePlayerList.add(new HardcorePlayer(in.next(), in.nextInt()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logError(e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logInfo("FitzNet Shutting down gracefully...");
        writeDatabase();
    }

    //**********************METHODS*************************************************


    //Check the plugins own directory for files
    private void verifyFiles() {
        File livesDatabase = new File(getDataFolder().getAbsolutePath() + "\\livesDatabase.txt");
        //Check if there is already a database. If not, create one.
        if (!livesDatabase.exists()) {
            logInfo("Writing blank database file \"livesDatabase.txt\".");
            try {
                if (livesDatabase.createNewFile()) {
                    logInfo("livesDatabase.txt CREATED");
                } else {
                    logError("livesDatabase.txt NOT CREATED");
                }
            } catch (IOException e) {
                e.printStackTrace();
                logError(e.getMessage());

            }
        } else {
            logInfo("Database file found.");
        }
    }

    //*********Events**************************

    /**
     * onJoin will preform multiple functions.
     * <p>
     * 1 - Check if the player is already in the database. If not, add them and play into prompt.
     * 2 - ...Uhh man i forgot where this one was going
     *
     * @param p - Player join event.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent p) {

        //Get the new player Object
        Player player = p.getPlayer();
        logInfo(p.getPlayer() + " Join Event Trigger.");
        //Check if player is in database
        if (exists(player)) {
            //Say welcome back or something, make a nice picture idk.
            logInfo("PLAYER EXISTS IN DATABASE");
        } else {
            logInfo("PLAYER DOESNT EXIST IN DATABASE");
            initWritePlayer(player);
        }

    }

    public void onPlayerDeath(PlayerDeathEvent p) {
        if (p.getEntity().getPlayer() != null) {
            Player deadPlayer = p.getEntity().getPlayer();
            //REMOVE LIFE FROM PLAYER
            removeLife(deadPlayer);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //COMMANDS
        //***************************
        //Fitznet Testing method only.
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


        //Returns the number of lives the player has

        if (command.getName().equals("lives")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.sendMessage(ChatColor.RED + player.getName() + "... you have " + getPlayerLives(player) + " lives!");

            } else {
                logInfo("This command cannot be used on Console.");
            }
        }
        if (command.getName().equals("addlife")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                //Add one life
                addLife(player);
            }
        }
        if (command.getName().equals("sublife")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                //Add one life
                removeLife(player);
            }
        }
        return false;
    }

    private int getPlayerLives(Player player) {
        for (int i = 0; i < hardcorePlayerList.size(); i++) {
            if (player.getName().matches(hardcorePlayerList.get(i).getUsername())) {
                return hardcorePlayerList.get(i).getLives();
            }
        }
        return -100;
    }
    // DATABASE METHODS

    /**
     * exists -  checks the database and checks if there is any matching usernames.
     * If so it will return true, else false.
     *
     * @param p - Player
     * @return true IF found in database
     */
    private boolean exists(Player p) {
        try {
            Scanner databaseIn = new Scanner(database);
            while (databaseIn.hasNext()) {
                if (databaseIn.next().matches(p.getName())) {
                    return true;
                }
            }
            databaseIn.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logError(e.getMessage());
        }
        return false;
    }

    /**
     * Inital writing of new player
     *
     * @param p - Player that just joined the server.
     */
    private void initWritePlayer(Player p) {
        //TODO add something for new players here.
        hardcorePlayerList.add(new HardcorePlayer(p.getDisplayName()));
        writeDatabase();
    }

    //Remove one life
    private void removeLife(Player p) {
        getHardcorePlayer(p).removeLife();
        writeDatabase();
        //TODO Check if user is at 0 hearts then ban from server
    }

    //Give em one life
    private void addNewPlayer(Player p) {
        hardcorePlayerList.add(new HardcorePlayer(p.getDisplayName()));
        writeDatabase();
    }
    //Add one life
    private void addLife(Player p) {
        //TODO get something here
        writeDatabase();
    }

    //Add life with param
    private void addLife(Player p, int lives) {
        getHardcorePlayer(p).addLife();
        writeDatabase();
    }

    //Ban from server. Blow them up, do something.
    private void fitzNetSmiteThisFoolForDying(Player player) {
        //Do something grate mate :)

    }

    /**
     * HardcorePlayer - Returns a HardcorePlayer if there is one in the database
     *
     * @param player
     * @return
     */
    public HardcorePlayer getHardcorePlayer(Player player) {
        for (int i = 0; i < hardcorePlayerList.size(); i++) {
            if (player.getName().matches(hardcorePlayerList.get(i).getUsername()))
                return hardcorePlayerList.get(i);
        }
        return null;
    }

    /**
     * TODO write method comment
     */
    private void writeDatabase() {
        try {
            PrintWriter pw = new PrintWriter(database);

            for (int i = 0; i < hardcorePlayerList.size(); i++) {
                //Write value to txt file
                logInfo("Logging player.");
                logInfo(hardcorePlayerList.get(i).getUsername() + "\t" + hardcorePlayerList.get(i).getLives());
                pw.println(hardcorePlayerList.get(i).getUsername() + "\t" + hardcorePlayerList.get(i).getLives());
                pw.flush();
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
