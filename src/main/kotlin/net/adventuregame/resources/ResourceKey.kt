package net.adventuregame.resources

class ResourceKey<T>(private val location: ResourceLocation) {
    fun location(): ResourceLocation {
        return location
    }

    override fun toString(): String {
        return location.toString()
    }

    companion object {
        fun <T> create(key: String?): ResourceKey<T?> {
            return ResourceKey<T?>(ResourceLocation.Companion.withDefaultNamespace(key))
        }
    }
}

