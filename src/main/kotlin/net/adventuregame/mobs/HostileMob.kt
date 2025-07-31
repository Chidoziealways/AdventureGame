package net.adventuregame.mobs

import com.chidozie.core.renderEngine.WindowManager
import com.chidozie.core.terrains.Terrain
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class HostileMob(
    model: TexturedModel,
    index: Int,
    position: Vector3f?,
    rotation: Vector3f?,
    scale: Float,
    maxHealth: Float,
    name: String?,
    speed: Float
) : Mob(model, index, position!!, rotation!!, scale, maxHealth, true, name, speed) {
    private var target: Mob? = null // Target for AI to follow
    private var path: MutableList<Vector3f>? = null // Path to follow

    override fun TakeDamage(damage: Float, Attacker: Mob?) {
        health -= damage
        log.info("{} has taken {} damage from {}!", name, damage, Attacker)

        if (health <= 0) {
            Die()
        } else {
            setTarget(Attacker)
        }
    }

    override fun Die() {
        log.info("This poor (but mean) mob has died. Its name was {}", name)
    }

    fun setTarget(target: Mob?) {
        this.target = target
    }

    fun updateAI(terrain: Terrain) {
        if (target == null) return

        if (path == null || path!!.isEmpty()) {
            path = Pathfinding.findPath(position, target!!.position, terrain)
        }

        if (!path!!.isEmpty()) {
            val nextPoint = path!![0]
            val direction = Vector3f(nextPoint).sub(position).normalize()

            Move(direction)

            if (position.distance(nextPoint) < 2.0f) {
                path!!.removeAt(0)
            }
        }
    }

    override fun Move(direction: Vector3f?) {
        val velocity: Vector3f? = Vector3f(direction).mul(speed * WindowManager.frameTimeSeconds)

        if (!checkCollision(position.add(velocity))) { // Prevents walking through walls
            position.add(velocity)
        }
    }

    abstract override fun attack(target: Mob?)

    private fun checkCollision(newPosition: Vector3f?): Boolean {
        // Placeholder for collision logic
        return false // Change this when implementing collision detection
    }

    companion object {
        @JvmStatic
        protected val log: Logger = LoggerFactory.getLogger(HostileMob::class.java)
    }
}
