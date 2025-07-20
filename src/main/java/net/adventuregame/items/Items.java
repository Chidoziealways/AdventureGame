package net.adventuregame.items;

import com.chidozie.core.renderEngine.OBJFileLoader;
import com.chidozie.core.textures.ModelTexture;
import net.adventuregame.models.TexturedModel;
import net.adventuregame.registries.ItemRegistry;
import org.joml.Vector3f;

public class Items {

    public static final GunItem PISTOL = new GunItem(
            "pistol",
            new TexturedModel(OBJFileLoader.loadOBJ("stall", GunItem.loader), new ModelTexture(GunItem.loader.loadGameTexture("stallTexture"))),
            10,
            10
    );

    public static final KatanaItem KATANA = new KatanaItem(
            "katana",
            new TexturedModel(OBJFileLoader.loadOBJ("torch", GunItem.loader), new ModelTexture(GunItem.loader.loadGameTexture("torch")))
    );

    public static void registerAll() {
        ItemRegistry.register("pistol", PISTOL);
        ItemRegistry.register("katana", KATANA);
    }

}
