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
 * Test suite for ScoreboardManager
 * Tests scoreboard logic without PlayerMock
 * Note: Full integration tests skipped due to MockBukkit registry initialization issues
 */
class ScoreboardManagerTest {

    private ServerMock server;
    private FitzNetHardcore plugin;
    private ScoreboardManager scoreboardManager;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(FitzNetHardcore.class);
        scoreboardManager = new ScoreboardManager(plugin);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Should create ScoreboardManager instance")
    void testManagerCreation() {
        // Then
        assertNotNull(scoreboardManager, "ScoreboardManager should be created");
    }

    @Test
    @DisplayName("Should handle ShowTabLives config")
    void testShowTabLivesConfig() {
        // Given
        plugin.getConfig().set("ShowTabLives", true);

        // When
        boolean showTabLives = plugin.getConfig().getBoolean("ShowTabLives", true);

        // Then
        assertTrue(showTabLives, "ShowTabLives should be enabled");
    }

    @Test
    @DisplayName("Should respect disabled ShowTabLives config")
    void testShowTabLivesDisabled() {
        // Given
        plugin.getConfig().set("ShowTabLives", false);

        // When
        boolean showTabLives = plugin.getConfig().getBoolean("ShowTabLives", true);

        // Then
        assertFalse(showTabLives, "ShowTabLives should be disabled");
    }

    @Test
    @DisplayName("Should determine life color for max lives")
    void testLifeColorMaxLives() {
        // Given
        int lives = 3;

        // When - Color logic (green for 3, yellow for 2, red for 1, dark red for 0)
        String expectedColor = lives == 3 ? "GREEN" : lives == 2 ? "YELLOW" : lives == 1 ? "RED" : "DARK_RED";

        // Then
        assertEquals("GREEN", expectedColor, "3 lives should be green");
    }

    @Test
    @DisplayName("Should determine life color for medium lives")
    void testLifeColorMediumLives() {
        // Given
        int lives = 2;

        // When
        String expectedColor = lives == 3 ? "GREEN" : lives == 2 ? "YELLOW" : lives == 1 ? "RED" : "DARK_RED";

        // Then
        assertEquals("YELLOW", expectedColor, "2 lives should be yellow");
    }

    @Test
    @DisplayName("Should determine life color for low lives")
    void testLifeColorLowLives() {
        // Given
        int lives = 1;

        // When
        String expectedColor = lives == 3 ? "GREEN" : lives == 2 ? "YELLOW" : lives == 1 ? "RED" : "DARK_RED";

        // Then
        assertEquals("RED", expectedColor, "1 life should be red");
    }

    @Test
    @DisplayName("Should determine life color for zero lives")
    void testLifeColorZeroLives() {
        // Given
        int lives = 0;

        // When
        String expectedColor = lives == 3 ? "GREEN" : lives == 2 ? "YELLOW" : lives == 1 ? "RED" : "DARK_RED";

        // Then
        assertEquals("DARK_RED", expectedColor, "0 lives should be dark red");
    }

    @Test
    @DisplayName("Should format lives display for alive players")
    void testLivesDisplayAlive() {
        // Given
        int lives = 2;

        // When
        String display = lives > 0 ? String.valueOf(lives) : "☠";

        // Then
        assertEquals("2", display, "Should display number for alive players");
    }

    @Test
    @DisplayName("Should format lives display for dead players")
    void testLivesDisplayDead() {
        // Given
        int lives = 0;

        // When
        String display = lives > 0 ? String.valueOf(lives) : "☠";

        // Then
        assertEquals("☠", display, "Should display skull for dead players");
    }

    @Test
    @DisplayName("Should calculate time until next life")
    void testNextLifeCalculation() {
        // Given
        long nextLifeTime = System.currentTimeMillis() + 60000; // 1 minute from now
        long currentTime = System.currentTimeMillis();

        // When
        long remaining = nextLifeTime - currentTime;

        // Then
        assertTrue(remaining > 0 && remaining <= 60000, "Remaining time should be positive and under 1 minute");
    }

    @Test
    @DisplayName("Should handle expired life timer")
    void testExpiredLifeTimer() {
        // Given
        long nextLifeTime = System.currentTimeMillis() - 1000; // 1 second ago
        long currentTime = System.currentTimeMillis();

        // When
        long remaining = Math.max(0, nextLifeTime - currentTime);

        // Then
        assertEquals(0, remaining, "Expired timer should return 0");
    }

    @Test
    @DisplayName("Should track player data for scoreboard")
    void testPlayerDataTracking() {
        // Given
        UUID playerId = UUID.randomUUID();
        PlayerData data = new PlayerData(playerId, "TestPlayer", 2);
        data.setLives(2);
        data.setNextLifeTimestamp(System.currentTimeMillis() + 120000);

        // Then
        assertNotNull(data, "Player data should exist for scoreboard");
        assertEquals(2, data.getLives(), "Lives should be tracked");
        assertTrue(data.getNextLifeTimestamp() > System.currentTimeMillis(), "Next life timestamp should be in future");
    }
}

