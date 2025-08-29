package net.adventuregame.mobs.hostile

import com.adv.core.renderEngine.Loader
import com.adv.core.renderEngine.OBJFileLoader
import com.adv.core.textures.ModelTexture
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f

class BadGuy(position: Vector3f?, rotation: Vector3f?, scale: Float, maxHealth: Float) : HostileMob(
    0, position, rotation, scale, maxHealth, speed = 5f
) {
    override fun GetModels(): List<TexturedModel> {
        return listOf(bbModel)
    }

    companion object {
        var loader: Loader = Loader()
        var bbModel: TexturedModel = TexturedModel(
            OBJFileLoader.loadOBJ("mobs/BG", loader),
            ModelTexture(loader.loadGameTexture("playerTexture"))
        )
    }
}
