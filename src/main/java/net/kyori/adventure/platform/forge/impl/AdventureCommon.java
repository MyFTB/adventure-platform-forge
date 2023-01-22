package net.kyori.adventure.platform.forge.impl;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.Adventure;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.forge.ComponentArgumentType;
import net.kyori.adventure.platform.forge.KeyArgumentType;
import net.kyori.adventure.platform.forge.PlayerLocales;
import net.kyori.adventure.platform.forge.impl.server.ForgeServerAudiencesImpl;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.pointer.Pointers;
import net.kyori.adventure.text.KeybindComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.translation.Translator;
import net.minecraft.client.KeyMapping;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraft.locale.Language;
import net.kyori.adventure.text.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod(AdventureCommon.MOD_ID)
public class AdventureCommon {
    public static final String MOD_ID = "adventure_platform_forge";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final ComponentFlattener FLATTENER;
    private static final Pattern LOCALIZATION_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?s");

    static {
        final ComponentFlattener.Builder flattenerBuilder = ComponentFlattener.basic().toBuilder();


        if (FMLLoader.getDist() == Dist.CLIENT) {
            flattenerBuilder.mapper(KeybindComponent.class, keybind -> KeyMapping.createNameSupplier(keybind.keybind()).get().getContents());
        }

        flattenerBuilder.complexMapper(TranslatableComponent.class, (translatable, consumer) -> {
            final String key = translatable.key();
            for (final Translator registry : GlobalTranslator.translator().sources()) {
                if (registry instanceof TranslationRegistry && ((TranslationRegistry) registry).contains(key)) {
                    consumer.accept(GlobalTranslator.render(translatable, Locale.getDefault()));
                    return;
                }
            }

            final @NotNull String translated = Language.getInstance().getOrDefault(key);
            final Matcher matcher = LOCALIZATION_PATTERN.matcher(translated);
            final List<Component> args = translatable.args();
            int argPosition = 0;
            int lastIdx = 0;
            while (matcher.find()) {
                // append prior
                if (lastIdx < matcher.start()) consumer.accept(Component.text(translated.substring(lastIdx, matcher.start())));
                lastIdx = matcher.end();

                final @Nullable String argIdx = matcher.group(1);
                // calculate argument position
                if (argIdx != null) {
                    try {
                        final int idx = Integer.parseInt(argIdx) - 1;
                        if (idx < args.size()) {
                            consumer.accept(args.get(idx));
                        }
                    } catch (final NumberFormatException ex) {
                        // ignore, drop the format placeholder
                    }
                } else {
                    final int idx = argPosition++;
                    if (idx < args.size()) {
                        consumer.accept(args.get(idx));
                    }
                }
            }

            // append tail
            if (lastIdx < translated.length()) {
                consumer.accept(Component.text(translated.substring(lastIdx)));
            }
        });

        FLATTENER = flattenerBuilder.build();
    }

    public AdventureCommon() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    static ResourceLocation res(final @NotNull String value) {
        return new ResourceLocation(Adventure.NAMESPACE, value);
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        ArgumentTypes.register("adventure:component", ComponentArgumentType.class, new ComponentArgumentTypeSerializer());
        ArgumentTypes.register("adventure:key", KeyArgumentType.class, new EmptyArgumentSerializer<>(KeyArgumentType::key));
    }

    public static Function<Pointered, Locale> localePartition() {
        return ptr -> ptr.getOrDefault(Identity.LOCALE, Locale.US);
    }

    @SubscribeEvent
    public void registerCommands(PlayerLocales.LocaleChangedEvent event) {
        ForgeServerAudiencesImpl.forEachInstance(instance -> {
            instance.bossBars().refreshTitles(event.player());
        });
    }

    public static Pointered pointered(final FPointered pointers) {
        return pointers;
    }

    @FunctionalInterface
    interface FPointered extends Pointered {
        @Override
        @NotNull
        Pointers pointers();
    }
}
