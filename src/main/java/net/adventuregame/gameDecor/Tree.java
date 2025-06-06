package net.adventuregame.gameDecor;

import com.chidozie.core.renderEngine.Loader;
import com.chidozie.core.renderEngine.OBJFileLoader;
import com.chidozie.core.textures.ModelTexture;
import net.adventuregame.entities.Entity;
import net.adventuregame.models.TexturedModel;
import org.joml.Vector3f;

public class Tree extends Entity {

    private static Loader loader = new Loader();
    private static TexturedModel treeModel = new TexturedModel(OBJFileLoader.loadOBJ("tree_legacy", loader), new ModelTexture(loader.loadGameTexture("tree")));
    static {
        ModelTexture texture = treeModel.getTexture();
        texture.setShineDamper(10);
        texture.setReflectivity(1);
    }

    public Tree(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(treeModel, position, rotX, rotY, rotZ, scale);
    }
}
