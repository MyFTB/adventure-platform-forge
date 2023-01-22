package net.kyori.adventure.platform.forge.impl.server;

import io.netty.util.AttributeKey;
import net.kyori.adventure.pointer.Pointered;
import org.jetbrains.annotations.Nullable;

public interface FriendlyByteBufBridge {
    AttributeKey<Pointered> CHANNEL_RENDER_DATA = AttributeKey.newInstance("adventure:render_data");

    void adventure$data(final @Nullable Pointered data);
}
