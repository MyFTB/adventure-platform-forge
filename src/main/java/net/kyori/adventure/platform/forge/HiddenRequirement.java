package net.kyori.adventure.platform.forge;

import java.util.Objects;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;

public record HiddenRequirement<V>(Predicate<V> base) implements Predicate<V> {
    public static <T> HiddenRequirement<T> alwaysAllowed() {
        return new HiddenRequirement<>(t -> true);
    }

    @Override
    public boolean test(final V v) {
        return this.base.test(v);
    }

    @Override
    public @NotNull Predicate<V> and(final @NotNull Predicate<? super V> other) {
        return new HiddenRequirement<>(this.base.and(unwrap(Objects.requireNonNull(other, "other"))));
    }

    @Override
    public @NotNull Predicate<V> negate() {
        return new HiddenRequirement<>(this.base.negate());
    }

    @Override
    public @NotNull Predicate<V> or(final @NotNull Predicate<? super V> other) {
        return new HiddenRequirement<>(this.base.or(unwrap(Objects.requireNonNull(other, "other"))));
    }

    private static <T> @NotNull Predicate<T> unwrap(final @NotNull Predicate<T> pred) {
        return pred instanceof HiddenRequirement<T> req ? req.base : pred;
    }
}
