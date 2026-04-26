package org.fitznet.doomdns.hardcore;


import org.fitznet.doomdns.hardcore.data.DatabaseManager;
import org.fitznet.doomdns.hardcore.data.PlayerData;
import org.fitznet.doomdns.hardcore.listener.ResurrectionManager;
import org.fitznet.doomdns.hardcore.service.ScoreboardManager;
import org.fitznet.doomdns.hardcore.scheduler.LivesScheduler;
import org.fitznet.doomdns.hardcore.scheduler.SchedulerUtil;
import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * DatabaseManager integration tests against real plugin context.
 */
class DatabaseManagerIntegrationTest {

    private Player sender;
    private Player target;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        MockBukkit.load(FitzNetHardcore.class);

        sender = mock(Player.class);
        target = mock(Player.class);
        when(sender.getUniqueId()).thenReturn(UUID.randomUUID());
        when(target.getUniqueId()).thenReturn(UUID.randomUUID());
        when(sender.getName()).thenReturn("Sender");
        when(target.getName()).thenReturn("Target");

        DatabaseManager.createPlayerData(sender);
        DatabaseManager.createPlayerData(target);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Should create and load player data")
    void testCreateAndLoadPlayerData() {
        PlayerData senderData = DatabaseManager.loadPlayerData(sender);
        PlayerData targetData = DatabaseManager.loadPlayerData(target);

        assertNotNull(senderData);
        assertNotNull(targetData);
        assertEquals(sender.getUniqueId(), senderData.getUuid());
        assertEquals(target.getUniqueId(), targetData.getUuid());
    }

    @Test
    @DisplayName("Should transfer one life atomically when valid")
    void testTransferOneLifeSuccess() {
        PlayerData senderData = DatabaseManager.loadPlayerData(sender);
        PlayerData targetData = DatabaseManager.loadPlayerData(target);
        assertNotNull(senderData);
        assertNotNull(targetData);

        senderData.setLives(4);
        targetData.setLives(1);
        DatabaseManager.savePlayerData(senderData);
        DatabaseManager.savePlayerData(targetData);

        boolean transferred = DatabaseManager.transferOneLife(sender, target, 5);

        assertTrue(transferred);
        assertEquals(3, DatabaseManager.getLives(sender));
        assertEquals(2, DatabaseManager.getLives(target));
    }

    @Test
    @DisplayName("Should fail transfer when sender has one life")
    void testTransferOneLifeFailsForLowSenderLives() {
        PlayerData senderData = DatabaseManager.loadPlayerData(sender);
        PlayerData targetData = DatabaseManager.loadPlayerData(target);
        assertNotNull(senderData);
        assertNotNull(targetData);

        senderData.setLives(1);
        targetData.setLives(1);
        DatabaseManager.savePlayerData(senderData);
        DatabaseManager.savePlayerData(targetData);

        boolean transferred = DatabaseManager.transferOneLife(sender, target, 5);

        assertFalse(transferred);
        assertEquals(1, DatabaseManager.getLives(sender));
        assertEquals(1, DatabaseManager.getLives(target));
    }

    @Test
    @DisplayName("Should fail transfer when target is at max")
    void testTransferOneLifeFailsAtMax() {
        PlayerData senderData = DatabaseManager.loadPlayerData(sender);
        PlayerData targetData = DatabaseManager.loadPlayerData(target);
        assertNotNull(senderData);
        assertNotNull(targetData);

        senderData.setLives(4);
        targetData.setLives(3);
        DatabaseManager.savePlayerData(senderData);
        DatabaseManager.savePlayerData(targetData);

        boolean transferred = DatabaseManager.transferOneLife(sender, target, 3);

        assertFalse(transferred);
        assertEquals(4, DatabaseManager.getLives(sender));
        assertEquals(3, DatabaseManager.getLives(target));
    }
}
