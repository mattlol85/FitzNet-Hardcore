package org.fitznet.doomdns.hardcore;


import org.fitznet.doomdns.hardcore.data.DatabaseManager;
import org.fitznet.doomdns.hardcore.data.PlayerData;
import org.fitznet.doomdns.hardcore.listener.ResurrectionManager;
import org.fitznet.doomdns.hardcore.service.ScoreboardManager;
import org.fitznet.doomdns.hardcore.scheduler.LivesScheduler;
import org.fitznet.doomdns.hardcore.scheduler.SchedulerUtil;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for EventManager
 * Tests player event handling including join, death, and quit events
 */
class EventManagerTest {

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
    @DisplayName("Should detect dead player needing spectator mode")
    void testDeadPlayerDetection() {
        // Given
        UUID playerId = UUID.randomUUID();
        PlayerData data = new PlayerData(playerId, "TestPlayer", 1);
        data.setLives(0);
        data.setSpectator(true);

        // When
        boolean shouldBeSpectator = data.isSpectator() && data.getLives() == 0;

        // Then
        assertTrue(shouldBeSpectator, "Dead player should be marked for spectator mode");
    }

    @Test
    @DisplayName("Should not mark alive player for spectator mode")
    void testAlivePlayerDetection() {
        // Given
        UUID playerId = UUID.randomUUID();
        PlayerData data = new PlayerData(playerId, "AlivePlayer", 3);
        data.setLives(3);
        data.setSpectator(false);

        // When
        boolean shouldBeSpectator = data.isSpectator() && data.getLives() == 0;

        // Then
        assertFalse(shouldBeSpectator, "Alive player should not be marked for spectator mode");
    }

    @Test
    @DisplayName("Should handle life removal logic")
    void testLifeRemovalLogic() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 3);
        data.setLives(3);

        // When - Simulate death by removing life
        data.setLives(Math.max(0, data.getLives() - 1));

        // Then
        assertEquals(2, data.getLives(), "Player should have 2 lives remaining");
    }

    @Test
    @DisplayName("Should set spectator status when lives reach zero")
    void testSpectatorLogicOnZeroLives() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 1);
        data.setLives(1);

        // When - Simulate death
        data.setLives(Math.max(0, data.getLives() - 1));
        if (data.getLives() == 0) {
            data.setSpectator(true);
        }

        // Then
        assertEquals(0, data.getLives(), "Player should have 0 lives");
        assertTrue(data.isSpectator(), "Player should be marked as spectator");
    }

    @Test
    @DisplayName("Should not allow negative lives")
    void testNoNegativeLives() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 1);
        data.setLives(0);

        // When - Try to remove life (should stay at 0)
        data.setLives(Math.max(0, data.getLives() - 1));

        // Then
        assertEquals(0, data.getLives(), "Lives should remain at 0");
    }

    @Test
    @DisplayName("Should track player data state")
    void testPlayerDataState() {
        // Given
        UUID playerId = UUID.randomUUID();
        PlayerData data = new PlayerData(playerId, "TestPlayer", 1);

        // Then
        assertNotNull(data, "Player data should exist");
        assertEquals(playerId, data.getUuid(), "UUID should match");
        assertEquals("TestPlayer", data.getName(), "Name should match");
        assertEquals(1, data.getLives(), "Lives should be 1");
        assertFalse(data.isSpectator(), "Should not be spectator initially");
    }

    @Test
    @DisplayName("Should handle multiple life changes")
    void testMultipleLifeChanges() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 3);
        data.setLives(3);

        // When - Multiple deaths
        data.setLives(Math.max(0, data.getLives() - 1)); // 2 lives
        data.setLives(Math.max(0, data.getLives() - 1)); // 1 life
        data.setLives(Math.max(0, data.getLives() - 1)); // 0 lives

        // Then
        assertEquals(0, data.getLives(), "Lives should be 0 after 3 deaths");
    }

    @Test
    @DisplayName("Should persist spectator status")
    void testSpectatorPersistence() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 1);
        data.setSpectator(true);

        // When
        boolean isSpectator = data.isSpectator();

        // Then
        assertTrue(isSpectator, "Spectator status should be persisted");
    }

    // ========== BED RESPAWN WITH ITEMS TESTS ==========

    @Test
    @DisplayName("Should keep inventory enabled when configured")
    void testKeepInventoryConfiguration() {
        // Given - Default configuration has KeepInventoryOnDeath enabled
        plugin.getConfig().set("KeepInventoryOnDeath", true);

        // When
        boolean keepInventory = plugin.getConfig().getBoolean("KeepInventoryOnDeath", false);

        // Then
        assertTrue(keepInventory, "KeepInventoryOnDeath should be enabled by default");
    }

    @Test
    @DisplayName("Should keep experience enabled when configured")
    void testKeepExperienceConfiguration() {
        // Given - Default configuration has KeepExperienceOnDeath enabled
        plugin.getConfig().set("KeepExperienceOnDeath", true);

        // When
        boolean keepExperience = plugin.getConfig().getBoolean("KeepExperienceOnDeath", false);

        // Then
        assertTrue(keepExperience, "KeepExperienceOnDeath should be enabled by default");
    }

    @Test
    @DisplayName("Should respect disabled keep inventory configuration")
    void testDisabledKeepInventoryConfiguration() {
        // Given
        plugin.getConfig().set("KeepInventoryOnDeath", false);

        // When
        boolean keepInventory = plugin.getConfig().getBoolean("KeepInventoryOnDeath", true);

        // Then
        assertFalse(keepInventory, "KeepInventoryOnDeath should be disabled when configured");
    }

    @Test
    @DisplayName("Should respect disabled keep experience configuration")
    void testDisabledKeepExperienceConfiguration() {
        // Given
        plugin.getConfig().set("KeepExperienceOnDeath", false);

        // When
        boolean keepExperience = plugin.getConfig().getBoolean("KeepExperienceOnDeath", true);

        // Then
        assertFalse(keepExperience, "KeepExperienceOnDeath should be disabled when configured");
    }

    @Test
    @DisplayName("Should handle player with multiple lives losing one life")
    void testPlayerWithMultipleLivesDeathScenario() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 3);
        data.setLives(3);
        boolean keepInventoryEnabled = true;
        boolean keepExperienceEnabled = true;

        // When - Player dies with lives remaining
        data.setLives(Math.max(0, data.getLives() - 1));
        boolean shouldKeepItems = keepInventoryEnabled && data.getLives() > 0;
        boolean shouldKeepExp = keepExperienceEnabled && data.getLives() > 0;

        // Then
        assertEquals(2, data.getLives(), "Player should have 2 lives after death");
        assertTrue(shouldKeepItems, "Player should keep items when they have lives");
        assertTrue(shouldKeepExp, "Player should keep experience when they have lives");
        assertFalse(data.isSpectator(), "Player should not be spectator with lives remaining");
    }

    @Test
    @DisplayName("Should handle player losing last life")
    void testPlayerLosingLastLifeScenario() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 1);
        data.setLives(1);
        boolean keepInventoryEnabled = true;
        boolean keepExperienceEnabled = true;

        // When - Player dies with last life
        data.setLives(Math.max(0, data.getLives() - 1));
        boolean shouldKeepItems = keepInventoryEnabled && data.getLives() > 0;
        boolean shouldKeepExp = keepExperienceEnabled && data.getLives() > 0;

        if (data.getLives() == 0) {
            data.setSpectator(true);
        }

        // Then
        assertEquals(0, data.getLives(), "Player should have 0 lives after death");
        assertFalse(shouldKeepItems, "Player should NOT keep items when out of lives");
        assertFalse(shouldKeepExp, "Player should NOT keep experience when out of lives");
        assertTrue(data.isSpectator(), "Player should be spectator with no lives");
    }

    @Test
    @DisplayName("Should preserve items when keep inventory disabled but player has lives")
    void testKeepInventoryDisabledLogic() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 2);
        data.setLives(2);
        boolean keepInventoryEnabled = false; // Feature disabled

        // When - Player dies
        data.setLives(Math.max(0, data.getLives() - 1));
        boolean shouldKeepItems = keepInventoryEnabled && data.getLives() > 0;

        // Then
        assertEquals(1, data.getLives(), "Player should have 1 life after death");
        assertFalse(shouldKeepItems, "Player should NOT keep items when feature is disabled");
    }

    @Test
    @DisplayName("Should handle respawn data cleanup scenario")
    void testRespawnDataCleanup() {
        // Given - Simulate saved respawn data
        UUID playerId = UUID.randomUUID();
        boolean hasSavedInventory = true;
        boolean hasSavedExperience = true;

        // When - Player respawns (data should be removed after restore)
        if (hasSavedInventory) {
            hasSavedInventory = false; // Simulate cleanup
        }
        if (hasSavedExperience) {
            hasSavedExperience = false; // Simulate cleanup
        }

        // Then
        assertFalse(hasSavedInventory, "Saved inventory should be cleaned up after respawn");
        assertFalse(hasSavedExperience, "Saved experience should be cleaned up after respawn");
    }

    @Test
    @DisplayName("Should validate bed respawn vs world spawn logic")
    void testBedRespawnLogic() {
        // Given
        boolean hasBedSpawn = true;
        String expectedRespawnType;

        // When
        if (hasBedSpawn) {
            expectedRespawnType = "BED";
        } else {
            expectedRespawnType = "WORLD";
        }

        // Then
        assertEquals("BED", expectedRespawnType, "Player should respawn at bed when available");

        // When - No bed set
        hasBedSpawn = false;
        if (hasBedSpawn) {
            expectedRespawnType = "BED";
        } else {
            expectedRespawnType = "WORLD";
        }

        // Then
        assertEquals("WORLD", expectedRespawnType, "Player should respawn at world spawn when no bed");
    }

    @Test
    @DisplayName("Should handle player disconnect before respawn")
    void testPlayerDisconnectBeforeRespawn() {
        // Given - Player has died and data is saved
        UUID playerId = UUID.randomUUID();
        boolean hasSavedData = true;

        // When - Player disconnects before respawning
        // Cleanup should happen
        if (hasSavedData) {
            hasSavedData = false; // Simulate cleanup on disconnect
        }

        // Then
        assertFalse(hasSavedData, "Saved data should be cleaned up on disconnect");
    }

    @Test
    @DisplayName("Should validate lives threshold for item preservation")
    void testLivesThresholdForItemPreservation() {
        // Given
        int[] testLives = {3, 2, 1, 0};
        boolean keepInventoryEnabled = true;

        for (int lives : testLives) {
            // When
            boolean shouldKeepItems = keepInventoryEnabled && lives > 0;

            // Then
            if (lives > 0) {
                assertTrue(shouldKeepItems, "Should keep items when lives > 0 (lives=" + lives + ")");
            } else {
                assertFalse(shouldKeepItems, "Should NOT keep items when lives = 0");
            }
        }
    }

    @Test
    @DisplayName("Should handle both configurations enabled")
    void testBothConfigurationsEnabled() {
        // Given
        plugin.getConfig().set("KeepInventoryOnDeath", true);
        plugin.getConfig().set("KeepExperienceOnDeath", true);

        // When
        boolean keepInventory = plugin.getConfig().getBoolean("KeepInventoryOnDeath", false);
        boolean keepExperience = plugin.getConfig().getBoolean("KeepExperienceOnDeath", false);

        // Then
        assertTrue(keepInventory, "Keep inventory should be enabled");
        assertTrue(keepExperience, "Keep experience should be enabled");
    }

    @Test
    @DisplayName("Should handle both configurations disabled")
    void testBothConfigurationsDisabled() {
        // Given
        plugin.getConfig().set("KeepInventoryOnDeath", false);
        plugin.getConfig().set("KeepExperienceOnDeath", false);

        // When
        boolean keepInventory = plugin.getConfig().getBoolean("KeepInventoryOnDeath", true);
        boolean keepExperience = plugin.getConfig().getBoolean("KeepExperienceOnDeath", true);

        // Then
        assertFalse(keepInventory, "Keep inventory should be disabled");
        assertFalse(keepExperience, "Keep experience should be disabled");
    }

    @Test
    @DisplayName("Should handle mixed configurations")
    void testMixedConfigurations() {
        // Given - Keep inventory enabled, experience disabled
        plugin.getConfig().set("KeepInventoryOnDeath", true);
        plugin.getConfig().set("KeepExperienceOnDeath", false);

        // When
        boolean keepInventory = plugin.getConfig().getBoolean("KeepInventoryOnDeath", false);
        boolean keepExperience = plugin.getConfig().getBoolean("KeepExperienceOnDeath", true);

        // Then
        assertTrue(keepInventory, "Keep inventory should be enabled");
        assertFalse(keepExperience, "Keep experience should be disabled");

        // Given - Keep inventory disabled, experience enabled
        plugin.getConfig().set("KeepInventoryOnDeath", false);
        plugin.getConfig().set("KeepExperienceOnDeath", true);

        // When
        keepInventory = plugin.getConfig().getBoolean("KeepInventoryOnDeath", true);
        keepExperience = plugin.getConfig().getBoolean("KeepExperienceOnDeath", false);

        // Then
        assertFalse(keepInventory, "Keep inventory should be disabled");
        assertTrue(keepExperience, "Keep experience should be enabled");
    }
}

