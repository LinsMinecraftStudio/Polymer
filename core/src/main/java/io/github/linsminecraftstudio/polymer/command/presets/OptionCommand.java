package io.github.linsminecraftstudio.polymer.command.presets;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.objects.array.SimpleTypeArray;
import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The command preset for add custom options
 * The format is '-O:THE_TOKEN=THE_VALUE'
 */
public abstract class OptionCommand extends PolymerCommand implements ICommand.IOptionCommand {
    public OptionCommand(@NotNull String name) {
        super(name);
    }

    private SimpleTypeArray<String> pre;

    @Override
    public boolean containsOption(String token) {
        String finalToken = tokenHead + token;
        return IterableUtil.getIf(pre, str -> str.startsWith(finalToken)).isPresent();
    }

    @Override
    @Nullable
    public String getOptionValue(String token) {
        String finalToken = tokenHead + token;
        if (containsOption(token)) {
            Optional<String> value = IterableUtil.getIf(pre, str -> str.startsWith(finalToken));
            if (value.isPresent()) {
                String original = value.get();
                original = original.replaceFirst(finalToken, "");
                return original.replaceFirst("=", "");
            }
            return null;
        }
        return null;
    }

    @Override
    public final void beforeAllExecute() {
        pre = this.arguments;

        this.arguments = new SimpleTypeArray<>(
                this.arguments.getStream().filter(str -> !str.startsWith(tokenHead)).toArray(String[]::new)
        );
    }
}
