package io.github.linsminecraftstudio.polymer.command.presets.sub;

import io.github.linsminecraftstudio.polymer.command.SubCommand;
import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.utils.ListUtil;
import org.jetbrains.annotations.NotNull;

public abstract class SubAppendableArgumentCommand extends SubCommand implements ICommand.IAppendableArgumentsCommand {
    public SubAppendableArgumentCommand(@NotNull String name) {
        super(name);
    }

    @Override
    public boolean containsArgument(String token) {
        String finalToken = "-" + token;
        return ListUtil.getIf(getArgs(), str -> str.equals(finalToken)).isPresent();
    }
}
