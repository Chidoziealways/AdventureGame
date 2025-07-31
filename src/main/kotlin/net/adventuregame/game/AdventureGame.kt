package net.adventuregame.game

import com.chidozie.core.font.FontType
import com.chidozie.core.font.MetaFile
import com.chidozie.core.renderEngine.Loader
import com.chidozie.core.renderEngine.WindowManager
import kotlin.collections.set

class AdventureGame(private val window: WindowManager) {
    private val init: GameInit
    private val loop: GameLoop
    companion object {
        val fontAtlas = mutableMapOf<Int, Int>()
        val loader = Loader()

        val font = loadFonts()

        fun loadFonts(): FontType {
            for (i in 0..483) {
                fontAtlas[i] = loader.loadFontTextureAtlas("YuMincho/yuMincho_%03d".format(i))
            }

            return FontType(fontAtlas, MetaFile("YuMincho/yuMincho"))
        }
    }

    init {
        this.init = GameInit(window)
        this.loop = GameLoop(init.state!!)
    }

    fun run() {
        // Title screen first
        val titleScreen = TitleScreen(window, loader)
        titleScreen.run()

        if (!titleScreen.shouldStartGame) return  // user exited

        val loadingScreen = LoadingScreen(window, loader, init)
        loadingScreen.run()
        // 2) Enter the perpetual game loop
        loop.run()
    }
}
