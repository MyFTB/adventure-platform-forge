package net.kyori.adventure.platform.forge.impl.server;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.Iterables;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.forge.ForgeAudiences;
import net.kyori.adventure.platform.forge.ForgeServerAudiences;
import net.kyori.adventure.platform.forge.AdventureCommandSourceStack;
import net.kyori.adventure.platform.forge.impl.AdventureCommandSourceStackInternal;
import net.kyori.adventure.platform.forge.impl.AdventureCommon;
import net.kyori.adventure.platform.forge.impl.WrappedComponent;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.renderer.ComponentRenderer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ForgeServerAudiencesImpl implements ForgeServerAudiences {

    private static final Set<ForgeServerAudiencesImpl> INSTANCES = Collections.newSetFromMap(new WeakHashMap<>());

    /**
     * Perform an action on every audience provider instance.
     *
     * @param actor a consumer that will be called for every provider
     */
    public static void forEachInstance(final Consumer<ForgeServerAudiencesImpl> actor) {
        synchronized (INSTANCES) {
            for (final ForgeServerAudiencesImpl instance : INSTANCES) {
                actor.accept(instance);
            }
        }
    }

    private final MinecraftServer server;
    private final Function<Pointered, ?> partition;
    private final ComponentRenderer<Pointered> renderer;
    final ServerBossBarListener bossBars;

    public ForgeServerAudiencesImpl(final MinecraftServer server, final Function<Pointered, ?> partition, final ComponentRenderer<Pointered> renderer) {
        this.server = server;
        this.partition = partition;
        this.renderer = renderer;
        this.bossBars = new ServerBossBarListener(this);
        synchronized (INSTANCES) {
            INSTANCES.add(this);
        }
    }

    @Override
    public @NotNull Audience all() {
        return Audience.audience(this.console(), this.players());
    }

    @Override
    public @NotNull Audience console() {
        return this.audience(this.server);
    }

    @Override
    public @NotNull Audience players() {
        return Audience.audience(this.audiences(this.server.getPlayerList().getPlayers()));
    }

    @Override
    public @NotNull Audience player(final @NotNull UUID playerId) {
        final @Nullable ServerPlayer player = this.server.getPlayerList().getPlayer(playerId);
        return player != null ? this.audience(player) : Audience.empty();
    }

    private Iterable<Audience> audiences(final Iterable<? extends ServerPlayer> players) {
        return Iterables.transform(players, this::audience);
    }

    @Override
    public @NotNull Audience permission(final @NotNull String permission) {
        return Audience.empty(); // TODO: permissions api
    }

    @Override
    public @NotNull AdventureCommandSourceStack audience(final @NotNull CommandSourceStack source) {
        if (!(source instanceof AdventureCommandSourceStackInternal internal)) {
            throw new IllegalArgumentException("The AdventureCommandSource mixin failed!");
        }

        return internal.adventure$audience(this.audience(internal.adventure$source()), this);
    }

    @Override
    public @NotNull Audience audience(final @NotNull CommandSource source) {
        if (source instanceof RenderableAudience renderable) {
            return renderable.renderUsing(this);
        } else if (source instanceof Audience audience) {
            // TODO: How to pass component renderer through
            return audience;
        } else {
            return new CommandSourceAudience(source, this);
        }
    }

    @Override
    public @NotNull Audience audience(final @NotNull Iterable<ServerPlayer> players) {
        return Audience.audience(this.audiences(players));
    }

    @Override
    public @NotNull Audience world(final @NotNull Key worldId) {
        final @Nullable ServerLevel level = this.server.getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY,
            ForgeAudiences.toNative(requireNonNull(worldId, "worldId"))));
        if (level != null) {
            return this.audience(level.players());
        }
        return Audience.empty();
    }

    @Override
    public @NotNull Audience server(final @NotNull String serverName) {
        return Audience.empty();
    }

    @Override
    public @NotNull ComponentFlattener flattener() {
        return AdventureCommon.FLATTENER;
    }

    @Override
    public @NotNull ComponentRenderer<Pointered> renderer() {
        return this.renderer;
    }


    @Override
    public net.minecraft.network.chat.@NotNull Component toNative(final @NotNull Component adventure) {
        if (adventure == Component.empty()) return TextComponent.EMPTY;

        return new WrappedComponent(requireNonNull(adventure, "adventure"), this.partition, this.renderer);
    }

    public ServerBossBarListener bossBars() {
        return this.bossBars;
    }

    @Override
    public void close() {
    }

    public static final class Builder implements ForgeServerAudiences.Builder {
        private final MinecraftServer server;
        private Function<Pointered, ?> partition;
        private ComponentRenderer<Pointered> renderer;

        public Builder(final MinecraftServer server) {
            this.server = server;
            this.componentRenderer(AdventureCommon.localePartition(), GlobalTranslator.renderer());
        }

        @Override
        public @NotNull ForgeServerAudiences.Builder componentRenderer(final @NotNull ComponentRenderer<Pointered> componentRenderer) {
            this.renderer = requireNonNull(componentRenderer, "componentRenderer");
            return this;
        }

        @Override
        public @NotNull ForgeServerAudiences.Builder partition(final @NotNull Function<Pointered, ?> partitionFunction) {
            this.partition = requireNonNull(partitionFunction, "partitionFunction");
            return this;
        }

        @Override
        public @NotNull ForgeServerAudiencesImpl build() {
            return new ForgeServerAudiencesImpl(this.server, this.partition, this.renderer);
        }
    }
}
