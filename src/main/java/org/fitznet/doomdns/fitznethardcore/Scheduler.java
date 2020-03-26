package org.fitznet.doomdns.fitznethardcore;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Score;
import org.fitznet.doomdns.util.BasicUtil;
/**
 * The schdule is the core game loop that every player runs on.
 * This will be used to update scoreboards
 */
public class Scheduler extends BukkitRunnable {

    FitzNetHardcore plugin;
    Player player;

    public Scheduler(FitzNetHardcore plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        if(BasicUtil.getPlayerLives(player) < plugin.getConfig().getInt("MaxLives")){
            Logger.logDebug("Scheduler: Adding Life for " + player.getName());
            BasicUtil.addLife(player);
        }
        updateScoreboard();
    }

    public void updateScoreboard(){
       // Score score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore("Lives:");
        //score.setScore(BasicUtil.getPlayerLives(player));
        for (Player online : Bukkit.getOnlinePlayers()){
            Score score = online.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore("Players:");
            Score score2 = online.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore("Lives:");
            score.setScore(Bukkit.getOnlinePlayers().size());
            score2.setScore(BasicUtil.getPlayerLives(online));
            player.sendMessage("UPDATING SCOREBOARD Scheduler");

        }
    }
}