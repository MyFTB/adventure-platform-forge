package net.kyori.adventure.platform.forge.impl.bridge;

import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.forge.impl.NonWrappingComponentSerializer;
import net.kyori.adventure.pointer.Pointers;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class PointerProviderBridge implements Identified {
    private final Player player;
    private Pointers pointers;

    public PointerProviderBridge(Player player) {
        this.player = player;
    }

    /**
     * Get a shared instance of pointers.
     *
     * @return the pointers
     */
    @NotNull
    public Pointers pointers(){
        Pointers pointers = this.pointers;
        if (pointers == null) {
            synchronized (this) {
                if (this.pointers != null) {
                    return this.pointers;
                }

                final Pointers.Builder builder = Pointers.builder()
                    .withDynamic(Identity.NAME, () -> player.getGameProfile().getName())
                    .withDynamic(Identity.UUID, () -> player.getGameProfile().getId())
                    .withDynamic(Identity.DISPLAY_NAME, () -> NonWrappingComponentSerializer.INSTANCE.deserialize(player.getDisplayName()));

                // add any extra data
                this.populateExtraPointers(builder);

                this.pointers = pointers = builder.build();
            }
        }

        return pointers;
    }

    protected void populateExtraPointers(final Pointers.Builder builder) {
        // for overriding by implementations
        // todo: support permissions here if Luck's permissions API is available
    }

    @Override
    public @NotNull Identity identity() {
        return new GameProfileIdentityBridge(player.getGameProfile());
    }

    public @NotNull Player player() {
        return player;
    }
}
