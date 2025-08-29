package com.adv.core.physics

import org.joml.Vector3f

data class AABB(val min: Vector3f, val max: Vector3f) {

    fun intersects(other: AABB): Boolean {
        return max.x >= other.min.x && min.x <= other.max.x &&
                max.y >= other.min.y && min.y <= other.max.y &&
                max.z >= other.min.z && min.z <= other.max.z
    }

    fun offset(dx: Float, dy: Float, dz: Float): AABB {
        return AABB(
            Vector3f(min.x + dx, min.y + dy, min.z + dz),
            Vector3f(max.x + dx, max.y + dy, max.z + dz)
        )
    }
}
