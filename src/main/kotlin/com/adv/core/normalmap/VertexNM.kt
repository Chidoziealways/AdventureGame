package com.adv.core.normalmap

import org.joml.Vector3f

class VertexNM(val index: Int, val position: Vector3f) {
    var textureIndex: Int = NO_INDEX
    var normalIndex: Int = NO_INDEX
    var duplicateVertex: VertexNM? = null
    val length: Float
    private var tangents: MutableList<Vector3f?> = ArrayList<Vector3f?>()
    val averageTangent: Vector3f = Vector3f(0f, 0f, 0f)

    init {
        this.length = position.length()
    }

    fun addTangent(tangent: Vector3f?) {
        tangents.add(tangent)
    }

    //NEW
    fun duplicate(newIndex: Int): VertexNM {
        val vertex = VertexNM(newIndex, position)
        vertex.tangents = this.tangents
        return vertex
    }

    fun averageTangents() {
        if (tangents.isEmpty()) {
            return
        }
        for (tangent in tangents) {
            averageTangent.add(tangent)
        }
        averageTangent.normalize()
    }

    val isSet: Boolean
        get() = textureIndex != NO_INDEX && normalIndex != NO_INDEX

    fun hasSameTextureAndNormal(textureIndexOther: Int, normalIndexOther: Int): Boolean {
        return textureIndexOther == textureIndex && normalIndexOther == normalIndex
    }

    companion object {
        private val NO_INDEX = -1
    }
}
