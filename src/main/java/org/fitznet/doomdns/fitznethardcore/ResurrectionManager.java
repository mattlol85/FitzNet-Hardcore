package org.fitznet.doomdns.fitznethardcore;

import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * ResurrectionManager - Handles player resurrection mechanics
 * Players can resurrect dead players by placing a diamond block and right-clicking it
 */
@Slf4j
public class ResurrectionManager implements Listener {
    private final FitzNetHardcore plugin;

    // Map of player UUID to their selected resurrection target UUID
    private final HashMap<UUID, UUID> resurrectionTargets = new HashMap<>();

    // Optional cooldown tracking
    private final HashMap<UUID, Long> resurrectionCooldowns = new HashMap<>();

    public ResurrectionManager(FitzNetHardcore plugin) {
        this.plugin = plugin;
    }

    /**
     * Set resurrection target for a player
     */
    public void setResurrectionTarget(Player resurrector, Player target) {
        resurrectionTargets.put(resurrector.getUniqueId(), target.getUniqueId());
    }

    /**
     * Get resurrection target for a player
     */
    public UUID getResurrectionTarget(Player resurrector) {
        return resurrectionTargets.get(resurrector.getUniqueId());
    }

    /**
     * Clear resurrection target for a player
     */
    public void clearResurrectionTarget(Player resurrector) {
        resurrectionTargets.remove(resurrector.getUniqueId());
    }

    /**
     * Check if player is on cooldown
     */
    public boolean isOnCooldown(Player player) {
        int cooldownMinutes = plugin.getConfig().getInt("ResurrectionCooldownMinutes", 0);
        if (cooldownMinutes <= 0) {
            return false;
        }

        Long lastResurrection = resurrectionCooldowns.get(player.getUniqueId());
        if (lastResurrection == null) {
            return false;
        }

        long cooldownMillis = cooldownMinutes * 60 * 1000L;
        return System.currentTimeMillis() - lastResurrection < cooldownMillis;
    }

    /**
     * Get remaining cooldown time in seconds
     */
    public long getRemainingCooldown(Player player) {
        int cooldownMinutes = plugin.getConfig().getInt("ResurrectionCooldownMinutes", 0);
        if (cooldownMinutes <= 0) {
            return 0;
        }

        Long lastResurrection = resurrectionCooldowns.get(player.getUniqueId());
        if (lastResurrection == null) {
            return 0;
        }

        long cooldownMillis = cooldownMinutes * 60 * 1000L;
        long elapsed = System.currentTimeMillis() - lastResurrection;
        long remaining = (cooldownMillis - elapsed) / 1000;
        return Math.max(0, remaining);
    }

    /**
     * Handle player interaction with blocks
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if resurrection is enabled
        if (!plugin.getConfig().getBoolean("ResurrectionEnabled", true)) {
            return;
        }

        Player resurrector = event.getPlayer();

        // Only handle right-click on blocks while sneaking
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !resurrector.isSneaking()) {
            return;
        }

        // Check if clicking the configured resurrection item
        String configuredMaterial = plugin.getConfig().getString("ResurrectionItem", "DIAMOND_BLOCK");
        Material resurrectionMaterial = Material.getMaterial(configuredMaterial);

        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != resurrectionMaterial) {
            return;
        }

        // Check if resurrector has permission
        if (!resurrector.hasPermission("fitznethardcore.resurrect")) {
            resurrector.sendMessage(Component.text("You don't have permission to resurrect players!", NamedTextColor.RED));
            return;
        }

        // Check if player has set a resurrection target
        UUID targetUUID = getResurrectionTarget(resurrector);
        if (targetUUID == null) {
            resurrector.sendMessage(Component.text("No resurrection target set! Use ", NamedTextColor.RED)
                .append(Component.text("/revive <player>", NamedTextColor.YELLOW))
                .append(Component.text(" first.", NamedTextColor.RED)));
            return;
        }

        // Check cooldown
        if (isOnCooldown(resurrector)) {
            long remaining = getRemainingCooldown(resurrector);
            resurrector.sendMessage(Component.text("You must wait " + remaining + " seconds before resurrecting again!", NamedTextColor.RED));
            return;
        }

        // Get target player
        Player target = plugin.getServer().getPlayer(targetUUID);
        if (target == null || !target.isOnline()) {
            resurrector.sendMessage(Component.text("Target player is not online!", NamedTextColor.RED));
            clearResurrectionTarget(resurrector);
            return;
        }

        // Check if target is actually dead/spectator
        PlayerData targetData = DatabaseManager.loadPlayerData(targetUUID);
        if (targetData == null) {
            resurrector.sendMessage(Component.text("Could not load target player data!", NamedTextColor.RED));
            return;
        }

        if (!targetData.isSpectator() || targetData.getLives() > 0) {
            resurrector.sendMessage(Component.text(target.getName() + " is not dead and doesn't need resurrection!", NamedTextColor.RED));
            clearResurrectionTarget(resurrector);
            return;
        }

        // Perform resurrection
        performResurrection(resurrector, target, event.getClickedBlock().getLocation());

        // Consume the diamond block
        event.getClickedBlock().setType(Material.AIR);

        // Cancel the event to prevent any default behavior
        event.setCancelled(true);
    }

    /**
     * Perform the resurrection ritual
     */
    private void performResurrection(Player resurrector, Player target, org.bukkit.Location reviveLocation) {
        // Grant one life to target
        PlayerData targetData = DatabaseManager.loadPlayerData(target);
        targetData.setLives(1);
        targetData.setSpectator(false);
        DatabaseManager.savePlayerData(targetData);

        // Teleport target to resurrection location (async for Folia compatibility)
        org.bukkit.Location teleportLocation = reviveLocation.clone().add(0, 1, 0); // Slightly above the block
        target.teleportAsync(teleportLocation).thenAccept(success -> {
            if (success) {
                // Set game mode to survival
                target.setGameMode(GameMode.SURVIVAL);

                // Visual effects
                target.getWorld().strikeLightningEffect(reviveLocation);
                target.getWorld().strikeLightningEffect(target.getLocation());
            } else {
                log.warn("Failed to teleport {} during resurrection", target.getName());
            }
        });

        // Messages
        target.sendMessage(Component.text("You have been resurrected by ", NamedTextColor.GREEN)
            .append(Component.text(resurrector.getName(), NamedTextColor.GOLD))
            .append(Component.text("!", NamedTextColor.GREEN)));
        target.sendMessage(Component.text("You now have 1 life. Be careful!", NamedTextColor.YELLOW));

        resurrector.sendMessage(Component.text("You have successfully resurrected ", NamedTextColor.GREEN)
            .append(Component.text(target.getName(), NamedTextColor.GOLD))
            .append(Component.text("!", NamedTextColor.GREEN)));

        // Broadcast if enabled
        if (plugin.getConfig().getBoolean("BroadcastDeaths", true)) {
            Component broadcast = Component.text(resurrector.getName(), NamedTextColor.GOLD)
                .append(Component.text(" has resurrected ", NamedTextColor.GREEN))
                .append(Component.text(target.getName(), NamedTextColor.GOLD))
                .append(Component.text("!", NamedTextColor.GREEN));
            plugin.getServer().broadcast(broadcast);
        }

        // Clear resurrection target
        clearResurrectionTarget(resurrector);

        // Set cooldown
        int cooldownMinutes = plugin.getConfig().getInt("ResurrectionCooldownMinutes", 0);
        if (cooldownMinutes > 0) {
            resurrectionCooldowns.put(resurrector.getUniqueId(), System.currentTimeMillis());
        }

        // Update scoreboards
        if (plugin.getScoreboardManager() != null) {
            plugin.getScoreboardManager().updateAllPlayers();
        }

        log.info("{} resurrected {} at {}", resurrector.getName(), target.getName(), reviveLocation);
    }
}

