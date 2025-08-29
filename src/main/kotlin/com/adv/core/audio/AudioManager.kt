package com.adv.core.audio

import org.joml.Vector3f
import org.lwjgl.openal.AL
import org.lwjgl.openal.AL10
import org.lwjgl.openal.ALC
import org.lwjgl.openal.ALC10
import org.lwjgl.stb.STBVorbis
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.IntBuffer

/**
 * Acts as the Manager for handling Audio in this Game
 * @author Chidozie Derek Chidozie-Uzowulu
 */
object AudioManager {
    private var context: Long = 0
    private var device: Long = 0

    private val buffers: MutableList<Int?> = ArrayList<Int?>()

    /**
     * Initialises OpenAL
     */
    fun init() {
        try {
            MemoryStack.stackPush().use { stack ->
                device = ALC10.alcOpenDevice(null as ByteBuffer?)
                check(device != 0L) { "Failed to open the default OpenAL device." }

                val alcCapabilities = ALC.createCapabilities(device)

                context = ALC10.alcCreateContext(device, null as IntBuffer?)
                if (context == 0L) {
                    ALC10.alcCloseDevice(device)
                    throw IllegalStateException("Failed to create OpenAL context.")
                }

                ALC10.alcMakeContextCurrent(context)
                AL.createCapabilities(alcCapabilities)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * -Sets the Default Listener Data
     */
    fun setListenerData(pos: Vector3f) {
        AL10.alListener3f(AL10.AL_POSITION, pos.x, pos.y, pos.z)
        AL10.alListener3f(AL10.AL_VELOCITY, 0f, 0f, 0f)
    }

    /***
     * @param file
     * -The File To Be Played
     * @return
     * -The ID or Name of the Buffer or Sound
     */
    fun loadSound(file: String?): Int {
        val buffer = AL10.alGenBuffers()
        buffers.add(buffer)

        // Load the audio file from the classpath
        try {
            AudioManager::class.java.getResourceAsStream("/assets/adventuregame/sounds/" + file + ".ogg")
                .use { inputStream ->
                    if (inputStream == null) {
                        throw RuntimeException("Failed to load sound file: " + file)
                    }
                    // Allocate a buffer to store the file data
                    val fileData = inputStream.readAllBytes()
                    val rawAudioBuffer = MemoryUtil.memAlloc(fileData.size)
                    rawAudioBuffer.put(fileData).flip()

                    // Decode the audio data using STBVorbis
                    try {
                        MemoryStack.stackPush().use { stack ->
                            val channelsBuffer = stack.mallocInt(1)
                            val sampleRateBuffer = stack.mallocInt(1)

                            val decodedAudioBuffer =
                                STBVorbis.stb_vorbis_decode_memory(rawAudioBuffer, channelsBuffer, sampleRateBuffer)

                            if (decodedAudioBuffer == null) {
                                throw RuntimeException("Failed to decode sound file: " + file)
                            }

                            val channels = channelsBuffer.get(0)
                            val sampleRate = sampleRateBuffer.get(0)

                            var format = -1
                            if (channels == 1) {
                                format = AL10.AL_FORMAT_MONO16
                            } else if (channels == 2) {
                                format = AL10.AL_FORMAT_STEREO16
                            }

                            AL10.alBufferData(buffer, format, decodedAudioBuffer, sampleRate)
                            MemoryUtil.memFree(decodedAudioBuffer)
                        }
                    } finally {
                        MemoryUtil.memFree(rawAudioBuffer)
                    }
                }
        } catch (e: IOException) {
            throw RuntimeException("Failed to read sound file: " + file, e)
        }

        return buffer
    }

    fun cleanUp() {
        for (buffer in buffers) {
            AL10.alDeleteBuffers(buffer!!)
        }
        ALC10.alcMakeContextCurrent(0)
        ALC10.alcDestroyContext(context)
        ALC10.alcCloseDevice(device)
    }
}
