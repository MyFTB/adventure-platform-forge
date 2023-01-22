package net.kyori.adventure.platform.forge.test;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.platform.forge.ForgeServerAudiences;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("adventure_platform_forge_test")
@Mod.EventBusSubscriber
public class TestMod {

    @SubscribeEvent
    public static void command(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("adventure")
                .then(Commands.literal("help").executes(TestMod::getHelp))
                .then(Commands.literal("message").executes(TestMod::regularMessage))
                .then(Commands.literal("status").executes(TestMod::status))
                .then(Commands.literal("title").executes(TestMod::title))
                .then(Commands.literal("bossbar").executes(TestMod::bossBar))
                .then(Commands.literal("sound").executes(TestMod::sound))
                .then(Commands.literal("book").executes(TestMod::book))
        );
    }


    private static int getHelp(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final UUID uuid = context.getSource().getPlayerOrException().getUUID();
        final Audience audience = ForgeServerAudiences.of(context.getSource().getServer()).player(uuid);
        audience.sendMessage(Component.text("Available tests: "));
        audience.sendMessage(Component.text("  /adventure help (yes this help itself is also a test)"));
        audience.sendMessage(Component.text("  /adventure message - test show chat message"));
        audience.sendMessage(Component.text("  /adventure status - test show action bar message"));
        audience.sendMessage(Component.text("  /adventure title - test show title"));
        audience.sendMessage(Component.text("  /adventure bossbar - test show boss bar"));
        audience.sendMessage(Component.text("  /adventure sound - test play sound"));
        audience.sendMessage(Component.text("  /adventure book - test opening book"));
        return Command.SINGLE_SUCCESS;
    }

    private static int regularMessage(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final UUID uuid = context.getSource().getPlayerOrException().getUUID();
        final Audience audience = ForgeServerAudiences.of(context.getSource().getServer()).player(uuid);
        audience.sendMessage(Component.text("Greetings fellow developer.").style(Style.style(NamedTextColor.GOLD)));
        audience.sendMessage(Component.text("If you see ")
            .append(Component.text("this").style(Style.style(TextDecoration.ITALIC)))
            .append(Component.text(" message, it means that "))
            .append(Component.text("adventure-platform-forge is now available in your environment").style(Style.style(TextDecoration.BOLD)))
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int status(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final UUID uuid = context.getSource().getPlayerOrException().getUUID();
        final Audience audience = ForgeServerAudiences.of(context.getSource().getServer()).player(uuid);
        audience.sendActionBar(Component.text("I AM THE SENATE").style(Style.style(NamedTextColor.AQUA, TextDecoration.STRIKETHROUGH)));
        return Command.SINGLE_SUCCESS;
    }

    private static int title(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final UUID uuid = context.getSource().getPlayerOrException().getUUID();
        final Audience audience = ForgeServerAudiences.of(context.getSource().getServer()).player(uuid);
        audience.showTitle(Title.title(Component.text("Surprise!"), Component.text("A wild title appears!")));
        return Command.SINGLE_SUCCESS;
    }

    private static int bossBar(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final UUID uuid = context.getSource().getPlayerOrException().getUUID();
        final Audience audience = ForgeServerAudiences.of(context.getSource().getServer()).player(uuid);
        audience.showBossBar(
            BossBar.bossBar(Component.text("adventure-platform-forge-test environment").append(Component.text("  appended").color(NamedTextColor.DARK_PURPLE)), 1F, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS));
        return Command.SINGLE_SUCCESS;
    }

    private static int sound(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final UUID uuid = context.getSource().getPlayerOrException().getUUID();
        final Audience audience = ForgeServerAudiences.of(context.getSource().getServer()).player(uuid);
        audience.playSound(Sound.sound(Key.key("minecraft", "entity.experience_orb.pickup"), Sound.Source.AMBIENT, 10F, 2F));
        return Command.SINGLE_SUCCESS;
    }

    private static int book(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final UUID uuid = context.getSource().getPlayerOrException().getUUID();
        final Audience audience = ForgeServerAudiences.of(context.getSource().getServer()).player(uuid);
        final Book book = Book.builder()
            .title(Component.text("User manual"))
            .author(Component.text("3TUSK"))
            .addPage(Component.text("You can use ")
                .append(Component.text("/adventure [action]")
                    .style(Style.style(TextDecoration.UNDERLINED))
                    .clickEvent(ClickEvent.suggestCommand("adventure help"))
                    .hoverEvent(HoverEvent.showText(Component.text("Click to run command /adventure help"))))
                .append(Component.text(" to test each functionality of adventure."))
                .append(Component.newline())
                .append(Component.text("Refer to ")
                    .append(Component.text("adventure documentation")
                        .style(Style.style(TextColor.color(0x0007FF), TextDecoration.UNDERLINED))
                        .clickEvent(ClickEvent.openUrl("https://docs.adventure.kyori.net/"))
                        .hoverEvent(HoverEvent.showText(Component.text("https://docs.adventure.kyori.net/").style(Style.style(NamedTextColor.AQUA)))))
                    .append(Component.text(" for more information about adventure library itself."))))
            .addPage(Component.text("Surprise! This user manual is still WIP! ")
                .style(Style.style(NamedTextColor.DARK_AQUA, TextDecoration.STRIKETHROUGH))
                .append(Component.text("!*@#^").style(Style.style(TextDecoration.OBFUSCATED))))
            .build();
        audience.openBook(book);
        return Command.SINGLE_SUCCESS;
    }
}
