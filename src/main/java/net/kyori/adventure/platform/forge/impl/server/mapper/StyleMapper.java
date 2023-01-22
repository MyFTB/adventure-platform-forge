package net.kyori.adventure.platform.forge.impl.server.mapper;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.network.chat.Style;

public class StyleMapper {
    private static Boolean toNullableBoolean(TextDecoration.State state) {
        switch (state) {
            case TRUE:
                return Boolean.TRUE;
            case FALSE:
                return Boolean.FALSE;
            default:
                return null; // Implied case NOT_SET
        }
    }

    public static Style toNative(final net.kyori.adventure.text.format.Style style) {
        Style nativeStyle = Style.EMPTY;
        if (style == null) {
            return nativeStyle;
        }
        nativeStyle = nativeStyle.withBold(toNullableBoolean(style.decoration(TextDecoration.BOLD)))
            .withItalic(toNullableBoolean(style.decoration(TextDecoration.ITALIC)))
            .withObfuscated(toNullableBoolean(style.decoration(TextDecoration.OBFUSCATED)))
            .withStrikethrough(toNullableBoolean(style.decoration(TextDecoration.STRIKETHROUGH)))
            .withUnderlined(toNullableBoolean(style.decoration(TextDecoration.UNDERLINED)))
            .withInsertion(style.insertion());

        TextColor color = style.color();
        if (color != null) {
            nativeStyle = nativeStyle.withColor(ColorMapper.toNative(color));
        }
        Key fontId = style.font();
        if (fontId != null) {
            nativeStyle = nativeStyle.withFont(KeyMapper.toNative(fontId));
        }
        ClickEvent clickEvent = style.clickEvent();
        if (clickEvent != null) {
            nativeStyle = nativeStyle.withClickEvent(ClickEventMapper.toNative(clickEvent));
        }
        HoverEvent<?> hoverEvent = style.hoverEvent();
        if (hoverEvent != null) {
            nativeStyle = nativeStyle.withHoverEvent(HoverEventMapper.toNative(hoverEvent));
        }

        return nativeStyle;
    }
}
