package net.adventuregame.cutscene

import com.adv.core.font.FontManager
import com.adv.core.font.GUIText
import com.adv.core.font.TextMaster
import net.adventuregame.guis.GuiRenderer
import org.joml.Vector2f
import org.joml.Vector3f

class CutsceneRender(
    private val guiRenderer: GuiRenderer,
    private val fontManager: FontManager,
    private val fps: Int = 60
) {
    private var currentCutscene: Cutscene? = null
    private var currentLineIndex = 0
    private var currentCharIndex = 0
    private var elapsedTime = 0f
    private var currentGUIText: GUIText? = null
    private var fullLineText: String = ""
    var life: Int = 0
    var duration: Int = 0

    fun start(cutscene: Cutscene, duration: Int) {
        stopCurrentText()
        currentCutscene = cutscene
        currentLineIndex = 0
        currentCharIndex = 0
        elapsedTime = 0f
        currentGUIText = null
        fullLineText = ""
        this.duration = duration
    }

    fun update(deltaTime: Float) {
        val cutscene = currentCutscene ?: return
        if (currentLineIndex >= cutscene.lines.size) return

        val line = cutscene.lines[currentLineIndex]
        val font = try {
            fontManager.getFont(line.fontName)
        } catch (e: Exception) {
            fontManager.getFont("default")
        }

        // Preload full line once
        if (currentGUIText == null) {
            fullLineText = line.text
            currentGUIText = GUIText(fullLineText, 2f, font)
            println("Cutscene mesh vertex count: ${currentGUIText!!.vertexCount}")
            currentGUIText!!.position = Vector2f(600f, 500f)
            currentGUIText!!.colour = Vector3f(1f, 0f, 0f)
            val vertices = currentGUIText!!.mesh?.vertexCount
        }

        if (line.typewriter) {
            elapsedTime += deltaTime
            currentCharIndex = (elapsedTime * line.speed).toInt().coerceAtMost(fullLineText.length)

            // reveal characters by truncating draw length
            currentGUIText!!.visibleLength = currentCharIndex // see note below

            if (currentCharIndex >= fullLineText.length && life >= duration) {
                life = 0
                nextLine()
            }
        } else {
            // Non-typewriter lines: show full text immediately
            currentGUIText!!.visibleLength = fullLineText.length
            if (life >= duration) {
                life = 0
                nextLine()
            }
        }
        life++
    }

    fun render() {
        val cutscene = currentCutscene ?: return
        GuiRenderer.addGui(cutscene.background)
        guiRenderer.render()
        TextMaster.render()
    }

    private fun nextLine() {
        currentLineIndex++
        currentCharIndex = 0
        elapsedTime = 0f
        currentGUIText?.remove()
        currentGUIText = null
    }

    private fun stopCurrentText() {
        currentGUIText?.remove()
        currentGUIText = null
    }

    fun isFinished(): Boolean = currentCutscene?.let { currentLineIndex >= it.lines.size } ?: true
}
