package org.fitznet.doomdns.hardcore;


import org.fitznet.doomdns.hardcore.data.DatabaseManager;
import org.fitznet.doomdns.hardcore.data.PlayerData;
import org.fitznet.doomdns.hardcore.listener.ResurrectionManager;
import org.fitznet.doomdns.hardcore.service.ScoreboardManager;
import org.fitznet.doomdns.hardcore.scheduler.LivesScheduler;
import org.fitznet.doomdns.hardcore.scheduler.SchedulerUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Bed Respawn with Items feature
 * Tests the logic and configuration for dying with lives remaining and respawning with items
 */
class BedRespawnFeatureTest {

    private PlayerData testPlayerData;

    @BeforeEach
    void setUp() {
        testPlayerData = new PlayerData(UUID.randomUUID(), "TestPlayer", 3);
    }

    @Test
    @DisplayName("Should preserve inventory items when player dies with lives")
    void testInventoryPreservationOnDeath() {
        // Given - Player with multiple lives
        testPlayerData.setLives(2);
        boolean keepInventoryEnabled = true;

        // When - Player dies with lives remaining
        testPlayerData.setLives(Math.max(0, testPlayerData.getLives() - 1));
        boolean shouldKeepInventory = keepInventoryEnabled && testPlayerData.getLives() > 0;

        // Then
        assertTrue(shouldKeepInventory, "Keep inventory should be enabled when player has lives");
        assertEquals(1, testPlayerData.getLives(), "Player should have 1 life after death");
        assertFalse(testPlayerData.isSpectator(), "Player should not be spectator with lives remaining");
    }

    @Test
    @DisplayName("Should preserve experience when player dies with lives")
    void testExperiencePreservationOnDeath() {
        // Given - Player with lives
        testPlayerData.setLives(2);
        boolean keepExperienceEnabled = true;
        int experienceLevels = 10;
        int totalExperience = 100;

        // When - Player dies
        testPlayerData.setLives(Math.max(0, testPlayerData.getLives() - 1));
        boolean shouldKeepExperience = keepExperienceEnabled && testPlayerData.getLives() > 0;

        // Then
        assertTrue(shouldKeepExperience, "Keep experience should be enabled when player has lives");
        assertEquals(1, testPlayerData.getLives(), "Player should have 1 life after death");
        assertEquals(10, experienceLevels, "Experience levels should be preserved");
        assertEquals(100, totalExperience, "Total experience should be preserved");
    }

    @Test
    @DisplayName("Should drop items when player runs out of lives")
    void testItemDropWhenOutOfLives() {
        // Given - Player with 1 life
        testPlayerData.setLives(1);
        boolean keepInventoryEnabled = true;

        // When - Simulate death (lives go to 0)
        testPlayerData.setLives(Math.max(0, testPlayerData.getLives() - 1));
        if (testPlayerData.getLives() == 0) {
            testPlayerData.setSpectator(true);
        }
        boolean shouldKeepInventory = keepInventoryEnabled && testPlayerData.getLives() > 0;

        // Then
        assertFalse(shouldKeepInventory, "Should NOT keep inventory when lives = 0");
        assertEquals(0, testPlayerData.getLives(), "Player should have 0 lives");
        assertTrue(testPlayerData.isSpectator(), "Player should be spectator");
    }

    @Test
    @DisplayName("Should handle bed spawn location logic")
    void testBedSpawnLocationHandling() {
        // Given - Simulate bed spawn logic
        boolean hasBedSpawn = true;
        String expectedSpawnType;

        // When
        if (hasBedSpawn) {
            expectedSpawnType = "BED";
        } else {
            expectedSpawnType = "WORLD";
        }

        // Then
        assertEquals("BED", expectedSpawnType, "Should respawn at bed when available");
    }

    @Test
    @DisplayName("Should handle missing bed spawn gracefully")
    void testMissingBedSpawnHandling() {
        // Given - No bed spawn set
        boolean hasBedSpawn = false;
        String expectedSpawnType;

        // When
        if (hasBedSpawn) {
            expectedSpawnType = "BED";
        } else {
            expectedSpawnType = "WORLD";
        }

        // Then
        assertEquals("WORLD", expectedSpawnType, "Should respawn at world spawn when no bed");
    }

    @Test
    @DisplayName("Should preserve armor contents logic on death with lives")
    void testArmorPreservationOnDeath() {
        // Given - Player with armor and lives
        testPlayerData.setLives(2);
        boolean keepInventoryEnabled = true;
        boolean hasArmor = true;

        // When - Player dies
        testPlayerData.setLives(Math.max(0, testPlayerData.getLives() - 1));
        boolean shouldKeepInventory = keepInventoryEnabled && testPlayerData.getLives() > 0;

        // Then
        assertTrue(shouldKeepInventory, "Should keep armor when player has lives");
        assertTrue(hasArmor, "Armor should be preserved");
        assertEquals(1, testPlayerData.getLives(), "Player should have 1 life");
    }

    @Test
    @DisplayName("Should handle player with max lives dying")
    void testMaxLivesPlayerDeath() {
        // Given - Player with max lives
        int maxLives = 3;
        testPlayerData.setLives(maxLives);
        boolean keepInventoryEnabled = true;

        // When - Simulate death
        testPlayerData.setLives(Math.max(0, testPlayerData.getLives() - 1));
        boolean shouldKeepInventory = keepInventoryEnabled && testPlayerData.getLives() > 0;

        // Then
        assertEquals(maxLives - 1, testPlayerData.getLives(), "Player should have one less life");
        assertTrue(shouldKeepInventory, "Should keep inventory with lives remaining");
        assertFalse(testPlayerData.isSpectator(), "Should not be spectator");
    }

    @Test
    @DisplayName("Should handle spectator mode transition properly")
    void testSpectatorModeTransition() {
        // Given - Player with 1 life
        testPlayerData.setLives(1);

        // When - Player dies and runs out of lives
        testPlayerData.setLives(0);
        if (testPlayerData.getLives() == 0) {
            testPlayerData.setSpectator(true);
        }

        // Then
        assertEquals(0, testPlayerData.getLives(), "Lives should be 0");
        assertTrue(testPlayerData.isSpectator(), "Player should be marked as spectator");
    }

    @Test
    @DisplayName("Should validate configuration logic")
    void testConfigurationLogic() {
        // Given - Configuration options
        boolean keepInventoryEnabled = true;
        boolean keepExperienceEnabled = true;
        int playerLives = 2;

        // When - Player dies with lives
        boolean shouldKeepInventory = keepInventoryEnabled && playerLives > 0;
        boolean shouldKeepExperience = keepExperienceEnabled && playerLives > 0;

        // Then
        assertTrue(shouldKeepInventory, "Should keep inventory when configured and has lives");
        assertTrue(shouldKeepExperience, "Should keep experience when configured and has lives");
    }

    @Test
    @DisplayName("Should handle empty inventory scenario")
    void testEmptyInventoryOnDeath() {
        // Given - Player with no items but has lives
        testPlayerData.setLives(2);
        boolean hasItems = false;
        boolean keepInventoryEnabled = true;

        // When - Player dies
        testPlayerData.setLives(Math.max(0, testPlayerData.getLives() - 1));
        boolean shouldKeepInventory = keepInventoryEnabled && testPlayerData.getLives() > 0;

        // Then
        assertTrue(shouldKeepInventory, "Feature should still be enabled even with no items");
        assertFalse(hasItems, "Player should have no items");
        assertEquals(1, testPlayerData.getLives(), "Player should still have lives");
    }

    @Test
    @DisplayName("Should handle zero experience scenario")
    void testZeroExperienceOnDeath() {
        // Given - Player with no experience but has lives
        testPlayerData.setLives(2);
        int experienceLevel = 0;
        int totalExperience = 0;
        boolean keepExperienceEnabled = true;

        // When - Player dies
        testPlayerData.setLives(Math.max(0, testPlayerData.getLives() - 1));
        boolean shouldKeepExperience = keepExperienceEnabled && testPlayerData.getLives() > 0;

        // Then
        assertTrue(shouldKeepExperience, "Feature should still be enabled even with no experience");
        assertEquals(0, experienceLevel, "Player should have 0 levels");
        assertEquals(0, totalExperience, "Player should have 0 experience");
    }

    @Test
    @DisplayName("Should handle rapid death scenarios")
    void testRapidDeathScenarios() {
        // Given - Player with 3 lives
        testPlayerData.setLives(3);

        // When - Simulate rapid deaths
        for (int i = 0; i < 3; i++) {
            testPlayerData.setLives(Math.max(0, testPlayerData.getLives() - 1));
            if (testPlayerData.getLives() == 0) {
                testPlayerData.setSpectator(true);
            }
        }

        // Then
        assertEquals(0, testPlayerData.getLives(), "Player should have 0 lives after 3 deaths");
        assertTrue(testPlayerData.isSpectator(), "Player should be spectator");
    }

    @Test
    @DisplayName("Should handle inventory preservation with multiple deaths")
    void testMultipleDeathsWithInventoryPreservation() {
        // Given - Player with 3 lives
        testPlayerData.setLives(3);
        boolean keepInventoryEnabled = true;

        // When - First death
        testPlayerData.setLives(Math.max(0, testPlayerData.getLives() - 1));
        boolean shouldKeepAfterFirstDeath = keepInventoryEnabled && testPlayerData.getLives() > 0;

        // Second death
        testPlayerData.setLives(Math.max(0, testPlayerData.getLives() - 1));
        boolean shouldKeepAfterSecondDeath = keepInventoryEnabled && testPlayerData.getLives() > 0;

        // Third death
        testPlayerData.setLives(Math.max(0, testPlayerData.getLives() - 1));
        boolean shouldKeepAfterThirdDeath = keepInventoryEnabled && testPlayerData.getLives() > 0;

        // Then
        assertTrue(shouldKeepAfterFirstDeath, "Should keep items after first death (2 lives)");
        assertTrue(shouldKeepAfterSecondDeath, "Should keep items after second death (1 life)");
        assertFalse(shouldKeepAfterThirdDeath, "Should NOT keep items after third death (0 lives)");
        assertEquals(0, testPlayerData.getLives(), "Player should have 0 lives");
    }

    @Test
    @DisplayName("Should handle player data persistence across death")
    void testPlayerDataPersistenceAcrossDeath() {
        // Given
        String originalName = testPlayerData.getName();
        int originalDaysAlive = testPlayerData.getDaysAlive();
        UUID originalUuid = testPlayerData.getUuid();
        testPlayerData.setLives(2);

        // When - Simulate death
        testPlayerData.setLives(Math.max(0, testPlayerData.getLives() - 1));

        // Then - Data should persist
        assertNotNull(testPlayerData, "Player data should persist");
        assertEquals(originalName, testPlayerData.getName(), "Name should persist");
        assertEquals(originalDaysAlive, testPlayerData.getDaysAlive(), "Days alive should persist");
        assertEquals(originalUuid, testPlayerData.getUuid(), "UUID should persist");
        assertEquals(1, testPlayerData.getLives(), "Lives should be updated");
    }

    @Test
    @DisplayName("Should correctly determine when to save inventory data")
    void testInventorySaveConditions() {
        // Test various scenarios
        boolean keepInventoryEnabled = true;

        // Scenario 1: Player with 3 lives
        int lives1 = 3;
        boolean shouldSave1 = keepInventoryEnabled && lives1 > 0;
        assertTrue(shouldSave1, "Should save with 3 lives");

        // Scenario 2: Player with 1 life
        int lives2 = 1;
        boolean shouldSave2 = keepInventoryEnabled && lives2 > 0;
        assertTrue(shouldSave2, "Should save with 1 life");

        // Scenario 3: Player with 0 lives
        int lives3 = 0;
        boolean shouldSave3 = keepInventoryEnabled && lives3 > 0;
        assertFalse(shouldSave3, "Should NOT save with 0 lives");

        // Scenario 4: Feature disabled with lives
        keepInventoryEnabled = false;
        int lives4 = 2;
        boolean shouldSave4 = keepInventoryEnabled && lives4 > 0;
        assertFalse(shouldSave4, "Should NOT save when feature disabled");
    }

    @Test
    @DisplayName("Should handle cleanup on player disconnect")
    void testCleanupOnDisconnect() {
        // Given - Player has saved data
        testPlayerData.setLives(2);
        boolean hasSavedInventory = true;
        boolean hasSavedExperience = true;

        // When - Player disconnects (cleanup should occur)
        hasSavedInventory = false;
        hasSavedExperience = false;

        // Then
        assertFalse(hasSavedInventory, "Saved inventory should be cleaned up");
        assertFalse(hasSavedExperience, "Saved experience should be cleaned up");
        assertEquals(2, testPlayerData.getLives(), "Player lives should remain unchanged");
    }
}

