package com.adv.core.font

import org.joml.Vector2f
import org.joml.Vector3f

class GUIText(
    var textString: String,
    val fontSize: Float,
    val font: FontType?,
    autoLoad: Boolean = true
) {
    init {
        if (autoLoad) TextMaster.loadText(this)
    }

    var position: Vector2f? = null
    var colour: Vector3f = Vector3f(1f, 1f, 1f)
    var mesh: TextMeshData? = null
    var vertexCount: Int = 0

    // NEW: how many characters to render from the text
    var visibleLength: Int = textString.length

    fun remove() {
        TextMaster.removeText(this)
    }
}
