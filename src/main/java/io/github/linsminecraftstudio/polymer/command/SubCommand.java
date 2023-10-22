package io.github.linsminecraftstudio.polymer.command;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.objects.array.SimpleTypeArray;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SubCommand implements ICommand{
    private final String name;
    private CommandSender sender;
    private SimpleTypeArray<String> args;
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
        if (enabled()) {
            execute(sender, "");
        } else {
            sender.sendMessage(noEnabledMsg());
        }
    }

    public final void sendMessage(String key, Object... args) {
        sendMessage(sender, key, args);
    }

    public final void sendMessage(CommandSender sender, String key, Object... args) {
        if (instance != null) {
            instance.getMessageHandler().sendMessage(sender, key, args);
        }
    }

    public abstract boolean enabled();

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
                Polymer.INSTANCE.getMessageHandler().sendMessage(sender, "Command.RunAsConsole");
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
            Polymer.INSTANCE.getMessageHandler().sendMessage(sender, "Command.PlayerNotFound");
        }
        return p;
    }

    public String getArg(int index) {
        return args.get(index);
    }

    protected double getArgAsDoubleOrInt(int index, boolean isInt, boolean allowNegative) {
        return getArgAsDoubleOrInt(sender, index, isInt, allowNegative);
    }

    protected boolean hasPermission(){
        return hasSubPermission(sender);
    }

    protected boolean hasSubPermission(CommandSender cs,String... subs){
        List<String> subList = new ArrayList<>(List.of(subs));
        subList.add(0, instance.getPluginMeta().getName().toLowerCase());
        subList.add(1, "command");
        subList.add(2, this.getName());
        return hasCustomPermission(cs, String.join(".", subList));
    }

    protected boolean hasCustomPermission(CommandSender cs,String perm){
        if (cs == null) return true;
        if (!cs.hasPermission(instance.getPluginMeta().getName().toLowerCase()+"."+perm)){
            Polymer.INSTANCE.getMessageHandler().sendMessage(cs,"Command.NoPermission");
            return false;
        }
        return true;
    }
}
