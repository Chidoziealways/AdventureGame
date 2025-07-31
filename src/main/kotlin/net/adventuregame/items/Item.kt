package net.adventuregame.items

import net.adventuregame.items.entities.ItemEntity
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f

abstract class Item(val name: String?) {
    abstract fun createEntity(position: Vector3f): ItemEntity?

    abstract fun getGuiModel(): TexturedModel?
}
