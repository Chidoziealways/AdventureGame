package net.adventuregame.codec

import com.chidozie.core.textures.ModelTexture
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

object ModelTextureCodec {
    val CODEC: Codec<ModelTexture> = RecordCodecBuilder.create { instance ->
        instance.group(
            Codec.INT.fieldOf("textureId").forGetter { it.textureId },
            Codec.INT.fieldOf("normalMap").forGetter { it.normalMap },
            Codec.INT.fieldOf("specularMap").forGetter { it.specularMap },
            Codec.FLOAT.fieldOf("shineDamper").forGetter { it.shineDamper },
            Codec.FLOAT.fieldOf("reflectivity").forGetter { it.reflectivity },
            Codec.BOOL.fieldOf("hasTransparency").forGetter { it.isHasTransparency },
            Codec.BOOL.fieldOf("useFakeLighting").forGetter { it.isUseFakeLighting },
            Codec.BOOL.fieldOf("hasSpecularMap").forGetter { it.hasSpecularMap() },
            Codec.BOOL.fieldOf("isSelected").forGetter { it.isSelected },
            Codec.INT.fieldOf("numberOfRows").forGetter { it.numberOfRows }
        ).apply(instance) { texId, normMap, specMap, shine, reflect, trans, fakeLight, hasSpec, selected, rows ->
            ModelTexture(texId).also {
                it.normalMap = normMap
                if (hasSpec) it.specularMap = specMap
                it.shineDamper = shine
                it.reflectivity = reflect
                it.isHasTransparency = trans
                it.isUseFakeLighting = fakeLight
                it.isSelected = selected
                it.numberOfRows = rows
            }
        }
    }

}