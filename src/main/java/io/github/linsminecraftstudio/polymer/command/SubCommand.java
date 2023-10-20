package io.github.linsminecraftstudio.polymer.command;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.objects.array.SimpleTypeArray;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class SubCommand implements ICommand{
    private final String name;
    private CommandSender sender;
    private SimpleTypeArray<String> args;

    public SubCommand(@NotNull String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final void run(CommandSender sender, String[] args){
        this.sender = sender;
        this.args = new SimpleTypeArray<>(args);
        if (enabled()) {
            execute(sender, "");
        } else {
            sender.sendMessage(noEnabledMsg());
        }
    }

    public abstract boolean enabled();

    public abstract Map<Integer, List<String>> tabCompletion(CommandSender sender);

    public Component noEnabledMsg() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize("&4This command is disabled by developer!");
    }

    public abstract void execute(CommandSender sender, String alias);

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

    protected Player toPlayer(){
        return toPlayer(false);
    }

    protected Player findPlayer(String name){
        Player p = Bukkit.getPlayer(name);
        if (p == null){
            Polymer.messageHandler.sendMessage(sender, "Command.PlayerNotFound");
        }
        return p;
    }

    public String getArg(int index) {
        return args.get(index);
    }

    protected double getArgAsDoubleOrInt(int index, boolean isInt, boolean allowNegative) {
        return getArgAsDoubleOrInt(sender, index, isInt, allowNegative);
    }
}
