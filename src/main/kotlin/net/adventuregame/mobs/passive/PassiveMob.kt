package net.adventuregame.mobs.passive

import com.adv.core.renderEngine.WindowManager
import com.adv.core.terrains.Terrain
import net.adventuregame.mobs.Mob
import net.adventuregame.scripting.api.LuaEntityAPI
import org.joml.Vector3f
import org.luaj.vm2.lib.jse.CoerceJavaToLua

abstract class PassiveMob(
    index: Int,
    position: Vector3f?,
    rotation: Vector3f?,
    scale: Float,
    maxHealth: Float,
    name: String = "PassiveMob",
    speed: Float
): Mob(index = index, position = position!!, rotation = rotation!!, scale = scale, maxHealth = maxHealth, isHostile = true, name = name, speed = speed) {
    override fun attack(target: Mob?) {
        println("This mob CAN'T ATTACK")
    }

    var aiTickCooldown = 0f

    override fun updateAI(terrain: Terrain) {
        aiTickCooldown -= WindowManager.frameTimeSeconds
        if (aiTickCooldown > 0) return

        aiTickCooldown = 0.5f

        LuaEntityAPI.getEventsForType(name)
            ?.get("updateAI")
            ?.call(CoerceJavaToLua.coerce(LuaPassiveMobContext(this, terrain)))
    }

}