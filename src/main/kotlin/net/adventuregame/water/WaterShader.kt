package net.adventuregame.water

import com.chidozie.core.shaders.ShaderProgram
import net.adventuregame.entity.Camera
import net.adventuregame.entity.Light
import net.adventuregame.toolbox.Maths
import org.joml.Matrix4f

class WaterShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var location_modelMatrix = 0
    private var location_viewMatrix = 0
    private var location_projectionMatrix = 0
    private var location_reflectionTexture = 0
    private var location_refractionTexture = 0
    private var location_dudvMap = 0
    private var location_moveFactor = 0
    private var location_cameraPosition = 0
    private var location_normalMap = 0
    private var location_lightColour = 0
    private var location_lightPosition = 0
    private var location_depthMap = 0


    override fun bindAttributes() {
        bindAttribute(0, "position")
    }


    override fun getAllUniformLocations() {
        location_projectionMatrix = getUniformLocation("projectionMatrix")
        location_viewMatrix = getUniformLocation("viewMatrix")
        location_modelMatrix = getUniformLocation("modelMatrix")
        location_reflectionTexture = getUniformLocation("reflectionTexture")
        location_refractionTexture = getUniformLocation("refractionTexture")
        location_dudvMap = getUniformLocation("dudvMap")
        location_moveFactor = getUniformLocation("moveFactor")
        location_cameraPosition = getUniformLocation("cameraPosition")
        location_normalMap = getUniformLocation("normalMap")
        location_lightColour = getUniformLocation("lightColour")
        location_lightPosition = getUniformLocation("lightPosition")
        location_depthMap = getUniformLocation("depthMap")
    }

    fun connectTextureUnits() {
        loadInt(location_reflectionTexture, 0)
        loadInt(location_refractionTexture, 1)
        loadInt(location_dudvMap, 2)
        loadInt(location_normalMap, 3)
        loadInt(location_depthMap, 4)
    }

    fun loadLight(sun: Light) {
        loadVector3f(location_lightColour, sun.colour!!)
        loadVector3f(location_lightPosition, sun.position!!)
    }

    fun loadMoveFactor(factor: Float) {
        loadFloat(location_moveFactor, factor)
    }


    fun loadProjectionMatrix(projection: Matrix4f) {
        loadMatrix(location_projectionMatrix, projection)
    }

    fun loadViewMatrix(camera: Camera) {
        val viewMatrix = Maths.createViewMatrix(camera)
        loadMatrix(location_viewMatrix, viewMatrix)
        loadVector3f(location_cameraPosition, camera.position)
    }


    fun loadModelMatrix(modelMatrix: Matrix4f) {
        loadMatrix(location_modelMatrix, modelMatrix)
    }

    companion object {
        private const val VERTEX_FILE = "water/waterVertexShader.vert"
        private const val FRAGMENT_FILE = "water/waterFragmentShader.frag"
    }
}
