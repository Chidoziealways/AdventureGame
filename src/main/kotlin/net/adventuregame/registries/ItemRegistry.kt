package net.adventuregame.registries

import net.adventuregame.items.Item

object ItemRegistry {
    val REGISTRY: Registry<Item> = Registry()

    fun register(name: String, item: Item) {
        REGISTRY.register(name, item)
    }

    fun get(name: String): Item {
        return REGISTRY.get(name)
    }
}


