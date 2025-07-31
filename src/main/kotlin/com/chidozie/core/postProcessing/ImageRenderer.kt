package com.chidozie.core.postProcessing

import org.lwjgl.opengl.GL11

class ImageRenderer {
    private var fbo: Fbo? = null

    constructor(width: Int, height: Int) {
        this.fbo = Fbo(width, height, Fbo.Companion.NONE)
    }

    constructor()

    fun renderQuad() {
        if (fbo != null) {
            fbo!!.bindFrameBuffer()
        }
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4)
        if (fbo != null) {
            fbo!!.unbindFrameBuffer()
        }
    }

    val outputTexture: Int
        get() = fbo!!.colourTexture

    fun cleanUp() {
        if (fbo != null) {
            fbo!!.cleanUp()
        }
    }
}
