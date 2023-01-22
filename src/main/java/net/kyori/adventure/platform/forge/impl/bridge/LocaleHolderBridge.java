package net.kyori.adventure.platform.forge.impl.bridge;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LocaleHolderBridge {
    /**
     * Take a locale string provided from a minecraft client and attempt to parse it as a locale.
     * These are not strictly compliant with the iso standard, so we try to make things a bit more normalized.
     *
     * @param mcLocale The locale string, in the format provided by the Minecraft client
     * @return A Locale object matching the provided locale string
     */
    static @Nullable Locale toLocale(final @Nullable String mcLocale) {
        if (mcLocale == null) return null;

        final String[] parts = mcLocale.split("_", 3);
        return switch (parts.length) {
            case 1 -> parts[0].isEmpty() ? null : new Locale(parts[0]);
            case 2 -> new Locale(parts[0], parts[1]);
            case 3 -> new Locale(parts[0], parts[1], parts[2]);
            default -> null;
        };
    }

    @NotNull Locale adventure$locale();
}
