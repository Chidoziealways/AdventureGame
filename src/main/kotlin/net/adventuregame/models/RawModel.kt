package net.adventuregame.models

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RawModel(val vaoId: Int, val vertexCount: Int) {
    init {
        log.info("CREATED A RAW MODEL")
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(RawModel::class.java)
    }
}
