package com.adv.core.renderEngine

import net.adventuregame.models.RawModel
import org.joml.Vector2f
import org.joml.Vector3f
import java.io.*

object OBJFileLoader {
    fun loadOBJ(objFileName: String, loader: Loader): RawModel? {
        val path = "assets/adventuregame/models/$objFileName.obj"
        val isr = OBJFileLoader::class.java.classLoader.getResourceAsStream(path)
            ?: throw FileNotFoundException("File not found in res: $path (don’t use extension)")

        val reader = BufferedReader(InputStreamReader(isr))
        val vertices = mutableListOf<Vertex>()
        val textures = mutableListOf<Vector2f>()
        val normals = mutableListOf<Vector3f>()
        val indices = mutableListOf<Int>()

        var line: String? = reader.readLine()

        try {
            while (line != null && !line.startsWith("f ")) {
                val tokens = line.trim().split("\\s+".toRegex())

                when {
                    line.startsWith("v ") -> {
                        val vertex = Vector3f(tokens[1].toFloat(), tokens[2].toFloat(), tokens[3].toFloat())
                        vertices.add(Vertex(vertices.size, vertex))
                    }
                    line.startsWith("vt ") -> {
                        val texture = Vector2f(tokens[1].toFloat(), tokens[2].toFloat())
                        textures.add(texture)
                    }
                    line.startsWith("vn ") -> {
                        val normal = Vector3f(tokens[1].toFloat(), tokens[2].toFloat(), tokens[3].toFloat())
                        normals.add(normal)
                    }
                }

                line = reader.readLine()
            }

            while (line != null && line.startsWith("f ")) {
                val tokens = line.trim().split("\\s+".toRegex())

                val verticesData = listOf(
                    tokens[1].split("/"),
                    tokens[2].split("/"),
                    tokens[3].split("/")
                )

                verticesData.forEach { processVertex(it.toTypedArray(), vertices, indices) }
                line = reader.readLine()
            }

            reader.close()
        } catch (e: IOException) {
            System.err.println("Error reading the OBJ file: ${e.message}")
            e.printStackTrace()
            return null
        }

        removeUnusedVertices(vertices)

        val verticesArray = FloatArray(vertices.size * 3)
        val texturesArray = FloatArray(vertices.size * 2)
        val normalsArray = FloatArray(vertices.size * 3)
        val furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray)
        val indicesArray = convertIndicesListToArray(indices)

        val data = ModelData(verticesArray, texturesArray, normalsArray, indicesArray, furthest)
        return loader.loadToVAO(data.vertices!!, data.textureCoords!!, data.normals!!, data.indices!!)
    }


    private fun processVertex(vertex: Array<String?>, vertices: MutableList<Vertex>, indices: MutableList<Int>) {
        val index = vertex[0]!!.toInt() - 1
        val currentVertex = vertices.get(index)
        val textureIndex = vertex[1]!!.toInt() - 1
        val normalIndex = vertex[2]!!.toInt() - 1
        if (!currentVertex.isSet) {
            currentVertex.textureIndex = textureIndex
            currentVertex.normalIndex = normalIndex
            indices.add(index)
        } else {
            dealWithAlreadyProcessedVertex(
                currentVertex, textureIndex, normalIndex, indices,
                vertices
            )
        }
    }

    private fun convertIndicesListToArray(indices: MutableList<Int>): IntArray {
        val indicesArray = IntArray(indices.size)
        for (i in indicesArray.indices) {
            indicesArray[i] = indices[i]
        }
        return indicesArray
    }

    private fun convertDataToArrays(
        vertices: MutableList<Vertex>, textures: MutableList<Vector2f>,
        normals: MutableList<Vector3f>, verticesArray: FloatArray, texturesArray: FloatArray,
        normalsArray: FloatArray
    ): Float {
        var furthestPoint = 0f
        for (i in vertices.indices) {
            val currentVertex = vertices[i]
            if (currentVertex.length > furthestPoint) {
                furthestPoint = currentVertex.length
            }
            val position = currentVertex.position!!
            val textureCoord = textures[currentVertex.textureIndex]
            val normalVector = normals[currentVertex.normalIndex]
            verticesArray[i * 3] = position.x
            verticesArray[i * 3 + 1] = position.y
            verticesArray[i * 3 + 2] = position.z
            texturesArray[i * 2] = textureCoord.x
            texturesArray[i * 2 + 1] = 1 - textureCoord.y
            normalsArray[i * 3] = normalVector.x
            normalsArray[i * 3 + 1] = normalVector.y
            normalsArray[i * 3 + 2] = normalVector.z
        }
        return furthestPoint
    }

    private fun dealWithAlreadyProcessedVertex(
        previousVertex: Vertex, newTextureIndex: Int,
        newNormalIndex: Int, indices: MutableList<Int>, vertices: MutableList<Vertex>
    ) {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.index)
        } else {
            val anotherVertex = previousVertex.duplicateVertex
            if (anotherVertex != null) {
                dealWithAlreadyProcessedVertex(
                    anotherVertex, newTextureIndex, newNormalIndex,
                    indices, vertices
                )
            } else {
                val duplicateVertex = Vertex(vertices.size, previousVertex.position!!)
                duplicateVertex.textureIndex = newTextureIndex
                duplicateVertex.normalIndex = newNormalIndex
                previousVertex.duplicateVertex = duplicateVertex
                vertices.add(duplicateVertex)
                indices.add(duplicateVertex.index)
            }
        }
    }

    private fun removeUnusedVertices(vertices: MutableList<Vertex>) {
        for (vertex in vertices) {
            if (!vertex.isSet) {
                vertex.textureIndex = 0
                vertex.normalIndex = 0
            }
        }
    }
}