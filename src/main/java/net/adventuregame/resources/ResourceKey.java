package net.adventuregame.resources;

public class ResourceKey<T> {
    private final ResourceLocation location;

    public ResourceKey(ResourceLocation location) {
        this.location = location;
    }

    public static <T> ResourceKey<T> create(String key) {
        return new ResourceKey<>(ResourceLocation.withDefaultNamespace(key));
    }

    public ResourceLocation location() {
        return location;
    }

    @Override
    public String toString() {
        return location.toString();
    }
}

