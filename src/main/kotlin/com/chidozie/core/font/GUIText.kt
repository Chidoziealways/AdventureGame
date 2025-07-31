package com.chidozie.core.font

import org.joml.Vector2f
import org.joml.Vector3f

/**
 * Represents a piece of text in the game.
 *
 * @author Karl
 */
class GUIText(
    /**
     * @return The string of text.
     */
    val textString: String?,
    /**
     * @return the font size of the text (a font size of 1 is normal).
     */
    val fontSize: Float,
    /**
     * @return The font used by this text.
     */
    val font: FontType?,
    /**
     * @return The position of the top-left corner of the text in screen-space.
     * (0, 0) is the top left corner of the screen, (1, 1) is the bottom
     * right.
     */
    val position: Vector2f?,
    /**
     * @return The maximum length of a line of this text.
     */
    val maxLineSize: Float,
    centered: Boolean
) {
    /**
     * @return the ID of the text's VAO, which contains all the vertex data for
     * the quads on which the text will be rendered.
     */
    var mesh: Int = 0
        private set

    val pageMeshes: MutableMap<Int, Int> = mutableMapOf()

    /**
     * Store the raw mesh data per page (used for draw call vertex count)
     */
    var pageMeshData: Map<Int, PageMeshData> = emptyMap()

    /**
     * @return The total number of vertices of all the text's quads.
     */
    var vertexCount: Int = 0

    /**
     * @return the colour of the text.
     */
    val colour: Vector3f = Vector3f(0f, 0f, 0f)

    /**
     * @return The number of lines of text. This is determined when the text is
     * loaded, based on the length of the text and the max line length
     * that is set.
     */
    /**
     * Sets the number of lines that this text covers (method used only in
     * loading).
     *
     * @param number
     */
    var numberOfLines: Int = 0

    /**
     * @return `true` if the text should be centered.
     */
    var isCentered: Boolean = false
        private set

    /**
     * Creates a new text, loads the text's quads into a VAO, and adds the text
     * to the screen.
     *
     * @param textString
     * - the text.
     * @param fontSize
     * - the font size of the text, where a font size of 1 is the
     * default size.
     * @param font
     * - the font that this text should use.
     * @param position
     * - the position on the screen where the top left corner of the
     * text should be rendered. The top left corner of the screen is
     * (0, 0) and the bottom right is (1, 1).
     * @param maxLineSize
     * - basically the width of the virtual page in terms of screen
     * width (1 is full screen width, 0.5 is half the width of the
     * screen, etc.) Text cannot go off the edge of the page, so if
     * the text is longer than this length it will go onto the next
     * line. When text is centered it is centered into the middle of
     * the line, based on this line length value.
     * @param centered
     * - whether the text should be centered or not.
     */
    init {
        this.isCentered = centered
        TextMaster.loadText(this)
    }

    /**
     * Remove the text from the screen.
     */
    fun remove() {
        TextMaster.removeText(this)
    }

    /**
     * Set the colour of the text.
     *
     * @param r
     * - red value, between 0 and 1.
     * @param g
     * - green value, between 0 and 1.
     * @param b
     * - blue value, between 0 and 1.
     */
    fun setColour(r: Float, g: Float, b: Float) {
        colour.set(r, g, b)
    }

    /**
     * Set the VAO and vertex count for this text.
     *
     * @param vao
     * - the VAO containing all the vertex data for the quads on
     * which the text will be rendered.
     * @param verticesCount
     * - the total number of vertices in all of the quads.
     */
    fun setMeshInfo(vao: Int, verticesCount: Int) {
        this.mesh = vao
        this.vertexCount = verticesCount
    }
}
