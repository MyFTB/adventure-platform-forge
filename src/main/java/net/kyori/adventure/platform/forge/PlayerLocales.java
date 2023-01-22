package net.kyori.adventure.platform.forge;

import java.util.Locale;
import net.kyori.adventure.platform.forge.impl.bridge.LocaleHolderBridge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * API for working with player locales.
 *
 * @since 4.0.0
 */
public interface PlayerLocales {
    /**
     * Get the active locale for a player, either on the server or client sides.
     *
     * <p>Will return the system-wide default value if the player has no locale set.</p>
     *
     * @param player the source of the locale
     * @return player locale
     * @since 4.0.0
     * @deprecated Use pointer instead, {@code player.get(Identity.LOCALE).orElse(Locale.getDefault())} on a server or client player
     */
    @Deprecated(forRemoval = true, since = "5.3.0")
    static @NotNull Locale locale(final @NotNull Player player) {
        return player instanceof LocaleHolderBridge ? ((LocaleHolderBridge) player).adventure$locale() : Locale.getDefault();
    }

    /**
     * An event called when a player locale update is received.
     *
     * <p>This event is only called if the player locale has actually changed from its previous value.</p>
     *
     * @since 4.0.0
     */
    public class LocaleChangedEvent extends Event {
        private final @NotNull ServerPlayer player;
        private final @Nullable Locale newLocale;

        public LocaleChangedEvent(@NotNull ServerPlayer player, @Nullable Locale newLocale) {
            this.player = player;
            this.newLocale = newLocale;
        }

        @NotNull
        public ServerPlayer player() {
            return player;
        }

        @Nullable
        public Locale newLocale() {
            return newLocale;
        }
    }
}

