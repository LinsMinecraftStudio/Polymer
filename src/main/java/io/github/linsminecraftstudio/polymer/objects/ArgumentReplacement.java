package io.github.linsminecraftstudio.polymer.objects;

/**
 * A record just stores args and used for formatting or replacement
 * @param args
 */
public record ArgumentReplacement(Object... args) {
    public boolean isEmpty() {
        return args.length == 0;
    }
}
