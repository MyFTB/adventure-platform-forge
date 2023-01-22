package net.kyori.adventure.platform.forge.impl.server.mapper;

import net.kyori.adventure.key.Key;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class HoverEventMapper {

    public static HoverEvent toNative(net.kyori.adventure.text.event.HoverEvent<?> event) {
        HoverEvent.Action<?> nativeAction = HoverEvent.Action.getByName(event.action().toString());
        if (nativeAction == HoverEvent.Action.SHOW_ENTITY) {
            net.kyori.adventure.text.event.HoverEvent<net.kyori.adventure.text.event.HoverEvent.ShowEntity> showEntityEvent
                = (net.kyori.adventure.text.event.HoverEvent<net.kyori.adventure.text.event.HoverEvent.ShowEntity>) event;
            net.kyori.adventure.text.event.HoverEvent.ShowEntity original = showEntityEvent.value();
            HoverEvent.EntityTooltipInfo nativeValue = new HoverEvent.EntityTooltipInfo(lookup(original.type()), original.id(), TextComponentMapper.toNative(original.name()));
            return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, nativeValue);
        } else if (nativeAction == HoverEvent.Action.SHOW_ITEM) {
            net.kyori.adventure.text.event.HoverEvent<net.kyori.adventure.text.event.HoverEvent.ShowItem> showEntityEvent
                = (net.kyori.adventure.text.event.HoverEvent<net.kyori.adventure.text.event.HoverEvent.ShowItem>) event;
            net.kyori.adventure.text.event.HoverEvent.ShowItem original = showEntityEvent.value();
            ItemStack theItem = new ItemStack(lookupItem(original.item()), original.count());
            if (original.nbt() != null) {
                try {
                    theItem.setTag(original.nbt().get(JsonLikeNBTCodec.INSTANCE));
                } catch (Exception ignored) {

                }
            }
            return new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(theItem));
        } else if (nativeAction == HoverEvent.Action.SHOW_TEXT) {
            net.kyori.adventure.text.event.HoverEvent<net.kyori.adventure.text.Component> showTextEvent
                = (net.kyori.adventure.text.event.HoverEvent<net.kyori.adventure.text.Component>) event;
            return new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentMapper.toNative(showTextEvent.value()));
        } else {
            throw new IllegalArgumentException("Cannot handle unknown hover event type '" + event.action().toString() + "'");
        }
    }

    private static EntityType<?> lookup(Key id) {
        return ForgeRegistries.ENTITIES.getValue(KeyMapper.toNative(id));
    }

    private static Item lookupItem(Key id) {
        return ForgeRegistries.ITEMS.getValue(KeyMapper.toNative(id));
    }
}
