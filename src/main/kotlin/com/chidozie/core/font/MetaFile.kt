package com.chidozie.core.font

import com.chidozie.core.renderEngine.WindowManager
import net.adventuregame.game.AdventureMain
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

/**
 * Provides functionality for getting the values from a font file.
 *
 * @author Karl
 */
class MetaFile(file: String?) {
    private val aspectRatio: Double


    private var verticalPerPixelSize = 0.0
    private var horizontalPerPixelSize = 0.0
    var spaceWidth: Double = 0.0
        private set
    private lateinit var padding: IntArray
    private var paddingWidth = 0
    private var paddingHeight = 0
    private val window: WindowManager

    private var currentPage = 0
    val pages = mutableMapOf<Int, String>()

    private val metaData: MutableMap<Int?, Character?> = HashMap<Int?, Character?>()

    private var currentLine: String = ""


    private var reader: BufferedReader? = null
    private val values: MutableMap<String?, String> = HashMap<String?, String>()


    /**
     * Opens a font file in preparation for reading.
     *
     * @param file
     * - the font file.
     */
    init {
        window = AdventureMain.window
        this.aspectRatio = window.width.toDouble() / window.height.toDouble()
        openFile(file)
        loadPaddingData()
        loadLineSizes()
        val imageWidth = getValueOfVariable("scaleW")
        loadCharacterData(imageWidth)
        close()
    }


    fun getCharacter(ascii: Int): Character? {
        return metaData[ascii]
    }


    /**
     * Read in the next line and store the variable values.
     *
     * @return `true` if the end of the file hasn't been reached.
     */
    private fun processNextLine(): Boolean {
        values.clear()
        try {
            val line = reader!!.readLine()
            if (line == null) return false
            currentLine = line
            for (part in line.split(SPLITTER).filter { it.isNotBlank() }) {
                val valuePairs = part.split("=")
                if (valuePairs.size == 2) {
                    values[valuePairs[0]] = valuePairs[1]
                }
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            System.err.println("Couldn't read the line!")
            return false
        }
    }

    /**
     * Gets the `int` value of the variable with a certain name on the
     * current line.
     *
     * @param variable
     * - the name of the variable.
     * @return The value of the variable.
     */
    private fun getValueOfVariable(variable: String?): Int {
        val value = values[variable]
            ?: throw IllegalStateException("Missing variable '$variable' in line: $values")

        return value.toIntOrNull()
            ?: throw NumberFormatException("Can't parse int from '$value' for variable '$variable'")
    }


    /**
     * Gets the array of ints associated with a variable on the current line.
     *
     * @param variable
     * - the name of the variable.
     * @return The int array of values associated with the variable.
     */
    private fun getValuesOfVariable(variable: String?): IntArray {
        val numbers: Array<String?> =
            values.get(variable)!!.split(NUMBER_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val actualValues = IntArray(numbers.size)
        for (i in actualValues.indices) {
            actualValues[i] = numbers[i]!!.toInt()
        }
        return actualValues
    }


    /**
     * Closes the font file after finishing reading.
     */
    private fun close() {
        try {
            reader!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * Opens the font file, ready for reading.
     *
     * @param path
     * - the font file.
     */
    private fun openFile(path: String?) {
        try {
            val input =
                javaClass.classLoader.getResourceAsStream("assets/adventuregame/textures/font/$path.fnt")
            val path = "assets/adventuregame/textures/font/YuMincho"
            val dirURL = javaClass.classLoader.getResource(path)
            println("dirURL = $dirURL")
            println("Font Resource: $path")
            if (input == null) {
                throw FileNotFoundException("Font meta file not found in classpath: $path")
            }
            reader = BufferedReader(InputStreamReader(input))
        } catch (e: Exception) {
            e.printStackTrace()
            System.err.println("Couldn't read font meta file: " + path)
        }
    }


    /**
     * Loads the data about how much padding is used around each character in
     * the texture atlas.
     */
    private fun loadPaddingData() {
        processNextLine()
        this.padding = getValuesOfVariable("padding")
        this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT]
        this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM]
    }


    /**
     * Loads information about the line height for this font in pixels, and uses
     * this as a way to find the conversion rate between pixels in the texture
     * atlas and screen-space.
     */
    private fun loadLineSizes() {
        processNextLine()
        val lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight
        verticalPerPixelSize = TextMeshCreator.Companion.LINE_HEIGHT / lineHeightPixels.toDouble()
        horizontalPerPixelSize = verticalPerPixelSize / aspectRatio
    }


    /**
     * Loads in data about each character and stores the data in the
     * [Character] class.
     *
     * @param imageWidth
     * - the width of the texture atlas in pixels.
     */
    private fun loadCharacterData(imageWidth: Int) {
        while (processNextLine()) {
            val line = currentLine // assuming processNextLine sets this
            if (line.startsWith("page") && values.containsKey("file")) {
                println("📜 Parsing page line: $values")
                val id = getValueOfVariable("id")
                val fileName = values["file"]!!
                    .replace("\"", "")
                    .removeSuffix(".png")
                pages[id] = fileName
            }
            if (line.startsWith("char ")) { // ✅ fixed here
                try {
                    val char = loadCharacter(imageWidth)
                    println("📜 Parsing char line: $values")
                    if (char != null) {
                        metaData[char.id] = char
                    } else {
                        println("Missing Char: $char")
                    }
                } catch (e: Exception) {
                    System.err.println("Couldn't load character from line: $values")
                    e.printStackTrace()
                }
            }
        }
    }



    /**
     * Loads all the data about one character in the texture atlas and converts
     * it all from 'pixels' to 'screen-space' before storing. The effects of
     * padding are also removed from the data.
     *
     * @param imageSize
     * - the size of the texture atlas in pixels.
     * @return The data about the character.
     */
    private fun loadCharacter(imageSize: Int): Character? {
        val id = getValueOfVariable("id")
        if (id == TextMeshCreator.Companion.SPACE_ASCII) {
            this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize
            return null
        }
        val xTex: Double = (getValueOfVariable("x").toDouble() + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize
        val yTex: Double = (getValueOfVariable("y").toDouble() + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize
        val width: Int = getValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING))
        val height: Int = getValueOfVariable("height") - ((paddingHeight) - (2 * DESIRED_PADDING))
        val quadWidth = width * horizontalPerPixelSize
        val quadHeight = height * verticalPerPixelSize
        val xTexSize = width.toDouble() / imageSize
        val yTexSize = height.toDouble() / imageSize
        val xOff: Double =
            (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * horizontalPerPixelSize
        val yOff: Double = (getValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING)) * verticalPerPixelSize
        val xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize
        return Character(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance, currentPage)
    }

    companion object {
        private const val PAD_TOP = 0
        private const val PAD_LEFT = 1
        private const val PAD_BOTTOM = 2
        private const val PAD_RIGHT = 3


        private const val DESIRED_PADDING = 8


        private const val SPLITTER = " "
        private const val NUMBER_SEPARATOR = ","
    }
}
