package com.adv.core.font

import net.adventuregame.game.AdventureMain
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*

class FontRenderer {
    private val shader = FontShader()

    fun render(texts: MutableMap<FontType, MutableList<GUIText>>) {
        prepare()

        val projection = Matrix4f().setOrtho2D(
            0f,
            AdventureMain.window.width.toFloat(),
            0f,
            AdventureMain.window.height.toFloat())
        shader.loadProjectionMatrix(projection)

        for ((font, textBatch) in texts) {
            val atlas = font.atlas

            glActiveTexture(GL_TEXTURE0)
            glBindTexture(GL_TEXTURE_2D, atlas.textureId)

            for (text in textBatch) {
                val mesh = text.mesh ?: continue
                renderText(text, mesh)
            }
        }
        endRendering()
    }

    fun cleanUp() {
        shader.cleanup()
    }

    private fun prepare() {
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glDisable(GL_DEPTH_TEST)
        shader.start()
    }

    private fun renderText(text: GUIText, mesh: TextMeshData) {
        glBindVertexArray(mesh.vaoId)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)

        shader.loadColour(text.colour)
        shader.loadTranslation(text.position!!)

        // Calculate vertices to draw based on visible characters
        val charsRatio = text.visibleLength.toFloat() / text.textString.length
        val vertexCountToDraw = (mesh.vertexCount * charsRatio).toInt()

        glDrawArrays(GL_TRIANGLES, 0, vertexCountToDraw)

        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glBindVertexArray(0)
    }

    private fun endRendering() {
        shader.stop()
        glDisable(GL_BLEND)
        glEnable(GL_DEPTH_TEST)
    }
}
