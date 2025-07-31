package net.adventuregame.items

import net.adventuregame.items.entities.ItemEntity
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f

class Gold() : Item("Gold") {
    override fun createEntity(position: Vector3f): ItemEntity? {
        return null
    }

    override fun getGuiModel(): TexturedModel? {
        return null
    }
}
