// GameLoop.java
package net.adventuregame.game

import com.adv.core.renderEngine.WindowManager
import net.adventuregame.processLuaMainThreadQueue

class GameLoop(state: GameState) {
    private val state: GameState?
    private val window: WindowManager

    init {
        this.state = state
        this.window = state.window
    }

    fun run() {
        while (!window.windowShouldClose()) {
            state!!.update()
            processLuaMainThreadQueue()
            state.render()
            window.update()
        }
        state!!.cleanup()
    }
}
