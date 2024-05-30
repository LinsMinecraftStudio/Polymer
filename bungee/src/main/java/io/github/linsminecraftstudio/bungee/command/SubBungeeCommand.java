package io.github.linsminecraftstudio.bungee.command;

import io.github.linsminecraftstudio.bungee.utils.StringUtil;
import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SubBungeeCommand {
    private final Map<String, CommandArgumentType> arguments;
    private final String permission;

    @Getter
    private final String[] aliases;
    @Getter
    private final String name;

    public SubBungeeCommand(PolymerBungeeCommand parent, String name) {
        this(parent, name, null);
    }

    public SubBungeeCommand(PolymerBungeeCommand parent, String name, String permission, String... aliases) {
        parent.addSubCommand(this);

        this.name = name;
        this.arguments = new HashMap<>();
        this.permission = permission;
        this.aliases = aliases;
    }

    protected abstract void run(CommandSender sender, String[] args);

    protected abstract Map<Integer, List<String>> tabCompletions(CommandSender sender, String[] args);

    protected List<String> copyPartialMatches(@Nonnull String token, Iterable<String> original) {
        if (original == null) {
            return new ArrayList<>();
        }
        return StringUtil.copyPartialMatches(token, original, new ArrayList<>());
    }

    public boolean hasPermission(CommandSender sender) {
        return permission == null || sender.hasPermission(permission);
    }

    public void addArgument(String name, CommandArgumentType argument) {
        arguments.put(name, argument);
    }
}
