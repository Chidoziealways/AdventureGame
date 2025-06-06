package net.adventuregame.registries;

import net.adventuregame.items.Item;

public class ItemRegistry {
    public static final Registry<Item> REGISTRY = new Registry<>();

    public static void register(String name, Item item) {
        REGISTRY.register(name, item);
    }

    public static Item get(String name) {
        return REGISTRY.get(name);
    }
}


