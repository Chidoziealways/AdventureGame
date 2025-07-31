package net.adventuregame.resources

@JvmRecord
data class ResourceLocation(val namespace: String?, val path: String?) {
    override fun toString(): String {
        return "$namespace:$path"
    }

    init {
        require(!(!namespace!!.matches("[a-z0-9_.-]+".toRegex()) || !path!!.matches("[a-z0-9_/.-]+".toRegex()))) { "Invalid ResourceLocation: " + namespace + ":" + path }
    }

    companion object {
        fun withDefaultNamespace(path: String?): ResourceLocation {
            return ResourceLocation("adventuregame", path)
        }

        fun fromString(id: String): ResourceLocation {
            val parts: Array<String?> = id.split(":".toRegex(), limit = 2).toTypedArray()
            if (parts.size == 1) return ResourceLocation("adventuregame", parts[0])
            return ResourceLocation(parts[0], parts[1])
        }
    }
}

