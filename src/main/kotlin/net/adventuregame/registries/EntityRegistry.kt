package net.adventuregame.registries

import net.adventuregame.entity.Entity

object EntityRegistry {
    val REGISTRY: Registry<Entity> = Registry()

    fun register(name: String, entity: Entity) {
        REGISTRY.register(name, entity)
    }

    fun get(name: String): Entity {
        return REGISTRY.get(name)
    }
}