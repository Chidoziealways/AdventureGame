package net.adventuregame.water

import com.chidozie.core.renderEngine.Loader
import com.chidozie.core.renderEngine.WindowManager
import net.adventuregame.entity.Camera
import net.adventuregame.entity.Light
import net.adventuregame.models.RawModel
import net.adventuregame.toolbox.Maths
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class WaterRenderer(
    loader: Loader,
    private val shader: WaterShader,
    projectionMatrix: Matrix4f?,
    private val fbos: WaterFrameBuffers
) {
    private var quad: RawModel? = null

    private var moveFactor = 0f

    private val dudvTexture: Int
    private val normalTexture: Int


    init {
        dudvTexture = loader.loadGameTexture("DuDvmaps/" + DUDV_MAP)
        normalTexture = loader.loadGameTexture("normalmaps/" + NORMAL_MAP)
        shader.start()
        shader.connectTextureUnits()
        shader.loadProjectionMatrix(projectionMatrix!!)
        shader.stop()
        setUpVAO(loader)
    }


    fun render(water: MutableList<WaterTile>, camera: Camera, sun: Light) {
        prepareRender(camera, sun)
        for (tile in water) {
            val modelMatrix = Maths.createTransformationMatrix(
                Vector3f(tile.x, tile.height, tile.z), 0f, 0f, 0f,
                WaterTile.Companion.TILE_SIZE
            )
            shader.loadModelMatrix(modelMatrix)
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad!!.vertexCount)
        }
        unbind()
    }

    private fun prepareRender(camera: Camera, sun: Light) {
        shader.start()
        shader.loadViewMatrix(camera)
        moveFactor += WAVE_SPEED * WindowManager.frameTimeSeconds
        moveFactor %= 1f
        shader.loadMoveFactor(moveFactor)
        shader.loadLight(sun)
        GL30.glBindVertexArray(quad!!.vaoId)
        GL20.glEnableVertexAttribArray(0)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.reflectionTexture)
        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.refractionTexture)
        GL13.glActiveTexture(GL13.GL_TEXTURE2)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture)
        GL13.glActiveTexture(GL13.GL_TEXTURE3)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalTexture)
        GL13.glActiveTexture(GL13.GL_TEXTURE4)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.refractionDepthTexture)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    }

    private fun unbind() {
        GL11.glDisable(GL11.GL_BLEND)
        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
        shader.stop()
    }


    private fun setUpVAO(loader: Loader) {
        // Just x and z vectex positions here, y is set to 0 in v.shader
        val vertices = floatArrayOf(-1f, -1f, -1f, 1f, 1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f)
        quad = loader.loadToVAO(vertices, 2)
    }


    companion object {
        private const val DUDV_MAP = "waterDUDV"
        private const val NORMAL_MAP = "normalMap"
        private const val WAVE_SPEED = 0.03f
    }
}
