package com.adv.core.postProcessing

import com.adv.core.shaders.ShaderProgram

class ContrastShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    override fun getAllUniformLocations() {
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
    }

    companion object {
        private const val VERTEX_FILE = "contrast/contrastVertexShader.vert"
        private const val FRAGMENT_FILE = "contrast/contrastFragmentShader.frag"
    }
}
