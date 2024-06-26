package io.github.linsminecraftstudio.polymer.command;

import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.objectutils.CommandArgumentType;
import io.github.linsminecraftstudio.polymer.objectutils.TuplePair;
import io.github.linsminecraftstudio.polymer.objectutils.array.SimpleTypeArray;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class SubCommand implements ICommand {
    private final String name;
    private CommandSender sender;
    protected SimpleTypeArray<String> args;
    private PolymerPlugin instance;

    private final Map<String, CommandArgumentType> argumentWithTypes = new LinkedHashMap<>();

    @Setter(AccessLevel.PACKAGE)
    private PolymerCommand parent;

    @Getter(AccessLevel.PACKAGE)
    private final List<String> aliases;

    public SubCommand(@NotNull String name, String... aliases) {
        this.name = name;
        this.aliases = Arrays.asList(aliases);
    }

    public final String getName() {
        return name;
    }

    public final void run(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = new SimpleTypeArray<>(args);

        beforeExecute();

        if (enabled()) {
            execute(sender, "");
        } else {
            sender.sendMessage(noEnabledMsg());
        }

        afterExecute();
    }

    void setup(PolymerPlugin plugin) {
        this.instance = plugin;
    }

    public String getHelpDescription() {
        return "description unavailable";
    }
    
    public void beforeExecute() {
    }
    
    public void afterExecute() {
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

    public final void sendMessage(String key, Object... args) {
        sendMessage(sender, key, args);
    }

    public final void sendMessage(CommandSender sender, String key, Object... args) {
        if (instance != null) {
            instance.getMessageHandler().sendMessage(sender, key, args);
        }
    }

    public boolean enabled() {
        return true;
    }

    public abstract Map<Integer, List<String>> tabCompletion(CommandSender sender);

    public abstract void execute(CommandSender sender, String alias);

    public Component noEnabledMsg() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize("&4This command is disabled by developer!");
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

    protected Player toPlayer(){
        return toPlayer(false);
    }

    protected Player findPlayer(String name){
        Player p = Bukkit.getPlayer(name);
        if (p == null){
            sendPolymerMessage(sender, "Command.PlayerNotFound");
        }
        return p;
    }

    public String getArg(int index) {
        return args.get(index);
    }

    protected TuplePair<Boolean, Double> getArgAsDoubleOrInt(int index, boolean isInt, boolean allowNegative) {
        return getArgAsDoubleOrInt(sender, index, isInt, allowNegative);
    }

    protected boolean hasPermission(){
        return hasSubPermission(sender);
    }

    protected boolean hasSubPermission(CommandSender cs,String... subs){
        List<String> subList = new ArrayList<>(List.of(subs));
        subList.add(0, instance.getPluginName().toLowerCase());
        subList.add(1, "command");
        subList.add(2, this.getName());
        return hasCustomPermission(cs, String.join(".", subList));
    }

    protected final boolean hasCustomPermission(CommandSender cs, String perm) {
        if (cs == null) return true;
        if (!cs.hasPermission(instance.getPluginName().toLowerCase()+"."+perm)){
            sendPolymerMessage(cs,"Command.NoPermission");
            return false;
        }
        return true;
    }

    protected SimpleTypeArray<String> getArgs() {
        return args;
    }

    public int argSize() {
        return args.size();
    }

    public final @NotNull String getUsage() {
        Map<String, CommandArgumentType> options = new HashMap<>();
        List<Map.Entry<String, CommandArgumentType>> arguments = argumentWithTypes.entrySet().stream().filter(e -> {
            if (e.getValue() == CommandArgumentType.USABLE_OPTION) {
                options.put(e.getKey(), e.getValue());
                return false;
            }
            return true;
        }).toList();
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(parent.getName()).append(" ").append(getName());

        if (!aliases.isEmpty()) {
            for (String alias : aliases) {
                sb.append("/").append(alias);
            }
        }

        if (!arguments.isEmpty()) {
            sb.append(" ");
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
                if (arguments.indexOf(argument) != arguments.size() - 1) {
                    sb.append(" ");
                }
            }
        }

        if (!options.isEmpty()) {
            sb.append(" ");
            sb.append("{");

            List<Map.Entry<String, CommandArgumentType>> optionSet = options.entrySet().stream().toList();

            for (Map.Entry<String, CommandArgumentType> option : optionSet) {
                sb.append("--").append(option.getKey());
                if (optionSet.indexOf(option) != optionSet.size() - 1) {
                    sb.append(" ");
                }
            }

            sb.append("}");
        }

        return sb.toString();
    }

    protected final void addArgument(String argName, CommandArgumentType type) {
        this.argumentWithTypes.put(argName, type);
    }

    public boolean hasArgumentWithTypes() {
        return !argumentWithTypes.isEmpty();
    }

    public boolean hasArgumentOption() {
        return argumentWithTypes.containsValue(CommandArgumentType.USABLE_OPTION);
    }
}
