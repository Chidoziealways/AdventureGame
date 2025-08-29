package net.adventuregame.bloom

import com.adv.core.postProcessing.ImageRenderer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13

class CombineFilter {
    private val renderer: ImageRenderer
    private val shader: CombineShader

    init {
        shader = CombineShader()
        shader.start()
        shader.connectTextureUnits()
        shader.stop()
        renderer = ImageRenderer()
    }

    fun render(colourTexture: Int, highlightTexture: Int) {
        shader.start()
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture)
        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, highlightTexture)
        renderer.renderQuad()
        shader.stop()
    }

    fun cleanUp() {
        renderer.cleanUp()
        shader.cleanup()
    }
}
