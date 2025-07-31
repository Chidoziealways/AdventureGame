package com.chidozie.core.renderEngine

import com.chidozie.core.normalmap.NormalMappingRenderer
import com.chidozie.core.shaders.StaticShader
import com.chidozie.core.shaders.TerrainShader
import com.chidozie.core.shadows.ShadowMapMasterRenderer
import com.chidozie.core.terrains.Terrain
import net.adventuregame.entity.Camera
import net.adventuregame.entity.Entity
import net.adventuregame.entity.Light
import net.adventuregame.game.AdventureMain
import net.adventuregame.models.TexturedModel
import net.adventuregame.skybox.SkyBoxRenderer
import net.adventuregame.toolbox.Consts
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL13
import kotlin.math.tan

class MasterRenderer(loader: Loader, camera: Camera?) {
    var projectionMatrix: Matrix4f? = null

    private val window = AdventureMain.window

    val entityShader: StaticShader = StaticShader()
    val entityRenderer: EntityRenderer

    private val terrainRenderer: TerrainRenderer
    private val terrainShader = TerrainShader()
    private val skyBoxRenderer: SkyBoxRenderer
    private val shadowMapRenderer: ShadowMapMasterRenderer

    private val normalMapRenderer: NormalMappingRenderer

    private val entities: MutableMap<TexturedModel, MutableList<Entity>> =
        HashMap<TexturedModel, MutableList<Entity>>()
    private val normalMapEntities: MutableMap<TexturedModel, MutableList<Entity>> =
        HashMap<TexturedModel, MutableList<Entity>>()
    private val terrains: MutableList<Terrain> = ArrayList<Terrain>()

    init {
        enableCulling()
        createProjectionMatrix()
        entityRenderer = EntityRenderer(entityShader, projectionMatrix)
        terrainRenderer = TerrainRenderer(terrainShader, projectionMatrix)
        skyBoxRenderer = SkyBoxRenderer(loader, projectionMatrix!!)
        normalMapRenderer = NormalMappingRenderer(projectionMatrix)
        this.shadowMapRenderer = ShadowMapMasterRenderer(camera)
    }

    fun renderScene(
        entities: MutableList<Entity>,
        normalEntites: MutableList<Entity>,
        terrains: MutableList<Terrain>,
        lights: MutableList<Light>,
        camera: Camera?,
        clipPlane: Vector4f?
    ) {
        for (entity in entities) {
            processEntity(entity)
            entityShader.loadIsSelected(entity.model?.texture!!.isSelected)
        }
        for (terrain in terrains) {
            processTerrain(terrain)
        }
        for (entity in normalEntites) {
            processNormalMapEntity(entity)
        }
        render(lights, camera, clipPlane)
    }

    fun render(lights: MutableList<Light>, camera: Camera?, clipPlane: Vector4f?) {
        prepare()
        entityShader.start()
        entityShader.loadClipPlane(clipPlane!!)
        entityShader.loadSkyColour(RGB.x, RGB.y, RGB.z)
        entityShader.loadLights(lights)
        entityShader.loadViewMatrix(camera!!)
        entityShader.loadMapSize(ShadowMapMasterRenderer.Companion.SHADOW_MAP_SIZE.toFloat())
        entityRenderer.render(entities, shadowMapRenderer.toShadowMapSpaceMatrix)
        entityShader.stop()
        normalMapRenderer.render(normalMapEntities, clipPlane, lights, camera)
        terrainShader.start()
        terrainShader.loadClipPlane(clipPlane)
        terrainShader.loadSkyColour(RGB.x, RGB.y, RGB.z)
        terrainShader.loadLights(lights)
        terrainShader.loadViewMatrix(camera)
        terrainShader.loadMapSize(ShadowMapMasterRenderer.Companion.SHADOW_MAP_SIZE.toFloat())
        terrainRenderer.render(terrains, shadowMapRenderer.toShadowMapSpaceMatrix)
        terrainShader.stop()
        skyBoxRenderer.render(camera, RGB.x, RGB.y, RGB.z)
        terrains.clear()
        entities.clear()
        normalMapEntities.clear()
    }

    fun processTerrain(terrain: Terrain?) {
        terrains.add(terrain!!)
    }

    fun processEntity(entity: Entity) {
        val entityModel = entity.model
        val batch = entities[entityModel]
        if (batch != null) {
            batch.add(entity)
        } else {
            val newBatch: MutableList<Entity> = ArrayList()
            newBatch.add(entity)
            entities.put(entityModel!!, newBatch)
        }
    }

    fun processNormalMapEntity(entity: Entity) {
        val entityModel = entity.model
        val batch = normalMapEntities[entityModel]
        if (batch != null) {
            batch.add(entity)
        } else {
            val newBatch: MutableList<Entity> = ArrayList()
            newBatch.add(entity)
            normalMapEntities.put(entityModel!!, newBatch)
        }
    }

    fun renderShadowMap(entityList: MutableList<Entity>, sun: Light) {
        for (entity in entityList) {
            processEntity(entity)
        }
        shadowMapRenderer.render(entities, sun)
        entities.clear()
    }

    val shadowMapTexture: Int
        get() = shadowMapRenderer.shadowMap

    fun cleanup() {
        entityShader.cleanup()
        terrainShader.cleanup()
        normalMapRenderer.cleanUp()
        shadowMapRenderer.cleanUp()
    }

    fun prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        //glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClearColor(RGB.x, RGB.y, RGB.z, 1f)
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
        GL13.glActiveTexture(GL13.GL_TEXTURE5)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.shadowMapTexture)
    }

    private fun createProjectionMatrix() {
        projectionMatrix = Matrix4f()
        val aspectRatio = window.width.toFloat() / window.height.toFloat()
        val y_scale = ((1f / tan(Math.toRadians((Fov / 2f).toDouble())))).toFloat()
        val x_scale = y_scale / aspectRatio
        val frustum_length: Float = FarPlane - NearPlane

        projectionMatrix!!.m00(x_scale)
        projectionMatrix!!.m11(y_scale)
        projectionMatrix!!.m22(-((FarPlane + NearPlane) / frustum_length))
        projectionMatrix!!.m23(-1f)
        projectionMatrix!!.m32(-((2 * NearPlane * FarPlane) / frustum_length))
        projectionMatrix!!.m33(0f)
    }

    companion object {
        private val Fov = Consts.FOV
        private val NearPlane = Consts.NEAR_PLANE
        private val FarPlane = Consts.FAR_PLANE

        val RGB: Vector3f = Vector3f(0.5f, 0.5f, 0.5f)

        fun enableCulling() {
            GL11.glEnable(GL11.GL_CULL_FACE)
            GL11.glCullFace(GL11.GL_BACK)
        }

        fun disableCulling() {
            GL11.glDisable(GL11.GL_CULL_FACE)
        }
    }
}
