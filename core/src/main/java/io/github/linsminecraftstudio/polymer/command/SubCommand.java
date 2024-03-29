package io.github.linsminecraftstudio.polymer.command;

import io.github.linsminecraftstudio.polymer.command.interfaces.ICommand;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.objectutils.TuplePair;
import io.github.linsminecraftstudio.polymer.objectutils.array.SimpleTypeArray;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SubCommand implements ICommand {
    private final String name;
    private CommandSender sender;
    protected SimpleTypeArray<String> args;
    private PolymerPlugin instance;

    public SubCommand(@NotNull String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final void run(CommandSender sender, String[] args, PolymerPlugin instance){
        this.sender = sender;
        this.args = new SimpleTypeArray<>(args);
        this.instance = instance;

        beforeExecute();

        if (enabled()) {
            execute(sender, "");
        } else {
            sender.sendMessage(noEnabledMsg());
        }

        afterExecute();
    }
    
    public void beforeExecute() {
    }
    
    public void afterExecute() {
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

    protected boolean isArgEmpty() {
        return args.isEmpty();
    }

    protected SimpleTypeArray<String> getArgs() {
        return args;
    }

    public int argSize() {
        return args.size();
    }
}
