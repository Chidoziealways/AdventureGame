package net.adventuregame.game

import com.chidozie.core.audio.AudioManager
import com.chidozie.core.font.TextMaster
import com.chidozie.core.normalmap.NormalMappedObjLoader
import com.chidozie.core.postProcessing.Fbo
import com.chidozie.core.postProcessing.PostProcessing
import com.chidozie.core.renderEngine.Loader
import com.chidozie.core.renderEngine.MasterRenderer
import com.chidozie.core.renderEngine.OBJFileLoader
import com.chidozie.core.renderEngine.WindowManager
import com.chidozie.core.scripts.LuaEngine
import com.chidozie.core.terrains.Terrain
import com.chidozie.core.textures.ModelTexture
import com.chidozie.core.textures.TerrainTexture
import com.chidozie.core.textures.TerrainTexturePack
import net.adventuregame.data.CodecRegistry
import net.adventuregame.entity.Camera
import net.adventuregame.entity.Entity
import net.adventuregame.entity.Light
import net.adventuregame.gameDecor.Tree
import net.adventuregame.gui.HotbarRenderer3D
import net.adventuregame.guis.GuiRenderer
import net.adventuregame.guis.GuiTexture
import net.adventuregame.hud.HudShader
import net.adventuregame.items.Items
import net.adventuregame.items.entities.GunEntity
import net.adventuregame.items.entities.ItemEntity
import net.adventuregame.items.entities.KatanaEntity
import net.adventuregame.mobs.BadGuy
import net.adventuregame.mobs.Mob
import net.adventuregame.models.TexturedModel
import net.adventuregame.particles.ParticleSystem
import net.adventuregame.particles.ParticleTexture
import net.adventuregame.particles.粒子の先生
import net.adventuregame.player.Player
import net.adventuregame.story.StoryHandler
import net.adventuregame.story.StoryManager
import net.adventuregame.toolbox.MousePicker
import net.adventuregame.water.WaterFrameBuffers
import net.adventuregame.water.WaterRenderer
import net.adventuregame.water.WaterShader
import net.adventuregame.water.WaterTile
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.MarkerManager
import org.joml.Vector3f
import org.joml.Vector4f
import java.util.*

/**
 * @author Chidozie Derek Chidozie-Uzowulu
 */
class GameState private constructor(window: WindowManager) {

    @JvmField
    val window: WindowManager

    var camera: Camera? = null
    var terrain: Terrain? = null

    private var storyHandler: StoryHandler? = null

    private var guiRenderer: GuiRenderer? = null
    private var waterRenderer: WaterRenderer? = null
    private var particleSystem: ParticleSystem? = null

    /** 1) Core systems: terrain, player, camera, renderer, picker
     */
    fun initSystems() {
        // Terrain
        terrains.add(terrain!!)

        // Player & camera
        storyManager = StoryManager()
        println("Game runtime StoryManager: ${storyManager.hashCode()}")
        LuaEngine.loadAllScripts()
        storyHandler = StoryHandler(this)
        camera = Camera(player!!)

        // Renderer & picker
        renderer = MasterRenderer(loader, camera)
        hotbarRenderer = HotbarRenderer3D(HudShader())
        picker = MousePicker(camera!!, renderer!!.projectionMatrix, terrain, entities)

        // Put player in the world
        entities.add(player!!)
    }

    /** 2) Fonts, GUI text, audio & advancements  */
    fun loadFontsAndAudio() {
        TextMaster.init(loader)
        /*val font = FontType(
            loader.loadFontTextureAtlas("yuMincho"),
            File("src/main/resources/assets/adventuregame/textures/font/yuMincho.fnt")
        )*/

        //GUIText title = new GUIText("Adventure Game", 3, font, new Vector2f(0f,0f), 0.5f, true);
        //title.setColour(1,0,0);
        AudioManager.init()
        AudioManager.setListenerData(player!!.position)
    }

    /** 3) Populate the world with lights, flora, props, mobs & pickups  */
    fun initWorld() {
        // Sun
        val sun = Light(Vector3f(100e6f, 150e6f, -100e6f), Vector3f(1.3f, 1.3f, 1.3f))
        lights.add(sun)

        val rand = Random()
        // Trees
        for (i in 0..1999) {
            val x = rand.nextFloat() * 1999
            val z = rand.nextFloat() * -1999
            val y = terrain!!.getHeightOfTerrain(x, z)
            entities.add(Tree(Vector3f(x, y, z), 0f, 0f, 0f, 10f))
        }

        // Ferns
        val fernTex = ModelTexture(loader.loadGameTexture("fern"))
        fernTex.numberOfRows = 2
        val fern = TexturedModel(OBJFileLoader.loadOBJ("fern", loader), fernTex)
        for (i in 0..999) {
            val x = rand.nextFloat() * 1000
            val z = rand.nextFloat() * -1000
            val y = terrain!!.getHeightOfTerrain(x, z)
            entities.add(Entity(fern, rand.nextInt(4), Vector3f(x, y, z), 0f, 0f, 0f, 1f))
        }

        // Barrel with normal map
        val barrel = TexturedModel(
            NormalMappedObjLoader.loadOBJ("barrel", loader),
            ModelTexture(loader.loadGameTexture("barrel"))
        )
        barrel.texture!!.normalMap = loader.loadGameTexture("normalmaps/barrelNormal")
        barrel.texture.shineDamper = 10f
        barrel.texture.reflectivity = 0.5f
        barrel.texture.specularMap = loader.loadGameTexture("specularmaps/barrelS")
        normalMapEntities.add(Entity(barrel, Vector3f(75f, 10f, -75f), 0f, 0f, 0f, 1f))

        // Bad guy
        val bg = BadGuy(Vector3f(100f, 0f, -50f), Vector3f(0f, 0f, 0f), 1f, 20f, "BadGuy1")
        entities.add(bg)


        if(!player!!.hasItemByName("pistol")) {
            val x = 214.8f + 5f
            val z = -1108 + 5f
            val y = terrain!!.getHeightOfTerrain(x, z)
            // Gun pickup
            val pistolEntity = Items.PISTOL.createEntity(Vector3f(x, y, z)).also {
                log.info(mark, "Pistol: {}", it)
                entities.add(it)
            } as GunEntity
        }
        if(!player!!.hasItemByName("katana")) {
            val x = 214.8f + 5f
            val z = -1108 + 5f
            val y = terrain!!.getHeightOfTerrain(x, z)
            val katanaEntity = Items.KATANA.createEntity(Vector3f(x, y, z)).also {
                log.info(mark, "Katana: {}", it)
                entities.add(it)
            } as KatanaEntity
        }
    }

    /** 4) Set up GUI, water, particles & post-processing  */
    fun initRenderers() {
        guiRenderer = GuiRenderer(loader)

        val fbos = WaterFrameBuffers()
        val wsh = WaterShader()
        waterRenderer = WaterRenderer(loader, wsh, renderer!!.projectionMatrix, fbos)
        waters.add(WaterTile(1000f, -1000f, -10f))
        粒子の先生.init(loader, renderer!!.projectionMatrix!!)

        val pt = ParticleTexture(loader.loadParticleTexture("fire"), 8, false)
        particleSystem = ParticleSystem(pt, 50f, 25f, 0.3f, 4f, 1f)
        particleSystem!!.randomizeRotation()
        particleSystem!!.setDirection(Vector3f(0f, 1f, 0f), 0.1f)
        particleSystem!!.setLifeError(0.1f)
        particleSystem!!.setSpeedError(0.4f)
        particleSystem!!.setScaleError(0.8f)

        Fbo(window.width, window.height)
        Fbo(window.width, window.height, Fbo.DEPTH_TEXTURE)
        Fbo(window.width, window.height, Fbo.DEPTH_TEXTURE)
        PostProcessing.init(loader)
    }

    /** Called once per frame to advance logic  */
    fun update() {
        player!!.move(terrain)
        storyHandler!!.update()
        camera!!.move()
        for (e in ArrayList(entities)) {
            if (e is BadGuy) e.updateAI(terrain!!)
            if (e is ItemEntity) e.update()
        }
        picker!!.update()

        particleSystem!!.generateParticles(player!!.position)
    }

    /** Called once per frame to draw everything  */
    fun render() {
        renderer!!.renderShadowMap(entities, lights[0])
        renderer!!.renderScene(entities, normalMapEntities, terrains, lights, camera, Vector4f(0f, 1f, 0f, 100f))
        waterRenderer!!.render(waters, camera!!, lights[0])
        粒子の先生.update(camera!!)
        粒子の先生.renderParticles(camera!!)
        hotbarRenderer!!.render()
        guiRenderer!!.render()
        TextMaster.render()
    }

    /** Cleanup on exit  */
    fun cleanup() {
        PostProcessing.cleanUp()
        粒子の先生.cleanUp()
        TextMaster.cleanUp()
        AudioManager.cleanUp()
        loader.cleanUp()
        renderer!!.cleanup()
        guiRenderer!!.cleanUp()
    }

    init {
        CodecRegistry.registerAll()
        this.window = window

        player = Player(Vector3f(214.8f, 0f, -1108.0f), 0f, 0f, 0f, 1f)
        terrain = Terrain(0, -1, loader, texturePack, blendMap, 0)

        seed = terrain!!.seed

        println("Seed: $seed")
    }

    fun toSerializable(): GameStateSerializable {
        return GameStateSerializable(
            seed = seed!!,
            player = player,
            storyManager = storyManager
            //entities = entities
        )
    }


    companion object {
        @JvmStatic
        var seed: Int? = null

        private val log: Logger = LogManager.getLogger(GameState::class.java)
        private val mark: Marker = MarkerManager.getMarker("GAMESTATE")
        @JvmField
        var player: Player? = null
        @JvmField
        var storyManager: StoryManager? = null
        @JvmField
        var picker: MousePicker? = null

        @Volatile
        private var instance: GameState? = null

        @JvmStatic
        fun getInstance(window: WindowManager): GameState {
            return instance ?: synchronized(this) {
                instance ?: GameState(window).also {
                    instance = it
                }
            }
        }

        @JvmStatic
        fun getInstance(): GameState {
            return instance ?: throw IllegalStateException("GameState not initialized. Call getInstance(WindowManager) first.")
        }

        @JvmStatic
        fun reset() {
            instance = null
        }

        private val loader = Loader()
        private var renderer: MasterRenderer? = null
        private var hotbarRenderer: HotbarRenderer3D? = null
        private var entities: MutableList<Entity> = ArrayList()
        private val terrains: MutableList<Terrain> = ArrayList()
        private val lights: MutableList<Light> = ArrayList()
        private val normalMapEntities: MutableList<Entity> = ArrayList()
        private val guis: List<GuiTexture> = ArrayList()
        private val waters: MutableList<WaterTile> = ArrayList()

        // Terrain textures (shared)
        private val backgroundTexture = TerrainTexture(loader.loadGameTexture("grass"))
        private val rTexture = TerrainTexture(loader.loadGameTexture("dirt"))
        private val gTexture = TerrainTexture(loader.loadGameTexture("grassFlowers"))
        private val bTexture = TerrainTexture(loader.loadGameTexture("path"))
        private val texturePack = TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture)
        private val blendMap = TerrainTexture(loader.loadGameTexture("blendMap"))

        @JvmStatic
        fun removeEntity(entity: Entity?) {
            entities.remove(entity)
        }

        @JvmStatic
        fun addEntity(entity: Entity) {
            entities.add(entity)
        }

        @JvmStatic
        fun getEntities(): List<Entity?> {
            return entities
        }

        @JvmStatic
        val target: Mob?
            get() {
                val target = picker!!.closestEntity
                var t: Mob? = null
                if (target is Mob) {
                    t = target
                }
                return t
            }

        fun fromSerializable(serializable: GameStateSerializable, window: WindowManager): GameState {
            return getInstance(window).apply {
                seed = serializable.seed
                player = serializable.player
                storyManager = serializable.storyManager
                //entities = serializable.entities as MutableList<Entity?>
                terrain = Terrain(0, -1, loader, texturePack, blendMap, serializable.seed)
            }.also {
                log.info("Inventory: ${serializable.player!!.inventory}")
            }
        }
    }
}
