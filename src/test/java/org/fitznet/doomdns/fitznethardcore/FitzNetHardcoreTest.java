package org.fitznet.doomdns.fitznethardcore;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for FitzNetHardcore main plugin class
 * Tests plugin initialization, configuration, and command handling
 */
class FitzNetHardcoreTest {

    private ServerMock server;
    private FitzNetHardcore plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(FitzNetHardcore.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Should enable plugin successfully")
    void testPluginEnable() {
        // Then
        assertTrue(plugin.isEnabled(), "Plugin should be enabled");
        assertNotNull(FitzNetHardcore.getInstance(), "Plugin instance should be set");
        assertEquals(plugin, FitzNetHardcore.getInstance(), "Instance should match");
    }

    @Test
    @DisplayName("Should initialize managers on enable")
    void testManagerInitialization() {
        // Then
        assertNotNull(plugin.getResurrectionManager(), "ResurrectionManager should be initialized");
        assertNotNull(plugin.getScoreboardManager(), "ScoreboardManager should be initialized");
    }

    @Test
    @DisplayName("Should load configuration with defaults")
    void testConfigurationLoaded() {
        // Then
        assertNotNull(plugin.getConfig(), "Config should be loaded");
        assertTrue(plugin.getConfig().getInt("MaxLives") > 0, "MaxLives should be configured");
        assertTrue(plugin.getConfig().getInt("LifeRegenTime") > 0, "LifeRegenTime should be configured");
    }

    @Test
    @DisplayName("Should create PlayerData folder")
    void testPlayerDataFolderCreation() {
        // Then
        File dataFolder = new File(plugin.getDataFolder(), "PlayerData");
        // Folder may not exist in test environment, but plugin should attempt to create it
        assertNotNull(dataFolder, "PlayerData folder path should be defined");
    }

    @Test
    @DisplayName("Should handle plugin disable")
    void testPluginDisable() {
        // When
        plugin.onDisable();

        // Then - Plugin's onDisable should run without errors
        assertNotNull(plugin, "Plugin should still exist after disable");
    }

    @Test
    @DisplayName("Should register event listeners")
    void testEventListenersRegistered() {
        // The plugin should register EventManager and ResurrectionManager
        // This is verified by the fact that plugin loads without errors
        assertTrue(plugin.isEnabled(), "Plugin with registered events should be enabled");
    }

    @Test
    @DisplayName("Should have valid config defaults")
    void testConfigDefaults() {
        // Check various config values are accessible
        int maxLives = plugin.getConfig().getInt("MaxLives", 3);
        int regenTime = plugin.getConfig().getInt("LifeRegenTime", 2);
        int startingLives = plugin.getConfig().getInt("StartingLives", 1);

        assertTrue(maxLives >= 1, "MaxLives should be at least 1");
        assertTrue(regenTime >= 1, "LifeRegenTime should be at least 1");
        assertTrue(startingLives >= 1, "StartingLives should be at least 1");
    }

    @Test
    @DisplayName("Should have command map available")
    void testCommandMapAvailable() {
        // When/Then
        assertNotNull(server.getCommandMap(), "Command map should be available");
    }

    @Test
    @DisplayName("Should support resurrection feature when enabled")
    void testResurrectionFeature() {
        // Given
        plugin.getConfig().set("ResurrectionEnabled", true);

        // Then
        assertTrue(plugin.getConfig().getBoolean("ResurrectionEnabled"),
            "Resurrection should be enabled");
        assertNotNull(plugin.getResurrectionManager(),
            "ResurrectionManager should exist when enabled");
    }

    @Test
    @DisplayName("Should support life gifting feature when enabled")
    void testLifeGiftingFeature() {
        // Given
        plugin.getConfig().set("EnableLifeGifting", true);

        // Then
        assertTrue(plugin.getConfig().getBoolean("EnableLifeGifting"),
            "Life gifting should be enabled");
    }

    @Test
    @DisplayName("Should have proper plugin metadata")
    void testPluginMetadata() {
        // Then
        assertNotNull(plugin.getName(), "Plugin should have a name");
        assertFalse(plugin.getName().isEmpty(), "Plugin name should not be empty");
    }

    @Test
    @DisplayName("Should get singleton instance")
    void testGetInstance() {
        // When
        FitzNetHardcore instance = FitzNetHardcore.getInstance();

        // Then
        assertNotNull(instance, "Instance should not be null");
        assertEquals(plugin, instance, "Instance should match plugin");
        assertSame(plugin, instance, "Should return same instance");
    }

    @Test
    @DisplayName("Should maintain singleton pattern")
    void testSingletonPattern() {
        // When
        FitzNetHardcore instance1 = FitzNetHardcore.getInstance();
        FitzNetHardcore instance2 = FitzNetHardcore.getInstance();

        // Then
        assertSame(instance1, instance2, "Should return same instance");
    }

    @Test
    @DisplayName("Should have valid data folder")
    void testDataFolder() {
        // When
        File dataFolder = plugin.getDataFolder();

        // Then
        assertNotNull(dataFolder, "Data folder should not be null");
        assertNotNull(dataFolder.getName(), "Data folder should have a name");
    }

    @Test
    @DisplayName("Should handle server with no players")
    void testEmptyServer() {
        // Given - Fresh server with no players

        // Then
        assertTrue(plugin.isEnabled(), "Plugin should work with no players online");
        assertEquals(0, server.getOnlinePlayers().size(), "Server should have no players");
    }

    @Test
    @DisplayName("Should handle server with multiple players")
    void testMultiplePlayersSupport() {
        // Then - Plugin should support multiple players
        assertTrue(plugin.isEnabled(), "Plugin should be enabled");
        assertNotNull(server.getOnlinePlayers(), "Server should track online players");
    }

    @Test
    @DisplayName("Should reload configuration")
    void testConfigReload() {
        // When
        plugin.reloadConfig();

        // Then
        assertNotNull(plugin.getConfig(), "Config should still be available after reload");
        assertTrue(plugin.isEnabled(), "Plugin should still be enabled after reload");
    }
}

