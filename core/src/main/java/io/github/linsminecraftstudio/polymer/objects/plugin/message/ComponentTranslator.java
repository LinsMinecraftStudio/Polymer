package io.github.linsminecraftstudio.polymer.objects.plugin.message;

import io.github.linsminecraftstudio.polymer.objectutils.translation.ContextTranslator;
import net.kyori.adventure.text.Component;

import java.util.Map;

public class ComponentTranslator extends ContextTranslator<String, Component> {
    @Override
    protected Component replace(Component context) {
        for (Map.Entry<String, Component> entry : replacements.entrySet()) {
            context = context.replaceText(b -> b.match(entry.getKey()).replacement(entry.getValue()));
        }
        return context;
    }
}
