package net.kyori.adventure.platform.forge.impl.mixin;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.forge.impl.bridge.ResourceLocationKeyBridge;
import net.minecraft.ResourceLocationException;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = Key.class, remap = false)
public interface KeyMixin {
    /**
     * Creates a key.
     *
     * @param namespace the namespace
     * @param value the value
     * @return the key
     * @throws RuntimeException the namespace or value contains an invalid character
     * @reason implemented by mixin to the Vanilla class
     * @author MojangPlsFix,zml2008
     */
    @Overwrite
    static @NotNull Key key(final String namespace, final String value) {
        try {
            return new ResourceLocationKeyBridge(namespace, value);
        } catch (final ResourceLocationException ex) {
            throw new RuntimeException(namespace + value + ex.getMessage());
        }
    }
}
