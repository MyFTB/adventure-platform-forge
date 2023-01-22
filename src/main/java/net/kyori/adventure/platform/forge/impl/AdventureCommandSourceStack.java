package net.kyori.adventure.platform.forge.impl;

import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.identity.Identified;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;

public interface AdventureCommandSourceStack  extends ForwardingAudience.Single, Identified {
    /**
     * Send a result message to the command source.
     *
     * @param text The text to send
     * @param sendToOps If this message should be sent to all ops listening
     * @since 4.0.0
     */
    default void sendSuccess(final @NotNull Component text, final boolean sendToOps) {
        // Implemented by Mixin
    }

    /**
     * Send an error message to the command source.
     *
     * @param text The error
     * @since 4.0.0
     */
    default void sendFailure(final @NotNull Component text) {
        // Implemented by Mixin
    }
}
