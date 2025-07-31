package com.chidozie.core.font

/**
 * Represents a font. It holds the font's texture atlas as well as having the
 * ability to create the quad vertices for any text using this font.
 *
 * @author Karl
 */
class FontType(
    /**
     * @return The font texture atlas.
     */
    val textureAtlas: Map<Int, Int>,
    metaFile: MetaFile
) {
    private val loader: TextMeshCreator


    /**
     * Creates a new font and loads up the data about each character from the
     * font file.
     *
     * @param textureAtlas
     * - the ID of the font atlas texture.
     * @param fontFile
     * - the font file containing information about each character in
     * the texture atlas.
     */
    init {
        this.loader = TextMeshCreator(metaFile)
    }


    /**
     * Takes in an unloaded text and calculate all of the vertices for the quads
     * on which this text will be rendered. The vertex positions and texture
     * coords and calculated based on the information from the font file.
     *
     * @param text
     * - the unloaded text.
     * @return Information about the vertices of all the quads.
     */
    fun loadText(text: GUIText?): TextMeshData? {
        if (text?.textString == null) {
            throw IllegalArgumentException("GUIText.textString is null")
        }
        return loader.createTextMesh(text)
    }
}
