package net.adventuregame.story

class Dialogue(val speaker: String?, val lines: MutableList<String?>) {
    var currentLine: Int = 0

    fun hasNext(): Boolean {
        return currentLine < lines.size
    }

    fun nextLine(): String? {
        if (hasNext()) {
            return lines[currentLine++]
        }
        return null
    }

    fun reset() {
        currentLine = 0
    }
}

