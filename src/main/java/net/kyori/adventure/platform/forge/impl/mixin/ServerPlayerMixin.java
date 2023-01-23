package net.kyori.adventure.platform.forge.impl.mixin;

import com.google.common.collect.MapMaker;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.platform.forge.ForgeServerAudiences;
import net.kyori.adventure.platform.forge.PlayerLocales;
import net.kyori.adventure.platform.forge.impl.bridge.LocaleHolderBridge;
import net.kyori.adventure.platform.forge.impl.server.ForgeServerAudiencesImpl;
import net.kyori.adventure.platform.forge.impl.server.FriendlyByteBufBridge;
import net.kyori.adventure.platform.forge.impl.server.RenderableAudience;
import net.kyori.adventure.platform.forge.impl.server.ServerPlayerAudience;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin extends PlayerMixin implements ForwardingAudience.Single, LocaleHolderBridge, RenderableAudience {

    private final Map<ForgeServerAudiencesImpl, Audience> adventure$renderers = new MapMaker().weakKeys().makeMap();
    @Shadow
    @Final
    public MinecraftServer server;
    @Shadow
    public ServerGamePacketListenerImpl connection;
    private Audience adventure$backing;
    private Locale adventure$locale;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void adventure$init(final CallbackInfo ci) {
        this.adventure$backing = ForgeServerAudiences.of(this.server).audience((ServerPlayer) (Object) this);
    }

    @Inject(method = "updateOptions", at = @At("HEAD"))
    private void adventure$handleLocaleUpdate(final ServerboundClientInformationPacket information, final CallbackInfo ci) {
        final String language = information.language();
        final @Nullable Locale locale = LocaleHolderBridge.toLocale(language);
        if (!Objects.equals(this.adventure$locale, locale)) {
            this.adventure$locale = locale;
            MinecraftForge.EVENT_BUS.post(new PlayerLocales.LocaleChangedEvent((ServerPlayer) (Object) this, locale));
        }
    }

    // Player tracking for boss bars and rendering

    @Inject(method = "restoreFrom", at = @At("RETURN"))
    private void copyData(final ServerPlayer old, final boolean alive, final CallbackInfo ci) {
        ForgeServerAudiencesImpl.forEachInstance(controller -> controller.bossBars().replacePlayer(old, (ServerPlayer) (Object) this));
        this.connection.connection.channel().attr(FriendlyByteBufBridge.CHANNEL_RENDER_DATA).set(this);
    }

    @Inject(method = "disconnect", at = @At("RETURN"))
    private void adventure$removeBossBarsOnDisconnect(final CallbackInfo ci) {
        ForgeServerAudiencesImpl.forEachInstance(controller -> controller.bossBars().unsubscribeFromAll((ServerPlayer) (Object) this));
    }

    @Override
    public @NotNull Audience audience() {
        return this.adventure$backing;
    }

    @Override
    public @NotNull Locale adventure$locale() {
        return this.adventure$locale;
    }

    @Override
    public Audience renderUsing(ForgeServerAudiencesImpl controller) {
        return this.adventure$renderers.computeIfAbsent(controller, ctrl -> new ServerPlayerAudience((ServerPlayer) (Object) this, ctrl));
    }

    @Override
    public void refresh() {

    }
}
