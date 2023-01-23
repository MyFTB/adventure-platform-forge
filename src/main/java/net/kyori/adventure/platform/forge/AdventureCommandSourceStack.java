package net.kyori.adventure.platform.forge;

import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface AdventureCommandSourceStack extends ForwardingAudience.Single, Identified {
    void sendSuccess(final @NotNull Component text, final boolean sendToOps);

    void sendFailure(final @NotNull Component text);
}
