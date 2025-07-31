package com.chidozie.core.font

class TextMeshCreator(metaFile: MetaFile) {
    private val metaData: MetaFile


    init {
        metaData = metaFile
    }


    fun createTextMesh(text: GUIText): TextMeshData {
        val lines = createStructure(text)
        val data = createQuadVertices(text, lines)
        return data
    }


    private fun createStructure(text: GUIText): MutableList<Line> {
        val chars = text.textString?.toCharArray() ?: throw IllegalArgumentException("Text string is null.")
        val lines: MutableList<Line> = ArrayList<Line>()
        var currentLine =
            Line(metaData.spaceWidth, text.fontSize.toDouble(), text.maxLineSize.toDouble())
        var currentWord = Word(text.fontSize.toDouble())
        for (c in chars) {
            val ascii = c.code
            if (ascii == SPACE_ASCII) {
                val added = currentLine.attemptToAddWord(currentWord)
                if (!added) {
                    lines.add(currentLine)
                    currentLine =
                        Line(metaData.spaceWidth, text.fontSize.toDouble(), text.maxLineSize.toDouble())
                    currentLine.attemptToAddWord(currentWord)
                }
                currentWord = Word(text.fontSize.toDouble())
                continue
            }
            val character = metaData.getCharacter(ascii)
            if (character == null) {
                println("🚨 Character not found in metaData: '${c}' (ASCII $ascii)")
            } else {
                currentWord.addCharacter(character)
            }
        }
        completeStructure(lines, currentLine, currentWord, text)
        return lines
    }


    private fun completeStructure(lines: MutableList<Line>, currentLine: Line, currentWord: Word, text: GUIText) {
        var currentLine = currentLine
        val added = currentLine.attemptToAddWord(currentWord)
        if (!added) {
            lines.add(currentLine)
            currentLine =
                Line(metaData.spaceWidth, text.fontSize.toDouble(), text.maxLineSize.toDouble())
            currentLine.attemptToAddWord(currentWord)
        }
        lines.add(currentLine)
    }


    private fun createQuadVertices(text: GUIText, lines: MutableList<Line>): TextMeshData {
        text.numberOfLines = lines.size
        var curserX = 0.0
        var curserY = 0.0

        val verticesPerPage = mutableMapOf<Int, MutableList<Float>>()
        val textureCoordsPerPage = mutableMapOf<Int, MutableList<Float>>()

        for (line in lines) {
            if (text.isCentered) {
                curserX = (line.maxLength - line.lineLength) / 2
            }

            for (word in line.words) {
                for (letter in word!!.characters) {
                    val page = letter!!.page

                    val verts = verticesPerPage.getOrPut(page) { mutableListOf() }
                    val texs = textureCoordsPerPage.getOrPut(page) { mutableListOf() }

                    addVerticesForCharacter(curserX, curserY, letter, text.fontSize.toDouble(), verts)
                    addTexCoords(texs, letter.getxTextureCoord(), letter.getyTextureCoord(), letter.xMaxTextureCoord, letter.yMaxTextureCoord)

                    curserX += letter.getxAdvance() * text.fontSize
                }
                curserX += metaData.spaceWidth * text.fontSize
            }

            curserX = 0.0
            curserY += LINE_HEIGHT * text.fontSize
        }

        val resultMap = mutableMapOf<Int, PageMeshData>()
        for ((pageId, verts) in verticesPerPage) {
            val texs = textureCoordsPerPage[pageId] ?: continue
            resultMap[pageId] = PageMeshData(
                vertexPositions = listToArray(verts),
                textureCoords = listToArray(texs)
            )
        }

        return TextMeshData(resultMap)
    }


    private fun addVerticesForCharacter(
        curserX: Double, curserY: Double, character: Character, fontSize: Double,
        vertices: MutableList<Float>
    ) {
        val x = curserX + (character.getxOffset() * fontSize)
        val y = curserY + (character.getyOffset() * fontSize)
        val maxX = x + (character.sizeX * fontSize)
        val maxY = y + (character.sizeY * fontSize)
        val properX = (2 * x) - 1
        val properY = (-2 * y) + 1
        val properMaxX = (2 * maxX) - 1
        val properMaxY = (-2 * maxY) + 1
        addVertices(vertices, properX, properY, properMaxX, properMaxY)
    }


    companion object {
        const val LINE_HEIGHT: Double = 0.03
        const val SPACE_ASCII: Int = 32


        private fun addVertices(vertices: MutableList<Float>, x: Double, y: Double, maxX: Double, maxY: Double) {
            vertices.add(x.toFloat())
            vertices.add(y.toFloat())
            vertices.add(x.toFloat())
            vertices.add(maxY.toFloat())
            vertices.add(maxX.toFloat())
            vertices.add(maxY.toFloat())
            vertices.add(maxX.toFloat())
            vertices.add(maxY.toFloat())
            vertices.add(maxX.toFloat())
            vertices.add(y.toFloat())
            vertices.add(x.toFloat())
            vertices.add(y.toFloat())
        }


        private fun addTexCoords(texCoords: MutableList<Float>, x: Double, y: Double, maxX: Double, maxY: Double) {
            texCoords.add(x.toFloat())
            texCoords.add(y.toFloat())
            texCoords.add(x.toFloat())
            texCoords.add(maxY.toFloat())
            texCoords.add(maxX.toFloat())
            texCoords.add(maxY.toFloat())
            texCoords.add(maxX.toFloat())
            texCoords.add(maxY.toFloat())
            texCoords.add(maxX.toFloat())
            texCoords.add(y.toFloat())
            texCoords.add(x.toFloat())
            texCoords.add(y.toFloat())
        }


        private fun listToArray(listOfFloats: MutableList<Float>): FloatArray {
            val array = FloatArray(listOfFloats.size)
            for (i in array.indices) {
                array[i] = listOfFloats[i]
            }
            return array
        }
    }
}
