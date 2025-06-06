package net.adventuregame.items;

import net.adventuregame.entities.Entity;
import net.adventuregame.items.entities.ItemEntity;
import net.adventuregame.models.TexturedModel;
import org.joml.Vector3f;

public class Gold extends Item {
    public Gold() {
        super("Gold");
    }

    @Override
    public ItemEntity createEntity(Vector3f position) {
        return null;
    }
}
