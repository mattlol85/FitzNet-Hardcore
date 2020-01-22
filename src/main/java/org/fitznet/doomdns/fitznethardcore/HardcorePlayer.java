package org.fitznet.doomdns.fitznethardcore;


public class HardcorePlayer {
    int lives = 1;
    String username;

    //Used to create new players
    public HardcorePlayer(String username) {
        this.username = username;
    }

    //Used when reading from database
    public HardcorePlayer(String username, int lives) {
        this.username = username;
        this.lives = lives;
    }

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

    public void addLife() {
        lives++;
    }

    public void addLife(int livesToAdd) {
        lives += livesToAdd;
    }

    public void removeLife() {
        lives--;
    }

    public void removeLife(int livesToRemove) {
        lives -= livesToRemove;
    }
}
