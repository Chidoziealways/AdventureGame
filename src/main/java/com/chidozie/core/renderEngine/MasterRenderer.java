package com.chidozie.core.renderEngine;

import com.chidozie.core.normalmap.NormalMappingRenderer;
import com.chidozie.core.shaders.StaticShader;
import com.chidozie.core.shaders.TerrainShader;
import com.chidozie.core.shadows.ShadowMapMasterRenderer;
import com.chidozie.core.terrains.Terrain;
import net.adventuregame.entities.Camera;
import net.adventuregame.entities.Entity;
import net.adventuregame.entities.Light;
import net.adventuregame.game.AdventureMain;
import net.adventuregame.models.TexturedModel;
import net.adventuregame.skybox.SkyBoxRenderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.adventuregame.toolbox.Consts.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class MasterRenderer {

    private static final float Fov = FOV, NearPlane = NEAR_PLANE, FarPlane = FAR_PLANE;

    public static final Vector3f RGB = new Vector3f(0.5f, 0.5f, 0.5f);

    private Matrix4f projectionMatrix;

    private WindowManager window = AdventureMain.getWindow();

    private StaticShader entityShader = new StaticShader();
    private EntityRenderer entityRenderer;

    public EntityRenderer getEntityRenderer() {
        return entityRenderer;
    }

    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();
    private SkyBoxRenderer skyBoxRenderer;
    private ShadowMapMasterRenderer shadowMapRenderer;

    public StaticShader getEntityShader() {
        return entityShader;
    }

    private NormalMappingRenderer normalMapRenderer;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();
    private Map<TexturedModel, List<Entity>> normalMapEntities = new HashMap<TexturedModel, List<Entity>>();
    private List<Terrain> terrains = new ArrayList<Terrain>();

    public MasterRenderer(Loader loader, Camera camera) {
        enableCulling();
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(entityShader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyBoxRenderer = new SkyBoxRenderer(loader, projectionMatrix);
        normalMapRenderer = new NormalMappingRenderer(projectionMatrix);
        this.shadowMapRenderer = new ShadowMapMasterRenderer(camera);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public static void enableCulling() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    public static void disableCulling() {
        glDisable(GL_CULL_FACE);
    }

    public void renderScene(List<Entity> entities, List<Entity> normalEntites, List<Terrain> terrains, List<Light> lights,
                            Camera camera, Vector4f clipPlane) {
        for(Entity entity : entities) {
            processEntity(entity);
            entityShader.loadIsSelected(entity.getModel().getTexture().isSelected());
        }
        for (Terrain terrain : terrains) {
            processTerrain(terrain);
        }
        for (Entity entity : normalEntites) {
            processNormalMapEntity(entity);
        }
        render(lights, camera, clipPlane);
    }

    public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
        prepare();
        entityShader.start();
        entityShader.loadClipPlane(clipPlane);
        entityShader.loadSkyColour(RGB.x, RGB.y, RGB.z);
        entityShader.loadLights(lights);
        entityShader.loadViewMatrix(camera);
        entityShader.loadMapSize(ShadowMapMasterRenderer.SHADOW_MAP_SIZE);
        entityRenderer.render(entities, shadowMapRenderer.getToShadowMapSpaceMatrix());
        entityShader.stop();
        normalMapRenderer.render(normalMapEntities, clipPlane, lights, camera);
        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColour(RGB.x, RGB.y, RGB.z);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainShader.loadMapSize(ShadowMapMasterRenderer.SHADOW_MAP_SIZE);
        terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
        terrainShader.stop();
        skyBoxRenderer.render(camera, RGB.x, RGB.y, RGB.z);
        terrains.clear();
        entities.clear();
        normalMapEntities.clear();
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        }else {
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void processNormalMapEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = normalMapEntities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        }else {
            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            normalMapEntities.put(entityModel, newBatch);
        }
    }

    public void renderShadowMap(List<Entity> entityList, Light sun) {
        for (Entity entity : entityList) {
            processEntity(entity);
        }
        shadowMapRenderer.render(entities, sun);
        entities.clear();
    }

    public int getShadowMapTexture() {
        return shadowMapRenderer.getShadowMap();
    }

    public void cleanup() {
        entityShader.cleanup();
        terrainShader.cleanup();
        normalMapRenderer.cleanUp();
        shadowMapRenderer.cleanUp();
    }

    public void prepare() {
        glEnable(GL_DEPTH_TEST);
        //glEnable(GL11.GL_STENCIL_TEST);
        glClearColor(RGB.x, RGB.y, RGB.z, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL_TEXTURE_2D, getShadowMapTexture());
    }

    private void createProjectionMatrix() {
        projectionMatrix = new Matrix4f();
        float aspectRatio = (float) window.getWidth() / (float) window.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(Fov / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FarPlane - NearPlane;

        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        projectionMatrix.m22(-((FarPlane + NearPlane) / frustum_length));
        projectionMatrix.m23(-1);
        projectionMatrix.m32(-((2 * NearPlane * FarPlane) / frustum_length));
        projectionMatrix.m33(0);
    }

}
