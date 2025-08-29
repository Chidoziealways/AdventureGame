package com.adv.core.font

class FontManager {
    private val fonts = mutableMapOf<String, FontType>()

    fun loadFont(
        name: String,
        path: String,
        size: Float,
        ranges: List<IntRange> = listOf(0x0020..0x00FF) // default: Latin
    ): FontType {
        val atlas = FontAtlas(path, fontSize = size, ranges = ranges)
        val font = FontType(atlas)
        fonts[name] = font
        return font
    }

    fun getFont(name: String): FontType {
        return fonts[name] ?: error("Font '$name' not loaded in FontManager")
    }

    fun unloadFont(name: String) {
        fonts.remove(name)
    }

    fun clear() {
        fonts.clear()
    }
}
