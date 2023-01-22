package net.kyori.adventure.platform.forge.impl.server;

import net.kyori.adventure.audience.Audience;

public interface RenderableAudience extends Audience {
    Audience renderUsing(final ForgeServerAudiencesImpl controller);


    /**
     * Refresh this audience, to update any actively displayed rendered content.
     *
     * @since 4.0.0
     */
    void refresh();
}
