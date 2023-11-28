package io.github.linsminecraftstudio.polymer.objects.other;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class StoreableConsumer<T> implements Consumer<T> {
    private T value;

    public final void accept(T t) {
        this.value = t;

        handleAccept(t);
    }

    public abstract void handleAccept(T value);

    public final @Nullable T getValue() {
        return value;
    }
}
