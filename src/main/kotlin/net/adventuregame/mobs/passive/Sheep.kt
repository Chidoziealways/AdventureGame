package net.adventuregame.mobs.passive

import com.adv.core.renderEngine.GLTFLoader
import com.adv.core.renderEngine.Loader
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f

class Sheep(index: Int = 0, position: Vector3f?, rotation: Vector3f?, scale: Float): PassiveMob( index, position,
    rotation,
    scale,
    5f,
    speed = 9f
) {

    override fun GetModels(): List<TexturedModel> {
        return MODEL
    }

    companion object {
        val loader = Loader()
        val MODEL = GLTFLoader.loadGLB("mobs/sheep", loader)
    }
}