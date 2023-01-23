package net.kyori.adventure.platform.forge.impl.server.bridge;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class ServerPlayerBridge {
    private final ServerPlayer player;
    private Component adventure$tabListHeader = TextComponent.EMPTY;
    private Component adventure$tabListFooter = TextComponent.EMPTY;
    public ServerPlayerBridge(ServerPlayer player) {
        this.player = player;
    }

    /**
     * Update the tab list header and footer.
     *
     * @param header header, null to leave unchanged
     * @param footer footer, null to leave unchanged
     * @since 4.0.0
     */
    public void updateTabList(final @Nullable Component header, final @Nullable Component footer) {
        if (header != null) {
            this.adventure$tabListHeader = header;
        }
        if (footer != null) {
            this.adventure$tabListFooter = footer;
        }
        final ClientboundTabListPacket packet = new ClientboundTabListPacket(
            this.adventure$tabListHeader,
            this.adventure$tabListFooter
        );

        player.connection.send(packet);
    }
}
