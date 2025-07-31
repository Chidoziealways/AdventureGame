package com.chidozie.core.font

/**
 * Stores the vertex data for all the quads on which a text will be rendered.
 * @author Karl
 */
class TextMeshData(val pageMeshes: Map<Int, PageMeshData>){
    val totalVertexCount: Int
        get() = pageMeshes.values.sumOf { it.vertexCount }
}
