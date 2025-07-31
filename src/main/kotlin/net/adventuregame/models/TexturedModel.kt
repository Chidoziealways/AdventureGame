package net.adventuregame.models

import com.chidozie.core.textures.ModelTexture
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TexturedModel(val rawModel: RawModel?, val texture: ModelTexture?) {
    init {
        log.info("Created Textured Model With")
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(TexturedModel::class.java)
    }
}
