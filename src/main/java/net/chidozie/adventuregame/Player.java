package net.chidozie.adventuregame;

public class Player {
    private String username;
    private String gender;
    public Player(String username, String gender){
    this.username = username;
    this.gender = gender;
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
}
