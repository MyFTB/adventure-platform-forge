package net.kyori.adventure.platform.forge.impl.mixin;

import com.google.common.collect.MapMaker;
import java.util.Map;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.permission.PermissionChecker;
import net.kyori.adventure.platform.forge.ForgeAudiences;
import net.kyori.adventure.platform.forge.ForgeServerAudiences;
import net.kyori.adventure.platform.forge.impl.server.ForgeServerAudiencesImpl;
import net.kyori.adventure.platform.forge.impl.server.PlainAudience;
import net.kyori.adventure.platform.forge.impl.server.RenderableAudience;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.util.TriState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.rcon.RconConsoleSource;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RconConsoleSource.class)
public class RconConsoleSourceMixin implements RenderableAudience, ForwardingAudience.Single{
    @Shadow @Final private MinecraftServer server;
    @Shadow @Final private StringBuffer buffer;
    private volatile Pointers adventure$pointers;
    private final Map<ForgeAudiences, Audience> adventure$renderers = new MapMaker().weakKeys().makeMap();

    @Override
    public Audience renderUsing(final ForgeServerAudiencesImpl controller) {
        return this.adventure$renderers.computeIfAbsent(controller, ctrl -> new PlainAudience(ctrl, this, this.buffer::append));
    }

    @Override
    public void refresh() {

    }

    @Override
    public @NotNull Audience audience() {
        return ForgeServerAudiences.of(this.server).audience((RconConsoleSource) (Object) this);
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
}
