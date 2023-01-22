package net.kyori.adventure.platform.forge.impl.bridge;

import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerIdentityBridge implements Identified {
    private final Player player;

    public PlayerIdentityBridge(Player player) {
        this.player = player;
    }

    @Override
    public @NotNull Identity identity() {
        return new GameProfileIdentityBridge(player.getGameProfile());
    }
}
