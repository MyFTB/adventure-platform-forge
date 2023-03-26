package net.kyori.adventure.platform.forge.impl.service;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.forge.impl.ClickCallbackRegistry;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import org.jetbrains.annotations.NotNull;

public class ForgeClickCallbackProviderImpl implements ClickCallback.Provider{
    @Override
    public @NotNull ClickEvent create(@NotNull ClickCallback<Audience> callback, ClickCallback.@NotNull Options options) {
        return ClickEvent.runCommand(ClickCallbackRegistry.INSTANCE.register(callback, options));
    }
}
