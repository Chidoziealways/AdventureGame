package com.chidozie.core.renderEngine

import com.chidozie.core.shaders.TerrainShader
import com.chidozie.core.terrains.Terrain
import net.adventuregame.game.AdventureMain
import net.adventuregame.toolbox.Maths
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class TerrainRenderer(private val shader: TerrainShader, projectionMatrix: Matrix4f?) {
    private val window = AdventureMain.window

    init {
        shader.start()
        shader.loadProjectionMatrix(projectionMatrix!!)
        shader.connectTextureUnits()
        shader.stop()
    }

    fun render(terrains: MutableList<Terrain>, toShadowSpace: Matrix4f?) {
        shader.loadToShadowSpaceMatrix(toShadowSpace!!)
        for (terrain in terrains) {
            prepareTerrain(terrain)
            loadModelMatrix(terrain)
            GL11.glDrawElements(
                GL11.GL_TRIANGLES, terrain.model!!.vertexCount,
                GL11.GL_UNSIGNED_INT, 0
            )
            unbindTexturedModel()
        }
    }

    private fun prepareTerrain(terrain: Terrain) {
        val rawModel = terrain.model
        GL30.glBindVertexArray(rawModel!!.vaoId)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        GL20.glEnableVertexAttribArray(2)
        bindTextures(terrain)
        shader.loadShineVariables(1f, 0f)
    }

    private fun bindTextures(terrain: Terrain) {
        val texturePack = terrain.texturePack!!
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.backgroundTexture!!.textureId)
        GL13.glActiveTexture(GL13.GL_TEXTURE1)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture()!!.textureId)
        GL13.glActiveTexture(GL13.GL_TEXTURE2)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture()!!.textureId)
        GL13.glActiveTexture(GL13.GL_TEXTURE3)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture()!!.textureId)
        GL13.glActiveTexture(GL13.GL_TEXTURE4)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.blendMap!!.textureId)
    }

    private fun unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }

    private fun loadModelMatrix(terrain: Terrain) {
        val transformationMatrix = Maths.createTransformationMatrix(
            Vector3f(terrain.x, 0f, terrain.z),
            0f, 0f, 0f, 1f
        )
        shader.loadTransformationMatrix(transformationMatrix)
    }
}
