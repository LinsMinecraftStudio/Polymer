package io.github.linsminecraftstudio.polymer.objects.array;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

public record SimpleTypeArray<T> (@NotNull T... args) implements IArray<T>, Iterable<T> {
    @SafeVarargs
    public SimpleTypeArray {}

    public boolean isEmpty() {
        return args.length == 0;
    }

    public Stream<T> getStream(){
        return Arrays.stream(args);
    }

    @Override
    public T get(int index) {
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
}
