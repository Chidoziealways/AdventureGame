package net.adventuregame.cutscene

import net.adventuregame.guis.GuiTexture

data class CutsceneLine(
    val text: String,
    val typewriter: Boolean = true,
    val speed: Float = 30f, // characters per second
    val fontName: String = "japanese"
)

data class Cutscene(
    val background: GuiTexture?,
    val lines: List<CutsceneLine>
)