package net.adventuregame.resources;

public record ResourceLocation(String namespace, String path) {

    public ResourceLocation {
        if (!namespace.matches("[a-z0-9_.-]+") || !path.matches("[a-z0-9_/.-]+")) {
            throw new IllegalArgumentException("Invalid ResourceLocation: " + namespace + ":" + path);
        }
    }

    public static ResourceLocation withDefaultNamespace(String path) {
        return new ResourceLocation("adventuregame", path);
    }

    public static ResourceLocation fromString(String id) {
        String[] parts = id.split(":", 2);
        if (parts.length == 1) return new ResourceLocation("adventuregame", parts[0]);
        return new ResourceLocation(parts[0], parts[1]);
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }
}

