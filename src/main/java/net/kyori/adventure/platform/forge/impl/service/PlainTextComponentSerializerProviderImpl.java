package net.kyori.adventure.platform.forge.impl.service;

import java.util.function.Consumer;
import net.kyori.adventure.platform.forge.impl.AdventureCommon;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class PlainTextComponentSerializerProviderImpl implements PlainTextComponentSerializer.Provider {
    @Override
    public @NotNull PlainTextComponentSerializer plainTextSimple() {
        return PlainTextComponentSerializer.builder().flattener(AdventureCommon.FLATTENER).build();
    }

    @Override
    public @NotNull Consumer<PlainTextComponentSerializer.Builder> plainText() {
        return builder -> builder.flattener(AdventureCommon.FLATTENER);
    }
}
