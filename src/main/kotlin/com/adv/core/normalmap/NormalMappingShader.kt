package com.adv.core.normalmap

import com.adv.core.shaders.ShaderProgram
import net.adventuregame.entity.Light
import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f

class NormalMappingShader : ShaderProgram(VERTEX_FILE, FRAGMENT_FILE) {
    private var location_transformationMatrix = 0
    private var location_projectionMatrix = 0
    private var location_viewMatrix = 0
    private lateinit var location_lightPositionEyeSpace: IntArray
    private lateinit var location_lightColour: IntArray
    private lateinit var location_attenuation: IntArray
    private var location_shineDamper = 0
    private var location_reflectivity = 0
    private var location_skyColour = 0
    private var location_numberOfRows = 0
    private var location_offset = 0
    private var location_plane = 0
    private var location_modelTexture = 0
    private var location_normalMap = 0
    private var location_specularMap = 0
    private var location_usesSpecularMap = 0

    override fun bindAttributes() {
        super.bindAttribute(0, "position")
        super.bindAttribute(1, "textureCoordinates")
        super.bindAttribute(2, "normal")
        super.bindAttribute(3, "tangent")
    }

    override fun getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix")
        location_specularMap = super.getUniformLocation("specularMap")
        location_usesSpecularMap = super.getUniformLocation("usesSpecularMap")
        location_projectionMatrix = super.getUniformLocation("projectionMatrix")
        location_viewMatrix = super.getUniformLocation("viewMatrix")
        location_shineDamper = super.getUniformLocation("shineDamper")
        location_reflectivity = super.getUniformLocation("reflectivity")
        location_skyColour = super.getUniformLocation("skyColour")
        location_numberOfRows = super.getUniformLocation("numberOfRows")
        location_offset = super.getUniformLocation("offset")
        location_plane = super.getUniformLocation("plane")
        location_modelTexture = super.getUniformLocation("modelTexture")
        location_normalMap = super.getUniformLocation("normalMap")

        location_lightPositionEyeSpace = IntArray(MAX_LIGHTS)
        location_lightColour = IntArray(MAX_LIGHTS)
        location_attenuation = IntArray(MAX_LIGHTS)
        for (i in 0..<MAX_LIGHTS) {
            location_lightPositionEyeSpace[i] = super.getUniformLocation("lightPositionEyeSpace[$i]")
            location_lightColour[i] = super.getUniformLocation("lightColour[$i]")
            location_attenuation[i] = super.getUniformLocation("attenuation[$i]")
        }
    }

    fun connectTextureUnits() {
        super.loadInt(location_modelTexture, 0)
        super.loadInt(location_normalMap, 1)
        loadInt(location_specularMap, 2)
    }

    fun loadUseSpecularMap(useMap: Boolean) {
        loadBoolean(location_usesSpecularMap, useMap)
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

    fun loadShineVariables(damper: Float, reflectivity: Float) {
        super.loadFloat(location_shineDamper, damper)
        super.loadFloat(location_reflectivity, reflectivity)
    }

    fun loadTransformationMatrix(matrix: Matrix4f) {
        super.loadMatrix(location_transformationMatrix, matrix)
    }

    fun loadLights(lights: MutableList<Light>, viewMatrix: Matrix4f) {
        for (i in 0..<MAX_LIGHTS) {
            if (i < lights.size) {
                super.loadVector3f(
                    location_lightPositionEyeSpace[i],
                    getEyeSpacePosition(lights[i], viewMatrix)
                )
                super.loadVector3f(location_lightColour[i], lights[i]!!.colour!!)
                super.loadVector3f(location_attenuation[i], lights[i]!!.attenuation!!)
            } else {
                super.loadVector3f(location_lightPositionEyeSpace[i], Vector3f(0f, 0f, 0f))
                super.loadVector3f(location_lightColour[i], Vector3f(0f, 0f, 0f))
                super.loadVector3f(location_attenuation[i], Vector3f(1f, 0f, 0f))
            }
        }
    }

    fun loadViewMatrix(viewMatrix: Matrix4f) {
        super.loadMatrix(location_viewMatrix, viewMatrix)
    }

    fun loadProjectionMatrix(projection: Matrix4f) {
        super.loadMatrix(location_projectionMatrix, projection)
    }

    private fun getEyeSpacePosition(light: Light, viewMatrix: Matrix4f): Vector3f {
        val position = light.position
        val eyeSpacePos = Vector4f(position, 1.0f)
        viewMatrix.transform(eyeSpacePos)
        return Vector3f(eyeSpacePos.x, eyeSpacePos.y, eyeSpacePos.z)
    }


    companion object {
        private const val MAX_LIGHTS = 4

        private const val VERTEX_FILE = "normalmap/normalMapVertexShader.vert"
        private const val FRAGMENT_FILE = "normalmap/normalMapFragmentShader.frag"
    }
}
