package net.adventuregame.items;

import net.adventuregame.entities.Entity;
import net.adventuregame.items.entities.ItemEntity;
import net.adventuregame.models.TexturedModel;
import org.joml.Vector3f;

public abstract class Item{
    private String name;

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract ItemEntity createEntity(Vector3f position);

    public abstract TexturedModel getGuiModel();
}
