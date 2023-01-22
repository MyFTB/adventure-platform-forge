package net.kyori.adventure.platform.forge.impl.bridge;

import net.kyori.adventure.key.Key;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ResourceLocationKeyBridge(ResourceLocation location) implements Key {

    @Override
    public @NotNull String namespace() {
        return location.getNamespace();
    }

    @Override
    public @NotNull String value() {
        return location.getPath();
    }

    @Override
    public @NotNull String asString() {
        return location.toString();
    }

    @Override
    public @NotNull ResourceLocation location() {
        return location;
    }
}
