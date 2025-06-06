package net.adventuregame.mobs;

import com.chidozie.core.terrains.Terrain;
import net.adventuregame.entities.Entity;
import net.adventuregame.game.AdventureMain;
import net.adventuregame.game.GameState;
import net.adventuregame.models.TexturedModel;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class Mob extends Entity {
    private static final Logger log = LoggerFactory.getLogger(Mob.class);
    protected float maxHealth;
    protected boolean isHostile;
    protected static Vector3f position;
    protected float speed;
    protected String name;
    protected float health = maxHealth;

    public Mob(TexturedModel model, int index, Vector3f position, Vector3f rotation, float scale, float maxHealth, boolean isHostile, String name, float speed) {
        super(model, index, position, rotation.x, rotation.y, rotation.z, scale);
        this.maxHealth = maxHealth;
        this.isHostile = isHostile;
        this.name = name;
        this.position = position;
        this.speed = speed;
    }

    public void Move(Vector3f direction) {
        System.out.print("hh");
    }

    public abstract void move(Terrain terrain);


    public abstract void TakeDamage(float damage, Mob Attacker);

    public abstract void Die();

    public abstract void attack(Mob target);

    public String getName() {
        return name;
    }

    protected Mob findClosestEnemy() {
        Mob closest = null;
        float closestDistance = Float.MAX_VALUE;

        for (Mob mob : getNearbyMobs(10.0f)) { // Implement this
            float distance = position.distance(mob.getPosition());
            if (distance < 2.0f && distance < closestDistance) {
                closest = mob;
                closestDistance = distance;
                log.info("Closes Mob FOUND! It's name is: {}", mob.getName());
            }
        }
        return closest;
    }

    private List<Mob> getNearbyMobs(float detectionRadius) {
        List<Mob> nearbyMobs = new ArrayList<>();

        for (Entity entity : GameState.getEntities()) { // Assuming getEntities() returns all entities
            if (entity instanceof Mob mob && mob != this) { // Check if it's a Mob and not the player itself
                if (position.distance(mob.getPosition()) <= detectionRadius) {
                    nearbyMobs.add(mob);
                }
            }
        }
        return nearbyMobs;
    }
}
