package net.adventuregame.bloom

import com.chidozie.core.postProcessing.ImageRenderer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13

class BrightFilter(width: Int, height: Int) {
    private val renderer: ImageRenderer
    private val shader: BrightFilterShader

    init {
        shader = BrightFilterShader()
        renderer = ImageRenderer(width, height)
    }

    fun render(texture: Int) {
        shader.start()
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture)
        renderer.renderQuad()
        shader.stop()
    }

    val outputTexture: Int
        get() = renderer.outputTexture

    fun cleanUp() {
        renderer.cleanUp()
        shader.cleanup()
    }
}
