package net.adventuregame.registries;

import net.adventuregame.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Registry<T> {
    private final Map<ResourceLocation, T> entries = new HashMap<>();

    public void register(ResourceLocation id, T entry) {
        if (entries.containsKey(id)) throw new IllegalStateException("Duplicate: " + id);
        entries.put(id, entry);
    }

    public void register(String id, T entry) {
        register(ResourceLocation.withDefaultNamespace(id), entry);
    }

    public T get(ResourceLocation id) {
        return entries.get(id);
    }

    public T get(String id) {
        return get(ResourceLocation.fromString(id));
    }

    public Collection<T> getAll() {
        return entries.values();
    }
}
