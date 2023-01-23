package net.kyori.adventure.platform.forge;

import static java.util.Objects.requireNonNull;

import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import net.kyori.adventure.Adventure;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.Index;
import net.minecraft.commands.arguments.ComponentArgument;
import org.jetbrains.annotations.NotNull;

public class ComponentArgumentType implements ArgumentType<Component> {

    private static final ComponentArgumentType JSON_INSTANCE = new ComponentArgumentType(Format.JSON);
    private static final ComponentArgumentType MINIMESSAGE_INSTANCE = new ComponentArgumentType(Format.MINIMESSAGE);

    private final Format format;

    private ComponentArgumentType(final Format format) {
        this.format = requireNonNull(format, "format");
    }

    /**
     * Get the argument type for component arguments.
     *
     * @return argument type instance
     * @since 4.0.0
     * @deprecated use {@link #json()} or {@link #miniMessage()} instead.
     */
    @Deprecated(forRemoval = true, since = "5.1.0")
    public static @NotNull ComponentArgumentType component() {
        return JSON_INSTANCE;
    }

    /**
     * Get the component from the provided context.
     *
     * @param ctx Context to get from
     * @param key argument key
     * @return parsed component
     * @since 4.0.0
     */
    public static @NotNull Component component(final @NotNull CommandContext<?> ctx, final @NotNull String key) {
        return ctx.getArgument(key, Component.class);
    }

    /**
     * Get the argument type for component arguments in JSON format.
     *
     * @return argument type instance
     * @since 5.1.0
     */
    public static @NotNull ComponentArgumentType json() {
        return JSON_INSTANCE;
    }

    /**
     * Get the argument type for component arguments in MiniMessage format.
     *
     * @return argument type instance
     * @since 5.1.0
     */
    public static @NotNull ComponentArgumentType miniMessage() {
        return MINIMESSAGE_INSTANCE;
    }

    /**
     * Get an argument type for component arguments.
     *
     * @param format the format to use when parsing component text
     * @return an argument type
     * @since 5.1.0
     */
    public static @NotNull ComponentArgumentType component(final @NotNull Format format) {
        return switch (format) {
            case JSON -> JSON_INSTANCE;
            case MINIMESSAGE -> MINIMESSAGE_INSTANCE;
        };
    }

    @Override
    public @NotNull Component parse(final @NotNull StringReader reader) throws CommandSyntaxException {
        final String remaining = reader.getRemaining();
        try {
            final ReadResult result = this.format.parse(remaining);
            reader.setCursor(reader.getCursor() + result.charsConsumed());
            return result.parsed();
        } catch (final Exception ex) {
            final String message = ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage();
            throw ComponentArgument.ERROR_INVALID_JSON.createWithContext(reader, message);
        }
    }

    @Override
    public @NotNull Collection<String> getExamples() {
        return this.format.examples();
    }

    /**
     * Get the format used for this argument.
     *
     * @return the format used for this argument
     * @since 4.1.0
     */
    public @NotNull Format format() {
        return this.format;
    }

    /**
     * Supported text formats for registering components.
     *
     * @since 5.1.0
     */
    public enum Format {
        JSON(
            Key.key(Adventure.NAMESPACE, "json"),
            "\"Hello world!\"",
            "[\"Message\", {\"text\": \"example\", \"color\": \"#aabbcc\"}]"
        ) {
            @Override
            ReadResult parse(final String allInput) throws Exception {
                try (final JsonReader json = new JsonReader(new java.io.StringReader(allInput))) {
                    final Component ret = net.minecraft.network.chat.Component.Serializer.GSON.fromJson(json, Component.class);
                    return new ReadResult(ret, net.minecraft.network.chat.Component.Serializer.JSON_READER_POS.getInt(json));
                }
            }
        },
        MINIMESSAGE(
            Key.key(Adventure.NAMESPACE, "minimessage/v1"),
            "<rainbow>hello world!",
            "hello <bold>everyone</bold> here!",
            "hello <hover:show_text:'sneak sneak'>everyone</hover> who likes <blue>cats"
        ) {
            @Override
            ReadResult parse(final String allInput) throws Exception {
                final Component parsed = MiniMessage.miniMessage().deserialize(allInput);
                return new ReadResult(parsed, allInput.length());
            }
        };
        public static final Index<Key, Format> INDEX = Index.create(Format.class, Format::id);

        private final Key id;
        private final List<String> examples;

        Format(final Key id, final String... examples) {
            this.id = id;
            this.examples = List.of(examples);
        }

        abstract ReadResult parse(final String allInput) throws Exception;

        /**
         * Get a unique identifier for this format.
         *
         * @return a unique identifier for this format
         * @since 5.1.0
         */
        public Key id() {
            return this.id;
        }

        /**
         * Get examples of this format in use.
         *
         * @return examples of this format in use, as an unmodifiable list
         * @since 5.1.0
         */
        public List<String> examples() {
            return this.examples;
        }
    }

    /**
     * The result of reading a component.
     *
     * @param parsed        the parsed component
     * @param charsConsumed the number of characters consumed from the input string
     */
    record ReadResult(Component parsed, int charsConsumed) {
    }
}
