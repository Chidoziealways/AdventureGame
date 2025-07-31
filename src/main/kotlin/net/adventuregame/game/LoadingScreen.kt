package net.adventuregame.game

import com.chidozie.core.font.FontType
import com.chidozie.core.font.GUIText
import com.chidozie.core.font.TextMaster
import com.chidozie.core.renderEngine.Loader
import com.chidozie.core.renderEngine.WindowManager
import org.joml.Vector2f
import org.lwjgl.opengl.GL11

class LoadingScreen(
    private val window: WindowManager,
    private val loader: Loader,
    private val gameInit: GameInit
) {
    private var currentStage = LoadingStage.INIT_TEXT_SYSTEM
    private lateinit var font: FontType
    private lateinit var loadingText: GUIText

    fun run() {
        // Setup
        TextMaster.init(loader)
        font = AdventureGame.font
        loadingText = GUIText("Loading...", 2f, font, Vector2f(0f, 0f), 1f, true).apply {
            setColour(1f, 1f, 1f)
        }

        // Loop
        while (!window.windowShouldClose() && currentStage != LoadingStage.DONE) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

            step() // do one step per frame
            TextMaster.render()
            window.update()
        }

        cleanup()
    }

    private fun step() {
        when (currentStage) {
            LoadingStage.INIT_TEXT_SYSTEM -> {
                currentStage = LoadingStage.INIT_GL
            }
            LoadingStage.INIT_GL -> {
                gameInit.initializeGLStuff()
                currentStage = LoadingStage.INIT_WORLD
            }
            LoadingStage.INIT_WORLD -> {
                gameInit.state!!.initWorld()
                currentStage = LoadingStage.INIT_RENDERERS
            }
            LoadingStage.INIT_RENDERERS -> {
                gameInit.state!!.initRenderers()
                currentStage = LoadingStage.DONE
            }
            else -> {}
        }
    }

    private fun cleanup() {
        TextMaster.removeText(loadingText)
        TextMaster.cleanUp()
    }
}
