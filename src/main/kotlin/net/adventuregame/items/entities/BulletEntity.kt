package net.adventuregame.items.entities

import net.adventuregame.entity.Entity
import org.joml.Vector3f

class BulletEntity(position: Vector3f, direction: Vector3f) : Entity(null, position, 0f, 0f, 0f, 0.1f) {
    private val velocity: Vector3f

    init {
        this.velocity = Vector3f(direction).normalize().mul(0.5f)
    }

    fun update() {
        increasePosition(velocity.x, velocity.y, velocity.z)
    }
}
