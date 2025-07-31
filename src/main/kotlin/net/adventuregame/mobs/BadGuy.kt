package net.adventuregame.mobs

import com.chidozie.core.renderEngine.Loader
import com.chidozie.core.renderEngine.OBJFileLoader
import com.chidozie.core.terrains.Terrain
import com.chidozie.core.textures.ModelTexture
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f

class BadGuy(position: Vector3f?, rotation: Vector3f?, scale: Float, maxHealth: Float, name: String?) : HostileMob(
    bbModel, 0, position, rotation, scale, maxHealth, name, 0.5f
) {
    override fun attack(Target: Mob?) {
        if (position.distance(Target!!.position) < 2.04f) {
            Target.TakeDamage(10f, this)
            log.info("{} attacked {} for 10 damage!", name, Target.name)
        }
    }

    override fun move(terrain: Terrain?) {
    }

    companion object {
        var loader: Loader = Loader()
        var bbModel: TexturedModel = TexturedModel(
            OBJFileLoader.loadOBJ("mobs/BG", loader),
            ModelTexture(loader.loadGameTexture("playerTexture"))
        )
    }
}
