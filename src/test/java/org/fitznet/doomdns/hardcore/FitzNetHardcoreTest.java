package org.fitznet.doomdns.hardcore;


import org.fitznet.doomdns.hardcore.data.DatabaseManager;
import org.fitznet.doomdns.hardcore.data.PlayerData;
import org.fitznet.doomdns.hardcore.listener.ResurrectionManager;
import org.fitznet.doomdns.hardcore.service.ScoreboardManager;
import org.fitznet.doomdns.hardcore.scheduler.LivesScheduler;
import org.fitznet.doomdns.hardcore.scheduler.SchedulerUtil;
import be.seeseemelk.mockbukkit.MockBukkit;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Integration-style unit tests for command behavior and feature flows.
 */
class FitzNetHardcoreTest {

    private FitzNetHardcore plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(FitzNetHardcore.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Plugin should enable and initialize managers")
    void testPluginEnable() {
        assertTrue(plugin.isEnabled());
        assertNotNull(FitzNetHardcore.getInstance());
        assertNotNull(plugin.getResurrectionManager());
        assertNotNull(plugin.getScoreboardManager());
    }

    @Test
    @DisplayName("Should have plugin commands available")
    void testCommandRegistration() {
        assertNotNull(plugin.getCommand("fitznet"));
        assertNotNull(plugin.getCommand("lives"));
        assertNotNull(plugin.getCommand("addlife"));
        assertNotNull(plugin.getCommand("sublife"));
        assertNotNull(plugin.getCommand("setlife"));
        assertNotNull(plugin.getCommand("givelife"));
        assertNotNull(plugin.getCommand("revive"));
        assertNotNull(plugin.getCommand("fndebug"));
    }

    @Test
    @DisplayName("Unknown command should return false")
    void testUnknownCommand() {
        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        when(command.getName()).thenReturn("unknown");

        assertFalse(plugin.onCommand(sender, command, "unknown", new String[0]));
    }

    @Test
    @DisplayName("/lives from non-player should return true and send message")
    void testLivesFromConsoleSender() {
        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        when(command.getName()).thenReturn("lives");

        assertTrue(plugin.onCommand(sender, command, "lives", new String[0]));
        verify(sender, times(1)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("/givelife from non-player should return true and send message")
    void testGiveLifeFromConsoleSender() {
        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        when(command.getName()).thenReturn("givelife");

        assertTrue(plugin.onCommand(sender, command, "givelife", new String[] {"Bob"}));
        verify(sender, times(1)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("/revive from non-player should return true and send message")
    void testReviveFromConsoleSender() {
        Command command = mock(Command.class);
        CommandSender sender = mock(CommandSender.class);
        when(command.getName()).thenReturn("revive");

        assertTrue(plugin.onCommand(sender, command, "revive", new String[] {"Bob"}));
        verify(sender, times(1)).sendMessage(any(Component.class));
    }

    @Test
    @DisplayName("Config defaults should be available")
    void testConfigDefaults() {
        assertNotNull(plugin.getConfig());
    }
}
