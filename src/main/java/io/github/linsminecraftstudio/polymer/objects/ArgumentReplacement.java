package io.github.linsminecraftstudio.polymer.objects;

public record ArgumentReplacement(Object... args) {
    public boolean isEmpty() {
        return args.length == 0;
    }

    public static ArgumentReplacement empty(){
        return new ArgumentReplacement();
    }
}
