package net.kyori.adventure.platform.forge.impl.bridge;

import java.util.Objects;
import net.kyori.adventure.key.Key;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public final class ResourceLocationKeyBridge implements Key {
    private final ResourceLocation location;

    public ResourceLocationKeyBridge(ResourceLocation location) {
        this.location = location;
    }

    public ResourceLocationKeyBridge(String namespace,String value) {
        this.location = new ResourceLocation(namespace,value);
    }

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

    public @NotNull ResourceLocation location() {
        return location;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (ResourceLocationKeyBridge) obj;
        return Objects.equals(this.location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }

    @Override
    public String toString() {
        return "ResourceLocationKeyBridge[" +
            "location=" + location + ']';
    }

}
