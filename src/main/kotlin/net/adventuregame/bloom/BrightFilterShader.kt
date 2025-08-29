package net.adventuregame.bloom

import com.adv.core.shaders.ShaderProgram

class BrightFilterShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    override fun getAllUniformLocations() {
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
    }

    companion object {
        private const val VERTEX_FILE = "bloom/simpleVertexShader.vert"
        private const val FRAGMENT_FILE = "bloom/brightFilterFragmentShader.frag"
    }
}
