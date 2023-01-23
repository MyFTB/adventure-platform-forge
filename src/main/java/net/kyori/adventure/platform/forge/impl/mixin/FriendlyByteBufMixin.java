package net.kyori.adventure.platform.forge.impl.mixin;

import net.kyori.adventure.platform.forge.impl.WrappedComponent;
import net.kyori.adventure.platform.forge.impl.server.FriendlyByteBufBridge;
import net.kyori.adventure.pointer.Pointered;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FriendlyByteBuf.class)
public class FriendlyByteBufMixin implements FriendlyByteBufBridge {
    private @Nullable Pointered adventure$data;

    @ModifyVariable(method = "writeComponent", at = @At("HEAD"), argsOnly = true)
    private Component adventure$localizeComponent(final Component input) {
        if (this.adventure$data != null && input instanceof WrappedComponent) {
            return ((WrappedComponent) input).rendered(this.adventure$data);
        }
        return input;
    }

    @Override
    public void adventure$data(final @Nullable Pointered data) {
        this.adventure$data = data;
    }
}
