package com.adv.core.renderEngine

import com.adv.core.textures.TextureData
import net.adventuregame.models.RawModel
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.channels.Channels
import kotlin.math.min

class Loader {
    private val vaos: MutableList<Int?> = ArrayList<Int?>()
    private val vbos: MutableList<Int?> = ArrayList<Int?>()
    private val textures: MutableList<Int?> = ArrayList<Int?>()

    fun createOpenGLTexture(buffer: ByteBuffer, width: Int, height: Int): Int {
        val textureId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, textureId)

        glTexImage2D(
            GL_TEXTURE_2D, 0, GL_RGBA,
            width, height, 0,
            GL_RGBA, GL_UNSIGNED_BYTE, buffer
        )

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        glBindTexture(GL_TEXTURE_2D, 0)
        return textureId
    }

    fun loadToVAO(positions: FloatArray, textureCoords: FloatArray, normals: FloatArray, indices: IntArray): RawModel {
        val vaoId = createVAO()
        bindIndicesBuffer(indices)
        storeDataInAttributeList(0, 3, positions)
        storeDataInAttributeList(1, 2, textureCoords)
        storeDataInAttributeList(2, 3, normals)
        unbindVAO()
        return RawModel(vaoId, indices.size)
    }

    fun loadToVAO(positions: FloatArray, textureCoords: FloatArray): Int {
        val vaoId = createVAO()
        storeDataInAttributeList(0, 2, positions)
        storeDataInAttributeList(1, 2, textureCoords)
        unbindVAO()
        return vaoId
    }


    fun loadToVAO(
        positions: FloatArray, textureCoords: FloatArray, normals: FloatArray, tangents: FloatArray,
        indices: IntArray
    ): RawModel {
        val vaoId = createVAO()
        bindIndicesBuffer(indices)
        storeDataInAttributeList(0, 3, positions)
        storeDataInAttributeList(1, 2, textureCoords)
        storeDataInAttributeList(2, 3, normals)
        storeDataInAttributeList(3, 3, tangents)
        unbindVAO()
        return RawModel(vaoId, indices.size)
    }

    fun createEmptyVBO(floatCount: Int): Int {
        val vbo = GL15.glGenBuffers()
        vbos.add(vbo)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (floatCount * 4).toLong(), GL15.GL_STREAM_DRAW)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        return vbo
    }

    fun addInstancedAttribute(
        vao: Int, vbo: Int, attribute: Int, dataSize: Int,
        instancedDataLength: Int, offset: Int
    ) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        GL30.glBindVertexArray(vao)
        GL20.glVertexAttribPointer(
            attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4,
            (offset * 4).toLong()
        )
        GL33.glVertexAttribDivisor(attribute, 1)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)
    }

    fun updateVBO(vbo: Int, data: FloatArray, buffer: FloatBuffer) {
        buffer.clear()
        buffer.put(data)
        buffer.flip()
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (buffer.capacity() * 4).toLong(), GL15.GL_STREAM_DRAW)
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    fun loadCubeMap(textureFiles: Array<String?>): Int {
        val texId = GL11.glGenTextures()
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texId)

        for (i in textureFiles.indices) {
            val data: TextureData = decodeTextureFile(textureFiles[i])
            GL11.glTexImage2D(
                GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.width, data.height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.buffer
            )
        }
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR)
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR)
        textures.add(texId)
        return texId
    }

    fun loadToVAO(positions: FloatArray, dimensions: Int): RawModel {
        val vaoID = createVAO()
        this.storeDataInAttributeList(0, dimensions, positions)
        unbindVAO()
        return RawModel(vaoID, positions.size / dimensions)
    }

    fun loadGameTexture(fileName: String): Int {
        val imageBuffer: ByteBuffer = try {
            Loader::class.java.classLoader
                .getResourceAsStream("assets/adventuregame/textures/$fileName.png")?.use { stream ->
                    val bytes = stream.readAllBytes()
                    ByteBuffer.allocateDirect(bytes.size).apply {
                        put(bytes)
                        flip()
                    }
                } ?: throw RuntimeException("Resource not found: $fileName")
        } catch (e: Exception) {
            throw RuntimeException("Failed to load resource: $fileName", e)
        }

        val textureId: Int

        MemoryStack.stackPush().use { stack ->
            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val channels = stack.mallocInt(1)

            val buf = STBImage.stbi_load_from_memory(imageBuffer, w, h, channels, 4)
                ?: throw RuntimeException("Image load failed: $fileName — ${STBImage.stbi_failure_reason()}")

            val width = w.get()
            val height = h.get()

            textureId = GL11.glGenTextures()
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)

            // Upload texture data
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                buf
            )

            // Set filtering (good for UI / pixel art)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)

            // Mipmapping
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0f)

            // Optional: Anisotropic filtering
            if (GL.getCapabilities().GL_EXT_texture_filter_anisotropic) {
                val maxAniso = min(
                    4f,
                    GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT)
                )
                GL11.glTexParameterf(
                    GL11.GL_TEXTURE_2D,
                    EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                    maxAniso
                )
            } else {
                println("Anisotropic filtering not supported.")
            }

            // Free image memory
            STBImage.stbi_image_free(buf)
        }

        return textureId
    }


    fun loadParticleTexture(fileName: String?): Int {
        val id: Int

        // Load image as ByteBuffer from classpath
        val imageBuffer: ByteBuffer?
        try {
            Loader::class.java.getClassLoader()
                .getResourceAsStream("assets/adventuregame/textures/particles/" + fileName + ".png").use { stream ->
                    if (stream == null) {
                        throw RuntimeException("Resource not found: " + fileName)
                    }
                    // Read image into ByteBuffer
                    val bytes = stream.readAllBytes()
                    imageBuffer = ByteBuffer.allocateDirect(bytes.size)
                    imageBuffer.put(bytes)
                    imageBuffer.flip()
                    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
                    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -2.4f)
                }
        } catch (e: Exception) {
            throw RuntimeException("Failed to load resource: " + fileName, e)
        }

        MemoryStack.stackPush().use { stack ->
            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val channels = stack.mallocInt(1)

            val buf = STBImage.stbi_load_from_memory(imageBuffer!!, w, h, channels, 4)
            if (buf == null) {
                throw RuntimeException("Image file [" + fileName + "] not loaded: " + STBImage.stbi_failure_reason())
            }

            val width = w.get()
            val height = h.get()

            id = GL11.glGenTextures()
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                buf
            )
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
            STBImage.stbi_image_free(buf)
        }
        return id
    }


    fun loadFontTextureAtlas(fileName: String?): Int {
        val id: Int

        // Load image as ByteBuffer from classpath
        val imageBuffer: ByteBuffer?
        try {
            Loader::class.java.getClassLoader()
                .getResourceAsStream("assets/adventuregame/textures/font/$fileName.png").use { stream ->
                    if (stream == null) {
                        throw RuntimeException("Resource not found: " + fileName)
                    }
                    // Read image into ByteBuffer
                    val bytes = stream.readAllBytes()
                    imageBuffer = ByteBuffer.allocateDirect(bytes.size)
                    imageBuffer.put(bytes)
                    imageBuffer.flip()
                    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR)
                    GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0f)
                }
        } catch (e: Exception) {
            throw RuntimeException("Failed to load resource: " + fileName, e)
        }

        MemoryStack.stackPush().use { stack ->
            val w = stack.mallocInt(1)
            val h = stack.mallocInt(1)
            val channels = stack.mallocInt(1)

            val buf = STBImage.stbi_load_from_memory(imageBuffer!!, w, h, channels, 4)
            if (buf == null) {
                throw RuntimeException("Image file [" + fileName + "] not loaded: " + STBImage.stbi_failure_reason())
            }

            val width = w.get()
            val height = h.get()

            id = GL11.glGenTextures()
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                buf
            )
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
            STBImage.stbi_image_free(buf)
        }
        return id
    }

    fun cleanUp() {
        for (vao in vaos) {
            GL30.glDeleteVertexArrays(vao!!)
        }

        for (vbo in vbos) {
            GL15.glDeleteBuffers(vbo!!)
        }

        for (texture in textures) {
            GL11.glDeleteTextures(texture!!)
        }
    }

    private fun createVAO(): Int {
        val vaoId = GL30.glGenVertexArrays()
        vaos.add(vaoId)
        GL30.glBindVertexArray(vaoId)
        return vaoId
    }

    private fun storeDataInAttributeList(attributeNumber: Int, coordinateSize: Int, data: FloatArray) {
        val vboId = GL15.glGenBuffers()
        vbos.add(vboId)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
        val buffer = storeDataInFloatBuffer(data)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    }

    private fun unbindVAO() {
        GL30.glBindVertexArray(0)
    }

    private fun bindIndicesBuffer(indices: IntArray) {
        val vboId = GL15.glGenBuffers()
        vbos.add(vboId)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId)
        val buffer = storeDataInIntBuffer(indices)
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW)
    }

    private fun storeDataInIntBuffer(data: IntArray): IntBuffer {
        val buffer = BufferUtils.createIntBuffer(data.size)
        buffer.put(data)
        buffer.flip()
        return buffer
    }

    private fun storeDataInFloatBuffer(data: FloatArray): FloatBuffer {
        val buffer = BufferUtils.createFloatBuffer(data.size)
        buffer.put(data)
        buffer.flip()
        return buffer
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(Loader::class.java)
        fun decodeTextureFile(fileName: String?): TextureData {
            val width: Int
            val height: Int
            var buffer: ByteBuffer? = null

            try {
                MemoryStack.stackPush().use { stack ->
                    val widthBuffer = stack.mallocInt(1)
                    val heightBuffer = stack.mallocInt(1)
                    val comp = stack.mallocInt(1)
                    Loader::class.java.getClassLoader()
                        .getResourceAsStream("assets/adventuregame/textures/cubemaps/" + fileName + ".png")
                        .use { source ->
                            Channels.newChannel(source).use { rbc ->
                                var imageBuffer = ByteBuffer.allocateDirect(8 * 1024)
                                while (true) {
                                    val bytes = rbc.read(imageBuffer)
                                    if (bytes == -1) {
                                        break
                                    }
                                    if (imageBuffer.remaining() == 0) {
                                        val newBuffer = ByteBuffer.allocateDirect(imageBuffer.capacity() * 2)
                                        imageBuffer.flip()
                                        newBuffer.put(imageBuffer)
                                        imageBuffer = newBuffer
                                    }
                                }
                                imageBuffer.flip()

                                // Decode the image from the ByteBuffer
                                buffer = STBImage.stbi_load_from_memory(imageBuffer, widthBuffer, heightBuffer, comp, 4)
                                if (buffer == null) {
                                    throw RuntimeException("Failed to load texture file: " + fileName)
                                }

                                width = widthBuffer.get(0)
                                height = heightBuffer.get(0)
                            }
                        }
                }
            } catch (e: Exception) {
                System.err.println("Tried to load Texture " + fileName + ", didn't work")
                throw RuntimeException(e)
            }

            return TextureData(width, height, buffer)
        }
    }
}
