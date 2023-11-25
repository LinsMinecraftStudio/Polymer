package io.github.linsminecraftstudio.polymer.command.presets.sub;

import io.github.linsminecraftstudio.polymer.command.SubCommand;
import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.utils.ListUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class SubOptionCommand extends SubCommand implements ICommand.IOptionCommand {
    public SubOptionCommand(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean containsOption(String token) {
        String finalToken = "-" + token;
        return ListUtil.getIf(getArgs(), str -> str.equals(finalToken)).isPresent();
    }

    @Override
    @Nullable
    public String getOptionValue(String token) {
        String finalToken = "-" + token;
        List<String> strings = getArgs().getStream().toList();
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
}
