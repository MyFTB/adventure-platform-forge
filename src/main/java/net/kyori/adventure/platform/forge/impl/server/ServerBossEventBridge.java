package net.kyori.adventure.platform.forge.impl.server;

import java.util.Collection;
import net.minecraft.server.level.ServerPlayer;

/**
 * An interface for performing bulk adds and removes on a {@link net.minecraft.server.level.ServerBossEvent}.
 */
public interface ServerBossEventBridge {
    void adventure$addAll(Collection<ServerPlayer> players);

    void adventure$removeAll(Collection<ServerPlayer> players);

    void adventure$replaceSubscriber(ServerPlayer oldSub, ServerPlayer newSub);
}
