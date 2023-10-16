package io.github.linsminecraftstudio.polymer.objects.lambda;

import java.util.Objects;

@FunctionalInterface
public interface CiConsumer<T, U, C> {
    void accept(T t, U u, C c);

    default CiConsumer<T, U, C> andThen(CiConsumer<? super T, ? super U, ? super C> after) {
        Objects.requireNonNull(after);
        return (t, u, c) -> {
            this.accept(t, u, c);
            after.accept(t, u, c);
        };
    }
}
