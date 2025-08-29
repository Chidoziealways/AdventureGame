package com.adv.core.font

import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.GL_R8
import org.lwjgl.stb.STBTTPackContext
import org.lwjgl.stb.STBTTPackedchar
import org.lwjgl.stb.STBTruetype.*
import org.lwjgl.system.MemoryUtil
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.ByteBuffer

class FontAtlas(
    fontResourcePath: String,
    private val fontSize: Float,
    private val ranges: List<IntRange>,
    private val atlasSize: Int = 4096
) {
    val textureId: Int
    val characters: Map<Int, Character>

    init {
        if (GL.getCapabilities() == null)
            throw IllegalStateException("OpenGL not initialized. Call GL.createCapabilities() first!")

        // Load font bytes
        val fontData = loadResourceToByteBuffer("/assets/adventuregame/fonts/$fontResourcePath.otf")
        val numGlyphs = ranges.sumOf { it.count() }
        println("FontAtlas: packing $numGlyphs glyphs from '$fontResourcePath' into ${atlasSize}x$atlasSize atlas at size $fontSize")

        // Allocate atlas bitmap
        val bitmap = MemoryUtil.memAlloc(atlasSize * atlasSize)
        val pc = STBTTPackContext.malloc()
        if (!stbtt_PackBegin(pc, bitmap, atlasSize, atlasSize, 0, 1, MemoryUtil.NULL)) {
            pc.free(); MemoryUtil.memFree(bitmap)
            throw IllegalStateException("stbtt_PackBegin failed")
        }

        val charData = STBTTPackedchar.malloc(numGlyphs)
        var dataIndex = 0

        // Pack each contiguous range separately
        for (range in ranges) {
            val count = range.count()
            if (count == 0) continue

            charData.position(dataIndex)
            charData.limit(dataIndex + count)

            val ok = stbtt_PackFontRange(pc, fontData, 0, fontSize, range.first, charData)
            if (!ok) {
                stbtt_PackEnd(pc)
                pc.free()
                charData.free()
                MemoryUtil.memFree(bitmap)
                throw IllegalStateException("stbtt_PackFontRange failed for range $range")
            }

            dataIndex += count
        }

        stbtt_PackEnd(pc)
        pc.free()
        charData.position(0) // reset for reading

        // Upload to GPU
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
        textureId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, textureId)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_R8, atlasSize, atlasSize, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glBindTexture(GL_TEXTURE_2D, 0)
        MemoryUtil.memFree(bitmap)

        // Build character map
        val map = mutableMapOf<Int, Character>()
        var idx = 0
        for (range in ranges) {
            for (codepoint in range) {
                val pcg = charData.get(idx++)
                map[codepoint] = Character(
                    id = codepoint,
                    xTextureCoord = pcg.x0() / atlasSize.toFloat(),
                    yTextureCoord = pcg.y0() / atlasSize.toFloat(),
                    xMaxTextureCoord = pcg.x1() / atlasSize.toFloat(),
                    yMaxTextureCoord = pcg.y1() / atlasSize.toFloat(),
                    xOffset = pcg.xoff(),
                    yOffset = pcg.yoff(),
                    width = (pcg.x1() - pcg.x0()).toFloat(),
                    height = (pcg.y1() - pcg.y0()).toFloat(),
                    xAdvance = pcg.xadvance()
                )
            }
        }

        charData.free()
        characters = map.toMap()
        println("FontAtlas: packed ${characters.size} glyphs into texture $textureId")
    }

    private fun loadResourceToByteBuffer(path: String): ByteBuffer {
        val stream: InputStream = javaClass.getResourceAsStream(path)
            ?: throw IllegalArgumentException("Font resource not found: $path")
        val baos = ByteArrayOutputStream()
        stream.use { inp ->
            val buf = ByteArray(16 * 1024)
            var r: Int
            while (inp.read(buf).also { r = it } != -1) baos.write(buf, 0, r)
        }
        val bytes = baos.toByteArray()
        val bb = MemoryUtil.memAlloc(bytes.size)
        bb.put(bytes).flip()
        return bb
    }
}
