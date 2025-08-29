package com.adv.core.renderEngine

class KeyInputManager(private val windowManager: WindowManager) {
    private val keyStates: MutableMap<Int?, Boolean?> = HashMap<Int?, Boolean?>()
    private val lastKeyPressTimes: MutableMap<Int?, Long?> = HashMap<Int?, Long?>()
    private val debounceInterval: Long = 400 // Set debounce interval (in milliseconds)

    fun isKeyJustPressed(key: Int): Boolean {
        val currentTime = System.currentTimeMillis()
        val isPressed = windowManager.isKeyPressed(key)
        val wasPressed = keyStates.getOrDefault(key, false)!!

        if (isPressed && !wasPressed) {
            val lastPressTime = lastKeyPressTimes.getOrDefault(key, 0L)!!
            if (currentTime - lastPressTime >= debounceInterval) {
                keyStates.put(key, true)
                lastKeyPressTimes.put(key, currentTime)
                return true
            }
        } else if (!isPressed) {
            keyStates.put(key, false)
        }

        return false
    }
}
