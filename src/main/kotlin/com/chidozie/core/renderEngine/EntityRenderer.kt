package com.chidozie.core.renderEngine

import com.chidozie.core.shaders.StaticShader
import net.adventuregame.entity.Entity
import net.adventuregame.game.AdventureMain
import net.adventuregame.models.TexturedModel
import net.adventuregame.toolbox.Maths
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class EntityRenderer(private val shader: StaticShader, projectionMatrix: Matrix4f?) {
    private val window = AdventureMain.window

    init {
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix!!)
        shader.connectTextureUnits()
        shader.stop()
    }

    fun render(entities: MutableMap<TexturedModel, MutableList<Entity>>, toShadowSpace: Matrix4f?) {
        shader.loadToShadowSpaceMatrix(toShadowSpace!!)
        for (model in entities.keys) {
            prepareTexturedModel(model)
            val batch: MutableList<Entity> = entities[model]!!
            for (entity in batch) {
                prepareInstance(entity)
                GL11.glDrawElements(
                    GL11.GL_TRIANGLES, model.rawModel!!.vertexCount,
                    GL11.GL_UNSIGNED_INT, 0
                )
            }
            unbindTexturedModel()
        }
    }

    private fun prepareTexturedModel(model: TexturedModel) {
        val rawModel = model.rawModel!!
        GL30.glBindVertexArray(rawModel.vaoId)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2)
        val texture = model.texture
        shader.loadNumberOfRows(texture!!.numberOfRows)
        if (texture.isHasTransparency) {
            MasterRenderer.Companion.disableCulling()
        }
        shader.loadFakeLightingVariable(texture.isUseFakeLighting)
        shader.loadShineVariables(texture.shineDamper, texture.reflectivity)
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.texture.textureId)
        shader.loadUseSpecularMap(texture.hasSpecularMap())
        if (texture.hasSpecularMap()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE1)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.specularMap)
        }
    }

    private fun unbindTexturedModel() {
        MasterRenderer.Companion.enableCulling()
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }

    private fun prepareInstance(entity: Entity) {
        val transformationMatrix = Maths.createTransformationMatrix(
            entity.position,
            entity.rotX, entity.rotY, entity.rotZ, entity.scale
        )
        shader.loadTransformationMatrix(transformationMatrix)
        shader.loadOffset(entity.textureXOffset, entity.textureYOffset)
    }
}
