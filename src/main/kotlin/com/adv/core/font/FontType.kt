package com.adv.core.font

class FontType(
    val atlas: FontAtlas
) {
    fun getCharacter(codepoint: Int): Character? = atlas.characters[codepoint]
}