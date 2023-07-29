package io.github.linsminecraftstudio.polymer.objects;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * A record just stores args and used for formatting or replacement
 * @param args
 */
public record Arguments(Object... args) {
    public boolean isEmpty() {
        return args.length == 0;
    }

    public Stream<Object> getStream(){
        return Arrays.stream(args);
    }
}
