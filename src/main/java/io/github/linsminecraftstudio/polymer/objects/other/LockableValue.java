package io.github.linsminecraftstudio.polymer.objects.other;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@Getter
public class LockableValue<T> {
    private boolean locked = false;
    public @Nullable T value;

    public LockableValue() {
        this.value = null;
    }

    public LockableValue(@Nullable T value) {
        this.value = value;
    }

    public LockableValue(Supplier<T> supplier) {
        this.value = supplier.get();
    }

    public void set(T value) {
        if (this.value != value && this.locked) {
            throw new IllegalStateException("Cannot set a locked value");
        }
        this.value = value;
    }

    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }
}
