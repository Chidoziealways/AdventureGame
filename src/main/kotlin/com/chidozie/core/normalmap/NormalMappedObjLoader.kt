package com.chidozie.core.normalmap

import com.chidozie.core.renderEngine.Loader
import net.adventuregame.models.RawModel
import org.joml.Vector2f
import org.joml.Vector3f
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object NormalMappedObjLoader {
    fun loadOBJ(objFileName: String?, loader: Loader): RawModel? {
        val isr: InputStream? = null
        val objFile =
            NormalMappedObjLoader::class.java.getResourceAsStream("/assets/adventuregame/models/" + objFileName + ".obj")
        if (objFile == null) {
            System.err.println("File not found in class path; don't use any extension")
            return null
        }
        val reader = BufferedReader(InputStreamReader(objFile))
        var line: String?
        val vertices: MutableList<VertexNM> = ArrayList<VertexNM>()
        val textures: MutableList<Vector2f> = ArrayList<Vector2f>()
        val normals: MutableList<Vector3f> = ArrayList<Vector3f>()
        val indices: MutableList<Int?> = ArrayList<Int?>()
        try {
            while (true) {
                line = reader.readLine()
                if (line == null) {
                    break
                }
                if (line.startsWith("v ")) {
                    val currentLine: Array<String?> =
                        line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val vertex = Vector3f(
                        currentLine[1]!!.toFloat(),
                        currentLine[2]!!.toFloat(),
                        currentLine[3]!!.toFloat()
                    )
                    val newVertex = VertexNM(vertices.size, vertex)
                    vertices.add(newVertex)
                } else if (line.startsWith("vt ")) {
                    val currentLine: Array<String?> =
                        line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val texture = Vector2f(
                        currentLine[1]!!.toFloat(),
                        currentLine[2]!!.toFloat()
                    )
                    textures.add(texture)
                } else if (line.startsWith("vn ")) {
                    val currentLine: Array<String?> =
                        line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val normal = Vector3f(
                        currentLine[1]!!.toFloat(),
                        currentLine[2]!!.toFloat(),
                        currentLine[3]!!.toFloat()
                    )
                    normals.add(normal)
                } else if (line.startsWith("f ")) {
                    break
                }
            }
            while (line != null && line.startsWith("f ")) {
                val currentLine: Array<String?> =
                    line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val vertex1: Array<String?> =
                    currentLine[1]!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val vertex2: Array<String?> =
                    currentLine[2]!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val vertex3: Array<String?> =
                    currentLine[3]!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val v0 = processVertex(vertex1, vertices, indices)
                val v1 = processVertex(vertex2, vertices, indices)
                val v2 = processVertex(vertex3, vertices, indices)
                calculateTangents(v0, v1, v2, textures) // NEW
                line = reader.readLine()
            }
            reader.close()
        } catch (e: IOException) {
            System.err.println("Error reading the file")
        }
        removeUnusedVertices(vertices)
        val verticesArray = FloatArray(vertices.size * 3)
        val texturesArray = FloatArray(vertices.size * 2)
        val normalsArray = FloatArray(vertices.size * 3)
        val tangentsArray = FloatArray(vertices.size * 3)
        val furthest = convertDataToArrays(
            vertices, textures, normals, verticesArray,
            texturesArray, normalsArray, tangentsArray
        )
        val indicesArray = convertIndicesListToArray(indices)

        return loader.loadToVAO(verticesArray, texturesArray, normalsArray, tangentsArray, indicesArray)
    }

    //NEW 
    private fun calculateTangents(v0: VertexNM, v1: VertexNM, v2: VertexNM, textures: MutableList<Vector2f>) {
        val deltaPos1 = Vector3f(v1.position).sub(v0.position)
        val deltaPos2 = Vector3f(v2.position).sub(v0.position)
        val uv0: Vector2f? = textures[v0.textureIndex]
        val uv1 = textures[v1.textureIndex]
        val uv2 = textures[v2.textureIndex]
        val deltaUv1 = Vector2f(uv1).sub(uv0)
        val deltaUv2 = Vector2f(uv2).sub(uv0)

        val r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x)
        deltaPos1.mul(deltaUv2.y)
        deltaPos2.mul(deltaUv1.y)
        val tangent = deltaPos1.sub(deltaPos2).mul(r)
        v0.addTangent(tangent)
        v1.addTangent(tangent)
        v2.addTangent(tangent)
    }

    private fun processVertex(
        vertex: Array<String?>, vertices: MutableList<VertexNM>,
        indices: MutableList<Int?>
    ): VertexNM {
        val index = vertex[0]!!.toInt() - 1
        val currentVertex = vertices.get(index)
        val textureIndex = vertex[1]!!.toInt() - 1
        val normalIndex = vertex[2]!!.toInt() - 1
        if (!currentVertex.isSet) {
            currentVertex.textureIndex = textureIndex
            currentVertex.normalIndex = normalIndex
            indices.add(index)
            return currentVertex
        } else {
            return dealWithAlreadyProcessedVertex(
                currentVertex, textureIndex, normalIndex, indices,
                vertices
            )
        }
    }

    private fun convertIndicesListToArray(indices: MutableList<Int?>): IntArray {
        val indicesArray = IntArray(indices.size)
        for (i in indicesArray.indices) {
            indicesArray[i] = indices.get(i)!!
        }
        return indicesArray
    }

    private fun convertDataToArrays(
        vertices: MutableList<VertexNM>, textures: MutableList<Vector2f>,
        normals: MutableList<Vector3f>, verticesArray: FloatArray, texturesArray: FloatArray,
        normalsArray: FloatArray, tangentsArray: FloatArray
    ): Float {
        var furthestPoint = 0f
        for (i in vertices.indices) {
            val currentVertex = vertices[i]
            if (currentVertex.length > furthestPoint) {
                furthestPoint = currentVertex.length
            }
            val position = currentVertex.position
            val textureCoord = textures[currentVertex.textureIndex]
            val normalVector = normals[currentVertex.normalIndex]
            val tangent = currentVertex.averageTangent
            verticesArray[i * 3] = position.x
            verticesArray[i * 3 + 1] = position.y
            verticesArray[i * 3 + 2] = position.z
            texturesArray[i * 2] = textureCoord.x
            texturesArray[i * 2 + 1] = 1 - textureCoord.y
            normalsArray[i * 3] = normalVector.x
            normalsArray[i * 3 + 1] = normalVector.y
            normalsArray[i * 3 + 2] = normalVector.z
            tangentsArray[i * 3] = tangent.x
            tangentsArray[i * 3 + 1] = tangent.y
            tangentsArray[i * 3 + 2] = tangent.z
        }
        return furthestPoint
    }

    private fun dealWithAlreadyProcessedVertex(
        previousVertex: VertexNM, newTextureIndex: Int,
        newNormalIndex: Int, indices: MutableList<Int?>, vertices: MutableList<VertexNM>
    ): VertexNM {
        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
            indices.add(previousVertex.index)
            return previousVertex
        } else {
            val anotherVertex = previousVertex.duplicateVertex
            if (anotherVertex != null) {
                return dealWithAlreadyProcessedVertex(
                    anotherVertex, newTextureIndex,
                    newNormalIndex, indices, vertices
                )
            } else {
                val duplicateVertex = previousVertex.duplicate(vertices.size) //NEW
                duplicateVertex.textureIndex = newTextureIndex
                duplicateVertex.normalIndex = newNormalIndex
                previousVertex.duplicateVertex = duplicateVertex
                vertices.add(duplicateVertex)
                indices.add(duplicateVertex.index)
                return duplicateVertex
            }
        }
    }

    private fun removeUnusedVertices(vertices: MutableList<VertexNM>) {
        for (vertex in vertices) {
            vertex.averageTangents()
            if (!vertex.isSet) {
                vertex.textureIndex = 0
                vertex.normalIndex = 0
            }
        }
    }
}