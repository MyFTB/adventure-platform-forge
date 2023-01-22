package net.kyori.adventure.platform.forge.impl.server.mapper;

import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.util.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.NbtComponent;
import org.jetbrains.annotations.NotNull;

public class JsonLikeNBTCodec implements Codec<CompoundTag, String, Exception, Exception> {
    public static final JsonLikeNBTCodec INSTANCE = new JsonLikeNBTCodec();

    @Override
    public @NotNull CompoundTag  decode(@NotNull String encoded) throws Exception {
        return TagParser.parseTag(encoded);
    }

    @Override
    public @NotNull String encode(@NotNull CompoundTag decoded) {
        return decoded.toString();
    }
}
