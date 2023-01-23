package net.kyori.adventure.platform.forge.impl.server;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.util.Objects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.forge.ForgeAudiences;
import net.kyori.adventure.platform.forge.impl.GameEnums;
import net.kyori.adventure.platform.forge.impl.bridge.PointerProviderBridge;
import net.kyori.adventure.platform.forge.impl.server.bridge.ServerPlayerBridge;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundCustomSoundPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class ServerPlayerAudience implements Audience {
    private final ServerPlayer player;
    private final ServerPlayerBridge playerBridge;
    private final ForgeServerAudiencesImpl controller;

    public ServerPlayerAudience(final ServerPlayer player, final ForgeServerAudiencesImpl controller) {
        this.player = player;
        this.playerBridge = new ServerPlayerBridge(player);
        this.controller = controller;
    }

    private static String validateField(final String content, final int length, final String name) {
        if (content == null) {
            return content;
        }

        final int actual = content.length();
        if (actual > length) {
            throw new IllegalArgumentException(
                "Field '" + name + "' has a maximum length of " + length + " but was passed '" + content + "', which was " + actual +
                    " characters long.");
        }
        return content;
    }

    static int ticks(final @NotNull Duration duration) {
        return duration.getSeconds() == -1 ? -1 : (int) (duration.toMillis() / 50);
    }

    void sendPacket(final Packet<?> packet) {
        this.player.connection.send(packet);
    }

    @Override
    public void sendMessage(final Identity source, final Component text, final net.kyori.adventure.audience.MessageType type) {
        final ChatType mcType;
        if (type == net.kyori.adventure.audience.MessageType.CHAT) {
            mcType = ChatType.CHAT;
        } else {
            mcType = ChatType.SYSTEM;
        }

        this.player.sendMessage(this.controller.toNative(text), mcType, source.uuid());
    }

    @Override
    public void sendActionBar(final @NotNull Component message) {
        this.sendPacket(new ClientboundSetActionBarTextPacket(this.controller.toNative(message)));
    }

    @Override
    public void showBossBar(final @NotNull BossBar bar) {
        ForgeServerAudiencesImpl.forEachInstance(controller -> {
            if (controller != this.controller) {
                controller.bossBars.unsubscribe(this.player, bar);
            }
        });
        this.controller.bossBars.subscribe(this.player, bar);
    }

    @Override
    public void hideBossBar(final @NotNull BossBar bar) {
        ForgeServerAudiencesImpl.forEachInstance(controller -> controller.bossBars.unsubscribe(this.player, bar));
    }

    @Override
    public void playSound(final @NotNull Sound sound) {
        this.sendPacket(new ClientboundCustomSoundPacket(ForgeAudiences.toNative(sound.name()),
            GameEnums.SOUND_SOURCE.toMinecraft(sound.source()), this.player.position(), sound.volume(), sound.pitch()));
    }

    @Override
    public void playSound(final @NotNull Sound sound, final double x, final double y, final double z) {
        this.sendPacket(new ClientboundCustomSoundPacket(ForgeAudiences.toNative(sound.name()),
            GameEnums.SOUND_SOURCE.toMinecraft(sound.source()), new Vec3(x, y, z), sound.volume(), sound.pitch()));
    }

    @Override
    public void playSound(final @NotNull Sound sound, final Sound.@NotNull Emitter emitter) {
        final Entity targetEntity;
        if (emitter == Sound.Emitter.self()) {
            targetEntity = this.player;
        } else if (emitter instanceof Entity) {
            targetEntity = (Entity) emitter;
        } else {
            throw new IllegalArgumentException("Provided emitter '" + emitter + "' was not Sound.Emitter.self() or an Entity");
        }

        if (!this.player.level.equals(targetEntity.level)) {
            // don't send unless entities are in the same dimension
            return;
        }

        final SoundEvent event = Registry.SOUND_EVENT.getOptional(ForgeAudiences.toNative(sound.name())).orElse(null);
        if (event == null) {
            return;
        }

        this.sendPacket(
            new ClientboundSoundEntityPacket(event, GameEnums.SOUND_SOURCE.toMinecraft(sound.source()), targetEntity, sound.volume(), sound.pitch()));
    }

    @Override
    public void stopSound(final @NotNull SoundStop stop) {
        final @Nullable Key sound = stop.sound();
        final Sound.@Nullable Source src = stop.source();
        final @Nullable SoundSource cat = src == null ? null : GameEnums.SOUND_SOURCE.toMinecraft(src);
        this.sendPacket(new ClientboundStopSoundPacket(sound == null ? null : ForgeAudiences.toNative(sound), cat));
    }

    @Override
    public void openBook(final @NotNull Book book) {
        final ItemStack bookStack = new ItemStack(Items.WRITTEN_BOOK, 1);
        final CompoundTag bookTag = bookStack.getOrCreateTag();
        bookTag.putString(WrittenBookItem.TAG_TITLE,
            validateField(this.adventure$plain(book.title()), WrittenBookItem.TITLE_MAX_LENGTH, WrittenBookItem.TAG_TITLE));
        bookTag.putString(WrittenBookItem.TAG_AUTHOR, this.adventure$plain(book.author()));
        final ListTag pages = new ListTag();
        if (book.pages().size() > WrittenBookItem.MAX_PAGES) {
            throw new IllegalArgumentException(
                "Book provided had " + book.pages().size() + " pages, but is only allowed a maximum of " + WrittenBookItem.MAX_PAGES);
        }
        for (final Component page : book.pages()) {
            pages.add(StringTag.valueOf(validateField(this.adventure$serialize(page), WrittenBookItem.PAGE_LENGTH, "page")));
        }
        bookTag.put(WrittenBookItem.TAG_PAGES, pages);
        bookTag.putBoolean(WrittenBookItem.TAG_RESOLVED, true); // todo: any parseable texts?

        final ItemStack previous = this.player.getInventory().getSelected();
        this.sendPacket(
            new ClientboundContainerSetSlotPacket(-2, this.player.containerMenu.getStateId(), this.player.getInventory().selected, bookStack));
        this.player.openItemGui(bookStack, InteractionHand.MAIN_HAND);
        this.sendPacket(
            new ClientboundContainerSetSlotPacket(-2, this.player.containerMenu.getStateId(), this.player.getInventory().selected, previous));
    }

    private String adventure$plain(final @NotNull Component component) {
        return PlainTextComponentSerializer.plainText().serialize(this.controller.renderer().render(component, this));
    }

    private String adventure$serialize(final @NotNull Component component) {
        return GsonComponentSerializer.gson().serialize(this.controller.renderer().render(component, this));
        //return net.minecraft.network.chat.Component.Serializer.toJson(TextComponentMapper.toNative(component));
    }

    @Override
    public void showTitle(final @NotNull Title title) {
        if (title.subtitle() != Component.empty()) {
            this.sendPacket(new ClientboundSetSubtitleTextPacket(this.controller.toNative(title.subtitle())));
        }

        final Title.@Nullable Times times = title.times();
        if (times != null) {
            final int fadeIn = ticks(times.fadeIn());
            final int fadeOut = ticks(times.fadeOut());
            final int dwell = ticks(times.stay());
            if (fadeIn != -1 || fadeOut != -1 || dwell != -1) {
                this.sendPacket(new ClientboundSetTitlesAnimationPacket(fadeIn, dwell, fadeOut));
            }
        }

        if (title.title() != Component.empty()) {
            this.sendPacket(new ClientboundSetTitleTextPacket(this.controller.toNative(title.title())));
        }
    }

    @Override
    public <T> void sendTitlePart(final @NotNull TitlePart<T> part, @NotNull final T value) {
        Objects.requireNonNull(value, "value");
        if (part == TitlePart.TITLE) {
            this.sendPacket(new ClientboundSetTitleTextPacket(this.controller.toNative((Component) value)));
        } else if (part == TitlePart.SUBTITLE) {
            this.sendPacket(new ClientboundSetSubtitleTextPacket(this.controller.toNative((Component) value)));
        } else if (part == TitlePart.TIMES) {
            final Title.Times times = (Title.Times) value;
            final int fadeIn = ticks(times.fadeIn());
            final int fadeOut = ticks(times.fadeOut());
            final int dwell = ticks(times.stay());
            if (fadeIn != -1 || fadeOut != -1 || dwell != -1) {
                this.sendPacket(new ClientboundSetTitlesAnimationPacket(fadeIn, dwell, fadeOut));
            }
        } else {
            throw new IllegalArgumentException("Unknown TitlePart '" + part + "'");
        }
    }

    @Override
    public void clearTitle() {
        this.sendPacket(new ClientboundClearTitlesPacket(false));
    }

    @Override
    public void resetTitle() {
        this.sendPacket(new ClientboundClearTitlesPacket(true));
    }


    @Override
    public void sendPlayerListHeader(final @NotNull Component header) {
        requireNonNull(header, "header");
        this.playerBridge.updateTabList(this.controller.toNative(header), null);
    }

    @Override
    public void sendPlayerListFooter(final @NotNull Component footer) {
        requireNonNull(footer, "footer");
        this.playerBridge.updateTabList(null, this.controller.toNative(footer));

    }

    @Override
    public void sendPlayerListHeaderAndFooter(final @NotNull Component header, final @NotNull Component footer) {
        this.playerBridge.updateTabList(
            this.controller.toNative(requireNonNull(header, "header")),
            this.controller.toNative(requireNonNull(footer, "footer")));
    }

    @Override
    public @NotNull Pointers pointers() {
        return new PointerProviderBridge(player).pointers();
    }
}
