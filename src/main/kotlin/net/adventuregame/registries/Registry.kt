package net.adventuregame.registries

import net.adventuregame.resources.ResourceLocation

class Registry<T> {
    private val entries: MutableMap<ResourceLocation, T> = HashMap()

    fun register(id: ResourceLocation, entry: T) {
        check(!entries.containsKey(id)) { "Duplicate: $id" }
        entries.put(id, entry)
    }

    fun register(id: String, entry: T) {
        register(ResourceLocation.Companion.withDefaultNamespace(id), entry)
    }

    fun get(id: ResourceLocation): T {
        return entries[id]!!
    }

    fun get(id: String): T {
        return get(ResourceLocation.Companion.fromString(id))
    }

    val all: MutableCollection<T>
        get() = entries.values
}
