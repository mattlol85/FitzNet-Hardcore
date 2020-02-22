package org.fitznet.doomdns.fitznethardcore;

import org.bukkit.entity.Player;

public class HardcorePlayer {

    private int lives = 1;
    private String username;
    private int lifeTime; 
    private Player player;

    /**
     * Creates a new HardcorePlayer with the given username
     * @param username Username to set
     */
    public HardcorePlayer(final String username) {
        setUsername(username);
    }

    // Used when reading from database
    /**
     * Creates a new HardcorePlayer with the given username and number of lives
     * @param username Username to set
     * @param lives Lives to set
     */
    public HardcorePlayer(final String username, final int lives) {
        setUsername(username);
        setLives(lives);
    }

    /** 
     * Creates a new HardcorePlayer with given player
     * @param player Player object representing the player
     */
    public HardcorePlayer(final Player player)
    {
        this.player = player;
    }

    /** 
     * Creates a new HardcorePlayer with given player and a set amount of lives
     * @param player Player object representing the player
     * @param lives The numbers of lives the player should be created with
     */
    public HardcorePlayer(final Player player, final int lives)
    {
        this.player = player;
        setLives(lives);
    }

    // Setters & Getters
    /**
     * Gets the number of remaining lives for the player
     * @return Number of lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * Sets the number of remaining lives for the player
     * @param lives Lives to set
     */
    public void setLives(final int lives) {
        this.lives = lives;
    }

    /**
     * Gets the username of the player
     * @return Player Username
     */
    public String getUsername() {
        // TODO: check that player.getName() returns the appropriate name
        //return player.getName();
        return username;
    }

    /**
     * Sets the username of the player
     * @param username Username to set
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    //Object Methods
    /**
     * Adds one life to the player
     */
    public void addLife() {
        lives++;
    }

    /**
     * Removes one life from the player
     */
    public void removeLife() {
        lives--;
    }

}
