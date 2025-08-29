package com.adv.core.font

/**
 * Represents a word during text layout.
 * Stores the glyphs and total width in screen space.
 */
class Word(private val fontSize: Double) {

    val characters: MutableList<Character> = mutableListOf()

    var wordWidth: Double = 0.0
        private set

    /**
     * Adds a character to the word and updates the width.
     */
    fun addCharacter(character: Character) {
        characters.add(character)
        // advanceX is in pixels for the loaded font size (scaled from FreeType)
        this.wordWidth += character.xAdvance * fontSize
    }
}
