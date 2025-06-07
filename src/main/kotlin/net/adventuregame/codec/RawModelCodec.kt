package net.adventuregame.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.adventuregame.models.RawModel

object RawModelCodec {
    val CODEC: Codec<RawModel> = RecordCodecBuilder.create { instance ->
        instance.group(
            Codec.INT.fieldOf("vaoId").forGetter { it.vaoId },
            Codec.INT.fieldOf("vertexCount").forGetter { it.vertexCount }
        ).apply(instance) { vaoId, vertexCount ->
            RawModel(vaoId, vertexCount)
        }
    }

}