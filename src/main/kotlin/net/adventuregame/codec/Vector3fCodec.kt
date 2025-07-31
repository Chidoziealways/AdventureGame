package net.adventuregame.codec

import com.mojang.datafixers.util.Function3
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import org.joml.Vector3f
import java.util.function.Function

object Vector3fCodec {
    val CODEC =
        RecordCodecBuilder.create<Vector3f?>(Function { instance: RecordCodecBuilder.Instance<Vector3f?>? ->
            instance!!.group(
                Codec.FLOAT.fieldOf("x").forGetter<Vector3f?> { v: Vector3f? -> v!!.x },
                Codec.FLOAT.fieldOf("y").forGetter<Vector3f?> { v: Vector3f? -> v!!.y },
                Codec.FLOAT.fieldOf("z").forGetter<Vector3f?> { v: Vector3f? -> v!!.z }
            ).apply<Vector3f?>(instance) { x: Float?, y: Float?, z: Float? -> Vector3f(x!!, y!!, z!!) }
        })
}

