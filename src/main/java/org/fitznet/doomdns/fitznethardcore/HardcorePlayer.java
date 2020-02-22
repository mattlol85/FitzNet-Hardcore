package org.fitznet.doomdns.fitznethardcore;

public class HardcorePlayer {

    private int lives = 1;
    private String username;
    private int lifeTime; 

    //Used to create new players
    public HardcorePlayer(final String username) {
        this.username = username;
    }

    // Used when reading from database
    public HardcorePlayer(final String username, final int lives) {
        this.username = username;
        this.lives = lives;
    }

    // Setters & Getters

    /**
     * 
     * @return Number of lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * 
     * @param lives Lives to set
     */
    public void setLives(final int lives) {
        this.lives = lives;
    }

    /**
     * 
     * @return Player Username
     */
    public String getUsername() {
        return username;
    }

    /**
     * 
     * @param username Username to set
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    //Object Methods
    /**
     * Add one life to player object
     */
    public void addLife() {
        lives++;
    }

    public void removeLife() {
        lives--;
    }

}
