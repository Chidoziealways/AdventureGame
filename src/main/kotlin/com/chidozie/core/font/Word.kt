package com.chidozie.core.font

/**
 * During the loading of a text this represents one word in the text.
 * @author Karl
 */
class Word
/**
 * Create a new empty word.
 * @param fontSize - the font size of the text which this word is in.
 */(private val fontSize: Double) {
    /**
     * @return The list of characters in the word.
     */
    val characters: MutableList<Character?> = ArrayList<Character?>()

    /**
     * @return The width of the word in terms of screen size.
     */
    var wordWidth: Double = 0.0
        private set

    /**
     * Adds a character to the end of the current word and increases the screen-space width of the word.
     * @param character - the character to be added.
     */
    fun addCharacter(character: Character) {
        characters.add(character)
        this.wordWidth += character.getxAdvance() * fontSize
    }
}
