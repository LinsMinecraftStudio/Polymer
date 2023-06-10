package io.github.linsminecraftstudio.polymer.command;

import io.github.linsminecraftstudio.polymer.objects.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PolymerCommand extends Command {
    private final boolean forceReplace;
    public PolymerCommand(@Nonnull String name){
        this(name, new ArrayList<>());
    }
    public PolymerCommand(@Nonnull String name, List<String> aliases) {
        this(name, aliases, false);
    }
    public PolymerCommand(@Nonnull String name, List<String> aliases, boolean forceReplace) {
        super(name);
        this.forceReplace = forceReplace;
        if (requirePlugin() != null && !requirePlugin().isBlank()){
            if (Bukkit.getPluginManager().isPluginEnabled(requirePlugin())) putCommands(name, aliases);
        }else {
            putCommands(name, aliases);
        }
    }
    private void putCommands(String name, List<String> aliases) {
        CommandMap cmdMap = Bukkit.getCommandMap();
        cmdMap.getKnownCommands().put(name, this);
        List<String> strings = new ArrayList<>(aliases);
        strings.add(name);
        PolymerPlugin plugin = OtherUtils.findPolymerPlugin();
        if (plugin != null) {
            this.description = plugin.getDescription().getCommands().get(name).get("description").toString();
            String pluginName = plugin.getPluginMeta().getName().toLowerCase();
            for (String string : strings){
                cmdMap.getKnownCommands().put(pluginName+":"+string, this);
            }
        }
        for (String string : strings) {
            //check conflicts
            if (cmdMap.getCommand(string) == null) {
                cmdMap.getKnownCommands().put(string, this);
            }else {
                if (forceReplace) {
                    cmdMap.getKnownCommands().remove(string);
                    cmdMap.getKnownCommands().put(string, this);
                }
            }
        }
    }
    public abstract String requirePlugin();
    public abstract void sendMessage(CommandSender sender, String message, Object... args);
    public boolean hasPermission(CommandSender cs){
        return hasCustomPermission(cs,"command." + this.getName());
    }
    public boolean hasSubPermission(CommandSender cs,String... subs){
        List<String> subList = Arrays.asList(subs);
        subList.add("command");
        return hasCustomPermission(cs, String.join(".", subList));
    }
    public boolean hasCustomPermission(CommandSender cs,String perm){
        if (!cs.hasPermission("mixtools."+perm)){
            sendMessage(cs,"Command.NoPermission");
            return false;
        }
        return true;
    }
    public Player toPlayer(CommandSender cs){
        if (cs instanceof Player p){
            return p;
        }else {
            sendMessage(cs,"Command.RunAsConsole");
            return null;
        }
    }

    public Player findPlayer(CommandSender from,String name){
        Player p = Bukkit.getPlayer(name);
        if (p == null){
            sendMessage(from, "Command.PlayerNotFound");
        }
        return p;
    }

    public int toInteger(CommandSender cs,String s,int position){
        try {
            int i = Integer.parseInt(s);
            if (i < 1){
                sendMessage(cs,"Value.TooLow",position);
                return -100;
            }
            return i;
        }catch (NumberFormatException e){
            sendMessage(cs,"Value.NotInt",position);
            return -100;
        }
    }

    public double toDouble(CommandSender cs, String s, int position){
        try {
            double d = Double.parseDouble(s);
            if (d < 0.01){
                sendMessage(cs,"Value.TooLow",position);
                return -100;
            }
            return d;
        }catch (NumberFormatException e){
            sendMessage(cs,"Value.NotDouble",position);
            return -100;
        }
    }

    public List<String> copyPartialMatches(String token, Iterable<String> original){
        return StringUtil.copyPartialMatches(token,original,new ArrayList<>());
    }

    public List<String> getPlayerNames(){
        List<String> list = new ArrayList<>();
        for (Player p: Bukkit.getOnlinePlayers()){
            list.add(p.getName());
        }
        return list;
    }
}
