package net.adventuregame.items;

import com.chidozie.core.renderEngine.Loader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.adventuregame.codec.Vector3fCodec;
import net.adventuregame.entities.Entity;
import net.adventuregame.items.entities.GunEntity;
import net.adventuregame.items.entities.ItemEntity;
import net.adventuregame.models.TexturedModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.joml.Vector3f;

public class GunItem extends Item {
    private static final Logger log = LogManager.getLogger(GunItem.class);
    private static final Marker mark = MarkerManager.getMarker("GUNITEM");
    private int ammo;
    private int maxAmmo;
    private float cooldown;
    public static final Loader loader = new Loader();
    private final TexturedModel model;

    public static final Codec<GunItem> CODEC = RecordCodecBuilder.create(gunItemInstance -> gunItemInstance.group(
            Codec.INT.fieldOf("ammo").forGetter(i -> i.ammo),
            Codec.INT.fieldOf("maxAmmo").forGetter(i -> i.maxAmmo),
            Codec.FLOAT.fieldOf("cooldown").forGetter(i -> i.cooldown),
            Codec.STRING.fieldOf("name").forGetter(i -> i.getName())
    ).apply(gunItemInstance, (ammo, maxAmmo, cooldown, name) -> new GunItem(name,null, maxAmmo, cooldown)));

    public GunItem(String name, TexturedModel model, int maxAmmo, float cooldown) {
        super(name);
        this.maxAmmo = maxAmmo;
        this.ammo = maxAmmo;
        this.cooldown = cooldown;
        this.model = model;
    }

    public boolean canFire() {
        return ammo > 0 && cooldown <= 0;
    }

    public void fire() {
        if (canFire()) {
            ammo--;
            log.info(mark, "Fired Gun");
            cooldown = 1.0f; // example cooldown duration
            // Trigger shooting effects here or in GunEntity
        }
    }

    public void reload() {
        ammo = maxAmmo;
    }

    public int getAmmo() {
        return ammo;
    }

    public float getCooldown() {
        return cooldown;
    }

    public void updateCooldown(float delta) {
        if (cooldown > 0) cooldown -= delta;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public ItemEntity createEntity(Vector3f position) {
        return new GunEntity(model, 0, position, 0, this);
    }
}
