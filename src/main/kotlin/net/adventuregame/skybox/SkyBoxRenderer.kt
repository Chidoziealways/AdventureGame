package net.adventuregame.skybox

import com.chidozie.core.renderEngine.Loader
import com.chidozie.core.renderEngine.WindowManager
import net.adventuregame.entities.Camera
import net.adventuregame.models.RawModel
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class SkyBoxRenderer(loader: Loader, projectionMatrix: Matrix4f) {
    private val cube: RawModel
    private val texture: Int
    private val nightTexture: Int
    private val shader: SkyBoxShader
    private var time = 0f

    init {
        cube = loader.loadToVAO(VERTICES, 3)
        texture = loader.loadCubeMap(TEXTURE_FILES)
        nightTexture = loader.loadCubeMap(NIGHT_TEXTURE_FILES)
        shader = SkyBoxShader()
        shader.start()
        shader.connectTextureUnits()
        shader.loadProjectionMatrix(projectionMatrix)
        shader.stop()
    }

    fun render(camera: Camera, r: Float, g: Float, b: Float) {
        shader.start()
        shader.loadViewMatrix(camera)
        shader.loadFogColour(r, g, b)
        GL30.glBindVertexArray(cube.vaoId)
        GL20.glEnableVertexAttribArray(0)
        bindTextures()
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.vertexCount)
        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
        shader.stop()
    }

    private fun bindTextures() {
        time += WindowManager.frameTimeSeconds * 1000
        time %= 24000f
        val texture1: Int
        val texture2: Int
        val blendFactor: Float
        if (time >= 0 && time < 5000) {
            texture1 = nightTexture
            texture2 = nightTexture
            blendFactor = (time - 0) / (5000 - 0)
        } else if (time >= 5000 && time < 8000) {
            texture1 = nightTexture
            texture2 = texture
            blendFactor = (time - 5000) / (8000 - 5000)
        } else if (time >= 8000 && time < 21000) {
            texture1 = texture
            texture2 = texture
            blendFactor = (time - 8000) / (21000 - 8000)
        } else {
            texture1 = texture
            texture2 = nightTexture
            blendFactor = (time - 21000) / (24000 - 21000)
        }


        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture1)
        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture2)
        shader.loadBlendFactor(blendFactor)
    }


    companion object {
        private const val SIZE = 500f

        private val VERTICES = floatArrayOf(
            -SIZE, SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,


            -SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, -SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, -SIZE, SIZE,


            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,


            -SIZE, -SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            SIZE, -SIZE, SIZE,
            -SIZE, -SIZE, SIZE,


            -SIZE, SIZE, -SIZE,
            SIZE, SIZE, -SIZE,
            SIZE, SIZE, SIZE,
            SIZE, SIZE, SIZE,
            -SIZE, SIZE, SIZE,
            -SIZE, SIZE, -SIZE,


            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE, SIZE,
            SIZE, -SIZE, SIZE
        )

        private val TEXTURE_FILES = arrayOf<String?>("right", "left", "top", "bottom", "back", "front")
        private val NIGHT_TEXTURE_FILES =
            arrayOf<String?>("nightRight", "nightLeft", "nightTop", "nightBottom", "nightBack", "nightFront")
    }
}
