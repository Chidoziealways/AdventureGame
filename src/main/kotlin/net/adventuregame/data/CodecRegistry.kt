package net.adventuregame.data

import com.google.gson.JsonElement
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.JsonOps
import net.adventuregame.game.GameStateSerializable
import net.adventuregame.items.GunItem
import net.adventuregame.player.Inventory
import net.adventuregame.player.Inventory.Companion.CODEC
import net.adventuregame.player.Player
import net.adventuregame.story.StoryManager
import java.util.function.Supplier

object CodecRegistry {
    val all: MutableMap<String?, Codec<*>?> = HashMap<String?, Codec<*>?>()

    // Call this once during init
    fun registerAll() {
        register<GunItem?>("gun_item", GunItem.Companion.CODEC)
        register<Player?>("player", Player.Companion.CODEC)
        register<StoryManager?>("story", StoryManager.Companion.CODEC)
        register("inventory", CODEC as Codec<Inventory?>?)
        register<Int?>("seed", Codec.INT)
        register("game",
            GameStateSerializable.GAME_STATE_CODEC as Codec<GameStateSerializable?>?
        )
        //register("bullet_entity", BulletEntity.CODEC);
        // Add other entities here
    }

    fun <T> register(id: String?, codec: Codec<T?>?) {
        all.put(id, codec)
    }

    fun get(id: String?): Codec<*>? {
        return all[id]
    }

    // Saves an object with its type id
    fun <T> encodeWithType(id: String?, `object`: T?): JsonElement? {
        val codec = all[id] as Codec<T?>
        requireNotNull(codec) { "No codec for type: $id" }

        // directly use codec.encodeStart
        return codec
            .encodeStart(JsonOps.INSTANCE, `object`)
            .result()
            .orElseThrow(Supplier { IllegalStateException("Encoding failed for type: $id") })
    }


    fun decodeWithType(id: String?, json: JsonElement?): Any {
        val codec: Codec<*> = all[id]!!
        requireNotNull(codec) { "No codec for type: $id" }

        return codec.parse(JsonOps.INSTANCE, json).result().orElseThrow()
    }

    // Full dump to JSON
    fun encodeWorld(data: MutableMap<String?, Any?>): JsonElement? {
        val ops = JsonOps.INSTANCE
        val encoded: MutableMap<String?, JsonElement?> = HashMap<String?, JsonElement?>()

        for (entry in data.entries) {
            encoded.put(entry.key, encodeWithType<Any?>(entry.key, entry.value))
        }

        // Build a Stream<Pair<JsonElement, JsonElement>>:
        return ops.createMap(
            encoded.entries.stream()
                .map<Pair<JsonElement?, JsonElement?>?> { e: MutableMap.MutableEntry<String?, JsonElement?>? ->
                    Pair.of<JsonElement?, JsonElement?>(
                        ops.createString(e!!.key),
                        e.value
                    )
                }
        )
    }


    fun decodeWorld(root: JsonElement): MutableMap<String?, Any?> {
        val result: MutableMap<String?, Any?> = HashMap()
        val obj = root.getAsJsonObject()

        for (entry in obj.entrySet()) {
            result.put(entry.key, decodeWithType(entry.key, entry.value))
        }

        return result
    }
}
