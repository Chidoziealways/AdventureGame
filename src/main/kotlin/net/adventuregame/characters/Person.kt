package net.adventuregame.characters

import com.adv.core.terrains.Terrain
import net.adventuregame.cutscene.CutsceneLine
import net.adventuregame.mobs.Mob
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f

open class Person(
    name: String,
    val gender: String,
    position: Vector3f
) : Mob(
    index = 0,
    position = position,
    rotation = Vector3f(0f, 0f, 0f),
    scale = 1f,
    maxHealth = 30f,
    isHostile = false,
    speed = 10f,
    name = name
) {

    fun talk(statement: String, language: String): CutsceneLine {
        return CutsceneLine("$name: $statement", typewriter = true, fontName = language)
    }

    override fun GetModels(): List<TexturedModel> {
        TODO("Not yet implemented")
    }

    override fun attack(target: Mob?) {
        TODO("Not yet implemented")
    }

    override fun updateAI(terrain: Terrain) {
        TODO("Not yet implemented")
    }
}
