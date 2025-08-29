package net.adventuregame.mobs.hostile

import com.adv.core.terrains.Terrain
import net.adventuregame.luaMainThreadQueue
import net.adventuregame.mobs.Mob
import net.adventuregame.mobs.Pathfinding
import org.joml.Vector3f
import org.luaj.vm2.LuaFunction
import org.luaj.vm2.LuaTable
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import java.util.UUID

class LuaHostileMobContext(private val mob: HostileMob, private val terrain: Terrain? = null) {
    fun getPosition(): Vector3f = mob.position
    fun getTarget(): Mob? = mob.getTarget()
    fun get(): HostileMob = mob
    fun setTarget(target: Mob?) { mob.setTarget(target) }
    fun moveTo(position: Vector3f) {
        mob.Move(position)
    }
    fun takeDamage(damage: Float, attacker: Mob) = mob.TakeDamage(damage, attacker)
    fun setPath(path: List<Vector3f>) {
      //  println("Set Path to $path")
        mob.setPath(path)
    }
    fun findPathAsync(start: Vector3f, target: Vector3f?, callback: LuaFunction) {
        //println(">>> [Kotlin] findPathAsync called with $start -> $target")
        Pathfinding.findPathAsync(start, target, terrain!!) { result ->
          //  println(">>> [Kotlin] Pathfinding result: ${result.size}") // <--- Add this
            val luaTable = LuaTable()
            result.forEachIndexed { index, vec ->
                val vecTable = LuaTable()
                vecTable.set("x", vec.x.toDouble())
                vecTable.set("y", vec.y.toDouble())
                vecTable.set("z", vec.z.toDouble())
                luaTable.set(index + 1, vecTable)
            }

            // Queue it for the next main thread tick
            luaMainThreadQueue.add {
            //    println("Calling Lua callback with result size: ${result.size}")
                try {
                    callback.call(luaTable)
                } catch (e: Exception) {
              //      println("Lua Callback crashed: ${e.message}")
                }
            }
        }
    }
    fun findPath(target: Vector3f): List<Vector3f> {
        return Pathfinding.findPath(mob.position, target, terrain!!) ?: emptyList()
    }
    fun attack(target: Mob) = mob.attack(target)
    fun getNearbyEnemies(radius: Float): LuaTable {
        val enemies = mob.getNearbyEnemies(radius)
        val table = LuaTable()
        enemies.forEachIndexed { i, mob -> table.set(i + 1, CoerceJavaToLua.coerce(mob)) }
        return table
    }
    fun getHealth(): Float = mob.health
    fun setHealth(newHealth: Float){mob.health = newHealth}
    fun getID(): UUID = mob.getUUID()
    fun moveTowards(position: Vector3f) {
        val direction = Vector3f(position).sub(mob.position).normalize()
        //println("Direction towards nextPos: $direction")
        mob.Move(direction)
    }
}
