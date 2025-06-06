package net.adventuregame.items.entities;

import net.adventuregame.entities.Entity;
import net.adventuregame.models.TexturedModel;
import org.joml.Vector3f;

public class BulletEntity extends Entity {

    private Vector3f velocity;

    public BulletEntity(Vector3f position, Vector3f direction) {
        super(null, position, 0, 0, 0, 0.1f);
        this.velocity = new Vector3f(direction).normalize().mul(0.5f);
    }

    public void update() {
        increasePosition(velocity.x, velocity.y, velocity.z);
    }
}
