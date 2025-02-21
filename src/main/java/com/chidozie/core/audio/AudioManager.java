package com.chidozie.core.audio;

import org.joml.Vector3f;
import org.lwjgl.openal.*;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Acts as the Manager for handling Audio in this Game
 * @author Chidozie Derek Chidozie-Uzowulu
 */

public class AudioManager {

    private static long context;
    private static long device;

    private static List<Integer> buffers = new ArrayList<Integer>();

    /**
     * Initialises OpenAL
     */
    public static void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            device = ALC10.alcOpenDevice((ByteBuffer) null);
            if (device == 0) {
                throw new IllegalStateException("Failed to open the default OpenAL device.");
            }

            ALCCapabilities alcCapabilities = ALC.createCapabilities(device);

            context = ALC10.alcCreateContext(device, (IntBuffer) null);
            if (context == 0) {
                ALC10.alcCloseDevice(device);
                throw new IllegalStateException("Failed to create OpenAL context.");
            }

            ALC10.alcMakeContextCurrent(context);
            AL.createCapabilities(alcCapabilities);

            // Your OpenAL initialization code
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * -Sets the Default Listener Data
     */
    public static void setListenerData(Vector3f pos) {
        AL10.alListener3f(AL10.AL_POSITION, pos.x, pos.y, pos.z);
        AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
    }

    /***
     * @param file
     *          -The File To Be Played
     * @return
     *          -The ID or Name of the Buffer or Sound
     */
    public static int loadSound(String file) {
        int buffer = AL10.alGenBuffers();
        buffers.add(buffer);

        // Load the audio file from the classpath
        try (InputStream inputStream = AudioManager.class.getResourceAsStream("/assets/adventuregame/sounds/" + file + ".ogg")) {
            if (inputStream == null) {
                throw new RuntimeException("Failed to load sound file: " + file);
            }

            // Allocate a buffer to store the file data
            byte[] fileData = inputStream.readAllBytes();
            ByteBuffer rawAudioBuffer = MemoryUtil.memAlloc(fileData.length);
            rawAudioBuffer.put(fileData).flip();

            // Decode the audio data using STBVorbis
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer channelsBuffer = stack.mallocInt(1);
                IntBuffer sampleRateBuffer = stack.mallocInt(1);

                ShortBuffer decodedAudioBuffer = STBVorbis.stb_vorbis_decode_memory(rawAudioBuffer, channelsBuffer, sampleRateBuffer);

                if (decodedAudioBuffer == null) {
                    throw new RuntimeException("Failed to decode sound file: " + file);
                }

                int channels = channelsBuffer.get(0);
                int sampleRate = sampleRateBuffer.get(0);

                int format = -1;
                if (channels == 1) {
                    format = AL10.AL_FORMAT_MONO16;
                } else if (channels == 2) {
                    format = AL10.AL_FORMAT_STEREO16;
                }

                AL10.alBufferData(buffer, format, decodedAudioBuffer, sampleRate);
                MemoryUtil.memFree(decodedAudioBuffer);
            } finally {
                MemoryUtil.memFree(rawAudioBuffer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read sound file: " + file, e);
        }

        return buffer;
    }

    public static void cleanUp() {
        for (int buffer : buffers) {
            AL10.alDeleteBuffers(buffer);
        }
        ALC10.alcMakeContextCurrent(0);
        ALC10.alcDestroyContext(context);
        ALC10.alcCloseDevice(device);
    }

}
