package net.adventuregame.gui

import net.adventuregame.game.GameState
import net.adventuregame.hud.HudShader
import net.adventuregame.models.TexturedModel
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class HotbarRenderer3D(
    private val shader: HudShader
) {

    fun render() {
        val inventory = GameState.getInstance().player.inventory
        val items = inventory.allItems

        val projection = Matrix4f().ortho(-1f, 1f, -1f, 1f, 0.1f, 100f)
        val view = Matrix4f().lookAt(
            Vector3f(0f, 0f, 5f),
            Vector3f(0f, 0f, 0f),
            Vector3f(0f, 1f, 0f)
        )

        shader.start()

        val itemScale = 0.3f
        val itemSpacing = 0.7f
        val totalWidth = (items.size - 1) * itemSpacing
        val startX = -totalWidth / 2f

        for (i in items.indices) {
            val item = items[i] ?: continue
            val model: TexturedModel = item.getGuiModel() ?: continue

            val x = startX + i * itemSpacing
            val y = -0.5f
            val z = 0f

            val modelMatrix = Matrix4f()
                .translate(x, y, z)
                .rotateY(Math.toRadians(30.0).toFloat()) // angle the item
                .scale(itemScale)

            val mvp = Matrix4f(projection).mul(view).mul(modelMatrix)

            renderGuiModel(model, mvp, isSelected = (i == inventory.selectedIndex))
        }

        shader.stop()
    }


    private fun renderGuiModel(model: TexturedModel, mvp: Matrix4f, isSelected: Boolean) {
        val raw = model.rawModel
        val texture = model.texture

        // Bind VAO
        GL30.glBindVertexArray(requireNotNull(raw).vaoId)
        GL20.glEnableVertexAttribArray(0) // position
        GL20.glEnableVertexAttribArray(1) // texCoords
        GL20.glEnableVertexAttribArray(2) // normals

        // Shader uniforms
        shader.loadMvpMatrix(mvp)
        shader.loadUseFakeLighting(texture!!.isUseFakeLighting)
        shader.loadNumberOfRows(texture.numberOfRows.toFloat())
        shader.loadOffset(0f, 0f) // No texture atlas offsets in GUI
        shader.loadIsSelected(isSelected)

        // Bind texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.textureId)

        // Draw
        GL11.glDrawElements(GL11.GL_TRIANGLES, raw.vertexCount, GL11.GL_UNSIGNED_INT, 0)

        // Cleanup
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL20.glDisableVertexAttribArray(2)
        GL30.glBindVertexArray(0)
    }
}
