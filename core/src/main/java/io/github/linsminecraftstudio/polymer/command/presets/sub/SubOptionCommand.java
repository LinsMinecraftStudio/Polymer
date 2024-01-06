package io.github.linsminecraftstudio.polymer.command.presets.sub;

import io.github.linsminecraftstudio.polymer.command.SubCommand;
import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.objectutils.array.SimpleTypeArray;
import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class SubOptionCommand extends SubCommand implements ICommand.IOptionCommand {
    public SubOptionCommand(@NotNull String name) {
        super(name);
    }

    private SimpleTypeArray<String> pre;

    @Override
    public boolean containsOption(String token) {
        String finalToken = tokenHead + token;
        return IterableUtil.getIf(getArgs(), str -> str.startsWith(finalToken)).isPresent();
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
    public final void beforeExecute() {
        this.pre = this.args;

        this.args = new SimpleTypeArray<>(
                this.args.getStream().filter(str -> !str.startsWith(tokenHead)).toArray(String[]::new)
        );
    }
}
