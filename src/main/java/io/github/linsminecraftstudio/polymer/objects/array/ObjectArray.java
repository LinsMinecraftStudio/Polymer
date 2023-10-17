package io.github.linsminecraftstudio.polymer.objects.array;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.stream.Stream;


public record ObjectArray(Object... args) implements IArray<Object>{
    @ParametersAreNonnullByDefault
    public ObjectArray {}

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
}
