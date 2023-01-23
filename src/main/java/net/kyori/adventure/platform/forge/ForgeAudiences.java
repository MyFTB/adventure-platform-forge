package net.kyori.adventure.platform.forge;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.forge.impl.NonWrappingComponentSerializer;
import net.kyori.adventure.platform.forge.impl.bridge.ResourceLocationKeyBridge;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.renderer.ComponentRenderer;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.util.ComponentMessageThrowable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface ForgeAudiences {
/*
    static net.minecraft.network.chat.Component update(final net.minecraft.network.chat.Component input, final UnaryOperator<Component> modifier){
        final Component modified;
        final @Nullable Function<Pointered, ?> partition;
        final @Nullable ComponentRenderer<Pointered> renderer;
        if(input instanceof WrappedComponent){
            modified = requireNonNull(modifier).apply(((WrappedComponent) input).wrapped());
            partition = ((WrappedComponent) input).partition();
            renderer = ((WrappedComponent) input).renderer();
        }
        else{
            final Component original = net.minecraft.network.chat.Component.Serializer.GSON.fromJson(net.minecraft.network.chat.Component.Serializer.toJsonTree(input), Component.class);
            modified = modifier.apply(original);
            partition = null;
            renderer = null;
        }
        return new WrappedComponent(modified, partition, renderer);
    }
*/

    /**
     * Convert a Kyori {@link Key} instance to a MC ResourceLocation.
     *
     * <p>All {@link Key} instances created in a Fabric environment with this
     * mod are implemented by {@link ResourceLocation}, as long as loader 0.14 is present,
     * so this is effectively a cast.</p>
     *
     * @param key The Key to convert
     * @return The equivalent data as an Identifier
     * @since 4.0.0
     */
    @Contract("null -> null; !null -> !null")
    static ResourceLocation toNative(final Key key) {
        if (key == null) {
            return null;
        }
        if (key instanceof ResourceLocationKeyBridge) {
            return ((ResourceLocationKeyBridge) key).location();
        }

        return new ResourceLocation(key.namespace(), key.value());
    }

    /**
     * Expose a Brigadier CommandSyntaxException's message using the adventure-provided interface for rich-message exceptions.
     *
     * @param ex the exception to cast
     * @return a converted command exception
     * @since 5.3.0
     */
    static @NotNull ComponentMessageThrowable asComponentThrowable(final @NotNull CommandSyntaxException ex) {
        return (ComponentMessageThrowable) ex;
    }

    /**
     * Return a TextSerializer instance that will do deep conversions between
     * Adventure {@link Component Components} and Minecraft {@link net.minecraft.network.chat.Component Components}.
     *
     * <p>This serializer will never wrap text, and can provide {@link net.minecraft.network.chat.MutableComponent}
     * instances suitable for passing around the game.</p>
     *
     * @return a serializer instance
     * @since 4.0.0
     */
    static @NotNull ComponentSerializer<Component, Component, net.minecraft.network.chat.Component> nonWrappingSerializer() {
        return NonWrappingComponentSerializer.INSTANCE;
    }

    /**
     * Get an {@link Identity} representation of a {@link GameProfile}.
     *
     * @param profile the profile to represent
     * @return an identity of the game profile
     * @since 4.0.0
     */
    static @NotNull Identity identity(final @NotNull GameProfile profile) {
        return (Identity) profile;
    }

    /**
     * Return a component flattener that can use game data to resolve extra information about components.
     *
     * @return the flattener
     * @since 4.0.0
     */
    @NotNull ComponentFlattener flattener();

    /**
     * Active renderer to render components.
     *
     * @return Shared renderer
     * @since 4.0.0
     */
    @NotNull ComponentRenderer<Pointered> renderer();

    /**
     * Get a native {@link net.minecraft.network.chat.Component} from an adventure {@link Component}.
     *
     * <p>The specific type of the returned component is undefined. For example, it may be a wrapper object.</p>
     *
     * @param adventure adventure input
     * @return native representation
     * @since 4.0.0
     */
    net.minecraft.network.chat.@NotNull Component toNative(final @NotNull Component adventure);
}
