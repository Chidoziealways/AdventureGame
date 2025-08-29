package com.adv.core.audio

import org.joml.Vector3f
import org.lwjgl.openal.AL10

/**
 * Acts as the Source of any sound in the game
 * @author Chidozie Derek Chidozie-Uzowulu
 */
class Source {
    private val sourceId: Int

    init {
        sourceId = AL10.alGenSources()
        AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, 6f)
        AL10.alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, 6f)
        AL10.alSourcef(sourceId, AL10.AL_MAX_DISTANCE, 50f)
    }

    fun play(buffer: Int) {
        stop()
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer)
        continuePlaying()
    }

    fun delete() {
        stop()
        AL10.alDeleteSources(sourceId)
    }

    fun continuePlaying() {
        AL10.alSourcePlay(sourceId)
    }

    fun stop() {
        AL10.alSourceStop(sourceId)
    }

    fun pause() {
        AL10.alSourcePause(sourceId)
    }

    fun setVelocity(velocity: Vector3f) {
        AL10.alSource3f(sourceId, AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z)
    }

    fun setLooping(loop: Boolean) {
        AL10.alSourcei(sourceId, AL10.AL_LOOPING, if (loop) AL10.AL_TRUE else AL10.AL_FALSE)
    }

    val isPlaying: Boolean
        get() = AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING

    fun setVolume(volume: Float) {
        AL10.alSourcef(sourceId, AL10.AL_GAIN, volume)
    }

    fun setPitch(pitch: Float) {
        AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch)
    }

    fun setPosition(pos: Vector3f) {
        AL10.alSource3f(sourceId, AL10.AL_POSITION, pos.x, pos.y, pos.z)
    }
}
