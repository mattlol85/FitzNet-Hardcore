package org.fitznet.doomdns.fitznethardcore.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
/**
 *      // One Second = 20 Ticks
        // One Min = 1200 Ticks
        // Every 24 Mins (Every Minecraft Day)
        // Amount of Mins Until New life  * One Min in ticks
 */
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.fitznet.doomdns.fitznethardcore.DatabaseManager;
import org.fitznet.doomdns.fitznethardcore.FitzNetHardcore;
import org.fitznet.doomdns.fitznethardcore.Scheduler;
import org.fitznet.doomdns.util.BasicUtil;

import net.md_5.bungee.api.ChatColor;

public class FNLoginListener implements Listener {
    private FitzNetHardcore plugin;
    private static HashMap<Player, BukkitTask> timerMap = new HashMap<>();


    public FNLoginListener(FitzNetHardcore plugin){
        this.plugin = plugin;
    } 
    /**
     * onFirstJoin Catches all new players joining the server.
     * @param p
     */
    @EventHandler
    public void onFirstJoin(PlayerJoinEvent p){
        Player player = p.getPlayer();
        //Check if new
        if(!DatabaseManager.exists(player)){
        //Create config file for player
        DatabaseManager.createCustomConfig(player);
        }
    }
        /**
     * onJoin preforms multiple functions.
     * <p>
     *  - Checks if a player is the the database, if not create them
     *  - Adds a timer for each player into a map that triggers to add a life
     * @param p - Player join event.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent p) {
        Player player = p.getPlayer();
        System.out.println(Bukkit.getServer().getWorld("world").getTime());
        createScoreboard(player);
        updateScoreboard();
        timerMap.put(p.getPlayer(), new Scheduler(plugin,player).runTaskTimerAsynchronously(plugin, 0L, 100L));
        //timerMap.put(p.getPlayer(), new LivesScheduler(plugin,player).runTaskTimerAsynchronously(plugin, 2L, 99999999999999L));

    }
    /**
     * Creates new scoreboard on playerJoin
     */
    public void createScoreboard(Player p){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        Objective objective = board.registerNewObjective("Stats", "Dummy");
        Objective playerLives = board.registerNewObjective("PlayerLives", "A Number?");

        objective.setDisplayName(ChatColor.RED + "FITZNET HARDCORE");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = objective.getScore("Players:");
        Score score2 = objective.getScore("Lives:");
        score.setScore(Bukkit.getOnlinePlayers().size());
        score2.setScore(BasicUtil.getPlayerLives(p));
        p.setScoreboard(board);

    }
    public void updateScoreboard(){

    }


    @EventHandler
    //Stop the timer on disconnect
    public void onPlayerLeave(PlayerQuitEvent p){
        timerMap.get(p.getPlayer()).cancel();
        updateScoreboard();
    }
}