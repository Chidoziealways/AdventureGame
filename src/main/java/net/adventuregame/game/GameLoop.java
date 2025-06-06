// GameLoop.java
package net.adventuregame.game;

import com.chidozie.core.renderEngine.WindowManager;
import net.adventuregame.story.StoryHandler;

public class GameLoop {
    private final GameState state;
    private final WindowManager window;

    public GameLoop(GameState state) {
        this.state = state;
        this.window = state.window;
    }

    public void run() {
        while (!window.windowShouldClose()) {
            state.update();
            state.render();
            window.update();
        }
        state.cleanup();
    }
}
