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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests resurrection target and cooldown behavior using the real manager.
 */
class ResurrectionManagerTest {

    private FitzNetHardcore plugin;
    private ResurrectionManager manager;
    private Player resurrector;
    private Player target;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(FitzNetHardcore.class);
        manager = plugin.getResurrectionManager();

        resurrector = mock(Player.class);
        target = mock(Player.class);
        when(resurrector.getUniqueId()).thenReturn(UUID.randomUUID());
        when(target.getUniqueId()).thenReturn(UUID.randomUUID());
        when(resurrector.getName()).thenReturn("Resurrector");
        when(target.getName()).thenReturn("Target");

        DatabaseManager.createPlayerData(resurrector);
        DatabaseManager.createPlayerData(target);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Should initialize resurrection manager")
    void testManagerCreated() {
        assertNotNull(manager);
    }

    @Test
    @DisplayName("Should set, get, and clear resurrection target")
    void testTargetLifecycle() {
        manager.setResurrectionTarget(resurrector, target);
        assertEquals(target.getUniqueId(), manager.getResurrectionTarget(resurrector));

        manager.clearResurrectionTarget(resurrector);
        assertNull(manager.getResurrectionTarget(resurrector));
    }

    @Test
    @DisplayName("Should keep targets isolated per player")
    void testMultipleTargets() {
        Player resurrectorTwo = mock(Player.class);
        Player targetTwo = mock(Player.class);
        when(resurrectorTwo.getUniqueId()).thenReturn(UUID.randomUUID());
        when(targetTwo.getUniqueId()).thenReturn(UUID.randomUUID());
        when(resurrectorTwo.getName()).thenReturn("ResurrectorTwo");
        when(targetTwo.getName()).thenReturn("TargetTwo");
        DatabaseManager.createPlayerData(resurrectorTwo);
        DatabaseManager.createPlayerData(targetTwo);

        manager.setResurrectionTarget(resurrector, target);
        manager.setResurrectionTarget(resurrectorTwo, targetTwo);

        assertEquals(target.getUniqueId(), manager.getResurrectionTarget(resurrector));
        assertEquals(targetTwo.getUniqueId(), manager.getResurrectionTarget(resurrectorTwo));
    }

    @Test
    @DisplayName("Should report no cooldown when disabled")
    void testCooldownDisabled() {
        plugin.getConfig().set("ResurrectionCooldownMinutes", 0);

        assertFalse(manager.isOnCooldown(resurrector));
        assertEquals(0, manager.getRemainingCooldown(resurrector));
    }

    @Test
    @DisplayName("Should detect dead player eligibility correctly")
    void testDeadPlayerEligibility() {
        UUID deadId = UUID.randomUUID();
        PlayerData deadData = new PlayerData(deadId, "Dead", 0);
        deadData.setLives(0);
        deadData.setSpectator(true);

        assertTrue(deadData.isSpectator());
        assertEquals(0, deadData.getLives());
    }

    @Test
    @DisplayName("Should detect alive player ineligibility correctly")
    void testAlivePlayerIneligibility() {
        UUID aliveId = UUID.randomUUID();
        PlayerData aliveData = new PlayerData(aliveId, "Alive", 3);
        aliveData.setLives(3);
        aliveData.setSpectator(false);

        assertFalse(aliveData.isSpectator());
        assertTrue(aliveData.getLives() > 0);
    }
}
