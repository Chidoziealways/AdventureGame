package net.adventuregame

import org.joml.Vector3f

fun Vector3f.equalsPos(other: Vector3f, epsilon: Float = 0.1f): Boolean {
    return this.distance(other) < epsilon
}