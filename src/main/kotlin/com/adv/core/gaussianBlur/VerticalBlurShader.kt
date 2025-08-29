package com.adv.core.gaussianBlur

import com.adv.core.shaders.ShaderProgram

class VerticalBlurShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var location_targetHeight = 0

    fun loadTargetHeight(height: Float) {
        super.loadFloat(location_targetHeight, height)
    }

    override fun getAllUniformLocations() {
        location_targetHeight = super.getUniformLocation("targetHeight")
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
    }

    companion object {
        private const val VERTEX_FILE = "gaussianBlur/verticalBlurVertexShader.vert"
        private const val FRAGMENT_FILE = "gaussianBlur/blurFragmentShader.frag"
    }
}
