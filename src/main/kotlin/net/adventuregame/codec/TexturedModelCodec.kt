package net.adventuregame.codec

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.adventuregame.models.TexturedModel

object TexturedModelCodec {
    @JvmField
    val CODEC: Codec<TexturedModel> = RecordCodecBuilder.create { instance ->
        instance.group(
            RawModelCodec.CODEC.fieldOf("rawModel").forGetter { it.rawModel },
            ModelTextureCodec.CODEC.fieldOf("texture").forGetter { it.texture }
        ).apply(instance) { raw, tex ->
            TexturedModel(raw, tex)
        }
    }

}