package io.github.linsminecraftstudio.polymer.objects.array;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Stream;

public record TwoTypesArray<T, U>(@NotNull T[] first,@NotNull U[] second){
    public TwoTypesArray {}

    public TwoTypesArray(@NotNull SimpleTypeArray<T> first,@NotNull SimpleTypeArray<U> second) {
        this(first.args(), second.args());
    }

    public boolean isEmpty() {
        return first.length == 0 && second.length == 0;
    }

    public Stream<T> getFirstStream() {
        return Arrays.stream(first);
    }

    public Stream<U> getSecondStream() {
        return Arrays.stream(second);
    }

    public T getFromFirst(int index) {
        return first[index];
    }

    public U getFromSecond(int index) {
        return second[index];
    }

    public int sizeFirst() {
        return first.length;
    }

    public int sizeSecond() {
        return second.length;
    }
}
