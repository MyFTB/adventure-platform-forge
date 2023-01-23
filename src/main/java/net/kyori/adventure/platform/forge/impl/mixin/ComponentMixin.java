package net.kyori.adventure.platform.forge.impl.mixin;

import net.kyori.adventure.platform.forge.impl.NonWrappingComponentSerializer;
import net.kyori.adventure.text.ComponentLike;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Component.class)
public interface ComponentMixin extends ComponentLike {
    @Override
    default net.kyori.adventure.text.@NotNull Component asComponent() {
        return NonWrappingComponentSerializer.INSTANCE.deserialize((Component) this);
    }
}
