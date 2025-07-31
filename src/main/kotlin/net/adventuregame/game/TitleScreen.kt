package net.adventuregame.game

import com.chidozie.core.audio.AudioManager
import com.chidozie.core.audio.Source
import com.chidozie.core.font.FontType
import com.chidozie.core.font.GUIText
import com.chidozie.core.font.TextMaster
import com.chidozie.core.renderEngine.Loader
import com.chidozie.core.renderEngine.WindowManager
import net.adventuregame.guis.GuiRenderer
import net.adventuregame.guis.GuiTexture
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

class TitleScreen(private val window: WindowManager, loader: Loader) {

    private val guiRenderer = GuiRenderer(loader)
    private var musicSource: Source

    private val title: GUIText
    private val enter: GUIText
    private val bgGui: GuiTexture
    private val startButton: GuiTexture

    var shouldStartGame = false
        private set

    init {
        AudioManager.init()

        musicSource = Source()

        // Background and button textures
        bgGui = GuiTexture(loader.loadGameTexture("title/title_adventure_game"), Vector2f(0f, 0f), Vector2f(1f, 1f))
        startButton = GuiTexture(loader.loadGameTexture("gui/start_button"), Vector2f(0f, -0.3f), Vector2f(0.4f, 0.2f))

        // Load and play music
        AudioManager.loadSound("ao_to_natsu").also { buffer ->
            musicSource.apply {
                setLooping(true)
                setVolume(0.5f)
                setPosition(Vector3f(0f, 0f, 0f))
                play(buffer)
            }
        }

        window.toggleMouseLock()

        // Load fonts and text
        TextMaster.init(loader)
        val font = AdventureGame.font

        title = GUIText("Adventure Game", 3f, font, Vector2f(0f, 0f), 0.5f, true).apply {
            setColour(1f, 0f, 0f)
        }

        enter = GUIText("Press the Enter Key to Start!!", 2f, font, Vector2f(0f, 0.9f), 0.9f, true).apply {
            setColour(0f, 1f, 1f)
        }
    }

    fun run() {
        while (!window.windowShouldClose() && !shouldStartGame) {
            checkInput()
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
            guiRenderer.render()
            TextMaster.render()
            window.update()
        }

        cleanup()
    }

    private fun checkInput() {
        if (window.isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
            shouldStartGame = true
        }
    }

    private fun cleanup() {
        musicSource.stop()
        musicSource.delete()

        TextMaster.removeText(enter)
        TextMaster.removeText(title)
        TextMaster.cleanUp()

        GuiRenderer.removeGui(bgGui)
        GuiRenderer.removeGui(startButton)
        guiRenderer.cleanUp()
    }
}
