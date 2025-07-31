package com.chidozie.core.font

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL30

class FontRenderer {
    private val shader = FontShader()

    fun render(texts: MutableMap<FontType, MutableList<GUIText>?>) {
        prepare()
        for ((font, textBatch) in texts) {
            if (textBatch == null) continue

            for ((pageId, textureId) in font.textureAtlas) {
                GL13.glActiveTexture(GL13.GL_TEXTURE0)
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)

                for (text in textBatch) {
                    val pageMesh = text.pageMeshData[pageId] ?: continue
                    renderText(text, pageId, pageMesh)
                }
            }
        }
        endRendering()
    }

    fun cleanUp() {
        shader.cleanup()
    }

    private fun prepare() {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        shader.start()
    }

    private fun renderText(text: GUIText, pageId: Int, mesh: PageMeshData) {
        GL30.glBindVertexArray(text.pageMeshes[pageId] ?: return)
        GL20.glEnableVertexAttribArray(0)
        GL20.glEnableVertexAttribArray(1)
        shader.loadColour(text.colour)
        shader.loadTranslation(text.position!!)
        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, mesh.vertexCount)
        GL20.glDisableVertexAttribArray(0)
        GL20.glDisableVertexAttribArray(1)
        GL30.glBindVertexArray(0)
    }

    private fun endRendering() {
        shader.stop()
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
    }
}
