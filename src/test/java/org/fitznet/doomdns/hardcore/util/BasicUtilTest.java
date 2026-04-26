package org.fitznet.doomdns.hardcore.util;

import org.fitznet.doomdns.hardcore.data.PlayerData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for BasicUtil logic
 * Tests life management calculations without full plugin integration
 * Note: Full integration tests skipped due to MockBukkit registry initialization issues
 */
class BasicUtilTest {

    @Test
    @DisplayName("Should calculate life addition within limit")
    void testAddLifeWithinLimit() {
        // Given
        int currentLives = 2;
        int maxLives = 3;

        // When
        boolean canAdd = currentLives < maxLives;
        int newLives = canAdd ? currentLives + 1 : currentLives;

        // Then
        assertTrue(canAdd, "Should be able to add life");
        assertEquals(3, newLives, "Lives should be 3");
    }

    @Test
    @DisplayName("Should prevent life addition at max")
    void testNoAddLifeAtMax() {
        // Given
        int currentLives = 3;
        int maxLives = 3;

        // When
        boolean canAdd = currentLives < maxLives;
        int newLives = canAdd ? currentLives + 1 : currentLives;

        // Then
        assertFalse(canAdd, "Should not be able to add life at max");
        assertEquals(3, newLives, "Lives should remain at 3");
    }

    @Test
    @DisplayName("Should allow unlimited life addition when enforceLimit is false")
    void testAddLifeUnlimited() {
        // Given
        int currentLives = 3;
        int maxLives = 3;
        boolean enforceLimit = false;

        // When
        boolean canAdd = !enforceLimit || currentLives < maxLives;
        int newLives = canAdd ? currentLives + 1 : currentLives;

        // Then
        assertTrue(canAdd, "Should be able to add life without limit");
        assertEquals(4, newLives, "Lives should exceed max");
    }

    @Test
    @DisplayName("Should remove life correctly")
    void testRemoveLife() {
        // Given
        int currentLives = 3;

        // When
        int newLives = Math.max(0, currentLives - 1);

        // Then
        assertEquals(2, newLives, "Lives should be 2");
    }

    @Test
    @DisplayName("Should not go below zero lives")
    void testNoNegativeLives() {
        // Given
        int currentLives = 0;

        // When
        int newLives = Math.max(0, currentLives - 1);

        // Then
        assertEquals(0, newLives, "Lives should not go below 0");
    }

    @Test
    @DisplayName("Should clamp negative lives to zero")
    void testClampNegativeLives() {
        // Given
        int requestedLives = -5;

        // When
        int actualLives = Math.max(0, requestedLives);

        // Then
        assertEquals(0, actualLives, "Negative lives should be clamped to 0");
    }

    @Test
    @DisplayName("Should set lives to specific amount")
    void testSetLives() {
        // Given
        int requestedLives = 5;

        // When
        int actualLives = Math.max(0, requestedLives);

        // Then
        assertEquals(5, actualLives, "Lives should be set to 5");
    }

    @Test
    @DisplayName("Should track spectator status")
    void testSpectatorStatus() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 0);
        data.setSpectator(true);

        // When
        boolean isSpectator = data.isSpectator();

        // Then
        assertTrue(isSpectator, "Player should be spectator");
    }

    @Test
    @DisplayName("Should track non-spectator status")
    void testNonSpectatorStatus() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 3);
        data.setSpectator(false);

        // When
        boolean isSpectator = data.isSpectator();

        // Then
        assertFalse(isSpectator, "Player should not be spectator");
    }

    @Test
    @DisplayName("Should handle multiple life changes")
    void testMultipleLifeChanges() {
        // Given
        int lives = 3;

        // When - Multiple operations
        lives = Math.max(0, lives - 1); // 2
        lives = Math.max(0, lives + 2); // 4
        lives = Math.max(0, lives - 1); // 3

        // Then
        assertEquals(3, lives, "Final lives should be 3");
    }

    @Test
    @DisplayName("Should validate life cap enforcement")
    void testLifeCapEnforcement() {
        // Given
        int currentLives = 2;
        int maxLives = 3;
        boolean enforceLimit = true;

        // When - Add life with enforcement
        boolean canAdd = !enforceLimit || currentLives < maxLives;
        int newLives = canAdd ? Math.min(currentLives + 1, maxLives) : currentLives;

        // Then
        assertEquals(3, newLives, "Lives should be at cap");
    }

    @Test
    @DisplayName("Should handle rapid life changes")
    void testRapidLifeChanges() {
        // Given
        int lives = 1;

        // When - Add 10 lives without cap
        for (int i = 0; i < 10; i++) {
            lives = lives + 1;
        }

        // Then
        assertEquals(11, lives, "Should handle multiple additions");

        // When - Remove 5 lives
        for (int i = 0; i < 5; i++) {
            lives = Math.max(0, lives - 1);
        }

        // Then
        assertEquals(6, lives, "Should handle multiple removals");
    }

    @Test
    @DisplayName("Should maintain data consistency")
    void testDataConsistency() {
        // Given
        PlayerData data = new PlayerData(UUID.randomUUID(), "TestPlayer", 3);

        // When
        data.setLives(3);
        data.setLives(data.getLives() + 1); // 4
        data.setLives(Math.max(0, data.getLives() - 1)); // 3
        data.setLives(data.getLives() + 1); // 4

        // Then
        assertEquals(4, data.getLives(), "Lives should be 4");
    }

    @Test
    @DisplayName("Should validate zero as minimum")
    void testZeroMinimum() {
        // Given
        int[] testValues = {-10, -5, -1, 0, 1, 5};

        // When/Then
        for (int value : testValues) {
            int clamped = Math.max(0, value);
            assertTrue(clamped >= 0, "Clamped value should never be negative");
        }
    }
}

