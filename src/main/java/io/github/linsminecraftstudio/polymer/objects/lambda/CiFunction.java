package io.github.linsminecraftstudio.polymer.objects.lambda;

import java.util.Objects;
import java.util.function.Function;

@FunctionalInterface
public interface CiFunction <T, U, C, R>{
    R apply(T t, U u, C c);

    default <V> CiFunction<T, U, C, V> andThen(Function<? super R,? extends V> after) {
        Objects.requireNonNull(after);
        return (t, u, c) -> after.apply(this.apply(t, u, c));
    }
}
