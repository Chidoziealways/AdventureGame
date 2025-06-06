package net.adventuregame.mobs;

import com.chidozie.core.renderEngine.Loader;
import com.chidozie.core.renderEngine.OBJFileLoader;
import com.chidozie.core.renderEngine.WindowManager;
import com.chidozie.core.terrains.Terrain;
import com.chidozie.core.textures.ModelTexture;
import net.adventuregame.models.TexturedModel;
import org.joml.Vector3f;

public class BadGuy extends HostileMob{
    static Loader loader = new Loader();
    static TexturedModel bbModel = new TexturedModel(OBJFileLoader.loadOBJ("mobs/BG", loader), new ModelTexture(loader.loadGameTexture("playerTexture")));

    public BadGuy(Vector3f position, Vector3f rotation, float scale, float maxHealth, String name) {
        super(bbModel, 0, position, rotation, scale, maxHealth, name, 0.5f);
    }

    @Override
    public void attack(Mob Target) {
        if (position.distance(Target.getPosition()) < 2.04f) {
            Target.TakeDamage(10, this);
            log.info("{} attacked {} for 10 damage!", name, Target.name);
        }
    }

    @Override
    public void move(Terrain terrain) {

    }
}
