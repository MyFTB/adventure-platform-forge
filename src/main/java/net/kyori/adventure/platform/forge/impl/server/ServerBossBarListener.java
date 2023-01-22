package net.kyori.adventure.platform.forge.impl.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.platform.forge.ForgeServerAudiences;
import net.kyori.adventure.platform.forge.impl.AbstractBossBarListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;
public class ServerBossBarListener extends AbstractBossBarListener<ServerBossEvent> {
    public ServerBossBarListener(final ForgeServerAudiences controller) {
        super(controller);
    }

    public void subscribe(final ServerPlayer player, final BossBar bar) {
        this.minecraftCreating(requireNonNull(bar, "bar")).addPlayer(requireNonNull(player, "player"));
    }

    public void subscribeAll(final Collection<ServerPlayer> players, final BossBar bar) {
        ((ServerBossEventBridge) this.minecraftCreating(requireNonNull(bar, "bar"))).adventure$addAll(players);
    }

    public void unsubscribe(final ServerPlayer player, final BossBar bar) {
        this.bars.computeIfPresent(bar, (key, old) -> {
            old.removePlayer(player);
            if (old.getPlayers().isEmpty()) {
                key.removeListener(this);
                return null;
            } else {
                return old;
            }
        });
    }

    public void unsubscribeAll(final Collection<ServerPlayer> players, final BossBar bar) {
        this.bars.computeIfPresent(bar, (key, old) -> {
            ((ServerBossEventBridge) old).adventure$removeAll(players);
            if (old.getPlayers().isEmpty()) {
                key.removeListener(this);
                return null;
            } else {
                return old;
            }
        });
    }

    /**
     * Replace a player entity without sending any packets.
     *
     * <p>This should be triggered when the entity representing a player changes
     * (such as during a respawn)</p>
     *
     * @param old old player
     * @param newPlayer new one
     */
    public void replacePlayer(final ServerPlayer old, final ServerPlayer newPlayer) {
        for (final ServerBossEvent bar : this.bars.values()) {
            ((ServerBossEventBridge) bar).adventure$replaceSubscriber(old, newPlayer);
        }
    }

    /**
     * Refresh titles when a player's locale has changed.
     *
     * @param player player to refresh titles fro
     */
    public void refreshTitles(final ServerPlayer player) {
        for (final ServerBossEvent bar : this.bars.values()) {
            if (bar.getPlayers().contains(player)) {
                player.connection.send(ClientboundBossEventPacket.createUpdateNamePacket(bar));
            }
        }
    }

    /**
     * Remove the player from all associated boss bars.
     *
     * @param player The player to remove
     */
    public void unsubscribeFromAll(final ServerPlayer player) {
        for (final Iterator<Map.Entry<BossBar, ServerBossEvent>> it = this.bars.entrySet().iterator(); it.hasNext();) {
            final ServerBossEvent bar = it.next().getValue();
            if (bar.getPlayers().contains(player)) {
                bar.removePlayer(player);
                if (bar.getPlayers().isEmpty()) {
                    it.remove();
                }
            }
        }
    }

    @Override
    protected ServerBossEvent newBar(
        final @NotNull Component title,
        final net.minecraft.world.BossEvent.@NotNull BossBarColor color,
        final net.minecraft.world.BossEvent.@NotNull BossBarOverlay style,
        final float progress
    ) {
        final ServerBossEvent event = new ServerBossEvent(title, color, style);
        event.setProgress(progress);
        return event;
    }
}
