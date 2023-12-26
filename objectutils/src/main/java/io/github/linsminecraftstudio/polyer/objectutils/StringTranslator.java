package io.github.linsminecraftstudio.polyer.objectutils;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class StringTranslator {
    private final String def;
    private final Map<String, String> replacements = new HashMap<>();
    private String context = "";
    private Map<TranslationFunction, TranslationFunction.Priority> functions = new HashMap<>();
    private boolean replaceWordsFirst = true;

    public StringTranslator() {
        def = "";
    }

    public StringTranslator(String def) {
        this(def, new HashMap<>());
    }

    public StringTranslator(String def, Map<TranslationFunction, TranslationFunction.Priority> functions) {
        this(def, true, functions);
    }

    public StringTranslator(String def, boolean replaceWordsFirst, Map<TranslationFunction, TranslationFunction.Priority> functions) {
        this.def = def;
        this.functions = functions;
        this.replaceWordsFirst = replaceWordsFirst;
    }

    public StringTranslator(String def, boolean replaceWordsFirst) {
        this(def, replaceWordsFirst, new HashMap<>());
    }

    public static String translate(String context, TranslationFunction function) {
        return function.apply(context);
    }

    public void addFunction(TranslationFunction function, TranslationFunction.Priority priority) {
        functions.put(function, priority);
    }

    public void addFunctions(Map<TranslationFunction, TranslationFunction.Priority> functions) {
        this.functions.putAll(functions);
    }

    public void addFunctions(TranslationFunction.Priority priority, TranslationFunction... functions) {
        for (TranslationFunction function : functions) {
            addFunction(function, priority);
        }
    }

    public void addReplacement(String word, String replacement) {
        replacements.put(word, replacement);
    }

    public void addReplacements(String[] words, String[] replacements) {
        for (int i = 0; i < words.length; i++) {
            addReplacement(words[i], replacements[i]);
        }
    }

    public void addReplacements(Map<String, String> replacements) {
        this.replacements.putAll(replacements);
    }

    public String translate() {
        boolean state = replaceWordsFirst;
        context = def;

        if (state) {
            context = replace(context);
        }

        Arrays.stream(TranslationFunction.Priority.values()).forEach((p) -> {
            for (TranslationFunction fun : getAll(p)) {
                context = fun.apply(context);
            }
        });

        state = !state;

        if (state) {
            context = replace(context);
        }

        return context;
    }

    private String replace(String context) {
        AtomicReference<String> string = new AtomicReference<>(context);
        replacements.forEach((w, r) -> string.set(string.get().replaceAll(w, r)));
        return string.get();
    }

    private List<TranslationFunction> getAll(TranslationFunction.Priority priority) {
        List<TranslationFunction> list = new ArrayList<>();
        functions.forEach((fun, p) -> {
            if (p.getAsInt() == priority.getAsInt()) {
                list.add(fun);
            }
        });
        return list;
    }
}
