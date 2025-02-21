package com.chidozie.core.audio;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import java.io.IOException;

/**
 * Just a test to check if the game works
 * @author Chidozie Derek Chidozie-Uzowulu
 */
public class Test {

    public static void main(String[] args) throws IOException, InterruptedException {

        AudioManager.init();
        AudioManager.setListenerData(new Vector3f(0, 0, 0));
        AL10.alDistanceModel(AL11.AL_INVERSE_DISTANCE_CLAMPED);

        int buffer = AudioManager.loadSound("bounce");
        Source source = new Source();
        source.setLooping(true);
        source.play(buffer);
        source.setVolume(1000);

        float xPos = 0;
        source.setPosition(new Vector3f(xPos, 0, 0));

        char c = ' ';
        while (c != 'q') {

            xPos -= 0.03f;
            source.setPosition(new Vector3f(xPos, 0, 0));
            System.out.println(xPos);
            Thread.sleep(10);

        }

        source.delete();
        AudioManager.cleanUp();

    }

}
