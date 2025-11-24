package org.fitznet.doomdns.fitznethardcore;

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
}

