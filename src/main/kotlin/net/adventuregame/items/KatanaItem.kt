package net.adventuregame.items

import net.adventuregame.entities.Entity
import net.adventuregame.items.entities.ItemEntity
import net.adventuregame.items.entities.KatanaEntity
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f

class KatanaItem(name: String, val model: TexturedModel) : Item(name){
    override fun createEntity(position: Vector3f): ItemEntity {
        return KatanaEntity(model, 0, position, 0f, this);
    }

    override fun getGuiModel(): TexturedModel? {
        return model
    }

    override fun toString(): String {
        return name!!
    }
}