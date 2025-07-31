package net.adventuregame.player

import com.chidozie.core.renderEngine.Loader
import com.chidozie.core.renderEngine.OBJFileLoader
import com.chidozie.core.renderEngine.WindowManager
import com.chidozie.core.terrains.Terrain
import com.chidozie.core.textures.ModelTexture
import com.mojang.datafixers.util.Function4
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.adventuregame.codec.Vector3fCodec
import net.adventuregame.entities.Camera
import net.adventuregame.game.AdventureMain
import net.adventuregame.game.GameState
import net.adventuregame.game.GameState.Companion.addEntity
import net.adventuregame.game.GameState.Companion.getInstance
import net.adventuregame.items.GunItem
import net.adventuregame.items.Item
import net.adventuregame.items.entities.BulletEntity
import net.adventuregame.mobs.Mob
import net.adventuregame.models.RawModel
import net.adventuregame.models.TexturedModel
import net.adventuregame.toolbox.MousePicker
import net.adventuregame.toolbox.Settings
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.function.Function
import kotlin.math.cos
import kotlin.math.sin

class Player(
    position: Vector3f, rotX: Float, rotY: Float, rotZ: Float,
    scale: Float
) : Mob(playerTMod, 0, position, Vector3f(rotX, rotY, rotZ), scale, 20f, false, "Player1", RUN_SPEED) {
    private var window = AdventureMain.window
    private var currentSpeed = 0f
    private var currentTurnSpeed = 0f
    private var upwardsSpeed = 0f
    private var target: Mob? = null

    private var lastSelectTime: Long = 0
    private var isInAir = false

    var bullets = mutableListOf<BulletEntity>()

    var inventory: Inventory = Inventory()

    private var picker: MousePicker?

    override fun move(terrain: Terrain?) {
        checkInputs()
        super.increaseRotation(0f, currentTurnSpeed * WindowManager.frameTimeSeconds, 0f)
        val distance: Float = currentSpeed * WindowManager.frameTimeSeconds
        val dx = (distance * sin(Math.toRadians(super.rotY.toDouble()))).toFloat()
        val dz = (distance * cos(Math.toRadians(super.rotY.toDouble()))).toFloat()
        super.increasePosition(dx, 0f, dz)
        upwardsSpeed += GRAVITY * WindowManager.frameTimeSeconds
        super.increasePosition(0f, upwardsSpeed * WindowManager.frameTimeSeconds, 0f)
        val terrainHeight = terrain!!.getHeightOfTerrain(super.position.x, super.position.z)
        if (super.position.y < terrainHeight) {
            upwardsSpeed = 0f
            isInAir = false
            super.position.y = terrainHeight
        }
    }

    private fun jump() {
        if (!isInAir) {
            this.upwardsSpeed = JUMP_POWER
            isInAir = true
        }
    }

    private fun checkInputs() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSelectTime > SELECT_COOLDOWN_MS) {
            currentSpeed = when {
                window.isKeyPressed(Settings.WALK_FORWARD_KEY) && window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) -> RUN_SPEED * 5
                window.isKeyPressed(Settings.WALK_FORWARD_KEY) -> RUN_SPEED
                window.isKeyPressed(Settings.WALK_BACKWARDS_KEY) -> -RUN_SPEED
                else -> 0f
            }

            if (window.isKeyPressed(Settings.RIGHT_TURN_KEY_1) || window.isKeyPressed(Settings.RIGHT_TURN_KEY_2)) {
                this.currentTurnSpeed = -TURN_SPEED
            } else if (window.isKeyPressed(Settings.LEFT_TURN_KEY_1) || window.isKeyPressed(Settings.LEFT_TURN_KEY_2)) {
                this.currentTurnSpeed = TURN_SPEED
            } else {
                this.currentTurnSpeed = 0f
            }

            if (window.isKeyPressed(GLFW.GLFW_KEY_K)) {
                attack(target)
            }

            if (window.isKeyPressed(Settings.JUMP_KEY)) {
                jump()
            }

            if (window.isButtonPressed(GLFW.GLFW_MOUSE_BUTTON_1)) {
                if (this.inventory.hasItem(GunItem::class.java)) {
                    val gunItem = this.inventory.getItem(GunItem::class.java)
                    if (gunItem!!.canFire() && this.inventory.selectedItem === gunItem) {
                        log.info("Firing")
                        gunItem.fire()
                        shootBullet()
                    }
                }
            }

            if (window.isKeyPressed(GLFW.GLFW_KEY_Q)) {
                inventory.selectPrevious()
                log.info("🔁 Selected Previous Item: {}", inventory.getSelectedItemName())
                lastSelectTime = currentTime
            }

            if (window.isKeyPressed(GLFW.GLFW_KEY_E)) {
                inventory.selectNext()
                log.info("🔁 Selected Next Item: {}", inventory.getSelectedItemName())
                lastSelectTime = currentTime
            }

            if (window.isKeyPressed(GLFW.GLFW_KEY_F6)) window.toggleMouseLock()
            if (window.isKeyPressed(GLFW.GLFW_KEY_F5)) Objects.requireNonNull<Camera?>(getInstance().camera)
                .toggleCameraMode()
        }
    }

    fun shootBullet() {
        val bulletPos = Vector3f(position).add(0f, 1.5f, 0f) // Adjust for eye level
        val direction = this.direction // Implement this to get the direction player is facing

        val bullet = BulletEntity(bulletPos, direction)
        addEntity(bullet)
    }


    override fun TakeDamage(damage: Float, Attacker: Mob?) {
        this.health -= damage
        log.info("{} took {} damage from {}!", name, damage, Attacker?.name)

        if (this.health <= 0) {
            Die()
        }
    }


    override fun Die() {
        log.info("{} has died! Respawning...", name)
        this.health = 20f // Reset health
        position.set(0f, 5f, 0f) // Respawn position
    }


    override fun attack(target: Mob?) {
        var target = target
        if (target == null) {
            log.info("Target is null, finding an enemy")
            target = GameState.target
            this.target = target
            if (target == null) return  // No enemy nearby
        }

        if (position.distance(target.position) < 2.0f) {
            target.TakeDamage(10f, this)
            log.info("{} attacked {}!", name, target.name)
        }
    }

    val direction: Vector3f
        get() {
            val yaw = Math.toRadians(rotY.toDouble()).toFloat()
            val pitch = Math.toRadians(rotX.toDouble()).toFloat()

            val x = (-sin(yaw.toDouble()) * cos(pitch.toDouble())).toFloat()
            val y = (-sin(pitch.toDouble())).toFloat()
            val z = (cos(yaw.toDouble()) * cos(pitch.toDouble())).toFloat()

            val direction = Vector3f(x, y, z)
            direction.normalize()
            return direction
        }

    fun initAfterDecode() {
        this.picker = GameState.picker
        this.window = AdventureMain.window
        this.model = playerTMod
        // Anything else that gets set outside constructor
    }


    fun hasItemByName(itemName: String?): Boolean {
        return inventory.allItems.stream()
            .filter { obj: Item? -> Objects.nonNull(obj) }
            .anyMatch { item: Item? -> item!!.name.equals(itemName, ignoreCase = true) }
    }

    init {
        picker = GameState.picker
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(Player::class.java)
        private const val RUN_SPEED = 20f
        private const val TURN_SPEED = 160f
        val GRAVITY: Float = -90f
        private const val JUMP_POWER = 30f

        private const val SELECT_COOLDOWN_MS: Long = 200


        private val loader = Loader()
        private val playerMod: RawModel? = OBJFileLoader.loadOBJ("person", loader)
        private val playerTMod = TexturedModel(playerMod, ModelTexture(loader.loadGameTexture("playerTexture")))

        fun fromCodec(position: Vector3f?, rotation: Vector3f, scale: Float, inventory: Inventory): Player {
            val p = Player(position!!, rotation.x, rotation.y, rotation.z, scale)
            p.inventory = inventory
            p.initAfterDecode() // reconnect dependencies
            return p
        }

        val CODEC =
            RecordCodecBuilder.create<Player?>(Function { instance: RecordCodecBuilder.Instance<Player?>? ->
                instance!!.group<Vector3f?, Vector3f?, Float?, Inventory>(
                    Vector3fCodec.CODEC.fieldOf("position")
                        .forGetter<Player?> { obj: Player? -> obj!!.position },
                    Vector3fCodec.CODEC.fieldOf("rotation").forGetter<Player?> { i: Player? ->
                        Vector3f(
                            i!!.rotX,
                            i.rotY,
                            i.rotZ
                        )
                    },
                    Codec.FLOAT.fieldOf("scale").forGetter<Player?>(Function { i: Player? -> i!!.scale }),
                    Inventory.Companion.CODEC.fieldOf("inventory")
                        .forGetter<Player?> { i: Player? -> i!!.inventory }
                ).apply<Player?>(
                    instance
                ) { position: Vector3f?, rotation: Vector3f?, scale: Float?, inventory: Inventory? ->
                    fromCodec(
                        position,
                        rotation!!,
                        scale!!,
                        inventory!!
                    )
                }
            })
    }
}
