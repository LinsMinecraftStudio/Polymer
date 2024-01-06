package io.github.linsminecraftstudio.polymer.objectutils.array;

import java.util.stream.Stream;

public interface IArray<T> {
    boolean isEmpty();
    Stream<T> getStream();
    T get(int index);
    int size();
}
