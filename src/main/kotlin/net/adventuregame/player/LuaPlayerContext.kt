package net.adventuregame.player

import net.adventuregame.mobs.Mob
import org.joml.Vector3f

class LuaPlayerContext(private val player: Player) {
    fun takeDamage(damage: Float, attacker: Mob) = player.TakeDamage(damage, attacker)
    fun getPosition(): Vector3f = player.position
    fun get(): Player = player
}