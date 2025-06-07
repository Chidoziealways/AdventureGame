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
import java.io.File

class TitleScreen(private val window: WindowManager, loader: Loader) {
    private val guiRenderer = GuiRenderer(loader)
    private val guiTextures = mutableListOf<GuiTexture>()
    private var musicSource: Source? = null

    var title: GUIText? = null
    var enter: GUIText? = null

    var shouldStartGame = false
        private set

    init {
        AudioManager.init()

        musicSource = Source()

        val backgroundTex = loader.loadGameTexture("gui/title_bg")
        val startButtonTex = loader.loadGameTexture("gui/start_button")
        val bgGui = GuiTexture(backgroundTex, Vector2f(0f, 0f), Vector2f(1f, 1f))
        val startButton = GuiTexture(startButtonTex, Vector2f(0f, -0.3f), Vector2f(0.4f, 0.2f))
        guiTextures.add(bgGui)
        guiTextures.add(startButton)

        val buffer = AudioManager.loadSound("ao_to_natsu")
        musicSource!!.setLooping(true)
        musicSource!!.setVolume(0.5f)
        musicSource!!.setPosition(Vector3f(0f, 0f, 0f))
        musicSource!!.play(buffer)

        window.toggleMouseLock()

        TextMaster.init(loader)
        val font = FontType(
            loader.loadFontTextureAtlas("calibri"),
            "calibri"
        )

        title = GUIText("Adventure Game", 3f, font, Vector2f (0f, 0f), 0.5f, true)
        title!!.setColour(1f,0f,0f);

        enter = GUIText("Press the Enter Key to Start!!", 2f, font, Vector2f(0f, 0.9f), 0.9f, true)
        enter!!.setColour(0f, 1f, 1f)
    }

    fun run() {
        while (!window.windowShouldClose() && !shouldStartGame) {
            checkInput()

            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
            guiRenderer.render(guiTextures)
            TextMaster.render()
            window.update()
        }
        guiRenderer.cleanUp()
        guiTextures.clear()
        musicSource!!.stop()
        musicSource!!.delete()
        TextMaster.removeText(enter)
        TextMaster.removeText(title)
        TextMaster.cleanUp()
    }

    private fun checkInput() {
        if (window.isKeyPressed(GLFW.GLFW_KEY_ENTER)) {
            shouldStartGame = true
        }
    }
}