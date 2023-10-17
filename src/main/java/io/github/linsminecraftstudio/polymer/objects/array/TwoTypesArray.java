package io.github.linsminecraftstudio.polymer.objects.array;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.stream.Stream;

public record TwoTypesArray<T, U>(T[] first, U[] second){
    @ParametersAreNonnullByDefault
    public TwoTypesArray {}

    @ParametersAreNonnullByDefault
    public TwoTypesArray(SimpleTypeArray<T> first, SimpleTypeArray<U> second) {
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
