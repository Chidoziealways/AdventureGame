package net.adventuregame.player;

import com.chidozie.core.renderEngine.Loader;
import com.chidozie.core.renderEngine.OBJFileLoader;
import com.chidozie.core.renderEngine.WindowManager;
import com.chidozie.core.terrains.Terrain;
import com.chidozie.core.textures.ModelTexture;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.adventuregame.codec.Vector3fCodec;
import net.adventuregame.entities.Entity;
import net.adventuregame.game.AdventureMain;
import net.adventuregame.game.GameState;
import net.adventuregame.items.GunItem;
import net.adventuregame.items.entities.BulletEntity;
import net.adventuregame.mobs.Mob;
import net.adventuregame.models.RawModel;
import net.adventuregame.models.TexturedModel;

import static net.adventuregame.toolbox.Settings.*;
import static org.lwjgl.glfw.GLFW.*;

import net.adventuregame.toolbox.MousePicker;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player extends Mob {

    private static final Logger log = LoggerFactory.getLogger(Player.class);
    private WindowManager window = AdventureMain.getWindow();
    private static final float RUN_SPEED = 20;
    private static final float TURN_SPEED = 160;
    public static final float GRAVITY = -90;
    private static final float JUMP_POWER = 30;

    private float currentSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardsSpeed = 0;
    private Mob target;

    private long lastSelectTime = 0;
    private static final long SELECT_COOLDOWN_MS = 200;


    private boolean isInAir = false;

    private List<BulletEntity> bullets = new ArrayList<>();

    private Inventory inventory = new Inventory();

    private static final Loader loader = new Loader();
    private static final RawModel playerMod = OBJFileLoader.loadOBJ("person", loader);
    private static final TexturedModel playerTMod = new TexturedModel(playerMod, new ModelTexture(loader.loadGameTexture("playerTexture")));

    private MousePicker picker;

    public Player(Vector3f position, float rotX, float rotY, float rotZ,
                  float scale) {
        super(playerTMod, 0, position, new Vector3f(rotX, rotY, rotZ), scale, 20f, false, "Player1", RUN_SPEED);
        picker = GameState.picker;
    }

    @Override
    public void move(Terrain terrain) {
        checkInputs();
        super.increaseRotation(0, currentTurnSpeed * WindowManager.getFrameTimeSeconds(), 0);
        float distance = currentSpeed * WindowManager.getFrameTimeSeconds();
        float dx = (float) (distance * Math.sin(Math.toRadians(super.getRotY())));
        float dz = (float) (distance * Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx, 0, dz);
        upwardsSpeed += GRAVITY * WindowManager.getFrameTimeSeconds();
        super.increasePosition(0, upwardsSpeed * WindowManager.getFrameTimeSeconds(), 0);
        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if (super.getPosition().y < terrainHeight) {
            upwardsSpeed = 0;
            isInAir = false;
            super.getPosition().y = terrainHeight;
        }
    }

    private void jump() {
        if (!isInAir) {
            this.upwardsSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    private void checkInputs() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSelectTime > SELECT_COOLDOWN_MS) {
            if (window.isKeyPressed(WALK_FORWARD_KEY)) {
                this.currentSpeed = RUN_SPEED;
                if (window.isKeyPressed(GLFW_KEY_W) && window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
                    this.currentSpeed = RUN_SPEED * 5;
                }
            } else if (window.isKeyPressed(WALK_BACKWARDS_KEY)) {
                this.currentSpeed = -RUN_SPEED;
            }else {
                this.currentSpeed = 0;
            }

            if (window.isKeyPressed(RIGHT_TURN_KEY_1) || window.isKeyPressed(RIGHT_TURN_KEY_2)) {
                this.currentTurnSpeed = -TURN_SPEED;
            } else if (window.isKeyPressed(LEFT_TURN_KEY_1) || window.isKeyPressed(LEFT_TURN_KEY_2)) {
                this.currentTurnSpeed = TURN_SPEED;
            }else {
                this.currentTurnSpeed = 0;
            }

            if (window.isKeyPressed(GLFW_KEY_K)) {
                attack(target);
            }

            if (window.isKeyPressed(JUMP_KEY)) {
                jump();
            }

            if (window.isButtonPressed(GLFW_MOUSE_BUTTON_1)) {
                if (getInventory().hasItem(GunItem.class)) {
                    GunItem gunItem = getInventory().getItem(GunItem.class);
                    if (gunItem.canFire() && getInventory().getSelectedItem() == gunItem) {
                        log.info("Firing");
                        gunItem.fire();
                        shootBullet();
                    }
                }
            }

            if (window.isKeyPressed(GLFW_KEY_Q)) {
                inventory.selectPrevious();
                log.info("🔁 Selected Previous Item: {}", inventory.getSelectedItemName());
                lastSelectTime = currentTime;
            }

            if (window.isKeyPressed(GLFW_KEY_E)) {
                inventory.selectNext();
                log.info("🔁 Selected Next Item: {}", inventory.getSelectedItemName());
                lastSelectTime = currentTime;
            }

            if (window.isKeyPressed(GLFW_KEY_F6)) window.toggleMouseLock();
            if (window.isKeyPressed(GLFW_KEY_F5)) Objects.requireNonNull(GameState.getInstance().getCamera()).toggleCameraMode();
        }
    }

    public void shootBullet() {
        Vector3f bulletPos = new Vector3f(position).add(0, 1.5f, 0); // Adjust for eye level
        Vector3f direction = this.getDirection(); // Implement this to get the direction player is facing

        BulletEntity bullet = new BulletEntity(bulletPos, direction);
        GameState.addEntity(bullet);
    }


    @Override
    public void TakeDamage(float damage, Mob attacker) {
        this.health -= damage;
        log.info("{} took {} damage from {}!", name, damage, attacker.getName());

        if (this.health <= 0) {
            Die();
        }
    }


    @Override
    public void Die() {
        log.info("{} has died! Respawning...", name);
        this.health = 20f; // Reset health
        position.set(0, 5, 0); // Respawn position
    }


    @Override
    public void attack(Mob target) {
        if (target == null) {
            log.info("Target is null, finding an enemy");
                target = GameState.getTarget();
                this.target = target;
                if (target == null) return; // No enemy nearby
        }

        if (position.distance(target.getPosition()) < 2.0f) {
            target.TakeDamage(10f, this);
            log.info("{} attacked {}!", name, target.getName());
        }
    }

    public Vector3f getDirection() {
        float yaw = (float) Math.toRadians(getRotY());
        float pitch = (float) Math.toRadians(getRotX());

        float x = (float) (-Math.sin(yaw) * Math.cos(pitch));
        float y = (float) (-Math.sin(pitch));
        float z = (float) (Math.cos(yaw) * Math.cos(pitch));

        Vector3f direction = new Vector3f(x, y, z);
        direction.normalize();
        return direction;
    }

    public static Player fromCodec(Vector3f position, Vector3f rotation, float scale, Inventory inventory) {
        Player p = new Player(position, rotation.x, rotation.y, rotation.z, scale);
        p.setInventory(inventory);
        p.initAfterDecode(); // reconnect dependencies
        return p;
    }

    public void initAfterDecode() {
        this.picker = GameState.picker;
        this.window = AdventureMain.getWindow();
        this.setModel(playerTMod);
        // Anything else that gets set outside constructor
    }



    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory loadedInventory) {
        this.inventory = loadedInventory;
    }

    public boolean hasItemByName(String itemName) {
        return inventory.getAllItems().stream()
                .filter(Objects::nonNull)
                .anyMatch(item -> item.getName().equalsIgnoreCase(itemName));
    }

    public static final Codec<Player> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Vector3fCodec.CODEC.fieldOf("position").forGetter(Player::getPosition),
            Vector3fCodec.CODEC.fieldOf("rotation").forGetter(i -> new Vector3f(i.getRotX(), i.getRotY(), i.getRotZ())),
            Codec.FLOAT.fieldOf("scale").forGetter(i -> i.getScale()),
            Inventory.Companion.getCODEC().fieldOf("inventory").forGetter(i -> i.getInventory())
    ).apply(instance, (position, rotation, scale, inventory) -> Player.fromCodec(position, rotation, scale, inventory)));

    public List<BulletEntity> getBullets() {
        return bullets;
    }

    public void setBullets(List<BulletEntity> bullets) {
        this.bullets = bullets;
    }
}
