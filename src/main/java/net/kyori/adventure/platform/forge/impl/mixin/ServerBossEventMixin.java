package net.kyori.adventure.platform.forge.impl.mixin;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerBossEvent.class)
public class ServerBossEventMixin extends BossEvent {
    private static final float MINIMUM_PERCENT_CHANGE = 5e-4f;

    private float adventure$lastSentPercent;

    public ServerBossEventMixin(final UUID uuid, final Component name, final BossBarColor color, final BossBarOverlay style) {
        super(uuid, name, color, style);
        this.adventure$lastSentPercent = this.progress;
    }

    // If a player has respawned, we still want to be able to remove the player using old references to their entity
    @Redirect(method = "removePlayer", at = @At(value = "INVOKE", target = "Ljava/util/Set;remove(Ljava/lang/Object;)Z"))
    private boolean adventure$removeByUuid(final Set<?> instance, final Object player) {
        if (instance.remove(player)) {
            return true;
        }
        if (!(player instanceof ServerPlayer)) {
            return false;
        }

        final UUID testId = ((ServerPlayer) player).getUUID();
        for (final Iterator<?> it = instance.iterator(); it.hasNext(); ) {
            if (((ServerPlayer) it.next()).getUUID().equals(testId)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    // Optimization -- don't send a packet for tiny changes

    @Inject(method = "setProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BossEvent;setProgress(F)V"), cancellable = true, require = 0)
    private void adventure$onlySetPercentIfBigEnough(final float newPercent, final CallbackInfo ci) {
        if (Math.abs(newPercent - this.adventure$lastSentPercent) < MINIMUM_PERCENT_CHANGE) {
            this.progress = newPercent;
            ci.cancel();
        } else {
            this.adventure$lastSentPercent = newPercent;
            // continue as normal
        }
    }
}
