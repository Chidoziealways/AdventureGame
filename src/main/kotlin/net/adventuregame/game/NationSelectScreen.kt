package net.adventuregame.game

import com.adv.core.font.TextMaster
import com.adv.core.renderEngine.Loader
import com.adv.core.renderEngine.WindowManager
import com.mojang.serialization.Codec
import net.adventuregame.guis.GuiRenderer
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

class NationSelectScreen(private val window: WindowManager, private val loader: Loader) {

    var chosenNation: Nation? = null
        private set

    private val guiRenderer = GuiRenderer(loader)

    fun run() {
        while (!window.windowShouldClose() && chosenNation == null) {
            checkInput()
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
            guiRenderer.render()
            TextMaster.render()
            window.update()
        }
        cleanup()
    }

    private fun checkInput() {
        if (window.isKeyPressed(GLFW.GLFW_KEY_1)) { // for testing, 1 = Japan
            chosenNation = Nation.JAPAN
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_2)) { // 2 = Korea
            chosenNation = Nation.KOREA
        }
    }

    private fun cleanup() {
        guiRenderer.cleanUp()
    }
}

enum class Nation {
    JAPAN,
    KOREA;

    companion object {
        val CODEC: Codec<Nation> = Codec.STRING.xmap(
            { value -> Nation.valueOf(value.uppercase()) },  // decode (String -> Nation)
            { nation -> nation.name.lowercase() }            // encode (Nation -> String)
        )
    }
}
