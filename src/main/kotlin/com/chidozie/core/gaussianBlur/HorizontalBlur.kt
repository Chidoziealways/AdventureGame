package com.chidozie.core.gaussianBlur

import com.chidozie.core.postProcessing.ImageRenderer
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13

class HorizontalBlur(targetFboWidth: Int, targetFboHeight: Int) {
    private val renderer: ImageRenderer
    private val shader: HorizontalBlurShader

    init {
        shader = HorizontalBlurShader()
        shader.start()
        shader.loadTargetWidth(targetFboWidth.toFloat())
        shader.stop()
        renderer = ImageRenderer(targetFboWidth, targetFboHeight)
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
