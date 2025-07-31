package com.chidozie.core.normalmap

import com.chidozie.core.renderEngine.MasterRenderer
import net.adventuregame.entities.Camera
import net.adventuregame.entities.Entity
import net.adventuregame.entities.Light
import net.adventuregame.models.TexturedModel
import net.adventuregame.toolbox.Maths
import org.joml.Matrix4f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class NormalMappingRenderer(projectionMatrix: Matrix4f?) {
    private val shader: NormalMappingShader

    init {
        this.shader = NormalMappingShader()
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix!!)
        shader.connectTextureUnits()
        shader.stop()
    }

    fun render(
        entities: MutableMap<TexturedModel, MutableList<Entity>>,
        clipPlane: Vector4f?,
        lights: MutableList<Light>,
        camera: Camera
    ) {
        shader.start()
        prepare(clipPlane, lights, camera)
        for (model in entities.keys) {
            prepareTexturedModel(model)
            val batch: MutableList<Entity> = entities[model]!!
            for (entity in batch) {
                prepareInstance(entity)
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.rawModel!!.vertexCount, GL11.GL_UNSIGNED_INT, 0)
            }
            unbindTexturedModel()
        }
        shader.stop()
    }

    fun cleanUp() {
        shader.cleanup()
    }

    private fun prepareTexturedModel(model: TexturedModel) {
        val rawModel = model.rawModel
        GL30.glBindVertexArray(rawModel!!.vaoId)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2)
        GL20.glEnableVertexAttribArray(3)
        val texture = model.texture
        shader.loadNumberOfRows(texture!!.numberOfRows)
        if (texture.isHasTransparency) {
            MasterRenderer.Companion.disableCulling()
        }
        shader.loadShineVariables(texture.shineDamper, texture.reflectivity)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.texture.textureId)
        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.texture.normalMap)
        shader.loadUseSpecularMap(texture.hasSpecularMap())
        if (texture.hasSpecularMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE2)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.specularMap)
        }
    }

    private fun unbindTexturedModel() {
        MasterRenderer.Companion.enableCulling()
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL20.glDisableVertexAttribArray(3)
        GL30.glBindVertexArray(0)
    }

    private fun prepareInstance(entity: Entity) {
        val transformationMatrix = Maths.createTransformationMatrix(
            entity.position, entity.rotX,
            entity.rotY, entity.rotZ, entity.scale
        )
        shader.loadTransformationMatrix(transformationMatrix)
        shader.loadOffset(entity.textureXOffset, entity.textureYOffset)
    }

    private fun prepare(clipPlane: Vector4f?, lights: MutableList<Light>, camera: Camera) {
        shader.loadClipPlane(clipPlane!!)
        //need to be public variables in MasterRenderer
        shader.loadSkyColour(
            MasterRenderer.Companion.RGB.x,
            MasterRenderer.Companion.RGB.y,
            MasterRenderer.Companion.RGB.z
        )
        val viewMatrix = Maths.createViewMatrix(camera)

        shader.loadLights(lights, viewMatrix)
        shader.loadViewMatrix(viewMatrix)
    }
}
