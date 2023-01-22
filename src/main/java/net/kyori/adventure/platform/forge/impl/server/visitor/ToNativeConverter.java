package net.kyori.adventure.platform.forge.impl.server.visitor;
import java.util.Optional;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.forge.impl.server.mapper.StyleMapper;
import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.EntityNBTComponent;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.NbtComponent;
import net.minecraft.resources.ResourceLocation;

/**
    * A {@link ComponentVisitor} implementation that converts a adventure
    * {@link Component} instance to a native {@link MutableComponent}.
    */
public class ToNativeConverter implements ComponentVisitor {

    private MutableComponent nativeComponent;

    private static MutableComponent handleStyleAndChildren(Component original, MutableComponent converted) {
        converted.setStyle(StyleMapper.toNative(original.style()));
        for (Component child : original.children()) {
            final ToNativeConverter converter = new ToNativeConverter();
            converter.accept(child);
            converted.append(converter.nativeComponent);
        }
        return converted;
    }

    public MutableComponent getNative() {
        return this.nativeComponent;
    }

    @Override
    public void accept(BlockNBTComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new NbtComponent.BlockNbtComponent(c.nbtPath(), c.interpret(), c.pos().asString(), Optional.empty()));
    }

    @Override
    public void accept(EntityNBTComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new NbtComponent.EntityNbtComponent(c.nbtPath(), c.interpret(), c.selector(),Optional.empty()));
    }

    @Override
    public void accept(KeybindComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new net.minecraft.network.chat.KeybindComponent(c.keybind()));
    }

    @Override
    public void accept(ScoreComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new net.minecraft.network.chat.ScoreComponent(c.name(), c.objective()));
    }

    @Override
    public void accept(SelectorComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new net.minecraft.network.chat.SelectorComponent(c.pattern(),Optional.empty()));
    }

    @Override
    public void accept(StorageNBTComponent c) {
        ResourceLocation location = c == null ? null : new ResourceLocation(c.storage().namespace(),c.storage().value());
        this.nativeComponent = handleStyleAndChildren(c, new NbtComponent.StorageNbtComponent(c.nbtPath(), c.interpret(), location,Optional.empty()));
    }

    @Override
    public void accept(TextComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new net.minecraft.network.chat.TextComponent(c.content()));
    }

    @Override
    public void accept(TranslatableComponent c) {
        this.nativeComponent = handleStyleAndChildren(c, new net.minecraft.network.chat.TranslatableComponent(c.key(), c.args().stream().map(arg -> {
            final ToNativeConverter converter = new ToNativeConverter();
            converter.accept(arg);
            return converter.nativeComponent;
        }).toArray()));
    }
}
