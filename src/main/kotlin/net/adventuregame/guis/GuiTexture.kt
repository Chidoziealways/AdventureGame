package net.adventuregame.guis

import org.joml.Vector2f

class GuiTexture(val texture: Int, val position: Vector2f?, val scale: Vector2f?) {
    init {
        GuiRenderer.Companion.addGui(this)
    }
}
