package net.kyori.adventure.platform.forge.impl.server.mapper;

import net.kyori.adventure.text.format.TextColor;

public class ColorMapper {

    public static net.minecraft.network.chat.TextColor toNative(TextColor color) {
        return net.minecraft.network.chat.TextColor.fromRgb(color.value());
    }

    public static TextColor toAdventure(net.minecraft.network.chat.TextColor color) {
        return TextColor.color(color.getValue());
    }

}
