package org.fitznet.doomdns.fitznethardcore;

public class HardcorePlayer {
    
    private int lives = 1;
    private String username;

    //Used to create new players
    public HardcorePlayer(String username) {
        this.username = username;
    }

    //Used when reading from database
    public HardcorePlayer(String username, int lives) {
        this.username = username;
        this.lives = lives;
    }

    //Setters & Getters
    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    //Object Methods
    public void addLife() {
        lives++;
    }

    public void removeLife() {
        lives--;
    }

}
