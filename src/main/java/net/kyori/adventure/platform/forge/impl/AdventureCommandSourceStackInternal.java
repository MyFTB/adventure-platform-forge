package net.kyori.adventure.platform.forge.impl;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.forge.impl.server.ForgeServerAudiencesImpl;
import net.minecraft.commands.CommandSource;

public interface AdventureCommandSourceStackInternal extends AdventureCommandSourceStack{
    /**
     * Set the audience to be delegated to.
     *
     * @param wrapped wrapped audience
     * @param controller controller to render with
     * @return the wrapped audience
     */
    AdventureCommandSourceStack adventure$audience(final Audience wrapped, final ForgeServerAudiencesImpl controller);

    /**
     * The backing source for the command.
     *
     * @return backing source
     */
    CommandSource adventure$source();
}
