package net.adventuregame.items.entities;

import net.adventuregame.entities.Entity;
import net.adventuregame.game.GameState;
import net.adventuregame.items.GunItem;
import net.adventuregame.models.TexturedModel;
import net.adventuregame.player.Player;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GunEntity extends ItemEntity {

    private static final Logger log = LoggerFactory.getLogger(GunEntity.class);
    private final GunItem gunItem;

    public GunEntity(TexturedModel model, int index, Vector3f position, float scale, GunItem gunItem) {
        super(model, position, new Vector3f(0, 0, 0),  1, gunItem);
        this.gunItem = gunItem;
    }

    @Override
    public String toString() {
        return gunItem.toString();
    }
}
