package net.adventuregame.mobs.hostile

import com.adv.core.renderEngine.WindowManager
import com.adv.core.terrains.Terrain
import net.adventuregame.game.GameState
import net.adventuregame.mobs.Mob
import net.adventuregame.mobs.passive.LuaPassiveMobContext
import net.adventuregame.mobs.passive.PassiveMob
import net.adventuregame.player.LuaPlayerContext
import net.adventuregame.player.Player
import net.adventuregame.scripting.api.LuaEntityAPI
import org.joml.Vector3f
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.atan2

abstract class HostileMob(
    index: Int,
    position: Vector3f?,
    rotation: Vector3f?,
    scale: Float,
    maxHealth: Float,
    name: String = "HostileMob",
    speed: Float
) : Mob(index = index, position = position!!, rotation = rotation!!, scale = scale, maxHealth = maxHealth, isHostile = true, name = name, speed = speed) {
    private var target: Mob? = null // Target for AI to follow

    fun setTarget(target: Mob?) {
        this.target = target
    }

    fun getTarget(): Mob? {
        return target
    }

    var aiTickCooldown = 0f

    override fun updateAI(terrain: Terrain) {
        aiTickCooldown -= WindowManager.frameTimeSeconds
        if (aiTickCooldown > 0) return

        aiTickCooldown = 0.5f // AI runs once every 0.5 seconds

        LuaEntityAPI.getEventsForType(name ?: return)
            ?.get("updateAI")
            ?.call(CoerceJavaToLua.coerce(LuaHostileMobContext(this, terrain)))
    }

    fun tickPath(terrain: Terrain) {
        LuaEntityAPI.getEventsForType(name ?: return)
            ?.get("tickAI")
            ?.call(CoerceJavaToLua.coerce(LuaHostileMobContext(this, terrain)))
    }

    fun getNearbyEnemies(radius: Float): List<Mob> {
        //println("Getting Enemies")
        val enemies = mutableListOf<Mob>()
        for (entity in GameState.getEntities()) {
            if (entity is Mob && entity !== this && entity.isHostile != this.isHostile) {
                if (entity.position.distance(this.position) <= radius) {
                    enemies.add(entity)
                }
            }
        }
        //println("Enemies: $enemies")
        return enemies
    }

    override fun Move(direction: Vector3f?) {
        if (direction == null) return

        val angle = Math.toDegrees(atan2(direction.x.toDouble(), direction.z.toDouble())).toFloat()

        //
        val turnSpeed = 200f //tweak this for snappier or slower turning
        val delta = angle - rotY
        rotY += delta.coerceIn(-turnSpeed * WindowManager.frameTimeSeconds, turnSpeed * WindowManager.frameTimeSeconds)

        val velocity = Vector3f(direction).normalize().mul(speed * WindowManager.frameTimeSeconds)
        //println("Velocity: $velocity (Speed: $speed, FrameTime: ${WindowManager.frameTimeSeconds})")
        val newPosition = Vector3f(position).add(velocity)
        //println("Trying to move to: $newPosition")

        if (!checkCollision(newPosition)) {
            position.set(newPosition)
            //println("Moved to: $position")
        }else {
            //println("Collision detected, staying at: $position")
        }
    }

    override fun attack(target: Mob?) {
        LuaEntityAPI.getEventsForType(this.name)
            ?.get("onAttack")
            ?.let { callback ->
                val targetContext = when (target) {
                    is Player -> CoerceJavaToLua.coerce(LuaPlayerContext(target))
                    is PassiveMob -> CoerceJavaToLua.coerce(LuaPassiveMobContext(target))
                    else -> LuaValue.NIL
                }

                callback.call(
                    targetContext,
                    CoerceJavaToLua.coerce(LuaHostileMobContext(this))
                )
            }
    }


    private fun checkCollision(newPosition: Vector3f?): Boolean {
        // Placeholder for collision logic
        return false // Change this when implementing collision detection
    }

    companion object {
        @JvmStatic
        protected val log: Logger = LoggerFactory.getLogger(HostileMob::class.java)
    }
}
