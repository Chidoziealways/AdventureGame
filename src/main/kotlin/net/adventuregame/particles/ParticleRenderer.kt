package net.adventuregame.particles

import com.chidozie.core.renderEngine.Loader
import net.adventuregame.entities.Camera
import net.adventuregame.models.RawModel
import net.adventuregame.toolbox.Maths
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ParticleRenderer(private val loader: Loader, projectionMatrix: Matrix4f?) {
    private val quad: RawModel
    private val shader: ParticleShader

    private val vbo: Int
    private var pointer = 0

    init {
        this.vbo = loader.createEmptyVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES)
        quad = loader.loadToVAO(VERTICES, 2)
        loader.addInstancedAttribute(quad.vaoId, vbo, 1, 4, INSTANCE_DATA_LENGTH, 0)
        loader.addInstancedAttribute(quad.vaoId, vbo, 2, 4, INSTANCE_DATA_LENGTH, 4)
        loader.addInstancedAttribute(quad.vaoId, vbo, 3, 4, INSTANCE_DATA_LENGTH, 8)
        loader.addInstancedAttribute(quad.vaoId, vbo, 4, 4, INSTANCE_DATA_LENGTH, 12)
        loader.addInstancedAttribute(quad.vaoId, vbo, 5, 4, INSTANCE_DATA_LENGTH, 16)
        loader.addInstancedAttribute(quad.vaoId, vbo, 6, 1, INSTANCE_DATA_LENGTH, 20)
        shader = ParticleShader()
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix!!)
        shader.stop()
    }

    fun render(particles: MutableMap<ParticleTexture, MutableList<Particle>>, camera: Camera) {
        val viewMatrix = Maths.createViewMatrix(camera)
        prepare()
        for (texture in particles.keys) {
            bindTexture(texture)
            val particleList: MutableList<Particle> = particles[texture]!!
            pointer = 0
            val vboData = FloatArray(particleList.size * INSTANCE_DATA_LENGTH)
            for (particle in particleList) {
                updateModelViewMatrix(
                    particle.position, particle.rotation,
                    particle.scale, viewMatrix, vboData
                )
                updateTexCoordInfo(particle, vboData)
            }
            loader.updateVBO(vbo, vboData, buffer)
            GL31.glDrawArraysInstanced(
                GL11.GL_TRIANGLE_STRIP, 0, quad.vertexCount,
                particleList.size
            )
        }
        finishRendering()
    }

    fun cleanUp() {
        shader.cleanup()
    }

    private fun updateTexCoordInfo(particle: Particle, data: FloatArray) {
        data[pointer++] = particle.texOffset1.x
        data[pointer++] = particle.texOffset1.y
        data[pointer++] = particle.texOffset2.x
        data[pointer++] = particle.texOffset2.y
        data[pointer++] = particle.blend
    }

    private fun bindTexture(texture: ParticleTexture) {
        if (texture.isAdditive) {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
        } else {
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        }
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId)
        shader.loadNumberOfRows(texture.numberOfRows.toFloat())
    }

    private fun updateModelViewMatrix(
        position: Vector3f?, rotation: Float, scale: Float,
        viewMatrix: Matrix4f, vboData: FloatArray
    ) {
        val modelMatrix = Matrix4f()
        modelMatrix.translate(position)
        modelMatrix.m00(viewMatrix.m00())
        modelMatrix.m01(viewMatrix.m10())
        modelMatrix.m02(viewMatrix.m20())
        modelMatrix.m10(viewMatrix.m01())
        modelMatrix.m11(viewMatrix.m11())
        modelMatrix.m12(viewMatrix.m21())
        modelMatrix.m20(viewMatrix.m02())
        modelMatrix.m21(viewMatrix.m12())
        modelMatrix.m22(viewMatrix.m22())
        modelMatrix.rotate(Math.toRadians(rotation.toDouble()).toFloat(), Vector3f(0f, 0f, 1f))
        modelMatrix.scale(Vector3f(scale, scale, scale))
        val modelViewMatrix = Matrix4f().mul(viewMatrix, modelMatrix)
        storeMatrixData(modelViewMatrix, vboData)
    }

    private fun storeMatrixData(matrix: Matrix4f, vboData: FloatArray) {
        vboData[pointer++] = matrix.m00()
        vboData[pointer++] = matrix.m01()
        vboData[pointer++] = matrix.m02()
        vboData[pointer++] = matrix.m03()
        vboData[pointer++] = matrix.m10()
        vboData[pointer++] = matrix.m11()
        vboData[pointer++] = matrix.m12()
        vboData[pointer++] = matrix.m13()
        vboData[pointer++] = matrix.m20()
        vboData[pointer++] = matrix.m21()
        vboData[pointer++] = matrix.m22()
        vboData[pointer++] = matrix.m23()
        vboData[pointer++] = matrix.m30()
        vboData[pointer++] = matrix.m31()
        vboData[pointer++] = matrix.m32()
        vboData[pointer++] = matrix.m33()
    }

    private fun prepare() {
        shader.start()
        GL30.glBindVertexArray(quad.vaoId)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2)
        GL20.glEnableVertexAttribArray(3)
        GL20.glEnableVertexAttribArray(4)
        GL20.glEnableVertexAttribArray(5)
        GL20.glEnableVertexAttribArray(6)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDepthMask(false)
    }

    private fun finishRendering() {
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL20.glDisableVertexAttribArray(3)
        GL20.glDisableVertexAttribArray(4)
        GL20.glDisableVertexAttribArray(5)
        GL20.glDisableVertexAttribArray(6)
        GL30.glBindVertexArray(0)
        shader.stop()
    }

    companion object {
        private val VERTICES = floatArrayOf(-0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f)
        private const val MAX_INSTANCES = 10000
        private const val INSTANCE_DATA_LENGTH = 21
        private val log: Logger? = LoggerFactory.getLogger(ParticleRenderer::class.java)

        private val buffer = BufferUtils
            .createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH)
    }
}
