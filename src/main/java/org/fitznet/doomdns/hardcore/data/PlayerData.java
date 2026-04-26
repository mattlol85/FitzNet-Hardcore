package org.fitznet.doomdns.hardcore.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Represents all persistent player data for the hardcore lives system. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerData {
    private UUID uuid;
    private String name;
    private int lives;
    private int daysAlive;
    private boolean isSpectator;
    private long nextLifeTimestamp;
    private long lastDeathTime;

    /**
     * Create new PlayerData with default values
     */
    public PlayerData(UUID uuid, String name, int startingLives) {
        this.uuid = uuid;
        this.name = name;
        this.lives = startingLives;
        this.daysAlive = 0;
        this.isSpectator = false;
        this.nextLifeTimestamp = System.currentTimeMillis();
        this.lastDeathTime = 0;
    }
}

