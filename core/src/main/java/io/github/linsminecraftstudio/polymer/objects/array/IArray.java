package io.github.linsminecraftstudio.polymer.objects.array;

import java.util.stream.Stream;

public interface IArray<T> {
    boolean isEmpty();
    Stream<T> getStream();
    T get(int index);
    int size();
}