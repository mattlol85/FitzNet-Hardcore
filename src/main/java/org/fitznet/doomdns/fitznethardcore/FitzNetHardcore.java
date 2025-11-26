/**
 * FITZ NET HARDCORE MODE
 *
 * A comprehensive hardcore lives system with:
 * - 3 lives maximum (configurable)
 * - Life regeneration over time
 * - Spectator mode on death instead of ban
 * - Resurrection mechanics via diamond block sacrifice
 * - Life gifting between players
 * - Tab menu display with color-coded lives
 * - Death broadcasts and player statistics
 *
 * @author Matthew Fitzgerald
 * @version 2.0.0
 * @since January 19, 2020
 * Updated: November 2025
 */
package org.fitznet.doomdns.fitznethardcore;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.fitznet.doomdns.util.BasicUtil;

import java.io.File;

@Slf4j
public class FitzNetHardcore extends JavaPlugin {

    @Getter
    private static FitzNetHardcore instance;

    private DatabaseManager databaseManager;
    @Getter
    private ResurrectionManager resurrectionManager;
    @Getter
    private ScoreboardManager scoreboardManager;

    @Override
    public void onEnable() {
        instance = this;

        log.info("═══════════════════════════════════════");
        log.info("  FitzNet Hardcore v2.0 Starting Up");
        log.info("═══════════════════════════════════════");

        // Load configuration
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        // Initialize managers
        databaseManager = new DatabaseManager(this);
        resurrectionManager = new ResurrectionManager(this);
        scoreboardManager = new ScoreboardManager(this);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new EventManager(this), this);
        getServer().getPluginManager().registerEvents(resurrectionManager, this);

        // Ensure playerData folder is created
        createFolders();

        // Start scoreboard update task
        scoreboardManager.startUpdateTask();

        log.info("FitzNet Hardcore enabled successfully!");
        log.info("  - Max Lives: {}", getConfig().getInt("MaxLives", 3));
        log.info("  - Life Regen Time: {} minutes", getConfig().getInt("LifeRegenTime", 2));
        log.info("  - Resurrection: {}", getConfig().getBoolean("ResurrectionEnabled", true) ? "Enabled" : "Disabled");
        log.info("  - Life Gifting: {}", getConfig().getBoolean("EnableLifeGifting", true) ? "Enabled" : "Disabled");
        log.info("  - Keep Inventory on Death: {}", getConfig().getBoolean("KeepInventoryOnDeath", true) ? "Enabled" : "Disabled");
        log.info("  - Keep Experience on Death: {}", getConfig().getBoolean("KeepExperienceOnDeath", true) ? "Enabled" : "Disabled");
    }

    private void createFolders() {
        File userFiles = new File(getDataFolder(), "PlayerData");
        if (!userFiles.exists()) {
            userFiles.mkdirs();
            log.info("Created PlayerData folder");
        }
    }

    @Override
    public void onDisable() {
        log.info("FitzNet Hardcore shutting down gracefully.");
    }


    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        String cmd = command.getName().toLowerCase();

        switch (cmd) {
            case "fitznet":
                return handleFitznetCommand(sender);

            case "lives":
                return handleLivesCommand(sender);

            case "addlife":
                return handleAddLifeCommand(sender, args);

            case "sublife":
                return handleSubLifeCommand(sender, args);

            case "setlife":
                return handleSetLifeCommand(sender, args);

            case "givelife":
                return handleGiveLifeCommand(sender, args);

            case "revive":
                return handleReviveCommand(sender, args);

            case "fndebug":
                return handleDebugCommand(sender);

            default:
                return false;
        }
    }

    private boolean handleFitznetCommand(CommandSender sender) {
        if (!sender.hasPermission("fitznethardcore.admin")) {
            sender.sendMessage(Component.text("No permission!", NamedTextColor.RED));
            return true;
        }

        if (sender instanceof Player player) {
            player.sendMessage(Component.text("FitzNet Hardcore v2.0", NamedTextColor.GOLD));
            player.sendMessage(Component.text("Commands: /lives, /givelife, /revive", NamedTextColor.YELLOW));
        } else {
            log.info("Hello Server Master.");
        }
        return true;
    }

    private boolean handleLivesCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
            return true;
        }

        int lives = DatabaseManager.getLives(player);
        player.sendMessage(Component.text(player.getName() + ", you have ", NamedTextColor.YELLOW)
            .append(Component.text(lives, NamedTextColor.RED))
            .append(Component.text(" lives!", NamedTextColor.YELLOW)));

        return true;
    }

    private boolean handleAddLifeCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("fitznethardcore.admin")) {
            sender.sendMessage(Component.text("No permission!", NamedTextColor.RED));
            return true;
        }

        Player target;
        if (args.length > 0) {
            target = getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Component.text("Usage: /addlife <player>", NamedTextColor.RED));
            return true;
        }

        // Admin command can exceed cap
        BasicUtil.addLife(target, false);
        sender.sendMessage(Component.text("Added 1 life to " + target.getName(), NamedTextColor.GREEN));
        target.sendMessage(Component.text("You gained 1 life!", NamedTextColor.GREEN));

        if (scoreboardManager != null) {
            scoreboardManager.updateAllPlayers();
        }
        return true;
    }

    private boolean handleSubLifeCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("fitznethardcore.admin")) {
            sender.sendMessage(Component.text("No permission!", NamedTextColor.RED));
            return true;
        }

        Player target;
        if (args.length > 0) {
            target = getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(Component.text("Usage: /sublife <player>", NamedTextColor.RED));
            return true;
        }

        BasicUtil.removeLife(target);
        sender.sendMessage(Component.text("Removed 1 life from " + target.getName(), NamedTextColor.GREEN));
        target.sendMessage(Component.text("You lost 1 life!", NamedTextColor.RED));

        if (scoreboardManager != null) {
            scoreboardManager.updateAllPlayers();
        }
        return true;
    }

    private boolean handleSetLifeCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("fitznethardcore.admin")) {
            sender.sendMessage(Component.text("No permission!", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /setlife <player> <amount>", NamedTextColor.RED));
            return true;
        }

        Player target = getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid number!", NamedTextColor.RED));
            return true;
        }

        BasicUtil.setLives(target, amount);
        sender.sendMessage(Component.text("Set " + target.getName() + "'s lives to " + amount, NamedTextColor.GREEN));
        target.sendMessage(Component.text("Your lives have been set to " + amount, NamedTextColor.YELLOW));

        if (scoreboardManager != null) {
            scoreboardManager.updateAllPlayers();
        }
        return true;
    }

    private boolean handleGiveLifeCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
            return true;
        }

        if (!player.hasPermission("fitznethardcore.givelife")) {
            player.sendMessage(Component.text("No permission!", NamedTextColor.RED));
            return true;
        }

        if (!getConfig().getBoolean("EnableLifeGifting", true)) {
            player.sendMessage(Component.text("Life gifting is disabled!", NamedTextColor.RED));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(Component.text("Usage: /givelife <player>", NamedTextColor.RED));
            return true;
        }

        Player target = getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(Component.text("You cannot give a life to yourself!", NamedTextColor.RED));
            return true;
        }

        int senderLives = DatabaseManager.getLives(player);
        if (senderLives <= 1) {
            player.sendMessage(Component.text("You need at least 2 lives to give one away!", NamedTextColor.RED));
            return true;
        }

        int targetLives = DatabaseManager.getLives(target);
        int maxLives = getConfig().getInt("MaxLives", 3);
        if (targetLives >= maxLives) {
            player.sendMessage(Component.text(target.getName() + " already has maximum lives!", NamedTextColor.RED));
            return true;
        }

        // Transfer life
        BasicUtil.removeLife(player);
        BasicUtil.addLife(target, false); // Don't enforce cap for gifted lives

        player.sendMessage(Component.text("You gave 1 life to " + target.getName(), NamedTextColor.GREEN));
        target.sendMessage(Component.text(player.getName() + " gave you 1 life!", NamedTextColor.GREEN));

        // Broadcast
        if (getConfig().getBoolean("BroadcastDeaths", true)) {
            Component broadcast = Component.text(player.getName(), NamedTextColor.GOLD)
                .append(Component.text(" gave a life to ", NamedTextColor.YELLOW))
                .append(Component.text(target.getName(), NamedTextColor.GOLD));
            getServer().broadcast(broadcast);
        }

        if (scoreboardManager != null) {
            scoreboardManager.updateAllPlayers();
        }
        return true;
    }

    private boolean handleReviveCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
            return true;
        }

        if (!player.hasPermission("fitznethardcore.resurrect")) {
            player.sendMessage(Component.text("No permission!", NamedTextColor.RED));
            return true;
        }

        if (!getConfig().getBoolean("ResurrectionEnabled", true)) {
            player.sendMessage(Component.text("Resurrection is disabled!", NamedTextColor.RED));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(Component.text("Usage: /revive <player>", NamedTextColor.RED));
            return true;
        }

        Player target = getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
            return true;
        }

        if (target.equals(player)) {
            player.sendMessage(Component.text("You cannot resurrect yourself!", NamedTextColor.RED));
            return true;
        }

        PlayerData targetData = DatabaseManager.loadPlayerData(target);
        if (targetData == null || !targetData.isSpectator() || targetData.getLives() > 0) {
            player.sendMessage(Component.text(target.getName() + " is not dead!", NamedTextColor.RED));
            return true;
        }

        resurrectionManager.setResurrectionTarget(player, target);

        String itemName = getConfig().getString("ResurrectionItem", "DIAMOND_BLOCK").replace("_", " ").toLowerCase();
        player.sendMessage(Component.text("Resurrection target set to " + target.getName(), NamedTextColor.GREEN));
        player.sendMessage(Component.text("Place a " + itemName + " and right-click it while sneaking to revive them!", NamedTextColor.YELLOW));

        return true;
    }

    private boolean handleDebugCommand(CommandSender sender) {
        if (!sender.hasPermission("fitznethardcore.admin")) {
            sender.sendMessage(Component.text("No permission!", NamedTextColor.RED));
            return true;
        }

        sender.sendMessage(Component.text("=== FitzNet Debug ===", NamedTextColor.GOLD));

        for (Player player : getServer().getOnlinePlayers()) {
            int lives = DatabaseManager.getLives(player);
            boolean isSpectator = BasicUtil.isSpectator(player);

            Component line = Component.text(player.getName() + ": ", NamedTextColor.YELLOW)
                .append(Component.text(lives + " lives", NamedTextColor.WHITE))
                .append(Component.text(isSpectator ? " (DEAD)" : "", NamedTextColor.RED));

            sender.sendMessage(line);
        }

        return true;
    }
}
