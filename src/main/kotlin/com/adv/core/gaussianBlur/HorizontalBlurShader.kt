package com.adv.core.gaussianBlur

import com.adv.core.shaders.ShaderProgram

class HorizontalBlurShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var location_targetWidth = 0

    fun loadTargetWidth(width: Float) {
        super.loadFloat(location_targetWidth, width)
    }

    override fun getAllUniformLocations() {
        location_targetWidth = super.getUniformLocation("targetWidth")
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
    }

    companion object {
        private const val VERTEX_FILE = "gaussianBlur/horizontalBlurVertexShader.vert"
        private const val FRAGMENT_FILE = "gaussianBlur/blurFragmentShader.frag"
    }
}
