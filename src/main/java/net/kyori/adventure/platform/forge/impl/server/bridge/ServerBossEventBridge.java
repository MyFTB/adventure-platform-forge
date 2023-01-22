package net.kyori.adventure.platform.forge.impl.server.bridge;

import java.util.Collection;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;

/**
 * An interface for performing bulk adds and removes on a {@link net.minecraft.server.level.ServerBossEvent}.
 */
public class ServerBossEventBridge {

    private final ServerBossEvent event;

    public ServerBossEventBridge(ServerBossEvent event) {
        this.event = event;
    }

    public void addAll(Collection<ServerPlayer> players){
        final ClientboundBossEventPacket pkt = ClientboundBossEventPacket.createAddPacket(this.event);
        for (final ServerPlayer player : players) {
            this.event.addPlayer(player);
        }
    }

    public void removeAll(Collection<ServerPlayer> players){
        for (final ServerPlayer player : players) {
            this.event.removePlayer(player);
        }
    }

    public void replaceSubscriber(ServerPlayer oldSub, ServerPlayer newSub){
        this.event.removePlayer(oldSub);
        this.event.addPlayer(newSub);
    }
}
