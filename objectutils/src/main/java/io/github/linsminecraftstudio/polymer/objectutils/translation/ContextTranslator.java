package io.github.linsminecraftstudio.polymer.objectutils.translation;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * For improve message customize.
 *
 * @param <S> sender
 * @param <M> match contexts' type
 * @param <T> basic type
 * @author lijinhong11(mmmjjkx)
 */
public abstract class ContextTranslator<S, M, T> {
    protected final Map<M, M> replacements = new HashMap<>();
    protected final Map<TranslationFunction<S, M>, TranslationFunction.Priority> functions = new HashMap<>();

    @Setter
    @Getter
    private boolean replaceWordsFirst;

    public final void addFunction(TranslationFunction<S, M> function, TranslationFunction.Priority priority) {
        functions.put(function, priority);
    }

    public final void addFunctions(Map<TranslationFunction<S, M>, TranslationFunction.Priority> functions) {
        this.functions.putAll(functions);
    }

    @SafeVarargs
    public final void addFunctions(TranslationFunction.Priority priority, TranslationFunction<S, M>... functions) {
        for (TranslationFunction<S, M> function : functions) {
            addFunction(function, priority);
        }
    }

    public void addReplacement(M word, M replacement) {
        replacements.put(word, replacement);
    }

    public void addReplacements(M[] words, M[] replacements) {
        for (int i = 0; i < words.length; i++) {
            addReplacement(words[i], replacements[i]);
        }
    }

    public void addReplacements(Map<M, M> replacements) {
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
    protected T translate(S object, M object2) {
        boolean state = replaceWordsFirst;
        AtomicReference<M> context = new AtomicReference<>(object2);

        if (state) {
            context.set(replace(context.get()));
        }

        Arrays.stream(TranslationFunction.Priority.values()).forEach((p) -> {
            for (TranslationFunction<S, M> fun : getAll(p)) {
                context.set(fun.apply(object, context.get()));
            }
        });

        state = !state;

        if (state) {
            context.set(replace(context.get()));
        }

        return toT(context.get());
    }

    protected T reTranslate(S object, T object2) {
        boolean state = replaceWordsFirst;
        AtomicReference<M> context = new AtomicReference<>(toM(object2));

        if (state) {
            context.set(replace(context.get()));
        }

        Arrays.stream(TranslationFunction.Priority.values()).forEach((p) -> {
            for (TranslationFunction<S, M> fun : getAll(p)) {
                context.set(fun.apply(object, context.get()));
            }
        });

        state = !state;

        if (state) {
            context.set(replace(context.get()));
        }

        return toT(context.get());
    }

    protected abstract T toT(M context);

    protected abstract M toM(T object);

    protected abstract M replace(M context);

    private List<TranslationFunction<S, M>> getAll(TranslationFunction.Priority priority) {
        List<TranslationFunction<S, M>> list = new ArrayList<>();
        functions.forEach((fun, p) -> {
            if (p.getAsInt() == priority.getAsInt()) {
                list.add(fun);
            }
        });
        return list;
    }
}
