package net.adventuregame.entity

import net.adventuregame.models.TexturedModel
import org.joml.Vector3f

class MultiMeshEntity(
    val models: List<TexturedModel>,
    var position: Vector3f = Vector3f(),
    var rotX: Float = 0f,
    var rotY: Float = 0f,
    var rotZ: Float = 0f,
    var scale: Float = 1f
)
