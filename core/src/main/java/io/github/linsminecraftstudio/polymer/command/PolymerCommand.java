package io.github.linsminecraftstudio.polymer.command;

import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.objectutils.TuplePair;
import io.github.linsminecraftstudio.polymer.objectutils.array.SimpleTypeArray;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class PolymerCommand extends Command implements ICommand {
    protected PolymerPlugin pluginInstance;
    protected SimpleTypeArray<String> arguments;
    protected CommandSender sender;
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final Map<String, ArgumentType> argumentWithTypes = new HashMap<>();

    public PolymerCommand(@NotNull String name){
        this(name, new ArrayList<>());
    }
    public PolymerCommand(@NotNull String name, List<String> aliases) {
        super(name, "", "", new ArrayList<>());
        try {
            autoSetCMDInfo(aliases);
        } catch (Exception ignored) {
        }
    }


    public String getHelpDescription(){
        return "description unavailable";
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            return copyPartialMatches(args[0], subCommands.values().stream().map(SubCommand::getName).toList());
        } else if (args.length > 1){
            String sub = args[0];
            if (subCommands.containsKey(sub)) {
                Map<Integer, List<String>> map = subCommands.get(sub).tabCompletion(sender);
                if (map != null && !map.isEmpty()) {
                    return copyPartialMatches(args[args.length - 1], map.get(args.length - 2));
                }
                return new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public final boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        this.arguments = new SimpleTypeArray<>(strings);
        this.sender = commandSender;

        beforeAllExecute();

        if (strings.length == 0) {
            execute(commandSender, s);
        } else {
            if (subCommands.isEmpty()) {
                execute(commandSender, s);
                return true;
            }

            String subName = strings[0];
            if (subCommands.containsKey(subName)) {
                SubCommand subCommand = subCommands.get(subName);
                subCommand.run(commandSender, Arrays.copyOfRange(strings, 1, strings.length), pluginInstance);
            } else {
                execute(commandSender, s);
            }
        }

        afterAllExecute();
        return true;
    }

    /**
     * run it before execute the command
     */
    public void beforeAllExecute() {
    }


    /**
     * run it after execute the command
     */
    public void afterAllExecute() {
    }

    public final void sendMessage(String key, Object... args) {
        if (pluginInstance != null) {
            sendMessage(sender, key, args);
        }
    }

    public final void sendMessage(CommandSender sender, String key, Object... args) {
        if (pluginInstance != null) {
            pluginInstance.getMessageHandler().sendMessage(sender, key, args);
        }
    }

    public final void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName(), subCommand);
    }

    /**
     * Gets description for this command
     */
    private void autoSetCMDInfo(List<String> defAliases) {
        PolymerPlugin plugin = OtherUtils.findCallingPlugin();
        if (plugin != null) {
            pluginInstance = plugin;
            Map<String,Object> commandObject = plugin.getDescription().getCommands().get(this.getName());
            if (commandObject != null) {
                Object descriptionObject = commandObject.get("description");
                Object aliasesObject = commandObject.get("aliases");
                this.description = descriptionObject != null ? descriptionObject.toString() : "No descriptions available";
                if (aliasesObject.getClass().isArray()) {
                    List<String> list = Arrays.stream((Object[])aliasesObject).map(String::valueOf).toList();
                    defAliases.addAll(list);
                    this.setAliases(defAliases);
                } else {
                    this.setAliases(defAliases);
                }
            } else {
                this.setAliases(defAliases);
            }
        } else {
            this.setAliases(defAliases);
        }
    }

    public abstract String requirePlugin();

    public abstract void execute(CommandSender sender, String alias);

    protected boolean hasPermission(){
        return hasSubPermission(sender);
    }

    /**
     * FOR OUTSIDE USE ONLY
     *
     * @param sender the sender
     * @return the result
     */
    public final boolean hasPermission(CommandSender sender) {
        return hasSubPermission(sender);
    }

    protected final boolean hasSubPermission(CommandSender cs, String... subs) {
        List<String> subList = new ArrayList<>(List.of(subs));
        subList.add(0, pluginInstance.getPluginName().toLowerCase());
        subList.add(1, "command");
        subList.add(2, this.getName());
        return hasCustomPermission(cs, String.join(".", subList));
    }

    protected final boolean hasCustomPermission(CommandSender cs, String perm) {
        if (cs == null) return true;
        if (!cs.hasPermission(pluginInstance.getPluginName().toLowerCase()+"."+perm)){
            sendPolymerMessage(cs,"Command.NoPermission");
            return false;
        }
        return true;
    }

    protected final Player toPlayer(){
        return toPlayer(false);
    }

    protected final Player toPlayer(boolean noMsg){
        if (sender instanceof Player p){
            return p;
        }else {
            if (!noMsg) {
                sendPolymerMessage(sender, "Command.RunAsConsole");
            }
            return null;
        }
    }

    protected final Player findPlayer(String name){
        Player p = Bukkit.getPlayer(name);
        if (p == null){
            sendPolymerMessage(sender, "Command.PlayerNotFound");
        }
        return p;
    }

    @Nullable
    public final String getArg(int index) {
        try {
            return arguments.get(index);
        } catch (Exception e) {
            return null;
        }
    }

    protected final boolean isArgEmpty() {
        return arguments.isEmpty();
    }

    public final int argSize() {
        return arguments.size();
    }

    protected final TuplePair<Boolean, Double> getArgAsDoubleOrInt(int index, boolean isInt, boolean allowNegative) {
        return getArgAsDoubleOrInt(sender, index, isInt, allowNegative);
    }

    protected final void addArgument(String argName, ArgumentType type) {
        this.argumentWithTypes.put(argName, type);
    }

    @Override
    public final @NotNull String getUsage() {
        Map<String, ArgumentType> options = new HashMap<>();
        List<Map.Entry<String, ArgumentType>> arguments = argumentWithTypes.entrySet().stream().filter(e -> {
            if (e.getValue() == ArgumentType.USABLE_OPTION) {
                options.put(e.getKey(), e.getValue());
                return false;
            }
            return true;
        }).toList();
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(getLabel()).append(" ");
        if (!arguments.isEmpty()) {
            for (Map.Entry<String, ArgumentType> argument : arguments) {
                if (argument.getValue() == ArgumentType.OPTIONAL) {
                    sb.append("[");
                    sb.append(argument.getKey());
                    sb.append("]");
                } else if (argument.getValue() == ArgumentType.REQUIRED) {
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

            for (Map.Entry<String, ArgumentType> option : options.entrySet()) {
                sb.append(option.getKey());
                sb.append(" ");
            }

            sb.append("}");
        }

        return sb.toString();
    }

    public final boolean hasArgumentOption() {
        return !argumentWithTypes.isEmpty() && argumentWithTypes.containsValue(ArgumentType.USABLE_OPTION);
    }

    public final boolean hasArgumentWithTypes() {
        return !argumentWithTypes.isEmpty();
    }

    public enum ArgumentType {
        OPTIONAL,
        REQUIRED,
        USABLE_OPTION
    }
}
