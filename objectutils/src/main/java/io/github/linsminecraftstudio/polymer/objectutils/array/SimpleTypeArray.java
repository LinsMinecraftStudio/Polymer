package io.github.linsminecraftstudio.polymer.objectutils.array;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

public record SimpleTypeArray<T>(@NotNull T... args) implements IArray<T>, Iterable<T>, Cloneable {
    @SafeVarargs
    public SimpleTypeArray {}

    public boolean isEmpty() {
        return args.length == 0;
    }

    public Stream<T> getStream(){
        return Arrays.stream(args);
    }

    @Override
    @Nullable
    public T get(int index) {
        if (index < 0 || index >= args.length) {
            return null;
        }
        return args[index];
    }

    @Override
    public int size() {
        return args.length;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return getStream().iterator();
    }

    @SneakyThrows
    @Override
    public SimpleTypeArray<T> clone() {
        super.clone();
        return new SimpleTypeArray<>(args);
    }
}
