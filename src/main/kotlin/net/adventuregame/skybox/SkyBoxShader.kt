package net.adventuregame.skybox

import com.chidozie.core.renderEngine.WindowManager
import com.chidozie.core.shaders.ShaderProgram
import net.adventuregame.entity.Camera
import net.adventuregame.toolbox.Maths
import org.joml.Matrix4f
import org.joml.Vector3f

class SkyBoxShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var location_projectionMatrix = 0
    private var location_viewMatrix = 0
    private var location_fogColour = 0
    private var location_cubeMap = 0
    private var location_cubeMap2 = 0
    private var location_blendFactor = 0

    private var rotation = 0f

    fun loadProjectionMatrix(matrix: Matrix4f) {
        super.loadMatrix(location_projectionMatrix, matrix)
    }


    fun loadViewMatrix(camera: Camera) {
        val matrix = Maths.createViewMatrix(camera)
        matrix.m30(0f)
        matrix.m31(0f)
        matrix.m32(0f)
        rotation += ROTATE_SPEED * WindowManager.frameTimeSeconds
        matrix.rotate(Math.toRadians(rotation.toDouble()).toFloat(), Vector3f(0f, 1f, 0f))
        super.loadMatrix(location_viewMatrix, matrix)
    }

    fun loadFogColour(r: Float, g: Float, b: Float) {
        super.loadVector3f(location_fogColour, Vector3f(r, g, b))
    }

    fun connectTextureUnits() {
        super.loadInt(location_cubeMap, 0)
        super.loadInt(location_cubeMap2, 1)
    }

    fun loadBlendFactor(blend: Float) {
        super.loadFloat(location_blendFactor, blend)
    }

    override fun getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix")
        location_viewMatrix = super.getUniformLocation("viewMatrix")
        location_fogColour = super.getUniformLocation("fogColour")
        location_cubeMap = super.getUniformLocation("cubeMap")
        location_cubeMap2 = super.getUniformLocation("cubeMap2")
        location_blendFactor = super.getUniformLocation("blendFactor")
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
    }

    companion object {
        private const val VERTEX_FILE = "skybox/skyboxVertexShader.vert"
        private const val FRAGMENT_FILE = "skybox/skyboxFragmentShader.frag"

        private const val ROTATE_SPEED = 1f
    }
}
