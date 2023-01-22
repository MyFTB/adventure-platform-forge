package net.kyori.adventure.platform.forge.impl.server;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.forge.ForgeAudiences;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSource;
import org.jetbrains.annotations.NotNull;

public class CommandSourceAudience implements Audience {
    private final CommandSource output;
    private final ForgeAudiences serializer;

    CommandSourceAudience(final CommandSource output, final ForgeAudiences serializer) {
        this.output = output;
        this.serializer = serializer;
    }

    @Override
    public void sendMessage(final Identity source, final Component text, final MessageType type) {
        this.output.sendMessage(this.serializer.toNative(text), source.uuid());
    }

    @Override
    public void sendActionBar(final @NotNull Component message) {
        this.sendMessage(Identity.nil(), message);
    }
}
