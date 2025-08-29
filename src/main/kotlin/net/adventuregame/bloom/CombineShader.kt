package net.adventuregame.bloom

import com.adv.core.shaders.ShaderProgram

class CombineShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var location_colourTexture = 0
    private var location_highlightTexture = 0

    override fun getAllUniformLocations() {
        location_colourTexture = super.getUniformLocation("colourTexture")
        location_highlightTexture = super.getUniformLocation("highlightTexture")
    }

    fun connectTextureUnits() {
        super.loadInt(location_colourTexture, 0)
        super.loadInt(location_highlightTexture, 1)
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
    }

    companion object {
        private const val VERTEX_FILE = "bloom/simpleVertexShader.vert"
        private const val FRAGMENT_FILE = "bloom/combineFragmentShader.frag"
    }
}
