package com.adv.core.terrains

import com.adv.core.renderEngine.Loader
import com.adv.core.textures.TerrainTexture
import com.adv.core.textures.TerrainTexturePack
import net.adventuregame.models.RawModel
import net.adventuregame.toolbox.Maths
import org.joml.Vector2f
import org.joml.Vector3f
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.floor

class Terrain(
    gridX: Int, gridZ: Int, loader: Loader, val texturePack: TerrainTexturePack?,
    val blendMap: TerrainTexture?, seed: Int
) {
    var x: Float
    var z: Float
    var model: RawModel?
    private val generator: HeightsGenerator

    private lateinit var heights: Array<FloatArray?>

    init {
        this.x = gridX * SIZE
        this.z = gridZ * SIZE
        generator = HeightsGenerator(seed)
        this.model = generateTerrain(loader)
    }

    fun getHeightOfTerrain(worldX: Float, worldZ: Float): Float {
        val terrainX = worldX - this.x
        val terrainZ = worldZ - this.z
        val gridSquareSize: Float = SIZE / (heights.size.toFloat() - 1)
        val gridX = floor((terrainX / gridSquareSize).toDouble()).toInt()
        val gridZ = floor((terrainZ / gridSquareSize).toDouble()).toInt()
        if (gridX >= heights.size - 1 || gridZ >= heights.size - 1 || gridX < 0 || gridZ < 0) {
            return 0f
        }
        val xCoord = (terrainX % gridSquareSize) / gridSquareSize
        val zCoord = (terrainZ % gridSquareSize) / gridSquareSize
        val answer: Float
        if (xCoord <= (1 - zCoord)) {
            answer = Maths.barryCentric(
                Vector3f(0f, heights[gridX]!![gridZ], 0f), Vector3f(
                    1f,
                    heights[gridX + 1]!![gridZ], 0f
                ), Vector3f(0f, heights[gridX]!![gridZ + 1], 1f),
                Vector2f(xCoord, zCoord)
            )
        } else {
            answer = Maths.barryCentric(
                Vector3f(1f, heights[gridX + 1]!![gridZ], 0f),
                Vector3f(1f, heights[gridX + 1]!![gridZ + 1], 1f), Vector3f(
                    0f,
                    heights[gridX]!![gridZ + 1], 1f
                ), Vector2f(xCoord, zCoord)
            )
        }
        return answer
    }

    private fun generateTerrain(loader: Loader): RawModel? {
        val VERTEX_COUNT = 256
        heights = Array<FloatArray?>(VERTEX_COUNT) { FloatArray(VERTEX_COUNT) }
        val count = VERTEX_COUNT * VERTEX_COUNT
        val vertices = FloatArray(count * 3)
        val normals = FloatArray(count * 3)
        val textureCoords = FloatArray(count * 2)
        val indices = IntArray(6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1))
        var vertexPointer = 0
        for (i in 0..<VERTEX_COUNT) {
            for (j in 0..<VERTEX_COUNT) {
                vertices[vertexPointer * 3] = j.toFloat() / (VERTEX_COUNT.toFloat() - 1) * SIZE
                val height = getHeight(j, i, generator)
                heights[j]!![i] = height
                vertices[vertexPointer * 3 + 1] = height
                vertices[vertexPointer * 3 + 2] = i.toFloat() / (VERTEX_COUNT.toFloat() - 1) * SIZE
                val normal = calculateNormal(j, i, generator)
                normals[vertexPointer * 3] = normal.x
                normals[vertexPointer * 3 + 1] = normal.y
                normals[vertexPointer * 3 + 2] = normal.z
                textureCoords[vertexPointer * 2] = j.toFloat() / (VERTEX_COUNT.toFloat() - 1)
                textureCoords[vertexPointer * 2 + 1] = i.toFloat() / (VERTEX_COUNT.toFloat() - 1)
                vertexPointer++
            }
        }
        var pointer = 0
        for (gz in 0..<VERTEX_COUNT - 1) {
            for (gx in 0..<VERTEX_COUNT - 1) {
                val topLeft = (gz * VERTEX_COUNT) + gx
                val topRight = topLeft + 1
                val bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx
                val bottomRight = bottomLeft + 1
                indices[pointer++] = topLeft
                indices[pointer++] = bottomLeft
                indices[pointer++] = topRight
                indices[pointer++] = topRight
                indices[pointer++] = bottomLeft
                indices[pointer++] = bottomRight
            }
        }
        log.info("Generated Terrain of " + x + "x" + z)
        return loader.loadToVAO(vertices, textureCoords, normals, indices)
    }

    private fun calculateNormal(x: Int, z: Int, generator: HeightsGenerator): Vector3f {
        val heightL = getHeight(x - 1, z, generator)
        val heightR = getHeight(x + 1, z, generator)
        val heightD = getHeight(x, z - 1, generator)
        val heightU = getHeight(x, z + 1, generator)
        val normal = Vector3f(heightL - heightR, 2f, heightD - heightU)
        normal.normalize()
        return normal
    }

    private fun getHeight(x: Int, z: Int, generator: HeightsGenerator): Float {
        return generator.generateHeight(x, z)
    }

    var seed: Int
        get() = generator.seed
        set(seed) {
            generator.seed = seed
        }

    companion object {
        private const val SIZE = 2000f
        private const val MAX_HEIGHT = 80f
        private val MAX_PIXEL_COLOUR = (256 * 256 * 256).toFloat()
        private val log: Logger = LoggerFactory.getLogger(Terrain::class.java)
    }
}
