package net.adventuregame.hud

import com.adv.core.shaders.ShaderProgram
import org.joml.Matrix4f
import org.joml.Vector2f

class HudShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var location_mvpMatrix = 0
    private var location_useFakeLighting = 0
    private var location_numberOfRows = 0
    private var location_offset = 0
    private var location_isSelected = 0

    override fun getAllUniformLocations() {
        location_mvpMatrix = getUniformLocation("mvpMatrix")
        location_useFakeLighting = getUniformLocation("useFakeLighting")
        location_numberOfRows = getUniformLocation("numberOfRows")
        location_offset = getUniformLocation("offset")
        location_isSelected = getUniformLocation("isSelected")
    }

    override fun bindAttributes() {
        bindAttribute(0, "position")
        bindAttribute(1, "textureCoords")
        bindAttribute(2, "normal")
    }

    fun loadMvpMatrix(matrix: Matrix4f) {
        loadMatrix(location_mvpMatrix, matrix)
    }

    fun loadUseFakeLighting(useFake: Boolean) {
        loadBoolean(location_useFakeLighting, useFake)
    }

    fun loadNumberOfRows(rows: Float) {
        loadFloat(location_numberOfRows, rows)
    }

    fun loadOffset(x: Float, y: Float) {
        loadVector2f(location_offset, Vector2f(x, y))
    }

    fun loadIsSelected(isSelected: Boolean) {
        loadBoolean(location_isSelected, isSelected)
    }

    companion object {
        private const val VERTEX_FILE = "hud/hudVertexShader.vert"
        private const val FRAGMENT_FILE = "hud/hudFragmentShader.frag"
    }
}

