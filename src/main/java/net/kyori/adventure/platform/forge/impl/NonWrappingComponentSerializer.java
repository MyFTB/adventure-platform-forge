package net.kyori.adventure.platform.forge.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.network.chat.MutableComponent;

public final class NonWrappingComponentSerializer implements ComponentSerializer<Component, Component, net.minecraft.network.chat.Component> {
    public static final NonWrappingComponentSerializer INSTANCE = new NonWrappingComponentSerializer();

    private final ThreadLocal<Boolean> bypassIsAllowedFromServer = ThreadLocal.withInitial(() -> false);

    private NonWrappingComponentSerializer() {
    }

    public boolean bypassIsAllowedFromServer() {
        return this.bypassIsAllowedFromServer.get();
    }

    @Override
    public Component deserialize(final net.minecraft.network.chat.Component input) {
        if (input instanceof WrappedComponent) {
            return ((WrappedComponent) input).wrapped();
        }
        return net.minecraft.network.chat.Component.Serializer.GSON.fromJson(net.minecraft.network.chat.Component.Serializer.toJsonTree(input), Component.class);
    }

    @Override
    public MutableComponent serialize(final Component component) {
        this.bypassIsAllowedFromServer.set(true);
        final MutableComponent mutableComponent;
        try {
            mutableComponent = net.minecraft.network.chat.Component.Serializer.fromJson(net.minecraft.network.chat.Component.Serializer.GSON.toJsonTree(component));
        } finally {
            this.bypassIsAllowedFromServer.set(false);
        }
        return mutableComponent;
    }

}
