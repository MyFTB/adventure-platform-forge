package net.kyori.adventure.platform.forge.impl.server.mapper;

import java.util.EnumMap;
import net.minecraft.network.chat.ClickEvent;

public class ClickEventMapper {

    public static final EnumMap<ClickEvent.Action, net.kyori.adventure.text.event.ClickEvent.Action> NATIVE_ACTIONS_TO_ADVENTURE;
    public static final EnumMap<net.kyori.adventure.text.event.ClickEvent.Action, ClickEvent.Action> ADVENTURE_ACTIONS_TO_NATIVE;

    static {
        NATIVE_ACTIONS_TO_ADVENTURE = new EnumMap<>(ClickEvent.Action.class);
        NATIVE_ACTIONS_TO_ADVENTURE.put(ClickEvent.Action.CHANGE_PAGE, net.kyori.adventure.text.event.ClickEvent.Action.CHANGE_PAGE);
        NATIVE_ACTIONS_TO_ADVENTURE.put(ClickEvent.Action.COPY_TO_CLIPBOARD, net.kyori.adventure.text.event.ClickEvent.Action.COPY_TO_CLIPBOARD);
        NATIVE_ACTIONS_TO_ADVENTURE.put(ClickEvent.Action.OPEN_FILE, net.kyori.adventure.text.event.ClickEvent.Action.OPEN_FILE);
        NATIVE_ACTIONS_TO_ADVENTURE.put(ClickEvent.Action.OPEN_URL, net.kyori.adventure.text.event.ClickEvent.Action.OPEN_URL);
        NATIVE_ACTIONS_TO_ADVENTURE.put(ClickEvent.Action.RUN_COMMAND, net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND);
        NATIVE_ACTIONS_TO_ADVENTURE.put(ClickEvent.Action.SUGGEST_COMMAND, net.kyori.adventure.text.event.ClickEvent.Action.SUGGEST_COMMAND);

        ADVENTURE_ACTIONS_TO_NATIVE = new EnumMap<>(net.kyori.adventure.text.event.ClickEvent.Action.class);
        ADVENTURE_ACTIONS_TO_NATIVE.put(net.kyori.adventure.text.event.ClickEvent.Action.CHANGE_PAGE, ClickEvent.Action.CHANGE_PAGE);
        ADVENTURE_ACTIONS_TO_NATIVE.put(net.kyori.adventure.text.event.ClickEvent.Action.COPY_TO_CLIPBOARD, ClickEvent.Action.COPY_TO_CLIPBOARD);
        ADVENTURE_ACTIONS_TO_NATIVE.put(net.kyori.adventure.text.event.ClickEvent.Action.OPEN_FILE, ClickEvent.Action.OPEN_FILE);
        ADVENTURE_ACTIONS_TO_NATIVE.put(net.kyori.adventure.text.event.ClickEvent.Action.OPEN_URL, ClickEvent.Action.OPEN_URL);
        ADVENTURE_ACTIONS_TO_NATIVE.put(net.kyori.adventure.text.event.ClickEvent.Action.RUN_COMMAND, ClickEvent.Action.RUN_COMMAND);
        ADVENTURE_ACTIONS_TO_NATIVE.put(net.kyori.adventure.text.event.ClickEvent.Action.SUGGEST_COMMAND, ClickEvent.Action.SUGGEST_COMMAND);
    }

    public static ClickEvent toNative(net.kyori.adventure.text.event.ClickEvent event) {
        return new ClickEvent(ADVENTURE_ACTIONS_TO_NATIVE.get(event.action()), event.value());
    }

    public static net.kyori.adventure.text.event.ClickEvent toAdventure(ClickEvent event) {
        return net.kyori.adventure.text.event.ClickEvent.clickEvent(NATIVE_ACTIONS_TO_ADVENTURE.get(event.getAction()), event.getValue());
    }
}
