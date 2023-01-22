package net.kyori.adventure.platform.forge.impl.server;

import java.util.function.Consumer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.forge.ForgeAudiences;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class PlainAudience implements Audience {
    private final ForgeAudiences controller;
    private final Pointered source;
    private final Consumer<String> plainOutput;

    public PlainAudience(final ForgeAudiences controller, final Pointered source, final Consumer<String> plainOutput) {
        this.controller = controller;
        this.source = source;
        this.plainOutput = plainOutput;
    }

    @Override
    public void sendMessage(final @NotNull Identity source, final @NotNull Component message, final @NotNull MessageType type) {
        this.plainOutput.accept(PlainTextComponentSerializer.plainText().serialize(this.controller.renderer().render(message, this.source)));
    }

    @Override
    public void sendActionBar(final @NotNull Component message) {
        this.sendMessage(Identity.nil(), message);
    }

    @Override
    public @NotNull Pointers pointers() {
        return this.source.pointers();
    }
}
