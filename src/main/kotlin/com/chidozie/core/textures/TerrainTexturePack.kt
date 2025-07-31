package com.chidozie.core.textures

class TerrainTexturePack(
    val backgroundTexture: TerrainTexture?,
    private val rTexture: TerrainTexture?,
    private val gTexture: TerrainTexture?,
    private val bTexture: TerrainTexture?
) {
    fun getrTexture(): TerrainTexture? {
        return rTexture
    }

    fun getgTexture(): TerrainTexture? {
        return gTexture
    }

    fun getbTexture(): TerrainTexture? {
        return bTexture
    }
}
