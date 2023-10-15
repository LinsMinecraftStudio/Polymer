package io.github.linsminecraftstudio.polymer.command;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.objects.PolymerConstants;
import io.github.linsminecraftstudio.polymer.objects.array.SimpleTypeArray;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class PolymerCommand extends Command{
    protected JavaPlugin pluginInstance;
    protected SimpleTypeArray<String> arguments;
    private CommandSender sender;
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public PolymerCommand(@Nonnull String name){
        this(name, new ArrayList<>());
    }
    public PolymerCommand(@Nonnull String name, List<String> aliases) {
        super(name, null, null, new ArrayList<>());
        try {
            autoSetCMDInfo(aliases);
        }catch (Exception e) {
            e.printStackTrace();
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
                return copyPartialMatches(args[args.length - 1], map.get(args.length - 1));
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
                subCommand.run(commandSender, Arrays.copyOfRange(strings, 1, strings.length));
            }
        }
        return true;
    }

    public final void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName(), subCommand);
    }

    /**
     * Gets description for this command
     */
    private void autoSetCMDInfo(List<String> defAliases) {
        JavaPlugin plugin = OtherUtils.findCallingPlugin();
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
    protected abstract void execute(CommandSender sender, String alias);

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
            Polymer.messageHandler.sendMessage(cs,"Command.NoPermission");
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
                Polymer.messageHandler.sendMessage(sender, "Command.RunAsConsole");
            }
            return null;
        }
    }

    protected Player findPlayer(String name){
        Player p = Bukkit.getPlayer(name);
        if (p == null){
            Polymer.messageHandler.sendMessage(sender, "Command.PlayerNotFound");
        }
        return p;
    }

    protected List<String> copyPartialMatches(String token, Iterable<String> original){
        return StringUtil.copyPartialMatches(token,original,new ArrayList<>());
    }

    protected List<String> getPlayerNames(){
        List<String> list = new ArrayList<>();
        for (Player p: Bukkit.getOnlinePlayers()){
            list.add(p.getName());
        }
        return list;
    }

    @Nullable
    protected String getArg(int index) {
        try {
            return arguments.get(index);
        } catch (Exception e) {
            return null;
        }
    }
    protected boolean isArgEmpty() {
        return arguments.isEmpty();
    }
    protected int argSize() {
        return arguments.size();
    }
    protected double getArgAsDoubleOrInt(int index, boolean isInt, boolean allowNegative) {
        String s = getArg(index);
        try {
            double d = isInt ? Integer.parseInt(s) : Double.parseDouble(s);
            if (!allowNegative) {
                if ((isInt && d < 0) || (!isInt && d < 0.01)) {
                    Polymer.messageHandler.sendMessage(sender, "Value.TooLow", index + 1);
                    return PolymerConstants.ERROR_CODE;
                }
            }
            return d;
        }catch (NumberFormatException e){
            Polymer.messageHandler.sendMessage(sender, isInt ? "Value.NotInt" : "Value.NotDouble", index+1);
            return PolymerConstants.ERROR_CODE;
        }
    }
}
