package net.chidozie.adventuregame;

import net.chidozie.adventuregame.Player;

import java.util.Map;

public class PlayerState {
    private String username;
    private String gender;
    private double level;
    private double health;
    private double x;
    private double y;
    private double z;
    private String currentScreen;
    private transient Map<String, Object> screenState; // Exclude from serialization

    public PlayerState(Player player, String currentScreen, Map<String, Object> screenState) {
        this.username = player.getUsername();
        this.gender = player.getGender();
        this.level = player.getLevel();
        this.health = player.getHealth();
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
        this.currentScreen = currentScreen;
        this.screenState = screenState;

        // Getters and Setters...
    }



public String getCurrentScreen() {
        return this.currentScreen;
    }

    public Map<String, Object> getScreenState() {
        return this.screenState;
    }

    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
           this.username = username;
       }

       public String getGender() {
           return gender;
       }

       public void setGender(String gender) {
           this.gender = gender;
       }

       public double getLevel() {
           return level;
       }

       public void setLevel(double level) {
           this.level = level;
       }

       public double getHealth() {
           return health;
       }

       public void setHealth(double health) {
           this.health = health;
       }

       public double getX() {
           return x;
       }

       public void setX(double x) {
           this.x = x;
       }

       public double getY() {
           return y;
       }

       public void setY(double y) {
           this.y = y;
       }

       public double getZ() {
           return z;
       }

       public void setZ(double z) {
           this.z = z;
       }
       }


