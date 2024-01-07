package io.github.linsminecraftstudio.polymer.objects.plugin.message;

import io.github.linsminecraftstudio.polymer.objectutils.translation.ContextTranslator;
import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class ComponentTranslator extends ContextTranslator<CommandSender, String, Component> {
    @Override
    protected Component toT(String context) {
        return ObjectConverter.toComponent(context);
    }

    @Override
    protected String toM(Component object) {
        return ObjectConverter.componentAsString(object);
    }

    @Override
    protected String replace(String context) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            context = context.replaceAll(entry.getKey(), entry.getValue());
        }
        return context;
    }
}
