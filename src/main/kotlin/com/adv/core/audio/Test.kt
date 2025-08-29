package com.adv.core.audio

import org.joml.Vector3f
import org.lwjgl.openal.AL10
import org.lwjgl.openal.AL11
import java.io.IOException

/**
 * Just a test to check if the game works
 * @author Chidozie Derek Chidozie-Uzowulu
 */
object Test {
    @Throws(IOException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        AudioManager.init()
        AudioManager.setListenerData(Vector3f(0f, 0f, 0f))
        AL10.alDistanceModel(AL11.AL_INVERSE_DISTANCE_CLAMPED)

        val buffer = AudioManager.loadSound("bounce")
        val source = Source()
        source.setLooping(true)
        source.play(buffer)
        source.setVolume(1000f)

        var xPos = 0f
        source.setPosition(Vector3f(xPos, 0f, 0f))

        val c = ' '
        while (c != 'q') {
            xPos -= 0.03f
            source.setPosition(Vector3f(xPos, 0f, 0f))
            println(xPos)
            Thread.sleep(10)
        }

        source.delete()
        AudioManager.cleanUp()
    }
}
