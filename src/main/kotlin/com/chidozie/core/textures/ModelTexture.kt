package com.chidozie.core.textures

class ModelTexture(val textureId: Int) {
    var normalMap: Int = 0
    var specularMap: Int = 0
        set(specMap) {
            field = specMap
            this.hasSpecularMap = true
        }

    var shineDamper: Float = 1f
    var reflectivity: Float = 0f

    var isHasTransparency: Boolean = false
    var isUseFakeLighting: Boolean = false
    private var hasSpecularMap = false
    var isSelected: Boolean = false

    var numberOfRows: Int = 1

    fun hasSpecularMap(): Boolean {
        return hasSpecularMap
    }
}
