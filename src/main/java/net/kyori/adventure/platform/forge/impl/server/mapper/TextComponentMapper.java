package net.kyori.adventure.platform.forge.impl.server.mapper;

import net.kyori.adventure.platform.forge.impl.server.visitor.ToNativeConverter;
import net.kyori.adventure.text.Component;
import net.minecraft.network.chat.MutableComponent;

public class TextComponentMapper {
    public static MutableComponent toNative(Component component) {
        final ToNativeConverter converter = new ToNativeConverter();
        converter.accept(component);
        return converter.getNative();
    }
}
