package io.github.linsminecraftstudio.polymer.objectutils.translation;

import java.util.function.Function;

/**
 * For translate string.
 */
@FunctionalInterface
public interface TranslationFunction<T> extends Function<T, T> {

    enum Priority {
        HIGHEST(2),
        HIGH(1),
        NORMAL(0),
        LOW(-1),
        LOWEST(-2);

        private final int i;

        Priority(int i) {
            this.i = i;
        }

        public int getAsInt() {
            return i;
        }
    }
}