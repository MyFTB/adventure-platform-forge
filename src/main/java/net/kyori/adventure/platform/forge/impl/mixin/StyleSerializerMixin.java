package net.kyori.adventure.platform.forge.impl.mixin;

import net.kyori.adventure.platform.forge.impl.NonWrappingComponentSerializer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Style.Serializer.class)
public class StyleSerializerMixin {
    @Redirect(method = "getClickEvent", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/ClickEvent$Action;isAllowedFromServer()Z"))
    private static boolean adventure$redirectIsAllowedFromServer(final ClickEvent.@NotNull Action action) {
        if (NonWrappingComponentSerializer.INSTANCE.bypassIsAllowedFromServer()) {
            return true;
        }
        return action.isAllowedFromServer();
    }
}
