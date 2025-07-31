package net.adventuregame.items

import com.chidozie.core.renderEngine.OBJFileLoader
import com.chidozie.core.textures.ModelTexture
import net.adventuregame.models.TexturedModel
import net.adventuregame.registries.ItemRegistry

object Items {
    val PISTOL: GunItem = GunItem(
        "pistol",
        TexturedModel(
            OBJFileLoader.loadOBJ("stall", GunItem.Companion.loader),
            ModelTexture(GunItem.Companion.loader.loadGameTexture("stallTexture"))
        ),
        10,
        10f
    )

    val KATANA: KatanaItem = KatanaItem(
        "katana",
        TexturedModel(
            OBJFileLoader.loadOBJ("torch", GunItem.Companion.loader),
            ModelTexture(GunItem.Companion.loader.loadGameTexture("torch"))
        )
    )

    fun registerAll() {
        ItemRegistry.register("pistol", PISTOL)
        ItemRegistry.register("katana", KATANA)
    }
}
