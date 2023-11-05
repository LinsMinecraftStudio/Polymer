package io.github.linsminecraftstudio.polymer.command.presets;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.utils.ListUtil;
import org.jetbrains.annotations.NotNull;

public abstract class AppendArgumentCommand extends PolymerCommand implements ICommand.IAppendableArgumentsCommand {
    public AppendArgumentCommand(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean containsArgument(String token) {
        String finalToken = "-" + token;
        return ListUtil.getIf(arguments, str -> str.equals(finalToken)).isPresent();
    }
}
