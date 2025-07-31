package net.adventuregame.items

import com.chidozie.core.renderEngine.Loader
import com.mojang.datafixers.util.Function4
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.adventuregame.items.entities.GunEntity
import net.adventuregame.items.entities.ItemEntity
import net.adventuregame.models.TexturedModel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.joml.Vector3f
import java.util.function.Function

class GunItem(name: String?, model: TexturedModel?, private val maxAmmo: Int, cooldown: Float) : Item(name) {
    var ammo: Int
        private set
    var cooldown: Float
        private set
    private val model: TexturedModel?

    init {
        this.ammo = maxAmmo
        this.cooldown = cooldown
        this.model = model
    }

    fun canFire(): Boolean {
        return ammo > 0 && cooldown <= 0
    }

    fun fire() {
        if (canFire()) {
            ammo--
            log.info(mark, "Fired Gun")
            cooldown = 1.0f // example cooldown duration
            // Trigger shooting effects here or in GunEntity
        }
    }

    fun reload() {
        ammo = maxAmmo
    }

    fun updateCooldown(delta: Float) {
        if (cooldown > 0) cooldown -= delta
    }

    override fun toString(): String {
        return this.name!!
    }

    override fun createEntity(position: Vector3f): ItemEntity {
        return GunEntity(model!!, 0, position, 0f, this)
    }

    override fun getGuiModel(): TexturedModel? {
        return model
    }

    companion object {
        private val log: Logger = LogManager.getLogger(GunItem::class.java)
        private val mark: Marker? = MarkerManager.getMarker("GUNITEM")
        val loader: Loader = Loader()
        val CODEC: Codec<GunItem?>? =
            RecordCodecBuilder.create<GunItem?>(Function { gunItemInstance: RecordCodecBuilder.Instance<GunItem?>? ->
                gunItemInstance!!.group<Int?, Int?, Float?, String?>(
                    Codec.INT.fieldOf("ammo").forGetter<GunItem?> { i: GunItem? -> i!!.ammo },
                    Codec.INT.fieldOf("maxAmmo").forGetter<GunItem?> { i: GunItem? -> i!!.maxAmmo },
                    Codec.FLOAT.fieldOf("cooldown").forGetter<GunItem?> { i: GunItem? -> i!!.cooldown },
                    Codec.STRING.fieldOf("name").forGetter<GunItem?> { i: GunItem? -> i!!.name }
                ).apply<GunItem?>(
                    gunItemInstance
                ) { ammo: Int?, maxAmmo: Int?, cooldown: Float?, name: String? ->
                    GunItem(
                        name,
                        null,
                        maxAmmo!!,
                        cooldown!!
                    )
                }
            })
    }
}
