package net.adventuregame.particles

import com.adv.core.shaders.ShaderProgram
import org.joml.Matrix4f
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ParticleShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var location_numberOfRows = 0
    private var location_projectionMatrix = 0

    override fun getAllUniformLocations() {
        location_numberOfRows = getUniformLocation("numberOfRows")
        log.info("numberOfRowsLocation: $location_numberOfRows")
        location_projectionMatrix = super.getUniformLocation("projectionMatrix")
        log.info("projectionMatrixLocation: $location_projectionMatrix")
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "modelViewMatrix")
        super.bindAttribute(5, "texOffsets")
        super.bindAttribute(6, "blendFactor")
    }

    fun loadNumberOfRows(numberOfRows: Float) {
        loadFloat(location_numberOfRows, numberOfRows)
    }

    fun loadProjectionMatrix(projectionMatrix: Matrix4f) {
        super.loadMatrix(location_projectionMatrix, projectionMatrix)
    }

    companion object {
        private const val VERTEX_FILE = "particles/particleVertexShader.vert"
        private const val FRAGMENT_FILE = "particles/particleFragmentShader.frag"
        private val log: Logger = LoggerFactory.getLogger(ParticleShader::class.java)
    }
}
