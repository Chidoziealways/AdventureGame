package com.adv.core.font

class PageMeshData(
    val vertexPositions: FloatArray,
    val textureCoords: FloatArray
) {
    val vertexCount: Int
        get() = vertexPositions.size / 2
}