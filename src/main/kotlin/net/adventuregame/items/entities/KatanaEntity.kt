package net.adventuregame.items.entities

import net.adventuregame.entities.Entity
import net.adventuregame.game.GameState
import net.adventuregame.items.KatanaItem
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory

class KatanaEntity(model : TexturedModel, index: Int, pos: Vector3f, scale: Float, private val katanaItem: KatanaItem) : ItemEntity(model,  pos,
    Vector3f(0f, 0f, 0f), scale, katanaItem) {

    override fun toString(): String {
        return katanaItem.toString()
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(KatanaEntity::class.java)
        private val mark: Marker = MarkerFactory.getMarker("KATANAENTITY")
    }
}