package net.adventuregame.mobs;

import com.chidozie.core.renderEngine.WindowManager;
import com.chidozie.core.terrains.Terrain;
import net.adventuregame.models.TexturedModel;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class HostileMob extends Mob {
    protected static final Logger log = LoggerFactory.getLogger(HostileMob.class);
    private Mob target; // Target for AI to follow
    private List<Vector3f> path; // Path to follow

    public HostileMob(TexturedModel model, int index, Vector3f position, Vector3f rotation, float scale, float maxHealth, String name, float speed) {
        super(model, index, position, rotation, scale, maxHealth, true, name, speed);
    }

    @Override
    public void TakeDamage(float damage, Mob Attacker) {
        health -= damage;
        log.info("{} has taken {} damage from {}!", name, damage, Attacker);

        if (health <= 0) {
            Die();
        } else {
            setTarget(Attacker);
        }
    }

    @Override
    public void Die() {
        log.info("This poor (but mean) mob has died. Its name was {}", name);
    }

    public void setTarget(Mob target) {
        this.target = target;
    }

    public void updateAI(Terrain terrain) {
        if (target == null) return;

        if (path == null || path.isEmpty()) {
            path = Pathfinding.findPath(position, target.getPosition(), terrain);
        }

        if (!path.isEmpty()) {
            Vector3f nextPoint = path.get(0);
            Vector3f direction = new Vector3f(nextPoint).sub(position).normalize();

            Move(direction);

            if (position.distance(nextPoint) < 2.0f) {
                path.remove(0);
            }
        }
    }

    @Override
    public void Move(Vector3f direction) {
        Vector3f velocity = new Vector3f(direction).mul(speed * WindowManager.getFrameTimeSeconds());

        if (!checkCollision(position.add(velocity))) { // Prevents walking through walls
            position.add(velocity);
        }
    }

    public abstract void attack(Mob target);

    private boolean checkCollision(Vector3f newPosition) {
        // Placeholder for collision logic
        return false; // Change this when implementing collision detection
    }
}
