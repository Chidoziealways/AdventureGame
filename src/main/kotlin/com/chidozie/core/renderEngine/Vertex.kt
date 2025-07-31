package com.chidozie.core.renderEngine

import org.joml.Vector3f

class Vertex(val index: Int, position: Vector3f) {
    val position: Vector3f?
    var textureIndex: Int = NO_INDEX
    var normalIndex: Int = NO_INDEX
    var duplicateVertex: Vertex? = null
    val length: Float

    init {
        this.position = position
        this.length = position.length()
    }

    val isSet: Boolean
        get() = textureIndex != NO_INDEX && normalIndex != NO_INDEX

    fun hasSameTextureAndNormal(textureIndexOther: Int, normalIndexOther: Int): Boolean {
        return textureIndexOther == textureIndex && normalIndexOther == normalIndex
    }

    companion object {
        private const val NO_INDEX = -1
    }
}
