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
import org.bukkit.scheduler.BukkitTask;
import org.fitznet.doomdns.util.BasicUtil;

import java.io.File;

public final class FitzNetHardcore extends JavaPlugin {

    public final File database = new File(getDataFolder().getAbsolutePath() + "\\livesDatabase.txt");
    //private final ArrayList<HardcorePlayer> hardcorePlayerList = new ArrayList<>();
    private DatabaseManager dbm;
    // private final HashMap<String,Integer> playerMap = new HashMap<>();

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
        // Create and check for database
        // verifyFiles();
        // loadDatabase();

        // One Second = 20 Ticks
        // One Min = 1200 Ticks
        // Every 24 Mins (Every Minecraft Day)
        // BukkitTask mainSch = new Scheduler(this).runTaskTimer(this, 0L, 28800L);
        // Test call to speed things up Every 5 seconds
        BukkitTask mainSch = new LivesScheduler(this).runTaskTimer(this, 0L, 10L);

        // Enable new database
        createFolders();
        dbm = new DatabaseManager(this);

    }

    private void createFolders() {
        File userFiles = new File(getDataFolder(), "PlayerData");
        if (!userFiles.exists())
            userFiles.mkdirs();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Logger.logInfo("FitzNet Shutting down gracefully.");
    }

    // **********************METHODS*************************************************

    @Override
    /**
     * onCommand - Sets commands for the server
     *
     */
    public boolean onCommand(final CommandSender sender, final Command command, final String label,
            final String[] args) {
        // COMMANDS
        // ***************************
        // Fitznet Testing method only.
        if (command.getName().equals("fitznet")) {
            // Must be a player only
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                player.sendMessage("FitzNet Commands");
                // player.giveExp(10);
            } else
                // User is on the console (ADMIN ONLY)
                Logger.logInfo("Hello Server Master.");
        }

        // Returns the number of lives the player has

        if (command.getName().equals("lives")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                player.sendMessage(
                        ChatColor.RED + player.getName() + "... you have " + DatabaseManager.getInt(player, "Lives") + " lives!");

            } else {
                Logger.logInfo("This command cannot be used on Console.");
            }
        }
        // Add one life to player
        if (command.getName().equals("addlife")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                // Add one life
                BasicUtil.addLife(player);
            }
        }
        // Remove one life from player
        if (command.getName().equals("sublife")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                // Subtract one life
                BasicUtil.removeLife(player);
            }
        }

        // Print out list of players and
        if (command.getName().equals("fndebug")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                //TODO Debug command
            }
        }
        return false;

    }

    // Ban from server. Blow them up, do something.
    // private void fitzNetSmiteThisFoolForDying(final Player player) {
    // Do something grate mate :)

    // }


}
