package io.github.linsminecraftstudio.polymer.objectutils.translation;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * For improve message customize.
 *
 * @param <M> match contexts' type
 * @param <T> basic type
 * @author lijinhong11(mmmjjkx)
 */
public abstract class ContextTranslator<M, T> {
    protected final Map<M, T> replacements = new HashMap<>();
    protected final Map<TranslationFunction<T>, TranslationFunction.Priority> functions = new HashMap<>();

    @Setter
    @Getter
    private boolean replaceWordsFirst;

    public static <T> T translate(T context, TranslationFunction<T> function) {
        return function.apply(context);
    }

    public final void addFunction(TranslationFunction<T> function, TranslationFunction.Priority priority) {
        functions.put(function, priority);
    }

    public final void addFunctions(Map<TranslationFunction<T>, TranslationFunction.Priority> functions) {
        this.functions.putAll(functions);
    }

    @SafeVarargs
    public final void addFunctions(TranslationFunction.Priority priority, TranslationFunction<T>... functions) {
        for (TranslationFunction<T> function : functions) {
            addFunction(function, priority);
        }
    }

    public void addReplacement(M word, T replacement) {
        replacements.put(word, replacement);
    }

    public void addReplacements(M[] words, T[] replacements) {
        for (int i = 0; i < words.length; i++) {
            addReplacement(words[i], replacements[i]);
        }
    }

    public void addReplacements(Map<M, T> replacements) {
        this.replacements.putAll(replacements);
    }

    /**
     * Translate the given object. <br>
     * <p>
     * Default is protected, you can decide it is public or protected.
     *
     * @param object the object
     * @return translated object
     */
    protected T translate(T object) {
        boolean state = replaceWordsFirst;
        AtomicReference<T> context = new AtomicReference<>(object);

        if (state) {
            context.set(replace(context.get()));
        }

        Arrays.stream(TranslationFunction.Priority.values()).forEach((p) -> {
            for (TranslationFunction<T> fun : getAll(p)) {
                context.set(fun.apply(context.get()));
            }
        });

        state = !state;

        if (state) {
            context.set(replace(context.get()));
        }

        return context.get();
    }

    protected abstract T replace(T context);

    private List<TranslationFunction<T>> getAll(TranslationFunction.Priority priority) {
        List<TranslationFunction<T>> list = new ArrayList<>();
        functions.forEach((fun, p) -> {
            if (p.getAsInt() == priority.getAsInt()) {
                list.add(fun);
            }
        });
        return list;
    }
}
