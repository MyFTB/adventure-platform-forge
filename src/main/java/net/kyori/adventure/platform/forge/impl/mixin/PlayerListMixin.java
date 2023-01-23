package net.kyori.adventure.platform.forge.impl.mixin;


import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Replace the player list fields so we can safely access and iterate them off the server thread.
 */
@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Shadow @Final @Mutable private List<ServerPlayer> players;

    @Shadow @Final @Mutable private Map<UUID, ServerPlayer> playersByUUID;

    @Inject(method = "<init>", at = @At("RETURN"), require = 0)
    private void adventure$replacePlayerLists(final MinecraftServer server, final RegistryAccess.Frozen tracker, final PlayerDataStorage handler, final int i,
                                              final CallbackInfo ci) {
        this.players = new CopyOnWriteArrayList<>();
        this.playersByUUID = new ConcurrentHashMap<>();
    }
}
