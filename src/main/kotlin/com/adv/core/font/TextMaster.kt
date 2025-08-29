package com.adv.core.font

import com.adv.core.renderEngine.Loader

object TextMaster {
    private var loader: Loader? = null
    private val texts: MutableMap<FontType, MutableList<GUIText>> = HashMap()
    private var renderer: FontRenderer? = null
    val fontRenderer: FontRenderer?
        get() = renderer

    fun init(theLoader: Loader) {
        renderer = FontRenderer()
        loader = theLoader
    }

    fun render() {
        renderer?.render(texts)
    }

    fun loadText(text: GUIText) {
        val font = text.font ?: return
        val creator = TextMeshCreator(font)
        val data = creator.createTextMesh(text)

        println("Added Text: ${text.textString}")

        text.mesh = data
        text.vertexCount = data.vertexCount

        val textBatch = texts.getOrPut(font) { ArrayList() }
        textBatch.add(text)
    }

    fun removeText(text: GUIText) {
        texts[text.font]?.remove(text)?.also {
            if (texts[text.font]?.isEmpty() == true) texts.remove(text.font)
        }
    }

    fun cleanUp() {
        renderer?.cleanUp()
    }
}
