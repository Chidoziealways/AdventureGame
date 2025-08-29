package net.adventuregame.mobs

import com.adv.core.terrains.Terrain
import net.adventuregame.entity.Entity
import net.adventuregame.game.GameState.Companion.getEntities
import net.adventuregame.models.TexturedModel
import net.adventuregame.scripting.api.LuaEntityAPI
import org.joml.Vector3f
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

abstract class Mob(
    val id: UUID = UUID.randomUUID(),
    index: Int,
    override var position: Vector3f,
    rotation: Vector3f,
    scale: Float,
    protected var maxHealth: Float,
    var isHostile: Boolean,
    var name: String = "Mob",
    protected var speed: Float
) : Entity(
    index,
    position, rotation.x, rotation.y, rotation.z, scale
) {
    abstract override fun GetModels(): List<TexturedModel>

    var health: Float = maxHealth
    private var path: MutableList<Vector3f>? = null // Path to follow

    open fun Move(direction: Vector3f?) {
        print("hh")
    }

    fun getUUID(): UUID = id

    open fun move(terrain: Terrain?) {
        LuaEntityAPI.getEventsForType(this.name)
            ?.get("onMove")
            ?.let { it.call(CoerceJavaToLua.coerce(this)) }
    }


    open fun TakeDamage(damage: Float, Attacker: Mob?) {
        health -= damage

        LuaEntityAPI.getEventsForType(this.name)
            ?.get("onDamage")
            ?.let { it.call(CoerceJavaToLua.coerce(this), LuaValue.valueOf(damage.toInt())) }

        if (health <= 0f) Die()
    }

    open fun Die() {
        LuaEntityAPI.getEventsForType(this.name)
            ?.get("onDeath")
            ?.let { it.call(CoerceJavaToLua.coerce(this)) }
    }

    abstract fun attack(target: Mob?)

    protected fun findClosestEnemy(): Mob? {
        var closest: Mob? = null
        var closestDistance = Float.Companion.MAX_VALUE

        for (mob in getNearbyMobs(10.0f)) { // Implement this
            val distance: Float = position.distance(mob.position)
            if (distance < 2.0f && distance < closestDistance) {
                closest = mob
                closestDistance = distance
                log.info("Closes Mob FOUND! It's name is: {}", mob.name)
            }
        }
        return closest
    }

    private fun getNearbyMobs(detectionRadius: Float): MutableList<Mob> {
        val nearbyMobs: MutableList<Mob> = ArrayList<Mob>()

        for (entity in getEntities()) { // Assuming getEntities() returns all entities
            if (entity is Mob && entity !== this) { // Check if it's a Mob and not the player itself
                if (position.distance(entity.position) <= detectionRadius) {
                    nearbyMobs.add(entity)
                }
            }
        }
        return nearbyMobs
    }

    abstract fun updateAI(terrain: Terrain)

    fun setPath(newPath: List<Vector3f>?) {
        //println("SetPath to NewPath: $newPath")
        this.path = newPath?.toMutableList()
        //println("Path is: $path")
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(Mob::class.java)
    }
}
