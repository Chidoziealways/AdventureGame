package net.adventuregame.game;

import com.chidozie.core.audio.AudioManager;
import com.chidozie.core.audio.Source;
import com.chidozie.core.font.FontType;
import com.chidozie.core.font.GUIText;
import com.chidozie.core.font.TextMaster;
import com.chidozie.core.normalmap.NormalMappedObjLoader;
import com.chidozie.core.postProcessing.Fbo;
import com.chidozie.core.postProcessing.PostProcessing;
import com.chidozie.core.renderEngine.Loader;
import com.chidozie.core.renderEngine.MasterRenderer;
import com.chidozie.core.renderEngine.OBJFileLoader;
import com.chidozie.core.renderEngine.WindowManager;
import com.chidozie.core.terrains.Terrain;
import com.chidozie.core.textures.ModelTexture;
import com.chidozie.core.textures.TerrainTexture;
import com.chidozie.core.textures.TerrainTexturePack;
import net.adventuregame.entities.Camera;
import net.adventuregame.entities.Entity;
import net.adventuregame.entities.Light;
import net.adventuregame.entities.Player;
import net.adventuregame.gameObjects.Tree;
import net.adventuregame.guis.GuiRenderer;
import net.adventuregame.guis.GuiTexture;
import net.adventuregame.models.RawModel;
import net.adventuregame.models.TexturedModel;
import net.adventuregame.particles.ParticleSystem;
import net.adventuregame.particles.ParticleTexture;
import net.adventuregame.particles.粒子の先生;
import net.adventuregame.sounds.Music;
import net.adventuregame.toolbox.MousePicker;
import net.adventuregame.water.WaterFrameBuffers;
import net.adventuregame.water.WaterRenderer;
import net.adventuregame.water.WaterShader;
import net.adventuregame.water.WaterTile;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdventureGame {

    private static final Logger log = LoggerFactory.getLogger(AdventureGame.class);
    private WindowManager window;
    private Loader loader = new Loader();
    private List<Entity> entities = new ArrayList<Entity>();
    private List<Terrain> terrains = new ArrayList<Terrain>();
    private List<Light> lights = new ArrayList<Light>();
    private List<Entity> normalMapEntities = new ArrayList<Entity>();
    private List<GuiTexture> guis = new ArrayList<GuiTexture>();
    private List<WaterTile> waters = new ArrayList<WaterTile>();

    public AdventureGame(WindowManager window) {
        this.window = window;
    }



    public void run() {
        RawModel playerMod = OBJFileLoader.loadOBJ("person", loader);
        TexturedModel playerTMod = new TexturedModel(playerMod, new ModelTexture(loader.loadGameTexture("playerTexture")));

        Player player = new Player(playerTMod, new Vector3f(100, 0, -50), 0, 0, 0, 1);
        entities.add(player);
        Camera camera = new Camera(player);
        log.info("Created a Camera");
        log.info("Created YOU!");
        MasterRenderer renderer = new MasterRenderer(loader, camera);
        TextMaster.init(loader);
        AudioManager.init();
        AudioManager.setListenerData(player.getPosition());
        粒子の先生.init(loader, renderer.getProjectionMatrix());

        FontType font = new FontType(loader.loadFontTextureAtlas("yuMincho"), new File("src/main/resources/assets/adventuregame/textures/font/yuMincho.fnt"));
        GUIText text = new GUIText("THIS IS A LOT OF TEXT!!!! I DON'T KNOW WHEN I WILL BE ABLE TO USE YU MINCHO. I DON'T LIKE THIS FONT AT ALL!", 3, font, new Vector2f(0.5f, 0.5f), 0.5f, true);
        text.setColour(1, 0, 0);

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadGameTexture("grass"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadGameTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadGameTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadGameTexture("path"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
                gTexture, bTexture);
        TerrainTexture blendMap = new TerrainTexture(loader.loadGameTexture("blendMap"));

        Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap);
        log.info("This Terrain's Seed: {}", terrain.getSeed());
        terrains.add(terrain);


        log.info("About to Run");
        Light sun = new Light(new Vector3f(100000000, 150000000, -100000000), new Vector3f(1.3f, 1.3f, 1.3f));
        lights.add(sun);
        TexturedModel lamp = new TexturedModel(OBJFileLoader.loadOBJ("torch", loader), new ModelTexture(loader.loadGameTexture("torch")));


        log.info("Created 'Lights'");
        Light lampLight = new Light(new Vector3f(185, getY(185, -293, terrain) + 15, -293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, .002f));
        lights.add(lampLight);
        Entity lampEntity = new Entity(lamp, new Vector3f(185, getY(185, -293, terrain), -293), 0, 0, 0, 1);
        entities.add(lampEntity);

        Random random = new Random();
        for (int i = 0; i < 2000; i++) {
            float x = random.nextFloat() * 1999;
            float z = random.nextFloat() * -1999;
            float y = terrain.getHeightOfTerrain(x, z);
            entities.add(new Tree(new Vector3f(x, y, z), 0, 0, 0, 10));
        }

        ModelTexture fernTexture  = new ModelTexture(loader.loadGameTexture("fern"));
        fernTexture.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(OBJFileLoader.loadOBJ("fern", loader), fernTexture);

        for (int i = 0; i < 1000; i++) {
            float x = random.nextFloat() * 1000;
            float z = random.nextFloat() * -1000;
            float y = terrain.getHeightOfTerrain(x, z);
            entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, 0, 0, 1));
        }

        TexturedModel barrelModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("barrel", loader),
                new ModelTexture(loader.loadGameTexture("barrel")));
        barrelModel.getTexture().setNormalMap(loader.loadGameTexture("normalmaps/barrelNormal"));
        barrelModel.getTexture().setShineDamper(10);
        barrelModel.getTexture().setReflectivity(0.5f);
        barrelModel.getTexture().setSpecularMap(loader.loadGameTexture("specularmaps/barrelS"));

        Entity barrel = new Entity(barrelModel, new Vector3f(75, 10, -75), 0, 0, 0, 1f);
        normalMapEntities.add(barrel);

        GuiTexture socuwan = new GuiTexture(loader.loadGameTexture("gui/socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
        GuiTexture health = new GuiTexture(loader.loadGameTexture("gui/health"), new Vector2f(-0.5f, 0.9f), new Vector2f(0.25f, 0.25f));
        GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(),
                new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
        guis.add(health);
        //guis.add(shadowMap);

        GuiRenderer guiRenderer = new GuiRenderer(loader);

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrain, entities);

        WaterFrameBuffers waterFbos = new WaterFrameBuffers();

        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), waterFbos);
        WaterTile water = new WaterTile(1000, -1000, -10);
        waters.add(water);

        ParticleTexture fire = new ParticleTexture(loader.loadParticleTexture("fire"), 8, false);

        ParticleSystem system = new ParticleSystem(fire, 50, 25, 0.3f, 4, 1);
        system.randomizeRotation();
        system.setDirection(new Vector3f(0, 1, 0), 0.1f);
        system.setLifeError(0.1f);
        system.setSpeedError(0.4f);
        system.setScaleError(0.8f);

        Source source = new Source();
        source.setLooping(true);
        source.play(Music.AO_TO_NATSU);
        source.setVolume(1f);
        source.setPosition(player.getPosition().add(new Vector3f(5, 20, 9)));

        AL10.alDistanceModel(AL11.AL_INVERSE_DISTANCE_CLAMPED);

        Fbo multisampleFbo = new Fbo(window.getWidth(), window.getHeight());
        Fbo outputFbo = new Fbo(window.getWidth(), window.getHeight(), Fbo.DEPTH_TEXTURE);
        Fbo outputFbo2 = new Fbo(window.getWidth(), window.getHeight(), Fbo.DEPTH_TEXTURE);
        PostProcessing.init(loader);


        log.info("EXECUTING MAIN LOOP");
        while (!window.windowShouldClose()) {
            player.move(terrain);
            camera.move();
            picker.update();

            system.generateParticles(player.getPosition());

            if (window.isKeyPressed(GLFW.GLFW_KEY_E)) {
                guis.add(socuwan);
                log.info("Happened");
            }


            粒子の先生.update(camera);

            renderer.renderShadowMap(entities, sun);
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            //Render Reflection Texture
            waterFbos.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - water.getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight() + 1f));
            camera.getPosition().y += distance;
            camera.invertPitch();

            //Render Refraction Texture
            waterFbos.bindRefractionFrameBuffer();
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));

            //Render to the Screen
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            waterFbos.unbindCurrentFrameBuffer();

            multisampleFbo.bindFrameBuffer();
            renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 11, 0, 15));
            waterRenderer.render(waters, camera, sun);
            粒子の先生.renderParticles(camera);
            multisampleFbo.unbindFrameBuffer();
            multisampleFbo.resolveToFBO(GL30.GL_COLOR_ATTACHMENT0, outputFbo);
            multisampleFbo.resolveToFBO(GL30.GL_COLOR_ATTACHMENT1, outputFbo2);
            PostProcessing.doPostProcessing(outputFbo.getColourTexture(), outputFbo2.getColourTexture());

            guiRenderer.render(guis);
            TextMaster.render();

            window.update();
        }
        log.info("Cleaning up the Game");

        cleanup();
        multisampleFbo.cleanUp();
        outputFbo.cleanUp();
        outputFbo2.cleanUp();
        waterFbos.cleanUp();
        waterShader.cleanup();
        guiRenderer.cleanUp();
        loader.cleanUp();
        source.delete();
        renderer.cleanup();
    }

    private void cleanup() {
        PostProcessing.cleanUp();
        粒子の先生.cleanUp();
        TextMaster.cleanUp();
        AudioManager.cleanUp();
    }

    private static float getY(float x, float z, Terrain terrain) {
        return terrain.getHeightOfTerrain(x, z);
    }
}
