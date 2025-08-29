package com.adv.core.postProcessing

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13

class ContrastChanger {
    private val renderer: ImageRenderer
    private val shader: ContrastShader

    init {
        shader = ContrastShader()
        renderer = ImageRenderer()
    }

    fun render(texture: Int) {
        shader.start()
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture)
        renderer.renderQuad()
        shader.stop()
    }

    fun cleanUp() {
        renderer.cleanUp()
        shader.cleanup()
    }
}
