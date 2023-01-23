package net.kyori.adventure.platform.forge.impl;

import net.kyori.adventure.platform.forge.impl.server.visitor.ToNativeConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

public final class NonWrappingComponentSerializer implements ComponentSerializer<Component, Component, net.minecraft.network.chat.Component> {
    public static final NonWrappingComponentSerializer INSTANCE = new NonWrappingComponentSerializer();

    private final ThreadLocal<Boolean> bypassIsAllowedFromServer = ThreadLocal.withInitial(() -> false);

    private NonWrappingComponentSerializer() {
    }

    public boolean bypassIsAllowedFromServer() {
        return this.bypassIsAllowedFromServer.get();
    }

    @Override
    public @NotNull Component deserialize(final net.minecraft.network.chat.@NotNull Component input) {
        if (input instanceof WrappedComponent) {
            return ((WrappedComponent) input).wrapped();
        }
        AdventureCommon.LOGGER.info("DESERIALIZER");
        return net.minecraft.network.chat.Component.Serializer.GSON.fromJson(net.minecraft.network.chat.Component.Serializer.toJsonTree(input),
            Component.class);
    }

    @Override
    public MutableComponent serialize(final @NotNull Component component) {
        this.bypassIsAllowedFromServer.set(true);
        final MutableComponent mutableComponent;
        try {
            final ToNativeConverter converter = new ToNativeConverter();
            converter.accept(component);
            mutableComponent =
                net.minecraft.network.chat.Component.Serializer.fromJson(net.minecraft.network.chat.Component.Serializer.GSON.toJsonTree(component));
        } finally {
            this.bypassIsAllowedFromServer.set(false);
        }
        return mutableComponent;
    }

}
