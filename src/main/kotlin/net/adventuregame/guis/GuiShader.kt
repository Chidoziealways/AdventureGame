package net.adventuregame.guis

import com.chidozie.core.shaders.ShaderProgram
import org.joml.Matrix4f

class GuiShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var location_transformationMatrix = 0


    fun loadTransformation(matrix: Matrix4f) {
        super.loadMatrix(location_transformationMatrix, matrix)
    }


    override fun getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix")
    }


    override fun bindAttributes() {
        super.bindAttribute(0, "position")
    }

    companion object {
        private const val VERTEX_FILE = "guis/guiVertexShader.vert"
        private const val FRAGMENT_FILE = "guis/guiFragmentShader.frag"
    }
}
