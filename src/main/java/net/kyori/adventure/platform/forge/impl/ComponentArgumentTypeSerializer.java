package net.kyori.adventure.platform.forge.impl;

import com.google.gson.JsonObject;
import net.kyori.adventure.platform.forge.ComponentArgumentType;
import net.kyori.adventure.platform.forge.ForgeAudiences;
import net.kyori.adventure.platform.forge.impl.bridge.ResourceLocationKeyBridge;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public final class ComponentArgumentTypeSerializer implements ArgumentSerializer<ComponentArgumentType> {
    @Override
    public void serializeToNetwork(final ComponentArgumentType type, final FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(ForgeAudiences.toNative(type.format().id()));
    }

    @Override
    public ComponentArgumentType deserializeFromNetwork(final FriendlyByteBuf buffer) {
        final ResourceLocation id = buffer.readResourceLocation();
        final ComponentArgumentType.Format format = ComponentArgumentType.Format.INDEX.value(new ResourceLocationKeyBridge(id));
        if (format == null) {
            throw new IllegalArgumentException("Unknown Adventure component format: " + id);
        }
        return ComponentArgumentType.component(format);
    }

    @Override
    public void serializeToJson(final ComponentArgumentType type, final JsonObject json) {
        json.addProperty("serializer", type.format().id().asString());
    }
}
