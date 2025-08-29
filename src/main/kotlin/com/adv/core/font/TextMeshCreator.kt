package com.adv.core.font

import com.adv.core.renderEngine.Loader

class TextMeshCreator(private val font: FontType) {

    fun createTextMesh(text: GUIText): TextMeshData {
        val vertices = mutableListOf<Float>()
        val texCoords = mutableListOf<Float>()

        var cursorX = 0f
        var cursorY = 0f

        text.textString?.codePoints()?.forEach { cp ->
            val ch = font.getCharacter(cp) ?: return@forEach

            val x = cursorX + ch.xOffset * text.fontSize
            val y = cursorY - ch.yOffset * text.fontSize
            val maxX = x + ch.width * text.fontSize
            val maxY = y - ch.height * text.fontSize

            val xTex = ch.xTextureCoord
            val yTex = ch.yTextureCoord
            val xTexMax = ch.xMaxTextureCoord
            val yTexMax = ch.yMaxTextureCoord

            addVertices(vertices, x, y, maxX, maxY)
            addTexCoords(texCoords, xTex, yTex, xTexMax, yTexMax)

            cursorX += ch.xAdvance * text.fontSize
        }

        val vaoId = Loader().loadToVAO(vertices.toFloatArray(), texCoords.toFloatArray())
        return TextMeshData(vaoId, vertices.size / 2) // vertex count = num 2D coords
    }

    private fun addVertices(vertices: MutableList<Float>, x: Float, y: Float, maxX: Float, maxY: Float) {
        vertices.add(x); vertices.add(y)
        vertices.add(x); vertices.add(maxY)
        vertices.add(maxX); vertices.add(maxY)

        vertices.add(maxX); vertices.add(maxY)
        vertices.add(maxX); vertices.add(y)
        vertices.add(x); vertices.add(y)
    }

    private fun addTexCoords(texCoords: MutableList<Float>, x: Float, y: Float, maxX: Float, maxY: Float) {
        texCoords.add(x); texCoords.add(y)
        texCoords.add(x); texCoords.add(maxY)
        texCoords.add(maxX); texCoords.add(maxY)

        texCoords.add(maxX); texCoords.add(maxY)
        texCoords.add(maxX); texCoords.add(y)
        texCoords.add(x); texCoords.add(y)
    }
}
