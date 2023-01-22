package net.kyori.adventure.platform.forge;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.forge.impl.bridge.ResourceLocationKeyBridge;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * An argument that will be decoded as a Key.
 *
 * @since 4.0.0
 */
public final class KeyArgumentType implements ArgumentType<Key> {
    private static final KeyArgumentType INSTANCE = new KeyArgumentType();

    /**
     * Get an argument type instance for {@link Key}s.
     *
     * @return key argument type
     * @since 4.0.0
     */
    public static @NotNull KeyArgumentType key() {
        return INSTANCE;
    }

    /**
     * Get a {@link Key}-typed value from a parsed {@link CommandContext}.
     *
     * @param ctx context to get the value from
     * @param id  id the argument was taken from
     * @return provided argument
     * @since 4.0.0
     */
    public static @NotNull Key key(final @NotNull CommandContext<?> ctx, final @NotNull String id) {
        return ctx.getArgument(id, Key.class);
    }

    private KeyArgumentType() {
    }

    @Override
    public @NotNull Key parse(final @NotNull StringReader reader) throws CommandSyntaxException {
        return new ResourceLocationKeyBridge(ResourceLocation.read(reader));
    }
}
