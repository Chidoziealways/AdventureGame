package com.chidozie.core.shadows

import com.chidozie.core.renderEngine.MasterRenderer
import net.adventuregame.entity.Entity
import net.adventuregame.models.RawModel
import net.adventuregame.models.TexturedModel
import net.adventuregame.toolbox.Maths
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class ShadowMapEntityRenderer
/**
 * @param shader
 * - the simple shader program being used for the shadow render
 * pass.
 * @param projectionViewMatrix
 * - the orthographic projection matrix multiplied by the light's
 * "view" matrix.
 */(private val shader: ShadowShader, private val projectionViewMatrix: Matrix4f?) {
    /**
     * Renders entieis to the shadow map. Each model is first bound and then all
     * of the entities using that model are rendered to the shadow map.
     *
     * @param entities
     * - the entities to be rendered to the shadow map.
     */
    fun render(entities: MutableMap<TexturedModel, MutableList<Entity>>) {
        for (model in entities.keys) {
            val rawModel = model.rawModel!!
            bindModel(rawModel)
            GL13.glActiveTexture(GL13.GL_TEXTURE0)
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.texture!!.textureId)
            if (model.texture.isHasTransparency) {
                MasterRenderer.Companion.disableCulling()
            }
            for (entity in entities.get(model)!!) {
                prepareInstance(entity)
                GL11.glDrawElements(
                    GL11.GL_TRIANGLES, rawModel.vertexCount,
                    GL11.GL_UNSIGNED_INT, 0
                )
            }
            if (model.texture.isHasTransparency) {
                MasterRenderer.Companion.enableCulling()
            }
        }
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL30.glBindVertexArray(0)
    }

    /**
     * Binds a raw model before rendering. Only the attribute 0 is enabled here
     * because that is where the positions are stored in the VAO, and only the
     * positions are required in the vertex shader.
     *
     * @param rawModel
     * - the model to be bound.
     */
    private fun bindModel(rawModel: RawModel) {
        GL30.glBindVertexArray(rawModel.vaoId)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
    }

    /**
     * Prepares an entity to be rendered. The model matrix is created in the
     * usual way and then multiplied with the projection and view matrix (often
     * in the past we've done this in the vertex shader) to create the
     * mvp-matrix. This is then loaded to the vertex shader as a uniform.
     *
     * @param entity
     * - the entity to be prepared for rendering.
     */
    private fun prepareInstance(entity: Entity) {
        val modelMatrix = Maths.createTransformationMatrix(
            entity.position,
            entity.rotX,
            entity.rotY,
            entity.rotZ,
            entity.scale
        )

        val mvpMatrix = Matrix4f(projectionViewMatrix).mul(modelMatrix)
        shader.loadMvpMatrix(mvpMatrix)
    }
}
