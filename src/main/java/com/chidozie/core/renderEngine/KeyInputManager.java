package com.chidozie.core.renderEngine;

import java.util.HashMap;
import java.util.Map;

public class KeyInputManager {
    private final Map<Integer, Boolean> keyStates = new HashMap<>();
    private final WindowManager windowManager;
    private final Map<Integer, Long> lastKeyPressTimes = new HashMap<>();
    private final long debounceInterval = 400; // Set debounce interval (in milliseconds)

    public KeyInputManager(WindowManager windowManager) {
        this.windowManager = windowManager;
    }

    public boolean isKeyJustPressed(int key) {
        long currentTime = System.currentTimeMillis();
        boolean isPressed = windowManager.isKeyPressed(key);
        boolean wasPressed = keyStates.getOrDefault(key, false);

        if (isPressed && !wasPressed) {
            long lastPressTime = lastKeyPressTimes.getOrDefault(key, 0L);
            if (currentTime - lastPressTime >= debounceInterval) {
                keyStates.put(key, true);
                lastKeyPressTimes.put(key, currentTime);
                return true;
            }
        } else if (!isPressed) {
            keyStates.put(key, false);
        }

        return false;
    }
}
