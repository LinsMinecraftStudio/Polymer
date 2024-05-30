package io.github.linsminecraftstudio.bungee.command;

import io.github.linsminecraftstudio.bungee.Constants;
import io.github.linsminecraftstudio.bungee.utils.StringUtil;
import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public abstract class PolymerBungeeCommand extends Command implements TabExecutor {
    private final Map<String, CommandArgumentType> argumentWithTypes;
    private final Map<String, SubBungeeCommand> subCommands;

    public PolymerBungeeCommand(String name) {
        this(name, null);
    }

    public PolymerBungeeCommand(String name, String permission, String... aliases) {
        super(name, permission, aliases);

        this.argumentWithTypes = new HashMap<>();
        this.subCommands = new HashMap<>();
    }

    public final Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return copyPartialMatches(args[0], subCommands.values().stream().map(SubBungeeCommand::getName).collect(Collectors.toList()));
        } else if (args.length > 1) {
            String sub = args[0];
            String[] args2 = Arrays.copyOfRange(args, 1, args.length);
            if (subCommands.containsKey(sub)) {
                Map<Integer, List<String>> map = subCommands.get(sub).tabCompletions(sender, args2);
                if (map != null && !map.isEmpty()) {
                    return copyPartialMatches(args[args.length - 1], map.get(args.length - 2));
                }
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }

    public final void execute(CommandSender cs, String[] args) {
        if (args.length == 0) {
            defaultExecute(cs, args);
            return;
        }

        SubBungeeCommand subCommand = subCommands.get(args[0]);
        if (subCommand != null) {
            String[] args2 = Arrays.copyOfRange(args, 1, args.length);
            subCommand.run(cs, args2);
        } else {
            defaultExecute(cs, args);
        }
    }

    public abstract void defaultExecute(CommandSender cs, String[] args);

    protected ProxiedPlayer findPlayer(String name) {
        return ProxyServer.getInstance().getPlayer(name);
    }

    protected double toDouble(String s, boolean allowNegative) {
        try {
            double d = Double.parseDouble(s);
            if (!allowNegative && d < 0) {
                throw new NumberFormatException();
            }
            return d;
        } catch (NumberFormatException e) {
            return Constants.ERROR_CODE;
        }
    }

    protected int toInt(String s, boolean allowNegative) {
        try {
            int i = Integer.parseInt(s);
            if (!allowNegative && i < 0) {
                throw new NumberFormatException();
            }
            return i;
        } catch (NumberFormatException e) {
            return Constants.ERROR_CODE;
        }
    }

    protected void addArgument(String name, CommandArgumentType type) {
        argumentWithTypes.put(name, type);
    }

    public final @Nonnull String getUsage() {
        Map<String, CommandArgumentType> options = new HashMap<>();
        List<Map.Entry<String, CommandArgumentType>> arguments = argumentWithTypes.entrySet().stream().filter(e -> {
            if (e.getValue() == CommandArgumentType.USABLE_OPTION) {
                options.put(e.getKey(), e.getValue());
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(getName()).append(" ");
        if (!arguments.isEmpty()) {
            for (Map.Entry<String, CommandArgumentType> argument : arguments) {
                if (argument.getValue() == CommandArgumentType.OPTIONAL) {
                    sb.append("[");
                    sb.append(argument.getKey());
                    sb.append("]");
                } else if (argument.getValue() == CommandArgumentType.REQUIRED) {
                    sb.append("<");
                    sb.append(argument.getKey());
                    sb.append(">");
                }
                sb.append(" ");
            }
        }

        if (!options.isEmpty()) {
            sb.append(" ");
            sb.append("{");

            for (Map.Entry<String, CommandArgumentType> option : options.entrySet()) {
                sb.append(option.getKey());
                sb.append(" ");
            }

            sb.append("}");
        }

        return sb.toString();
    }

    protected List<String> copyPartialMatches(@Nonnull String token, Iterable<String> original) {
        if (original == null) {
            return new ArrayList<>();
        }
        return StringUtil.copyPartialMatches(token, original, new ArrayList<>());
    }

    void addSubCommand(SubBungeeCommand command) {
        subCommands.put(command.getName(), command);
    }
}
