package net.adventuregame.particles;

import com.chidozie.core.renderEngine.Loader;
import net.adventuregame.entities.Camera;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class 粒子の先生 {

    private static final Logger log = LoggerFactory.getLogger(粒子の先生.class);
    private static Map<ParticleTexture, List<Particle>> 粒子ら = new HashMap<ParticleTexture, List<Particle>>();
    private static ParticleRenderer renderer;

    public static void init(Loader loader, Matrix4f projectionMatrix) {
        renderer = new ParticleRenderer(loader, projectionMatrix);
    }

    public static void update(Camera camera) {
        Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIterator = 粒子ら.entrySet().iterator();
        while (mapIterator.hasNext()) {
            List<Particle> list = mapIterator.next().getValue();
            Iterator<Particle> iterator = list.iterator();
            while (iterator.hasNext()) {
                Particle p = iterator.next();
                boolean stillAlive = p.update(camera);
                if (!stillAlive) {
                    iterator.remove();
                    if (list.isEmpty()) {
                        mapIterator.remove();
                    }
                }
            }
            InsertionSort.sortHighToLow(list);
        }
    }

    public static void renderParticles(Camera カメラ) {
        renderer.render(粒子ら, カメラ);
    }

    public static void cleanUp() {
        renderer.cleanUp();
    }

    public static void addParticle(Particle 粒子) {
        List<Particle> list = 粒子ら.get(粒子.getTexture());
        if (list == null) {
            list = new ArrayList<Particle>();
            粒子ら.put(粒子.getTexture(), list);
        }
        list.add(粒子);
    }

}
