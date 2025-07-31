package com.chidozie.core.shaders

import net.adventuregame.entity.Camera
import net.adventuregame.entity.Light
import net.adventuregame.toolbox.Maths
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StaticShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var location_transformationMatrix = 0
    private var location_projectionMatrix = 0
    private var location_viewMatrix = 0
    private lateinit var location_lightPosition: IntArray
    private lateinit var location_lightColour: IntArray
    private lateinit var location_attenuation: IntArray
    private var location_shineDamper = 0
    private var location_reflectivity = 0
    private var location_useFakeLighting = 0
    private var location_skyColour = 0
    private var location_numberOfRows = 0
    private var location_offset = 0
    private var location_plane = 0
    private var location_toShadowMapSpace = 0
    private var location_shadowMap = 0
    private var location_mapSize = 0
    private var location_specularMap = 0
    private var location_usesSpecularMap = 0
    private var location_modelTexture = 0
    private var location_isSelected = 0

    override fun getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix")
        location_specularMap = super.getUniformLocation("specularMap")
        location_usesSpecularMap = super.getUniformLocation("usesSpecularMap")
        location_modelTexture = super.getUniformLocation("modelTexture")
        location_projectionMatrix = super.getUniformLocation("projectionMatrix")
        location_viewMatrix = super.getUniformLocation("viewMatrix")
        location_shineDamper = super.getUniformLocation("shineDamper")
        location_reflectivity = super.getUniformLocation("reflectivity")
        location_reflectivity = super.getUniformLocation("reflectivity")
        location_useFakeLighting = super.getUniformLocation("useFakeLighting")
        location_skyColour = super.getUniformLocation("skyColour")
        location_numberOfRows = super.getUniformLocation("numberOfRows")
        location_offset = super.getUniformLocation("offset")
        location_plane = super.getUniformLocation("plane")
        location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace")
        location_shadowMap = super.getUniformLocation("shadowMap")
        location_mapSize = super.getUniformLocation("mapSize")
        location_isSelected = getUniformLocation("isSelected")

        location_lightPosition = IntArray(MAX_LIGHTS)
        location_lightColour = IntArray(MAX_LIGHTS)
        location_attenuation = IntArray(MAX_LIGHTS)
        for (i in 0..<MAX_LIGHTS) {
            location_lightPosition[i] = super.getUniformLocation("lightPosition[$i]")
            location_lightColour[i] = super.getUniformLocation("lightColour[$i]")
            location_attenuation[i] = super.getUniformLocation("attenuation[$i]")
        }
    }

    fun connectTextureUnits() {
        loadInt(location_shadowMap, 5)
        loadInt(location_modelTexture, 0)
        loadInt(location_specularMap, 1)
    }

    fun loadIsSelected(isSelected: Boolean) {
        loadBoolean(location_isSelected, isSelected)
    }

    fun loadUseSpecularMap(useMap: Boolean) {
        loadBoolean(location_usesSpecularMap, useMap)
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

    fun loadNumberOfRows(numberOfRows: Int) {
        super.loadFloat(location_numberOfRows, numberOfRows.toFloat())
    }

    fun loadOffset(x: Float, y: Float) {
        super.loadVector2f(location_offset, Vector2f(x, y))
    }

    fun loadSkyColour(r: Float, g: Float, b: Float) {
        super.loadVector3f(location_skyColour, Vector3f(r, g, b))
    }

    fun loadFakeLightingVariable(useFake: Boolean) {
        super.loadBoolean(location_useFakeLighting, useFake)
    }

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoords")
        super.bindAttribute(2, "normal")
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

        private const val VERTEX_FILE = "entities/vertexShader.vert"
        private const val FRAGMENT_FILE = "entities/fragmentShader.frag"
        private val log: Logger? = LoggerFactory.getLogger(StaticShader::class.java)
    }
}
