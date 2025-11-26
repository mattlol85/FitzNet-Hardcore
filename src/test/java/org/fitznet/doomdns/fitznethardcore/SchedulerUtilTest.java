package org.fitznet.doomdns.fitznethardcore;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Test suite for SchedulerUtil
 * Tests Folia/Paper scheduler detection and compatibility layer
 */
class SchedulerUtilTest {

    @Test
    @DisplayName("Should detect server type (Folia or Paper)")
    void testServerTypeDetection() {
        // When
        boolean isFolia = SchedulerUtil.isFolia();

        // Then
        // The actual value depends on runtime environment
        // In MockBukkit environment, it should be false (Paper)
        assertFalse(isFolia, "MockBukkit should be detected as Paper, not Folia");
    }

    @Test
    @DisplayName("Should consistently report same server type")
    void testConsistentServerType() {
        // When
        boolean firstCall = SchedulerUtil.isFolia();
        boolean secondCall = SchedulerUtil.isFolia();

        // Then
        assertEquals(firstCall, secondCall, "Server type should be consistent across calls");
    }

    @Test
    @DisplayName("Should handle Paper environment")
    void testPaperEnvironment() {
        // When
        boolean isFolia = SchedulerUtil.isFolia();

        // Then
        assertFalse(isFolia, "Test environment should be Paper-based");
    }

    @Test
    @DisplayName("Should provide valid isFolia state")
    void testIsFoliaState() {
        // When/Then - isFolia() returns a primitive boolean, so it's always valid
        assertDoesNotThrow(() -> SchedulerUtil.isFolia(),
            "isFolia should return a valid boolean without throwing");
    }

    @Test
    @DisplayName("Should initialize static field correctly")
    void testStaticInitialization() {
        // This test verifies the static initializer doesn't throw exceptions
        // When/Then
        assertDoesNotThrow(() -> SchedulerUtil.isFolia(),
            "Static initialization should not throw exceptions");
    }

    @Test
    @DisplayName("Should handle class loading check gracefully")
    void testClassLoadingCheck() {
        // The SchedulerUtil checks for Folia classes using Class.forName
        // This should not throw exceptions in our test environment

        // When/Then
        assertDoesNotThrow(() -> SchedulerUtil.isFolia(),
            "Class loading check should be safe");
    }

    @Test
    @DisplayName("Should work in MockBukkit environment")
    void testMockBukkitCompatibility() {
        // Given - MockBukkit doesn't have Folia classes

        // When
        boolean isFolia = SchedulerUtil.isFolia();

        // Then
        assertFalse(isFolia, "MockBukkit should not be detected as Folia");
    }

    @Test
    @DisplayName("Should cache detection result")
    void testDetectionCaching() {
        // The IS_FOLIA field is static final, so it's only computed once
        // Multiple calls should return the same instance

        // When
        boolean first = SchedulerUtil.isFolia();
        boolean second = SchedulerUtil.isFolia();
        boolean third = SchedulerUtil.isFolia();

        // Then
        assertEquals(first, second, "First and second calls should match");
        assertEquals(second, third, "Second and third calls should match");
        assertEquals(first, third, "First and third calls should match");
    }
}

