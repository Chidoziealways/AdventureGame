package adventuregame.net.chidozie.adventuregame;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import org.joml.Vector3f;
import net.querz.nbt.tag.*;

import java.util.Map;

public class PlayerState {
    private String username;
    private String gender;
    private double level;
    private double health;
    private Vector3f position;
    private String currentScreen;
    private transient Map<String, Object> screenState; // Exclude from serialization

    public PlayerState(Player player, String currentScreen, Map<String, Object> screenState) {
        this.username = player.getUsername();
        this.gender = player.getGender();
        this.level = player.getLevel();
        this.health = player.getHealth();
        this.position = player.getPosition();
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

       public Vector3f getPosition(){return this.position;}

       public void setPosition(Vector3f position){this.position = position;}

    public CompoundTag toNBT(Player player) {
        CompoundTag compound = new CompoundTag();
        compound.put("playerName", new StringTag(player.getUsername()));
        compound.put("currentScreen", new StringTag(currentScreen));
        // Add additional player and screen state data to NBT as needed
        return compound;
    }

    public Dynamic<CompoundTag> serialize(DynamicOps ops, Player player) {
        CompoundTag compound = toNBT(player);
        return new Dynamic<>(ops, compound);
    }
       }


