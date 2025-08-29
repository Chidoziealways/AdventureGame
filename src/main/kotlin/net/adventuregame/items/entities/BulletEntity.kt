package net.adventuregame.items.entities

import net.adventuregame.entity.Entity
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f

class BulletEntity(position: Vector3f, direction: Vector3f) : Entity(position, 0f, 0f, 0f, 0.1f) {
    override fun GetModels(): List<TexturedModel> {
        return emptyList()
    }

    private val velocity: Vector3f

    init {
        this.velocity = Vector3f(direction).normalize().mul(0.5f)
    }

    fun update() {
        increasePosition(velocity.x, velocity.y, velocity.z)
    }
}
