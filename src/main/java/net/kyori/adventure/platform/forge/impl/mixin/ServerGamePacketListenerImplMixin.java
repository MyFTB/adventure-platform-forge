package net.kyori.adventure.platform.forge.impl.mixin;

import net.kyori.adventure.platform.forge.impl.server.FriendlyByteBufBridge;
import net.kyori.adventure.pointer.Pointered;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    // Initialize attribute tracking the player for component rendering
    @Inject(method = "<init>", at = @At("TAIL"))
    private void adventure$initTracking(final MinecraftServer server, final Connection conn, final ServerPlayer player, final CallbackInfo ci) {
        conn.channel().attr(FriendlyByteBufBridge.CHANNEL_RENDER_DATA)
            .set((Pointered) player);
    }
}
