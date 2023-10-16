package io.github.linsminecraftstudio.polymer.command;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.objects.PolymerConstants;
import io.github.linsminecraftstudio.polymer.objects.array.SimpleTypeArray;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class SubCommand {
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
            execute(sender, args);
        } else {
            sender.sendMessage(noEnabledMsg());
        }
    }

    public abstract boolean enabled();

    public abstract Map<Integer, List<String>> tabCompletion(CommandSender sender);

    public Component noEnabledMsg() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize("&4This command is disabled by developer!");
    }

    /**
     * DON'T INVOKE IT DIRECTLY
     * You should use {@link SubCommand#run(CommandSender, String[])} instead.
     */
    protected abstract void execute(CommandSender sender, String[] args);

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

    protected String getArg(int index) {
        return args.get(index);
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
