Fitz-NetHardcore
================

Minecraft hardcore-style lives plugin for Paper and Folia.

Features
--------

- Configurable lives system with per-player persistence.
- Spectator mode when a player reaches 0 lives.
- Periodic life regeneration scheduler.
- Optional keep-inventory and keep-experience for deaths with remaining lives.
- Player-to-player life gifting.
- Resurrection ritual using a configurable block item.
- Tab list and sidebar life display.
- Bukkit-native YAML player data storage under plugin data folder.

Compatibility
-------------

- Java: 21
- Paper API: 1.21.3-R0.1-SNAPSHOT
- Folia runtime support: 1.21.x (detected at runtime)

Build
-----

From the project root:

Windows:

./gradlew.bat clean build

Linux/macOS:

./gradlew clean build

Output jar:

- build/libs/Fitz-NetHardcore-<version>.jar

Install
-------

1. Copy the built jar into your server plugins folder.
2. Start the server once to generate config files.
3. Adjust settings in plugins/Fitz-NetHardcore/config.yml.
4. Restart the server.

Commands
--------

- /fitznet: Admin diagnostics/info command.
- /lives: Show your current life count.
- /addlife [player]: Admin adds 1 life (can exceed normal cap).
- /sublife [player]: Admin removes 1 life.
- /setlife <player> <amount>: Admin sets exact life count.
- /givelife <player>: Transfer one life to another player.
- /revive <player>: Set resurrection target before ritual interaction.
- /fndebug: Admin dump of online players and states.

Permissions
-----------

- fitznethardcore.admin: Required for admin commands.
- fitznethardcore.givelife: Required for life gifting.
- fitznethardcore.resurrect: Required for resurrection actions.

Configuration
-------------

- MaxLives: Maximum normal life cap.
- LifeRegenTime: Minutes between life regen ticks.
- StartingLives: New player starting lives.
- DaysPerLife: Reserved progression tuning field.
- ResurrectionEnabled: Enable/disable resurrection system.
- ResurrectionItem: Required block material for ritual.
- EnableLifeGifting: Enable/disable /givelife.
- BroadcastDeaths: Broadcast major life-state events.
- ShowTabLives: Enable tab/sidebar life display.
- ResurrectionCooldownMinutes: Cooldown between resurrections.
- KeepInventoryOnDeath: Preserve inventory when lives remain.
- KeepExperienceOnDeath: Preserve experience when lives remain.

Testing
-------

Run tests with:

./gradlew test

Refactor Notes
--------------

- Data persistence now uses plugin data folder paths consistently.
- Scheduler tasks are tracked with cancellable handles and cleaned up on disable/quit.
- Life transfer operation uses atomic persistence update semantics.

Notes
-----

- The plugin metadata enables Folia support.
- Keep Paper and Folia API versions aligned when upgrading dependencies.

