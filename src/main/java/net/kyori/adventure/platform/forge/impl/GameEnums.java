package net.kyori.adventure.platform.forge.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.sound.Sound;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.Nullable;

public final class GameEnums {
    public static final MappedRegistry<BossEvent.BossBarColor, BossBar.Color> BOSS_BAR_COLOR
        = MappedRegistry.named(BossEvent.BossBarColor.class, BossEvent.BossBarColor::byName,
        BossBar.Color.class, BossBar.Color.NAMES);
    public static final MappedRegistry<BossEvent.BossBarOverlay, BossBar.Overlay> BOSS_BAR_OVERLAY
        = MappedRegistry.named(BossEvent.BossBarOverlay.class, BossEvent.BossBarOverlay::byName,
        BossBar.Overlay.class, BossBar.Overlay.NAMES);

    public static final MappedRegistry<SoundSource, Sound.Source> SOUND_SOURCE
        = MappedRegistry.named(SoundSource.class, soundSourceProvider(),
        Sound.Source.class, Sound.Source.NAMES);

    private GameEnums() {
    }

    private static Function<String, @Nullable SoundSource> soundSourceProvider() {
        final Map<String, SoundSource> sources = new HashMap<>();
        for (final SoundSource source : SoundSource.values()) {
            sources.put(source.getName(), source);
        }

        return sources::get;
    }
}
