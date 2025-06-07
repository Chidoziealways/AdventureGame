// GameInit.java
package net.adventuregame.game;

import com.chidozie.core.renderEngine.WindowManager;
import net.adventuregame.items.Items;
import net.adventuregame.save.SaveManager;

/**
 * @version 1
 * @since 0.0.1
 * @author Chidozie Derek Chidozie-Uzowulu
 */
public class GameInit {
    private final GameState state;

    public GameInit(WindowManager window) {
        Items.registerAll();
        if (SaveManager.INSTANCE.getSaveFile().exists()) {
            this.state = SaveManager.INSTANCE.loadGame(window);
        } else {
            this.state = GameState.getInstance(window);
        }
    }

    /** Boot up every system in the correct order. */
    public void initializeAll() {
        state.initSystems();         // core: terrain, player, camera, renderer, picker
        state.loadFontsAndAudio();   // GUI text, audio, advancements
        state.initWorld();           // lights, trees, ferns, barrels, mobs, pickups
        state.initRenderers();       // GUI, water, particles, post-processing
    }

    public GameState getState() {
        return state;
    }
}
