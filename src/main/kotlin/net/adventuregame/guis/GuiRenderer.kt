package net.adventuregame.guis

import com.adv.core.renderEngine.Loader
import net.adventuregame.models.RawModel
import net.adventuregame.toolbox.Maths
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class GuiRenderer(loader: Loader) {
    private val quad: RawModel
    private val shader: GuiShader

    init {
        val positions = floatArrayOf(-1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f)
        quad = loader.loadToVAO(positions, 2)
        shader = GuiShader()
    }

    fun render() {
        shader.start()
        GL30.glBindVertexArray(quad.vaoId)
        GL20.glEnableVertexAttribArray(0)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        for (gui in guis) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.texture)
            val matrix = Maths.createTransformationMatrix(gui.position!!, gui.scale!!)
            shader.loadTransformation(matrix)
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.vertexCount)
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_BLEND)
        GL20.glDisableVertexAttribArray(0)
        GL30.glBindVertexArray(0)
        shader.stop()
    }

    fun cleanUp() {
        shader.cleanup()
    }

    companion object {
        private val guis: MutableList<GuiTexture> = ArrayList<GuiTexture>()

        fun addGui(gui: GuiTexture?) {
            if (gui != null) {
                guis.add(gui)
            }
        }

        fun removeGui(gui: GuiTexture?) {
            guis.remove(gui)
        }
    }
}
