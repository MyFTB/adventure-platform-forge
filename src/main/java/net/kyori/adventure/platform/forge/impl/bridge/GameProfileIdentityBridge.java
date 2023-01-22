package net.kyori.adventure.platform.forge.impl.bridge;

import com.mojang.authlib.GameProfile;
import net.kyori.adventure.identity.Identity;
import org.jetbrains.annotations.NotNull;

public record GameProfileIdentityBridge(GameProfile profile) implements Identity {

    @Override
    public java.util.@NotNull UUID uuid() {
        return profile.getId();
    }

    @Override
    public @NotNull GameProfile profile() {
        return profile;
    }
}
