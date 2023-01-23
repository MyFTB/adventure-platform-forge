package net.kyori.adventure.platform.forge.impl.server.visitor;

import net.kyori.adventure.text.BlockNBTComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.EntityNBTComponent;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.ScoreComponent;
import net.kyori.adventure.text.SelectorComponent;
import net.kyori.adventure.text.StorageNBTComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;

/**
 * Visitor that recursively traverses a given component.
 */
public interface ComponentVisitor {

    // Because adventure doesn't support visitor by default!
    default void accept(Component c) {
        if (c instanceof BlockNBTComponent) {
            this.accept((BlockNBTComponent) c);
        } else if (c instanceof EntityNBTComponent) {
            this.accept((EntityNBTComponent) c);
        } else if (c instanceof KeybindComponent) {
            this.accept((KeybindComponent) c);
        } else if (c instanceof ScoreComponent) {
            this.accept((ScoreComponent) c);
        } else if (c instanceof SelectorComponent) {
            this.accept((SelectorComponent) c);
        } else if (c instanceof StorageNBTComponent) {
            this.accept((StorageNBTComponent) c);
        } else if (c instanceof TextComponent) {
            this.accept((TextComponent) c);
        } else if (c instanceof TranslatableComponent) {
            this.accept((TranslatableComponent) c);
        }
    }

    default void accept(BlockNBTComponent c) {
        c.children().forEach(this::accept);
    }

    default void accept(EntityNBTComponent c) {
        c.children().forEach(this::accept);
    }

    default void accept(KeybindComponent c) {
        c.children().forEach(this::accept);
    }

    default void accept(ScoreComponent c) {
        c.children().forEach(this::accept);
    }

    default void accept(SelectorComponent c) {
        c.children().forEach(this::accept);
    }

    default void accept(StorageNBTComponent c) {
        c.children().forEach(this::accept);
    }

    default void accept(TextComponent c) {
        c.children().forEach(this::accept);
    }

    default void accept(TranslatableComponent c) {
        c.children().forEach(this::accept);
    }
}
