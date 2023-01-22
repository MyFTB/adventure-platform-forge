package net.kyori.adventure.platform.forge.impl.mixin;

import java.util.Objects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.forge.ForgeServerAudiences;
import net.kyori.adventure.platform.forge.impl.AdventureCommandSourceStack;
import net.kyori.adventure.platform.forge.impl.AdventureCommandSourceStackInternal;
import net.kyori.adventure.platform.forge.impl.server.ForgeServerAudiencesImpl;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandSourceStack.class)
public abstract class CommandSourceStackMixin implements AdventureCommandSourceStackInternal {
    // @formatter:off
    @Shadow @Final private CommandSource source;
    @Shadow @Final private boolean silent;
    @Shadow @Final private MinecraftServer server;

    @Shadow protected abstract void shadow$broadcastToAdmins(net.minecraft.network.chat.Component text);
    // @formatter:on

    private boolean adventure$assigned = false;
    private Audience adventure$out;
    private ForgeServerAudiences adventure$controller;

    @Override
    public void sendSuccess(final @NotNull Component text, final boolean sendToOps) {
        if (this.source.acceptsSuccess() && !this.silent) {
            this.sendMessage(Identity.nil(), text);
        }

        if (sendToOps && this.source.shouldInformAdmins() && !this.silent) {
            this.shadow$broadcastToAdmins(this.adventure$controller.toNative(text));
        }
    }

    @Override
    public void sendFailure(final @NotNull Component text) {
        if (this.source.acceptsFailure()) {
            this.sendMessage(Identity.nil(), text.color(NamedTextColor.RED));
        }
    }

    @Override
    public @NotNull Audience audience() {
        if (this.adventure$out == null) {
            if (this.server == null) {
                throw new IllegalStateException("Cannot use adventure operations without an available server!");
            }
            this.adventure$controller = ForgeServerAudiences.of(this.server);
            this.adventure$out = this.adventure$controller.audience(this.source);
        }
        return this.adventure$out;
    }

    @Override
    public @NotNull Identity identity() {
        if (this.source instanceof Identified) {
            return ((Identified) this.source).identity();
        } else {
            return Identity.nil();
        }
    }

    @Override
    public AdventureCommandSourceStack adventure$audience(final Audience wrapped, final ForgeServerAudiencesImpl controller) {
        if (this.adventure$assigned && !Objects.equals(controller, this.adventure$controller)) {
            throw new IllegalStateException("This command source has been attached to a specific renderer already!");
            // TODO: return a new instance
        }
        this.adventure$assigned = true;
        this.adventure$out = wrapped;
        this.adventure$controller = controller;
        return this;
    }

    @Override
    public CommandSource adventure$source() {
        return this.source;
    }
}
