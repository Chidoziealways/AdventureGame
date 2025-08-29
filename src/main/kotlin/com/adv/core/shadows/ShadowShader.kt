package com.adv.core.shadows

import com.adv.core.shaders.ShaderProgram
import org.joml.Matrix4f

class ShadowShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var location_mvpMatrix = 0

    override fun getAllUniformLocations() {
        location_mvpMatrix = super.getUniformLocation("mvpMatrix")
    }

    fun loadMvpMatrix(mvpMatrix: Matrix4f) {
        super.loadMatrix(location_mvpMatrix, mvpMatrix)
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "in_position")
        super.bindAttribute(1, "in_textureCoords")
    }

    companion object {
        private const val VERTEX_FILE = "shadows/shadowVertexShader.vert"
        private const val FRAGMENT_FILE = "shadows/shadowFragmentShader.frag"
    }
}
