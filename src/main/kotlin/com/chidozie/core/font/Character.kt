package com.chidozie.core.font

/**
 * Simple data structure class holding information about a certain glyph in the
 * font texture atlas. All sizes are for a font-size of 1.
 *
 * @author Karl
 */
class Character(
    val id: Int,
    private val xTextureCoord: Double,
    private val yTextureCoord: Double,
    xTexSize: Double,
    yTexSize: Double,
    private val xOffset: Double,
    private val yOffset: Double,
    val sizeX: Double,
    val sizeY: Double,
    private val xAdvance: Double,
    val page: Int = 0
) {
    val xMaxTextureCoord: Double
    val yMaxTextureCoord: Double

    /**
     * @param id
     * - the ASCII value of the character.
     * @param xTextureCoord
     * - the x texture coordinate for the top left corner of the
     * character in the texture atlas.
     * @param yTextureCoord
     * - the y texture coordinate for the top left corner of the
     * character in the texture atlas.
     * @param xTexSize
     * - the width of the character in the texture atlas.
     * @param yTexSize
     * - the height of the character in the texture atlas.
     * @param xOffset
     * - the x distance from the curser to the left edge of the
     * character's quad.
     * @param yOffset
     * - the y distance from the curser to the top edge of the
     * character's quad.
     * @param sizeX
     * - the width of the character's quad in screen space.
     * @param sizeY
     * - the height of the character's quad in screen space.
     * @param xAdvance
     * - how far in pixels the cursor should advance after adding
     * this character.
     */
    init {
        this.xMaxTextureCoord = xTexSize + xTextureCoord
        this.yMaxTextureCoord = yTexSize + yTextureCoord
    }

    fun getxTextureCoord(): Double {
        return xTextureCoord
    }

    fun getyTextureCoord(): Double {
        return yTextureCoord
    }

    fun getxOffset(): Double {
        return xOffset
    }

    fun getyOffset(): Double {
        return yOffset
    }

    fun getxAdvance(): Double {
        return xAdvance
    }
}
