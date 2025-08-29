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
        register("game",
            GameStateSerializable.GAME_STATE_CODEC as Codec<GameStateSerializable?>?
        )
        register<GunItem?>("gun_item", GunItem.Companion.CODEC)
        register<Player?>("player", Player.Companion.CODEC)
        register<StoryManager?>("story", StoryManager.Companion.CODEC)
        register("inventory", CODEC as Codec<Inventory?>?)
        register<Int?>("seed", Codec.INT)
        //register("bullet_entity", BulletEntity.CODEC);
        // Add other entities here
    }

    fun <T> register(id: String?, codec: Codec<T?>?) {
        all[id] = codec
    }

    fun get(id: String?): Codec<*>? {
        return all[id]
    }
}
