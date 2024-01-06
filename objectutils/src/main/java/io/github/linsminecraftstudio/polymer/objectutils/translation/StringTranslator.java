package io.github.linsminecraftstudio.polymer.objectutils.translation;

import java.util.Map;

public final class StringTranslator extends ContextTranslator<String, String> {
    @Override
    protected String replace(String context) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            context = context.replace(entry.getKey(), entry.getValue());
        }
        return context;
    }

    @Override
    public String translate(String object) {
        return super.translate(object);
    }
}
