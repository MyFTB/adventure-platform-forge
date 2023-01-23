package net.kyori.adventure.platform.forge.impl.mixin;

import com.mojang.authlib.GameProfile;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.forge.impl.bridge.GameProfileIdentityBridge;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Player.class)
public abstract class PlayerMixin implements Identified {


    @Shadow
    @Final
    private GameProfile gameProfile;

    @Override
    public @NotNull Identity identity() {
        return new GameProfileIdentityBridge(this.gameProfile);
    }
}
