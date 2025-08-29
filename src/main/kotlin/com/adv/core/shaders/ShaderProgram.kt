package com.adv.core.shaders

import org.joml.Matrix4f
import org.joml.Vector2f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL32
import org.lwjgl.system.MemoryStack
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStreamReader

abstract class ShaderProgram {
    private val programId: Int
    private val vertexShaderId: Int
    private var geometryShaderId = 0
    private val fragmentShaderId: Int

    constructor(vertexFile: String?, fragmentFile: String?) {
        vertexShaderId = loadShader(vertexFile, GL20.GL_VERTEX_SHADER)
        fragmentShaderId = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER)
        programId = GL20.glCreateProgram()
        GL20.glAttachShader(programId, vertexShaderId)
        GL20.glAttachShader(programId, fragmentShaderId)
        bindAttributes()
        GL20.glLinkProgram(programId)
        GL20.glValidateProgram(programId)
        this.getAllUniformLocations()
    }

    constructor(vertexFile: String?, geometryFile: String?, fragmentFile: String?) {
        vertexShaderId = loadShader(vertexFile, GL20.GL_VERTEX_SHADER)
        geometryShaderId = loadShader(geometryFile, GL32.GL_GEOMETRY_SHADER)
        fragmentShaderId = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER)
        programId = GL20.glCreateProgram()
        GL20.glAttachShader(programId, vertexShaderId)
        GL20.glAttachShader(programId, geometryShaderId)
        GL20.glAttachShader(programId, fragmentShaderId)
        bindAttributes()
        GL20.glLinkProgram(programId)
        GL20.glValidateProgram(programId)
        this.getAllUniformLocations()
    }

    protected abstract fun getAllUniformLocations(): Unit

    protected fun getUniformLocation(uniformName: String): Int {
        return GL20.glGetUniformLocation(programId, uniformName)
    }

    fun start() {
        GL20.glUseProgram(programId)
    }

    fun stop() {
        GL20.glUseProgram(0)
    }

    fun cleanup() {
        stop()
        GL20.glDetachShader(programId, vertexShaderId)
        GL20.glDetachShader(programId, geometryShaderId)
        GL20.glDetachShader(programId, fragmentShaderId)
        GL20.glDeleteShader(vertexShaderId)
        GL20.glDeleteShader(geometryShaderId)
        GL20.glDeleteShader(fragmentShaderId)
        GL20.glDeleteProgram(programId)
    }

    protected abstract fun bindAttributes()

    protected fun bindAttribute(attribute: Int, variableName: String) {
        GL20.glBindAttribLocation(programId, attribute, variableName)
    }

    protected fun loadFloat(location: Int, value: Float) {
        GL20.glUniform1f(location, value)
    }

    protected fun loadInt(location: Int, value: Int) {
        GL20.glUniform1i(location, value)
    }

    protected fun loadVector4f(location: Int, vector4f: Vector4f) {
        GL20.glUniform4f(location, vector4f.x, vector4f.y, vector4f.z, vector4f.w)
    }

    protected fun loadVector3f(location: Int, vector3f: Vector3f) {
        GL20.glUniform3f(location, vector3f.x, vector3f.y, vector3f.z)
    }

    protected fun loadVector2f(location: Int, vector2f: Vector2f) {
        GL20.glUniform2f(location, vector2f.x, vector2f.y)
    }

    protected fun loadBoolean(location: Int, value: Boolean) {
        var toLoad = 0f
        if (value) {
            toLoad = 1f
        }
        GL20.glUniform1f(location, toLoad)
    }

    protected fun loadMatrix(location: Int, matrix: Matrix4f) {
        MemoryStack.stackPush().use { stack ->
            val matrixBuffer = stack.mallocFloat(16)
            matrix.get(matrixBuffer)
            GL20.glUniformMatrix4fv(location, false, matrixBuffer)
        }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ShaderProgram::class.java)
        private val matrixBuffer = BufferUtils.createFloatBuffer(16)

        private fun loadShader(file: String?, type: Int): Int {
            val shaderSource = StringBuilder()
            try {
                val inputStream = ShaderProgram::class.java.getClassLoader()
                    .getResourceAsStream("assets/adventuregame/shaders/" + file)
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?
                while ((reader.readLine().also { line = it }) != null) {
                    shaderSource.append(line).append("\n")
                }
                reader.close()
            } catch (e: Exception) {
                log.error("Could not Read File")
                e.printStackTrace()
                System.exit(-1)
            }
            val shaderId = GL20.glCreateShader(type)
            GL20.glShaderSource(shaderId, shaderSource)
            GL20.glCompileShader(shaderId)
            if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
                log.error(GL20.glGetShaderInfoLog(shaderId, 500))
                log.error("Could not Compile Shader." + shaderId)
                System.exit(-1)
            }
            return shaderId
        }
    }
}
