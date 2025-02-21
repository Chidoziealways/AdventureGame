package com.chidozie.core.audio;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;

/**
 * Acts as the Source of any sound in the game
 * @author Chidozie Derek Chidozie-Uzowulu
 */
public class Source {

    private int sourceId;

    public Source() {
        sourceId = AL10.alGenSources();
        AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, 6);
        AL10.alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, 6);
        AL10.alSourcef(sourceId, AL10.AL_MAX_DISTANCE, 50);
    }

    public void play(int buffer) {
        stop();
        AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer);
        continuePlaying();
    }

    public void delete() {
        stop();
        AL10.alDeleteSources(sourceId);
    }

    public void continuePlaying() {
        AL10.alSourcePlay(sourceId);
    }

    public void stop() {
        AL10.alSourceStop(sourceId);
    }

    public void pause() {
        AL10.alSourcePause(sourceId);
    }

    public void setVelocity(Vector3f velocity) {
        AL10.alSource3f(sourceId, AL10.AL_VELOCITY, velocity.x, velocity.y, velocity.z);
    }

    public void setLooping(boolean loop) {
        AL10.alSourcei(sourceId, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
    }

    public boolean isPlaying() {
        return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
    }

    public void setVolume(float volume) {
        AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
    }

    public void setPitch(float pitch) {
        AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
    }

    public void setPosition(Vector3f pos) {
        AL10.alSource3f(sourceId, AL10.AL_POSITION, pos.x, pos.y, pos.z);
    }

}
