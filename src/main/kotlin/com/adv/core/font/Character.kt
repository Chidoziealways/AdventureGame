package com.adv.core.font

/**
 * Holds glyph metrics & texture coordinates from FreeType rasterization.
 */
class Character(
    val id: Int,                    // Unicode codepoint
    val xTextureCoord: Float,       // atlas u0
    val yTextureCoord: Float,       // atlas v0
    val xMaxTextureCoord: Float,    // atlas u1
    val yMaxTextureCoord: Float,    // atlas v1
    val xOffset: Float,             // offset from pen.x to glyph left
    val yOffset: Float,             // offset from baseline to glyph top
    val width: Float,               // glyph quad width (in pixels at given fontSize)
    val height: Float,              // glyph quad height
    val xAdvance: Float             // how far to advance pen.x after this glyph
)