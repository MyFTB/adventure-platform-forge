package net.kyori.adventure.platform.forge;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.forge.impl.AdventureCommandSourceStack;
import net.kyori.adventure.platform.forge.impl.server.ForgeServerAudiencesImpl;
import net.kyori.adventure.platform.forge.impl.server.MinecraftServerBridge;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;
public interface ForgeServerAudiences extends AudienceProvider, ForgeAudiences {
    /**
     * Get the shared audience provider for the server.
     *
     * <p>This provider will render messages using the global translation registry.</p>
     *
     * @param server server instance to work with
     * @return common audience provider
     * @since 4.0.0
     */
    static @NotNull ForgeServerAudiences of(final @NotNull MinecraftServer server) {
        return ((MinecraftServerBridge) server).adventure$globalProvider();
    }

    /**
     * Create an audience provider for this server with customized settings.
     *
     * @param server the server
     * @return audience provider builder
     * @since 4.0.0
     */
    static ForgeServerAudiences.@NotNull Builder builder(final @NotNull MinecraftServer server) {
        return new ForgeServerAudiencesImpl.Builder(requireNonNull(server, "server"));
    }

    /**
     * Get an audience to send to a {@link CommandSourceStack}.
     *
     * <p>This will delegate to the native implementation by the command source, or
     * otherwise use a wrapping implementation.</p>
     *
     * @param source source to send to.
     * @return the audience
     * @since 4.0.0
     */
    @NotNull AdventureCommandSourceStack audience(@NotNull CommandSourceStack source);

    /**
     * Get an audience that will send to the provided {@link CommandSource}.
     *
     * <p>Depending on the specific source, the returned audience may only support
     * a subset of abilities.</p>
     *
     * @param source source to send to
     * @return an audience for the source
     * @since 4.0.0
     */
    @NotNull Audience audience(@NotNull CommandSource source);

    /**
     * Create an audience that will send to every listed player.
     *
     * @param players Players to send to.
     * @return a new audience
     * @since 4.0.0
     */
    @NotNull Audience audience(@NotNull Iterable<ServerPlayer> players);


    /**
     * Builder for {@link ForgeServerAudiences} with custom attributes.
     *
     * @since 4.0.0
     */
    interface Builder extends AudienceProvider.Builder<ForgeServerAudiences, ForgeServerAudiences.Builder> {
    }
}
