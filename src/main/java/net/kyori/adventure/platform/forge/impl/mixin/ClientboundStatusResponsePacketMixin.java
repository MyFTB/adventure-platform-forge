package net.kyori.adventure.platform.forge.impl.mixin;

import com.google.gson.GsonBuilder;
import java.lang.reflect.Type;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientboundStatusResponsePacket.class)
public class ClientboundStatusResponsePacketMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lcom/google/gson/GsonBuilder;registerTypeAdapter(Ljava/lang/reflect/Type;Ljava/lang/Object;)Lcom/google/gson/GsonBuilder;", ordinal = 0), remap = false)
    private static GsonBuilder adventure$injectSerializers(final GsonBuilder instance, final Type type, final Object adapter) {
        return GsonComponentSerializer.gson().populator().apply(instance.registerTypeAdapter(type, adapter));
    }
}
