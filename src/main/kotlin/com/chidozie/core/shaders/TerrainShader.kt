package com.chidozie.core.shaders

import net.adventuregame.entities.Camera
import net.adventuregame.entities.Light
import net.adventuregame.toolbox.Maths
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

class TerrainShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var location_transformationMatrix = 0
    private var location_projectionMatrix = 0
    private var location_viewMatrix = 0
    private lateinit var location_lightPosition: IntArray
    private lateinit var location_lightColour: IntArray
    private lateinit var location_attenuation: IntArray
    private var location_shineDamper = 0
    private var location_reflectivity = 0
    private var location_skyColour = 0
    private var location_backgroundTexture = 0
    private var location_rTexture = 0
    private var location_gTexture = 0
    private var location_bTexture = 0
    private var location_blendMap = 0
    private var location_plane = 0
    private var location_toShadowMapSpace = 0
    private var location_shadowMap = 0
    private var location_mapSize = 0

    override fun getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix")
        location_projectionMatrix = super.getUniformLocation("projectionMatrix")
        location_viewMatrix = super.getUniformLocation("viewMatrix")
        location_shineDamper = super.getUniformLocation("shineDamper")
        location_reflectivity = super.getUniformLocation("reflectivity")
        location_skyColour = super.getUniformLocation("skyColour")
        location_backgroundTexture = super.getUniformLocation("backgroundTexture")
        location_rTexture = super.getUniformLocation("rTexture")
        location_gTexture = super.getUniformLocation("gTexture")
        location_bTexture = super.getUniformLocation("bTexture")
        location_blendMap = super.getUniformLocation("blendMap")
        location_plane = super.getUniformLocation("plane")
        location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace")
        location_shadowMap = super.getUniformLocation("shadowMap")
        location_mapSize = super.getUniformLocation("mapSize")

        location_lightPosition = IntArray(MAX_LIGHTS)
        location_lightColour = IntArray(MAX_LIGHTS)
        location_attenuation = IntArray(MAX_LIGHTS)
        for (i in 0..<MAX_LIGHTS) {
            location_lightPosition[i] = super.getUniformLocation("lightPosition[$i]")
            location_lightColour[i] = super.getUniformLocation("lightColour[$i]")
            location_attenuation[i] = super.getUniformLocation("attenuation[$i]")
        }
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoords")
        super.bindAttribute(2, "normal")
    }

    fun connectTextureUnits() {
        super.loadInt(location_backgroundTexture, 0)
        super.loadInt(location_rTexture, 1)
        super.loadInt(location_gTexture, 2)
        super.loadInt(location_bTexture, 3)
        super.loadInt(location_blendMap, 4)
        super.loadInt(location_shadowMap, 5)
    }

    fun loadMapSize(size: Float) {
        loadFloat(location_mapSize, size)
    }

    fun loadToShadowSpaceMatrix(matrix: Matrix4f) {
        loadMatrix(location_toShadowMapSpace, matrix)
    }

    fun loadClipPlane(plane: Vector4f) {
        super.loadVector4f(location_plane, plane)
    }

    fun loadSkyColour(r: Float, g: Float, b: Float) {
        super.loadVector3f(location_skyColour, Vector3f(r, g, b))
    }

    fun loadShineVariables(damper: Float, reflectivity: Float) {
        super.loadFloat(location_shineDamper, damper)
        super.loadFloat(location_reflectivity, reflectivity)
    }

    fun loadTransformationMatrix(matrix4f: Matrix4f) {
        super.loadMatrix(location_transformationMatrix, matrix4f)
    }

    fun loadLights(lights: MutableList<Light>) {
        for (i in 0..<MAX_LIGHTS) {
            if (i < lights.size) {
                super.loadVector3f(location_lightPosition[i], lights[i]!!.position!!)
                super.loadVector3f(location_lightColour[i], lights[i]!!.colour!!)
                super.loadVector3f(location_attenuation[i], lights[i]!!.attenuation!!)
            } else {
                super.loadVector3f(location_lightPosition[i], Vector3f(0f, 0f, 0f))
                super.loadVector3f(location_lightColour[i], Vector3f(0f, 0f, 0f))
                super.loadVector3f(location_attenuation[i], Vector3f(1f, 0f, 0f))
            }
        }
    }

    fun loadViewMatrix(camera: Camera) {
        val viewMatrix = Maths.createViewMatrix(camera)
        super.loadMatrix(location_viewMatrix, viewMatrix)
    }

    fun loadProjectionMatrix(projection: Matrix4f) {
        super.loadMatrix(location_projectionMatrix, projection)
    }

    companion object {
        private const val MAX_LIGHTS = 4

        private const val VERTEX_FILE = "terrains/terrainVertexShader.vert"
        private const val FRAGMENT_FILE = "terrains/terrainFragmentShader.frag"
    }
}
