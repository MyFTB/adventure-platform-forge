package net.kyori.adventure.platform.forge.impl.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.kyori.adventure.platform.forge.impl.server.FriendlyByteBufBridge;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PacketEncoder.class)
public class PacketEncoderMixin {
    @Inject(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;Lio/netty/buffer/ByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/FriendlyByteBuf;writeVarInt(I)Lnet/minecraft/network/FriendlyByteBuf;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void adventure$applyLocaleToBuffer(final ChannelHandlerContext ctx, final Packet<?> pkt, final ByteBuf orig, final CallbackInfo ci,
                                               final ConnectionProtocol unused$proto, final Integer unused$id, final FriendlyByteBuf buffer) {
        ((FriendlyByteBufBridge) buffer).adventure$data(ctx.channel().attr(FriendlyByteBufBridge.CHANNEL_RENDER_DATA).get());
    }
}
