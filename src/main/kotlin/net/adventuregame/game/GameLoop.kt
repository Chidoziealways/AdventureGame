// GameLoop.java
package net.adventuregame.game

import com.chidozie.core.renderEngine.WindowManager

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
            state.render()
            window.update()
        }
        state!!.cleanup()
    }
}
