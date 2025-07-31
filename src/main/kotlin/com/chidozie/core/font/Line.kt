package com.chidozie.core.font

/**
 * Represents a line of text during the loading of a text.
 *
 * @author Karl
 */
class Line(
    spaceWidth: Double, fontSize: Double,
    /**
     * @return The max length of the line.
     */
    val maxLength: Double
) {
    private val spaceSize: Double

    /**
     * @return The list of words in the line.
     */
    val words: MutableList<Word?> = ArrayList<Word?>()

    /**
     * @return The current screen-space length of the line.
     */
    var lineLength: Double = 0.0
        private set

    /**
     * Creates an empty line.
     *
     * @param spaceWidth
     * - the screen-space width of a space character.
     * @param fontSize
     * - the size of font being used.
     * @param maxLength
     * - the screen-space maximum length of a line.
     */
    init {
        this.spaceSize = spaceWidth * fontSize
    }

    /**
     * Attempt to add a word to the line. If the line can fit the word in
     * without reaching the maximum line length then the word is added and the
     * line length increased.
     *
     * @param word
     * - the word to try to add.
     * @return `true` if the word has successfully been added to the line.
     */
    fun attemptToAddWord(word: Word): Boolean {
        var additionalLength = word.wordWidth
        additionalLength += if (!words.isEmpty()) spaceSize else 0.0
        if (this.lineLength + additionalLength <= maxLength) {
            words.add(word)
            this.lineLength += additionalLength
            return true
        } else {
            return false
        }
    }
}
