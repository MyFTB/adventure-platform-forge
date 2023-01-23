package net.kyori.adventure.platform.forge.impl.mixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import net.kyori.adventure.platform.forge.impl.WrappedComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(net.minecraft.network.chat.Component.Serializer.class)
public abstract class ComponentSerializerMixin {
    // inject into the anonymous function to build a gson instance
    @Inject(method = "*()Lcom/google/gson/Gson;", at = @At(value = "INVOKE_ASSIGN", target = "com/google/gson/GsonBuilder.disableHtmlEscaping()Lcom/google/gson/GsonBuilder;", remap = false),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION, remap = false)
    private static void adventure$injectGson(final CallbackInfoReturnable<Gson> cir, final GsonBuilder gson) {
        GsonComponentSerializer.gson().populator().apply(gson);
    }

    @Shadow
    public abstract JsonElement serialize(Component p_130706_, Type p_130707_, JsonSerializationContext p_130708_);

    @Inject(method = "serialize(Lnet/minecraft/network/chat/Component;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;", at = @At("HEAD"), cancellable = true)
    public void adventure$writeComponentText(final Component text, final Type type, final JsonSerializationContext ctx,
                                             final CallbackInfoReturnable<JsonElement> cir) {
        if (text instanceof WrappedComponent) {
            final @Nullable Component converted = ((WrappedComponent) text).deepConvertedIfPresent();
            if (converted != null) {
                cir.setReturnValue(this.serialize(text, type, ctx));
            } else {
                cir.setReturnValue(ctx.serialize(((WrappedComponent) text).wrapped(), net.kyori.adventure.text.Component.class));
            }
        }
    }
}
