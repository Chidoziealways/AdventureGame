package net.adventuregame.game;

import com.chidozie.core.renderEngine.Loader;
import com.chidozie.core.renderEngine.WindowManager;
import net.adventuregame.items.Items;

public class AdventureGame {
    private final WindowManager window;
    private final GameInit init;
    private final GameLoop loop;

    public AdventureGame(WindowManager window) {
        this.window = window;

        this.init = new GameInit(window);
        this.loop = new GameLoop(init.getState());
    }

    public void run() {
        // Title screen first
        TitleScreen titleScreen = new TitleScreen(window, new Loader());
        titleScreen.run();

        if (!titleScreen.getShouldStartGame()) {
            return; // user exited
        }

        // 1) Do all the one-time setup
        init.initializeAll();
        // 2) Enter the perpetual game loop
        loop.run();
    }
}
