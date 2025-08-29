package com.adv.core.font

import org.lwjgl.opengl.GL30.glDeleteVertexArrays

class TextMeshData(
    val vaoId: Int,
    val vertexCount: Int
) {
    fun delete() {
        glDeleteVertexArrays(vaoId)
    }
}
