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

import org.bukkit.BanList;
import org.bukkit.Bukkit;
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

    private final File database = new File(getDataFolder().getAbsolutePath() + "\\livesDatabase.txt");
    private final ArrayList<HardcorePlayer> hardcorePlayerList = new ArrayList<>();

    //******************************************************************************

    /**
     *
     */
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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logInfo("FitzNet Shutting down gracefully.");
        writeDatabase();
    }

    //**********************METHODS*************************************************


    private void loadDatabase() {
        try {
            final Scanner in = new Scanner(database);
            while (in.hasNext()) {
                hardcorePlayerList.add(new HardcorePlayer(in.next(), in.nextInt()));
            }
            in.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
            logError(e.getMessage());
        }
    }

    //Check the plugins own directory for files

    /**
     * verifyFiles() - This checks the Plugins/Fitz-NetHardcore/ directory to see if
     * a database file is present. If not, create a blank database.
     */
    private void verifyFiles() {
        final File livesDatabase = new File(getDataFolder().getAbsolutePath() + "\\livesDatabase.txt");
        //Check if there is already a database. If not, create one.
        if (!livesDatabase.exists()) {
            logInfo("Writing blank database file \"livesDatabase.txt\".");
            try {
                if (livesDatabase.createNewFile()) {
                    logInfo("livesDatabase.txt CREATED");
                } else {
                    logError("livesDatabase.txt NOT CREATED");
                }
            } catch (final IOException e) {
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
    public void onJoin(final PlayerJoinEvent p) {

        //Get the new player Object
        final Player player = p.getPlayer();
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

    @EventHandler
    /**
     * onPlayerDeath() - Detect an instance where a player death occured.
     *
     * @param e - Entity that died
     */
    public void onPlayerDeath(final PlayerDeathEvent e) {
        final Player deadPlayer = e.getEntity().getPlayer();
        //REMOVE LIFE FROM PLAYER
        removeLife(deadPlayer);
        //DISRESPECT THE PLAYER
        deadPlayer.getWorld().strikeLightningEffect(deadPlayer.getLocation());
        deadPlayer.sendMessage(ChatColor.YELLOW + "Removing one life !");
    }

    @Override
    /**
     * onCommand - Sets commands for the server
     *
     */
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        //COMMANDS
        //***************************
        //Fitznet Testing method only.
        if (command.getName().equals("fitznet")) {
            //Must be a player only
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                player.sendMessage("FitzNet Commands");
                //   player.giveExp(10);
            } else
                //User is on the console (ADMIN ONLY)
                logInfo("Hello Server Master.");
        }


        //Returns the number of lives the player has

        if (command.getName().equals("lives")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                player.sendMessage(ChatColor.RED + player.getName() + "... you have " + getPlayerLives(player) + " lives!");

            } else {
                logInfo("This command cannot be used on Console.");
            }
        }
        //Add one life to player
        if (command.getName().equals("addlife")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                //Add one life
                addLife(player);
            }
        }
        //Remove one life from player
        if (command.getName().equals("sublife")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                //Subtract one life
                removeLife(player);
            }
        }

        //Print out list of players and
        if (command.getName().equals("fndebug")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                for (int i = 0; i < hardcorePlayerList.size(); i++) {
                    player.sendMessage(hardcorePlayerList.get(i).getUsername() + " | " + hardcorePlayerList.get(i).getLives());
                }
            } else {
                for (int i = 0; i < hardcorePlayerList.size(); i++) {
                    logInfo(hardcorePlayerList.get(i).getUsername() + " | " + hardcorePlayerList.get(i).getLives());
                }
            }
        }
        return false;
    }

    /**
     * getPlayerLives() - iterates through the hardcorePlayerList and
     * checks if the username matches the one stored in the database. Reutrns -85 as error
     * code.
     *
     * @param player - In game player.
     * @return - Amount of lives a player has. || -85
     */
    private int getPlayerLives(final Player player) {
        for (int i = 0; i < hardcorePlayerList.size(); i++) {
            if (player.getName().matches(hardcorePlayerList.get(i).getUsername())) {
                return hardcorePlayerList.get(i).getLives();
            }
        }
        return -85;
    }
    // DATABASE METHODS

    /**
     * exists() -  checks the database and checks if there is any matching usernames.
     * If so it will return true, else false.
     *
     * @param p - Player
     * @return true IF found in database
     */
    private boolean exists(final Player p) {
        try {
            final Scanner databaseIn = new Scanner(database);
            while (databaseIn.hasNext()) {
                if (databaseIn.next().matches(p.getName())) {
                    databaseIn.close();
                    return true;
                }
            }
            databaseIn.close();
        } catch (final FileNotFoundException e) {
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
    private void initWritePlayer(final Player p) {
        hardcorePlayerList.add(new HardcorePlayer(p.getDisplayName()));
        writeDatabase();
    }

    //Remove one life
    private void removeLife(final Player p) {
        getHardcorePlayer(p).removeLife();
        writeDatabase();
        if (getHardcorePlayer(p).getLives() == 0) {
            p.sendMessage(ChatColor.RED + "You are going to be banned !");
            Bukkit.getBanList(BanList.Type.NAME).addBan(p.getName(), ChatColor.RED + "You are out of lives! Permanently Banned!!!", null, "Out of lives! :(");
            p.kickPlayer("Out of lives :(. You are now Banned.");
        }
    }

    //Give em one life
    /*
    private void addNewPlayer(final Player p) {
        hardcorePlayerList.add(new HardcorePlayer(p.getDisplayName()));
        writeDatabase();
    }
    */
    //Add one life unless at MaxLives
    private void addLife(final Player p) {
        HardcorePlayer hcp = new HardcorePlayer(p.getDisplayName());
        // IF player has less than max_lives, add one
        if(getHardcorePlayer(p).getLives() < getConfig().getInt("MaxLives"))
        hcp.addLife();

        writeDatabase();
    }

    //Ban from server. Blow them up, do something.
    //private void fitzNetSmiteThisFoolForDying(final Player player) {
        //Do something grate mate :)

    //}

    /**
     * HardcorePlayer - Returns a HardcorePlayer if there is one in the database
     *
     * @param player - This is a player
     * @return hardcorePlayer - A hardcore player object
     */
    public HardcorePlayer getHardcorePlayer(final Player player) {
        for (final HardcorePlayer hardcorePlayer : hardcorePlayerList) {
            if (player.getName().matches(hardcorePlayer.getUsername()))
                return hardcorePlayer;
        }
        return null;
    }

    /**
     * writeDatabase - Iterates though the hardcorePlayerList arraylist and logs each player in the console.
     * <p>
     *     This method also prints this to the database.
     * </p>
     */
    //TODO Redesign and change main database storage to hashmap
    private void writeDatabase() {
        try {
            final PrintWriter pw = new PrintWriter(database);

            for (final HardcorePlayer hardcorePlayer : hardcorePlayerList) {
                //Write value to txt file
                logInfo("Logging player.");
                logInfo(hardcorePlayer.getUsername() + "\t" + hardcorePlayer.getLives());
                pw.println(hardcorePlayer.getUsername() + "\t" + hardcorePlayer.getLives());
                pw.flush();
            }
            pw.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
