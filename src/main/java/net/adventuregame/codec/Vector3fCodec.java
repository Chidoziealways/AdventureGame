package net.adventuregame.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.joml.Vector3f;

public class Vector3fCodec {
    public static final Codec<Vector3f> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(v -> v.x),
            Codec.FLOAT.fieldOf("y").forGetter(v -> v.y),
            Codec.FLOAT.fieldOf("z").forGetter(v -> v.z)
    ).apply(instance, Vector3f::new));
}

