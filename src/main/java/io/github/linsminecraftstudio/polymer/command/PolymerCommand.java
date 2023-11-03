package io.github.linsminecraftstudio.polymer.command;

import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.objects.array.SimpleTypeArray;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class PolymerCommand extends Command implements ICommand {
    protected PolymerPlugin pluginInstance;
    protected SimpleTypeArray<String> arguments;
    private CommandSender sender;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public PolymerCommand(@Nonnull String name){
        this(name, new ArrayList<>());
    }
    public PolymerCommand(@Nonnull String name, List<String> aliases) {
        super(name, "", "", new ArrayList<>());
        try {
            autoSetCMDInfo(aliases);
        }catch (Exception ignored) {
        }
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
        return true;
    }

    public final void sendMessage(String key, Object... args) {
        if (pluginInstance != null) {
            sendMessage(sender, key, args);
        }
    }

    public final void sendMessage(CommandSender sender, String key, Object... args) {
        pluginInstance.getMessageHandler().sendMessage(sender, key, args);
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

    protected boolean hasSubPermission(CommandSender cs,String... subs){
        List<String> subList = new ArrayList<>(List.of(subs));
        subList.add(0, pluginInstance.getPluginMeta().getName().toLowerCase());
        subList.add(1, "command");
        subList.add(2, this.getName());
        return hasCustomPermission(cs, String.join(".", subList));
    }

    protected boolean hasCustomPermission(CommandSender cs,String perm){
        if (cs == null) return true;
        if (!cs.hasPermission(pluginInstance.getPluginMeta().getName().toLowerCase()+"."+perm)){
            sendPolymerMessage(cs,"Command.NoPermission");
            return false;
        }
        return true;
    }

    protected Player toPlayer(){
        return toPlayer(false);
    }

    protected Player toPlayer(boolean NoMsg){
        if (sender instanceof Player p){
            return p;
        }else {
            if (!NoMsg) {
                sendPolymerMessage(sender, "Command.RunAsConsole");
            }
            return null;
        }
    }

    protected Player findPlayer(String name){
        Player p = Bukkit.getPlayer(name);
        if (p == null){
            sendPolymerMessage(sender, "Command.PlayerNotFound");
        }
        return p;
    }

    @Nullable
    public String getArg(int index) {
        try {
            return arguments.get(index);
        } catch (Exception e) {
            return null;
        }
    }

    protected boolean isArgEmpty() {
        return arguments.isEmpty();
    }

    public int argSize() {
        return arguments.size();
    }

    protected double getArgAsDoubleOrInt(int index, boolean isInt, boolean allowNegative) {
        return getArgAsDoubleOrInt(sender, index, isInt, allowNegative);
    }
}
