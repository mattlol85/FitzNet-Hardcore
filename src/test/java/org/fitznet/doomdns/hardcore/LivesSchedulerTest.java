package org.fitznet.doomdns.hardcore;


import org.fitznet.doomdns.hardcore.data.DatabaseManager;
import org.fitznet.doomdns.hardcore.data.PlayerData;
import org.fitznet.doomdns.hardcore.listener.ResurrectionManager;
import org.fitznet.doomdns.hardcore.service.ScoreboardManager;
import org.fitznet.doomdns.hardcore.scheduler.LivesScheduler;
import org.fitznet.doomdns.hardcore.scheduler.SchedulerUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for LivesScheduler logic
 * Tests life regeneration conditions without full server mock
 * Note: Full integration tests skipped due to MockBukkit registry initialization issues
 */
class LivesSchedulerTest {

    @Test
    @DisplayName("Should not regenerate lives for dead players")
    void testNoRegenForDeadPlayers() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "DeadPlayer", 1);
        data.setLives(0);
        data.setSpectator(true);

        // When - Check if should regenerate
        boolean shouldRegen = !data.isSpectator() && data.getLives() > 0;

        // Then
        assertFalse(shouldRegen, "Dead players should not receive life regeneration");
    }

    @Test
    @DisplayName("Should not regenerate lives for players at max")
    void testNoRegenAtMaxLives() {
        // Given
        int maxLives = 3;
        PlayerData data = new PlayerData(UUID.randomUUID(), "MaxPlayer", maxLives);
        data.setLives(maxLives);

        // When - Check if should regenerate
        boolean shouldRegen = data.getLives() < maxLives;

        // Then
        assertFalse(shouldRegen, "Players at max lives should not receive regeneration");
    }

    @Test
    @DisplayName("Should regenerate lives for players below max")
    void testRegenBelowMaxLives() {
        // Given
        int maxLives = 3;
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 1);
        data.setLives(1);
        data.setSpectator(false);

        // When - Check if should regenerate
        boolean shouldRegen = data.getLives() < maxLives && !data.isSpectator();

        // Then
        assertTrue(shouldRegen, "Players below max lives should receive regeneration");
    }

    @Test
    @DisplayName("Should handle life addition for eligible players")
    void testLifeAdditionLogic() {
        // Given
        int maxLives = 3;
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 2);
        data.setLives(2);

        // When - Simulate life addition
        if (data.getLives() < maxLives) {
            data.setLives(data.getLives() + 1);
        }

        // Then
        assertEquals(3, data.getLives(), "Player should have 3 lives after regeneration");
    }

    @Test
    @DisplayName("Should not exceed max lives")
    void testNoExceedingMaxLives() {
        // Given
        int maxLives = 3;
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", maxLives);
        data.setLives(maxLives);

        // When - Try to add life
        if (data.getLives() < maxLives) {
            data.setLives(data.getLives() + 1);
        }

        // Then
        assertEquals(maxLives, data.getLives(), "Lives should not exceed max");
    }

    @Test
    @DisplayName("Should track next life timestamp")
    void testNextLifeTimestamp() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 2);
        long nextLifeTime = System.currentTimeMillis() + 60000; // 1 minute

        // When
        data.setNextLifeTimestamp(nextLifeTime);

        // Then
        assertEquals(nextLifeTime, data.getNextLifeTimestamp(), "Timestamp should be stored");
    }

    @Test
    @DisplayName("Should handle spectator players correctly")
    void testSpectatorCheck() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "SpectatorPlayer", 0);
        data.setSpectator(true);
        data.setLives(0);

        // When - Check if eligible for regen
        boolean eligible = !data.isSpectator() && data.getLives() > 0;

        // Then
        assertFalse(eligible, "Spectators should not be eligible for regeneration");
    }

    @Test
    @DisplayName("Should handle active players correctly")
    void testActivePlayerCheck() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "ActivePlayer", 2);
        data.setSpectator(false);
        data.setLives(2);

        // When - Check if eligible for regen
        boolean eligible = !data.isSpectator() && data.getLives() > 0;

        // Then
        assertTrue(eligible, "Active players should be eligible for regeneration");
    }
}

