package io.github.linsminecraftstudio.polymer.command.presets;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.objects.array.SimpleTypeArray;
import io.github.linsminecraftstudio.polymer.utils.ListUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class OptionCommand extends PolymerCommand implements ICommand.IOptionCommand {
    public OptionCommand(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean containsOption(String token) {
        String finalToken = tokenHead + token;
        return ListUtil.getIf(arguments, str -> str.equals(finalToken)).isPresent();
    }

    @Override
    @Nullable
    public String getOptionValue(String token) {
        String finalToken = tokenHead + token;
        List<String> strings = arguments.getStream().toList();
        if (!strings.contains(finalToken)) {
            return null;
        }
        int index = strings.indexOf(finalToken) + 1;
        try {
            return strings.get(index);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public final void beforeAllExecute() {
        this.arguments = new SimpleTypeArray<>(
                this.arguments.getStream().filter(str -> !str.startsWith(tokenHead)).toArray(String[]::new)
        );
    }
}
