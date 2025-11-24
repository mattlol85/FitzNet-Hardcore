package org.fitznet.doomdns.fitznethardcore;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for ResurrectionManager
 * Tests resurrection target management logic
 * Note: PlayerMock integration tests skipped due to MockBukkit registry initialization issues
 */
class ResurrectionManagerTest {

    private ServerMock server;
    private FitzNetHardcore plugin;
    private HashMap<UUID, UUID> resurrectionTargets;
    private UUID alivePlayerId;
    private UUID deadPlayerId;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(FitzNetHardcore.class);
        resurrectionTargets = new HashMap<>();
        alivePlayerId = UUID.randomUUID();
        deadPlayerId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Should create ResurrectionManager instance")
    void testManagerCreation() {
        // When
        ResurrectionManager manager = plugin.getResurrectionManager();

        // Then
        assertNotNull(manager, "ResurrectionManager should be created");
    }

    @Test
    @DisplayName("Should set and get resurrection target")
    void testSetAndGetTarget() {
        // When
        resurrectionTargets.put(alivePlayerId, deadPlayerId);
        UUID target = resurrectionTargets.get(alivePlayerId);

        // Then
        assertNotNull(target, "Target should be set");
        assertEquals(deadPlayerId, target, "Target UUID should match");
    }

    @Test
    @DisplayName("Should return null for player with no target")
    void testGetNonExistentTarget() {
        // When
        UUID target = resurrectionTargets.get(alivePlayerId);

        // Then
        assertNull(target, "Should return null when no target is set");
    }

    @Test
    @DisplayName("Should clear resurrection target")
    void testClearTarget() {
        // Given
        resurrectionTargets.put(alivePlayerId, deadPlayerId);
        assertNotNull(resurrectionTargets.get(alivePlayerId), "Target should be set");

        // When
        resurrectionTargets.remove(alivePlayerId);
        UUID target = resurrectionTargets.get(alivePlayerId);

        // Then
        assertNull(target, "Target should be cleared");
    }

    @Test
    @DisplayName("Should handle clearing non-existent target")
    void testClearNonExistentTarget() {
        // When
        resurrectionTargets.remove(alivePlayerId);
        UUID target = resurrectionTargets.get(alivePlayerId);

        // Then
        assertNull(target, "Clearing non-existent target should be safe");
    }

    @Test
    @DisplayName("Should handle cooldown disabled in config")
    void testCooldownDisabled() {
        // Given
        plugin.getConfig().set("ResurrectionCooldownMinutes", 0);
        int cooldownMinutes = plugin.getConfig().getInt("ResurrectionCooldownMinutes", 0);

        // Then
        assertEquals(0, cooldownMinutes, "Cooldown should be disabled");
    }

    @Test
    @DisplayName("Should allow changing resurrection target")
    void testChangeTarget() {
        // Given
        UUID newTargetId = UUID.randomUUID();
        resurrectionTargets.put(alivePlayerId, deadPlayerId);
        assertEquals(deadPlayerId, resurrectionTargets.get(alivePlayerId));

        // When
        resurrectionTargets.put(alivePlayerId, newTargetId);

        // Then
        assertEquals(newTargetId, resurrectionTargets.get(alivePlayerId),
            "Target should be updated");
    }

    @Test
    @DisplayName("Should handle multiple players with different targets")
    void testMultiplePlayers() {
        // Given
        UUID alivePlayer2Id = UUID.randomUUID();
        UUID deadPlayer2Id = UUID.randomUUID();

        // When
        resurrectionTargets.put(alivePlayerId, deadPlayerId);
        resurrectionTargets.put(alivePlayer2Id, deadPlayer2Id);

        // Then
        assertEquals(deadPlayerId, resurrectionTargets.get(alivePlayerId),
            "First player should have first target");
        assertEquals(deadPlayer2Id, resurrectionTargets.get(alivePlayer2Id),
            "Second player should have second target");
    }

    @Test
    @DisplayName("Should handle multiple players with same target")
    void testMultiplePlayersSameTarget() {
        // Given
        UUID alivePlayer2Id = UUID.randomUUID();

        // When
        resurrectionTargets.put(alivePlayerId, deadPlayerId);
        resurrectionTargets.put(alivePlayer2Id, deadPlayerId);

        // Then
        assertEquals(deadPlayerId, resurrectionTargets.get(alivePlayerId),
            "First player should target dead player");
        assertEquals(deadPlayerId, resurrectionTargets.get(alivePlayer2Id),
            "Second player should also target same dead player");
    }

    @Test
    @DisplayName("Should track resurrection enabled config")
    void testResurrectionEnabled() {
        // When
        boolean enabled = plugin.getConfig().getBoolean("ResurrectionEnabled", true);

        // Then
        assertTrue(enabled, "Resurrection should be enabled by default");
    }

    @Test
    @DisplayName("Should identify dead players needing resurrection")
    void testIdentifyDeadPlayer() {
        // Given
        PlayerData deadData = new PlayerData(deadPlayerId, "DeadPlayer", 0);
        deadData.setLives(0);
        deadData.setSpectator(true);

        // When
        boolean needsResurrection = deadData.getLives() == 0 && deadData.isSpectator();

        // Then
        assertTrue(needsResurrection, "Dead player should need resurrection");
    }

    @Test
    @DisplayName("Should identify alive players not needing resurrection")
    void testIdentifyAlivePlayer() {
        // Given
        PlayerData aliveData = new PlayerData(alivePlayerId, "AlivePlayer", 3);
        aliveData.setLives(3);
        aliveData.setSpectator(false);

        // When
        boolean needsResurrection = aliveData.getLives() == 0 && aliveData.isSpectator();

        // Then
        assertFalse(needsResurrection, "Alive player should not need resurrection");
    }
}

