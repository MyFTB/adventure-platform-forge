package net.kyori.adventure.platform.forge.impl.server.mapper;

import net.kyori.adventure.key.Key;
import net.minecraft.resources.ResourceLocation;

public class KeyMapper {
    public static ResourceLocation toNative(Key key) {
        return key == null ? null : new ResourceLocation(key.namespace(), key.value());
    }

    public static Key toAdventure(ResourceLocation id) {
        return Key.key(id.getNamespace(), id.getPath());
    }
}
