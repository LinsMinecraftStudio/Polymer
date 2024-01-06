package io.github.linsminecraftstudio.polymer.objectutils.array;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;


public record ObjectArray(@NotNull Object... args) implements IArray<Object>, Iterable<Object>{

    public boolean isEmpty() {
        return args.length == 0;
    }

    public Stream<Object> getStream(){
        return Arrays.stream(args);
    }

    @Override
    public Object get(int index) {
        return args[index];
    }

    @Override
    public int size() {
        return args.length;
    }

    @NotNull
    @Override
    public Iterator<Object> iterator() {
        return getStream().iterator();
    }
}
