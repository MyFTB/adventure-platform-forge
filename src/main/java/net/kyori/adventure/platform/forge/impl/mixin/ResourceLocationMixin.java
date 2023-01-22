package net.kyori.adventure.platform.forge.impl.mixin;

import net.kyori.adventure.key.Key;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ResourceLocation.class)
@Implements(@Interface(iface = Key.class, prefix = "key$"))
public abstract class ResourceLocationMixin {
    // @formatter:off
    @Shadow
    public abstract String shadow$getNamespace();
    @Shadow public abstract String shadow$getPath();
    // @formatter:on

    public @NotNull String key$namespace() {
        return this.shadow$getNamespace();
    }

    public @NotNull String key$value() {
        return this.shadow$getPath();
    }

    public @NotNull String key$asString() {
        return this.toString();
    }
}
