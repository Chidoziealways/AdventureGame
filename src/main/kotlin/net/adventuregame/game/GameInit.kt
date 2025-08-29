// GameInit.java
package net.adventuregame.game

import com.adv.core.renderEngine.WindowManager
import net.adventuregame.game.GameState.Companion.getInstance
import net.adventuregame.items.Items
import net.adventuregame.save.SaveManager.loadGame
import net.adventuregame.save.SaveManager.saveFile

/**
 * @version 1
 * @since 0.0.1
 * @author Chidozie Derek Chidozie-Uzowulu
 */
class GameInit(window: WindowManager) {
    val state: GameState?

    init {
        Items.registerAll()
        if (saveFile.exists()) {
            this.state = loadGame(window)
        } else {
            this.state = getInstance(window)
        }
    }

    /** Boot up every system in the correct order.  */
    fun initializeGLStuff() {
        state!!.initSystems() // core: terrain, player, camera, renderer, picker
        state.loadFontsAndAudio() // GUI text, audio, advancements
    }
}
