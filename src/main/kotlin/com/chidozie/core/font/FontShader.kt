package com.chidozie.core.font

import com.chidozie.core.shaders.ShaderProgram
import org.joml.Vector2f
import org.joml.Vector3f

class FontShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var location_translation = 0
    private var location_colour = 0

    override fun getAllUniformLocations() {
        location_colour = getUniformLocation("colour")
        location_translation = getUniformLocation("translation")
    }

    override fun bindAttributes() {
        bindAttribute(0, "position")
        bindAttribute(1, "textureCoords")
    }

    fun loadColour(colour: Vector3f) {
        loadVector3f(location_colour, colour)
    }

    fun loadTranslation(translation: Vector2f) {
        loadVector2f(location_translation, translation)
    }

    companion object {
        private const val VERTEX_FILE = "font/fontVertexShader.vert"
        private const val FRAGMENT_FILE = "font/fontFragmentShader.frag"
    }
}
