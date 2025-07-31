package net.adventuregame.entity

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.adventuregame.codec.TexturedModelCodec
import net.adventuregame.codec.Vector3fCodec
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function

open class Entity {
    var model: TexturedModel?
    open var position: Vector3f = Vector3f()
    var rotX: Float
    var rotY: Float
    var rotZ: Float
    var scale: Float

    var textureIndex: Int = 0
        private set

    constructor(model: TexturedModel?, position: Vector3f, rotX: Float, rotY: Float, rotZ: Float, scale: Float) {
        this.model = model
        this.position = position
        this.rotX = rotX
        this.rotY = rotY
        this.rotZ = rotZ
        this.scale = scale
    }

    constructor(
        model: TexturedModel,
        index: Int,
        position: Vector3f,
        rotX: Float,
        rotY: Float,
        rotZ: Float,
        scale: Float
    ) {
        this.textureIndex = index
        this.model = model
        this.position = position
        this.rotX = rotX
        this.rotY = rotY
        this.rotZ = rotZ
        this.scale = scale
    }

    val textureXOffset: Float
        get() {
            val column = textureIndex % model?.texture!!.numberOfRows
            return column.toFloat() / model?.texture!!.numberOfRows.toFloat()
        }

    val textureYOffset: Float
        get() {
            val row = textureIndex / model?.texture!!.numberOfRows
            return row.toFloat() / model?.texture!!.numberOfRows.toFloat()
        }

    fun increasePosition(dx: Float, dy: Float, dz: Float) {
        this.position.x += dx
        this.position.y += dy
        this.position.z += dz
    }

    fun increaseRotation(dx: Float, dy: Float, dz: Float) {
        this.rotX += dx
        this.rotY += dy
        this.rotZ += dz
    }


    companion object {
        private val log: Logger? = LoggerFactory.getLogger(Entity::class.java)
        val CODEC: Codec<Entity?>? =
            RecordCodecBuilder.create<Entity?>(Function { instance: RecordCodecBuilder.Instance<Entity?>? ->
                instance!!.group<TexturedModel, Int?, Vector3f?, Float?, Float?, Float?, Float?>(
                    TexturedModelCodec.CODEC.fieldOf("model")
                        .forGetter<Entity?> { entity: Entity? -> entity!!.model },
                    Codec.INT.optionalFieldOf("textureIndex", 0)
                        .forGetter<Entity?> { obj: Entity? -> obj!!.textureIndex },
                    Vector3fCodec.CODEC!!.fieldOf("position")
                        .forGetter<Entity?> { entity: Entity? -> entity!!.position },
                    Codec.FLOAT.fieldOf("rotX").forGetter<Entity?> { entity: Entity? -> entity!!.rotX },
                    Codec.FLOAT.fieldOf("rotY").forGetter<Entity?> { entity: Entity? -> entity!!.rotY },
                    Codec.FLOAT.fieldOf("rotZ").forGetter<Entity?> { entity: Entity? -> entity!!.rotZ },
                    Codec.FLOAT.fieldOf("scale").forGetter<Entity?> { entity: Entity? -> entity!!.scale }
                ).apply<Entity?>(
                    instance
                ) { model: TexturedModel?, index: Int?, position: Vector3f?, rotX: Float?, rotY: Float?, rotZ: Float?, scale: Float? ->
                    Entity(
                        model!!, index!!, position!!, rotX!!, rotY!!, rotZ!!, scale!!
                    )
                }
            }
            )
    }
}
