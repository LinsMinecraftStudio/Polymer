package io.github.linsminecraftstudio.polymer.objects.array;

import java.util.Arrays;
import java.util.stream.Stream;

public record SimpleTypeArray<T> (T... args) implements IArray<T>{
    @SafeVarargs
    public SimpleTypeArray {}

    public boolean isEmpty() {
        return args.length == 0;
    }
    public Stream<Object> getStream(){
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
}
