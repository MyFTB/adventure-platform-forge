package net.kyori.adventure.platform.forge.impl.mixin;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.platform.forge.ForgeAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.ComponentMessageThrowable;
import net.minecraft.network.chat.ComponentUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CommandSyntaxException.class, remap = false)
public abstract class CommandSyntaxExceptionMixin implements ComponentMessageThrowable {

    @Shadow
    public abstract Message getRawMessage();

    @Override
    public @NotNull Component componentMessage() {
        final net.minecraft.network.chat.Component minecraft = ComponentUtils.fromMessage(this.getRawMessage());

        return ForgeAudiences.nonWrappingSerializer().deserialize(minecraft);
    }
}
