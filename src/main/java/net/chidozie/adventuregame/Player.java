package net.chidozie.adventuregame;

public class Player {
    private String username;
    private String gender;
    private double level;
    private double health;
    public Player(String username, String gender, double level, double health){
    this.username = username;
    this.gender = gender;
    this.level = level;
    this.health = health;
    }
    public String getUsername(){
        return this.username;
    }
    public void setUsername(String name){
        this.username = name;
    }
    public String getGender(){
        return this.gender;
    }
    public void setGender(String gender){
        this.gender = gender;
    }
    public double getLevel(){
        return this.level;
    }
    public void setLevel(double level){
        this.level = level;
    }
    public double getHealth(){
        return  this.health;
    }

    public void setHealth(double health) {
        this.health = health;
    }
}
