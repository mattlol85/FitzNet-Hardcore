package org.fitznet.doomdns.fitznethardcore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for PlayerData JSON serialization/deserialization
 * Tests the core data model without requiring full plugin mock
 */
class DatabaseManagerTest {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private File testDir;
    private UUID testUuid;

    @BeforeEach
    void setUp() throws IOException {
        // Create temporary test directory
        testDir = Files.createTempDirectory("fitznet-test").toFile();
        testUuid = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        // Clean up test files
        if (testDir != null && testDir.exists()) {
            deleteDirectory(testDir);
        }
    }

    private void deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }

    @Test
    @DisplayName("Should serialize PlayerData to JSON correctly")
    void testPlayerDataSerialization() {
        // Given
        PlayerData data = new PlayerData(testUuid, "TestPlayer", 3);
        data.setDaysAlive(10);
        data.setSpectator(false);
        data.setNextLifeTimestamp(1234567890L);
        data.setLastDeathTime(9876543210L);

        // When
        String json = gson.toJson(data);

        // Then
        assertNotNull(json, "JSON should not be null");
        assertTrue(json.contains("TestPlayer"), "JSON should contain player name");
        assertTrue(json.contains("\"lives\": 3"), "JSON should contain lives value");
        assertTrue(json.contains("\"daysAlive\": 10"), "JSON should contain daysAlive");
        assertTrue(json.contains("\"isSpectator\": false"), "JSON should contain spectator status");
    }

    @Test
    @DisplayName("Should deserialize PlayerData from JSON correctly")
    void testPlayerDataDeserialization() {
        // Given
        String json = "{\n" +
                "  \"uuid\": \"" + testUuid.toString() + "\",\n" +
                "  \"name\": \"TestPlayer\",\n" +
                "  \"lives\": 2,\n" +
                "  \"daysAlive\": 5,\n" +
                "  \"isSpectator\": true,\n" +
                "  \"nextLifeTimestamp\": 1234567890,\n" +
                "  \"lastDeathTime\": 9876543210\n" +
                "}";

        // When
        PlayerData data = gson.fromJson(json, PlayerData.class);

        // Then
        assertNotNull(data, "Deserialized data should not be null");
        assertEquals(testUuid, data.getUuid(), "UUID should match");
        assertEquals("TestPlayer", data.getName(), "Name should match");
        assertEquals(2, data.getLives(), "Lives should be 2");
        assertEquals(5, data.getDaysAlive(), "Days alive should be 5");
        assertTrue(data.isSpectator(), "Should be spectator");
        assertEquals(1234567890L, data.getNextLifeTimestamp(), "Timestamp should match");
        assertEquals(9876543210L, data.getLastDeathTime(), "Death time should match");
    }

    @Test
    @DisplayName("Should create PlayerData with default constructor")
    void testPlayerDataDefaultConstructor() {
        // Given/When
        PlayerData data = new PlayerData(testUuid, "TestPlayer", 1);

        // Then
        assertNotNull(data, "Data should not be null");
        assertEquals(testUuid, data.getUuid(), "UUID should match");
        assertEquals("TestPlayer", data.getName(), "Name should match");
        assertEquals(1, data.getLives(), "Should have 1 life");
        assertEquals(0, data.getDaysAlive(), "Days alive should be 0");
        assertFalse(data.isSpectator(), "Should not be spectator");
        assertTrue(data.getNextLifeTimestamp() > 0, "Timestamp should be set");
        assertEquals(0, data.getLastDeathTime(), "Last death time should be 0");
    }

    @Test
    @DisplayName("Should handle PlayerData modifications")
    void testPlayerDataModifications() {
        // Given
        PlayerData data = new PlayerData(testUuid, "TestPlayer", 1);

        // When
        data.setLives(3);
        data.setDaysAlive(15);
        data.setSpectator(true);
        data.setNextLifeTimestamp(9999999L);
        data.setLastDeathTime(8888888L);
        data.setName("UpdatedName");

        // Then
        assertEquals(3, data.getLives(), "Lives should be updated to 3");
        assertEquals(15, data.getDaysAlive(), "Days alive should be 15");
        assertTrue(data.isSpectator(), "Should be spectator");
        assertEquals(9999999L, data.getNextLifeTimestamp(), "Timestamp should be updated");
        assertEquals(8888888L, data.getLastDeathTime(), "Death time should be updated");
        assertEquals("UpdatedName", data.getName(), "Name should be updated");
    }

    @Test
    @DisplayName("Should roundtrip PlayerData through JSON")
    void testPlayerDataRoundtrip() throws IOException {
        // Given
        PlayerData original = new PlayerData(testUuid, "TestPlayer", 3);
        original.setDaysAlive(20);
        original.setSpectator(false);
        original.setNextLifeTimestamp(5555555L);
        original.setLastDeathTime(4444444L);

        File jsonFile = new File(testDir, testUuid.toString() + ".json");

        // When - Save to file
        try (FileWriter writer = new FileWriter(jsonFile)) {
            gson.toJson(original, writer);
        }

        // Then - Read from file
        assertTrue(jsonFile.exists(), "JSON file should exist");

        String content = Files.readString(jsonFile.toPath());
        PlayerData loaded = gson.fromJson(content, PlayerData.class);

        assertNotNull(loaded, "Loaded data should not be null");
        assertEquals(original.getUuid(), loaded.getUuid(), "UUID should match");
        assertEquals(original.getName(), loaded.getName(), "Name should match");
        assertEquals(original.getLives(), loaded.getLives(), "Lives should match");
        assertEquals(original.getDaysAlive(), loaded.getDaysAlive(), "Days alive should match");
        assertEquals(original.isSpectator(), loaded.isSpectator(), "Spectator status should match");
        assertEquals(original.getNextLifeTimestamp(), loaded.getNextLifeTimestamp(), "Timestamp should match");
        assertEquals(original.getLastDeathTime(), loaded.getLastDeathTime(), "Death time should match");
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void testNullHandling() {
        // Given
        PlayerData data = new PlayerData();

        // When/Then - Should not throw exceptions
        assertDoesNotThrow(() -> {
            data.setUuid(null);
            data.setName(null);
            data.setLives(0);
        });
    }

    @Test
    @DisplayName("Should handle negative lives value")
    void testNegativeLives() {
        // Given
        PlayerData data = new PlayerData(testUuid, "TestPlayer", 3);

        // When
        data.setLives(-5);

        // Then
        assertEquals(-5, data.getLives(), "Should store negative value (validation happens in BasicUtil)");
    }

    @Test
    @DisplayName("Should handle large timestamp values")
    void testLargeTimestamps() {
        // Given
        PlayerData data = new PlayerData(testUuid, "TestPlayer", 1);
        long futureTimestamp = System.currentTimeMillis() + 1000000000L;

        // When
        data.setNextLifeTimestamp(futureTimestamp);

        // Then
        assertEquals(futureTimestamp, data.getNextLifeTimestamp(), "Should handle large timestamps");
    }
}

