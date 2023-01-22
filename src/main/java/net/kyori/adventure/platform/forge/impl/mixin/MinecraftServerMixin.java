package net.kyori.adventure.platform.forge.impl.mixin;

import com.google.common.collect.MapMaker;
import java.util.Map;
import java.util.logging.Logger;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.permission.PermissionChecker;
import net.kyori.adventure.platform.forge.ForgeAudiences;
import net.kyori.adventure.platform.forge.impl.AdventureCommon;
import net.kyori.adventure.platform.forge.impl.server.ForgeServerAudiencesImpl;
import net.kyori.adventure.platform.forge.impl.server.PlainAudience;
import net.kyori.adventure.platform.forge.impl.server.RenderableAudience;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.util.TriState;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Implement ComponentCommandOutput for output to the server console.
 */
@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements RenderableAudience, ForwardingAudience.Single{
    private final Map<ForgeAudiences, Audience> adventure$renderers = new MapMaker().weakKeys().makeMap();

    private volatile Pointers adventure$pointers;
    @Override
    public Audience renderUsing(ForgeServerAudiencesImpl controller) {
        return this.adventure$renderers.computeIfAbsent(controller, ctrl -> new PlainAudience(ctrl, this, AdventureCommon.LOGGER::info));
    }

    @Override
    public @NotNull Pointers pointers() {
        if (this.adventure$pointers == null) {
            synchronized (this) {
                if (this.adventure$pointers == null) {
                    return this.adventure$pointers = Pointers.builder()
                        .withStatic(Identity.NAME, "Server")
                        .withStatic(PermissionChecker.POINTER, perm -> TriState.TRUE)
                        .build();
                }
            }
        }
        return this.adventure$pointers;
    }

    @Override
    public void refresh() {
        // nothing to refresh
    }

    @Override
    public @NotNull Audience audience() {
        return null;
    }
}
