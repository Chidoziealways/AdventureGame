package com.chidozie.core.font

import com.chidozie.core.renderEngine.Loader

object TextMaster {
    private var loader: Loader? = null
    private val texts: MutableMap<FontType, MutableList<GUIText>?> = HashMap()
    private var renderer: FontRenderer? = null

    fun init(theLoader: Loader) {
        renderer = FontRenderer()
        loader = theLoader
    }

    fun render() {
        renderer!!.render(texts)
    }

    fun loadText(text: GUIText) {
        val font = text.font ?: return
        val data = font.loadText(text)

        // Store the mesh data
        text.pageMeshData = data!!.pageMeshes

        // Load VAOs for each texture page and store them in text.pageMeshes
        for ((pageId, meshData) in data.pageMeshes) {
            val vao = loader!!.loadToVAO(meshData.vertexPositions, meshData.textureCoords)
            text.pageMeshes[pageId] = vao
        }

        // If needed, store total vertex count
        text.vertexCount = data.totalVertexCount!!

        // Add to batch
        val textBatch = texts.getOrPut(font) { ArrayList() }
        textBatch?.add(text)
    }

    fun removeText(text: GUIText) {
        val textBatch = texts[text.font] ?: return
        textBatch.remove(text)
        if (textBatch.isEmpty()) {
            texts.remove(text.font)
        }
    }

    fun cleanUp() {
        renderer?.cleanUp()
    }
}
