package net.adventuregame.gameDecor

import com.chidozie.core.renderEngine.Loader
import com.chidozie.core.renderEngine.OBJFileLoader
import com.chidozie.core.textures.ModelTexture
import net.adventuregame.entity.Entity
import net.adventuregame.models.TexturedModel
import org.joml.Vector3f

class Tree(position: Vector3f, rotX: Float, rotY: Float, rotZ: Float, scale: Float) :
    Entity(treeModel, position, rotX, rotY, rotZ, scale) {
    companion object {
        private val loader = Loader()
        private val treeModel =
            TexturedModel(OBJFileLoader.loadOBJ("tree_legacy", loader), ModelTexture(loader.loadGameTexture("tree")))

        init {
            val texture: ModelTexture = treeModel.texture!!
            texture.shineDamper = 10f
            texture.reflectivity = 1f
        }
    }
}
