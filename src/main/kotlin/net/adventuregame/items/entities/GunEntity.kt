package net.adventuregame.items.entities

import net.adventuregame.items.GunItem
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GunEntity(model: TexturedModel, index: Int, position: Vector3f, scale: Float, private val gunItem: GunItem) :
    ItemEntity(
        model, position, Vector3f(0f, 0f, 0f), 1f,
        gunItem
    ) {
    override fun toString(): String {
        return gunItem.toString()
    }

    companion object {
        private val log: Logger? = LoggerFactory.getLogger(GunEntity::class.java)
    }
}
