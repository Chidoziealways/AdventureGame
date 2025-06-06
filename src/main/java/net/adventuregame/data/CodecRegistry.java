package net.adventuregame.data;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.google.gson.JsonElement;
import net.adventuregame.game.GameState;
import net.adventuregame.game.GameStateSerializable;
import net.adventuregame.items.GunItem;
import net.adventuregame.player.Inventory;
import net.adventuregame.player.Player;
import net.adventuregame.story.StoryManager;

import java.util.*;

public class CodecRegistry {

    private static final Map<String, Codec<?>> registry = new HashMap<>();

    // Call this once during init
    public static void registerAll() {
        register("gun_item", GunItem.CODEC);
        register("player", Player.CODEC);
        register("story", StoryManager.CODEC);
        register("inventory", Inventory.Companion.getCODEC());
        register("seed", Codec.INT);
        register("game", GameStateSerializable.GAME_STATE_CODEC);
        //register("bullet_entity", BulletEntity.CODEC);
        // Add other entities here
    }

    public static <T> void register(String id, Codec<T> codec) {
        registry.put(id, codec);
    }

    public static Codec<?> get(String id) {
        return registry.get(id);
    }

    public static Map<String, Codec<?>> getAll() {
        return registry;
    }

    // Saves an object with its type id
    public static <T> JsonElement encodeWithType(String id, T object) {
        @SuppressWarnings("unchecked")
        Codec<T> codec = (Codec<T>) registry.get(id);
        if (codec == null) throw new IllegalArgumentException("No codec for type: " + id);

        // directly use codec.encodeStart
        return codec
                .encodeStart(JsonOps.INSTANCE, object)
                .result()
                .orElseThrow(() -> new IllegalStateException("Encoding failed for type: " + id));
    }


    public static Object decodeWithType(String id, JsonElement json) {
        Codec<?> codec = registry.get(id);
        if (codec == null) throw new IllegalArgumentException("No codec for type: " + id);

        return codec.parse(JsonOps.INSTANCE, json).result().orElseThrow();
    }

    // Full dump to JSON

    public static JsonElement encodeWorld(Map<String, Object> data) {
        JsonOps ops = JsonOps.INSTANCE;
        Map<String, JsonElement> encoded = new HashMap<>();

        for (var entry : data.entrySet()) {
            encoded.put(entry.getKey(), encodeWithType(entry.getKey(), entry.getValue()));
        }

        // Build a Stream<Pair<JsonElement, JsonElement>>:
        return ops.createMap(
                encoded.entrySet().stream()
                        .map(e -> Pair.of(
                                ops.createString(e.getKey()),
                                e.getValue()
                        ))
        );
    }


    public static Map<String, Object> decodeWorld(JsonElement root) {
        Map<String, Object> result = new HashMap<>();
        var obj = root.getAsJsonObject();

        for (var entry : obj.entrySet()) {
            result.put(entry.getKey(), decodeWithType(entry.getKey(), entry.getValue()));
        }

        return result;
    }

}
