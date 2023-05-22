package io.github.linsminecraftstudio.polymer.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public interface PolymerCommandExecutor extends CommandExecutor {
    String name();
    String requirePlugin();
    void sendMessage(CommandSender sender, String message, Object... args);
    default boolean hasPermission(CommandSender cs){
        return hasCustomPermission(cs,"command."+name());
    }
    default boolean hasSubPermission(CommandSender cs,String sub){
        return hasCustomPermission(cs,"command."+name()+"."+sub);
    }
    default boolean hasCustomPermission(CommandSender cs,String perm){
        boolean b = cs.hasPermission("mixtools."+perm);
        if (!b){
            sendMessage(cs,"Command.NoPermission");
        }
        return b;
    }
    default void register(JavaPlugin plugin){
        String require = requirePlugin();
        if (require==null){
            require = "";
        }
        if (!require.isBlank()){
            if (Bukkit.getPluginManager().isPluginEnabled(require)){
                try {
                    PluginCommand cmd = plugin.getCommand(name());
                    cmd.setExecutor(this);
                }catch (Exception e) {
                    plugin.getLogger().warning("Failed to register command '"+name()+"' : "+e.getMessage());
                }
            }
        }else {
            try {
                PluginCommand cmd = plugin.getCommand(name());
                cmd.setExecutor(this);
            }catch (Exception e) {
                plugin.getLogger().warning("Failed to register command '"+name()+"' : "+e.getMessage());
            }
        }
    }
    default Player toPlayer(CommandSender cs){
        if (cs instanceof Player){
            return (Player)cs;
        }else {
            sendMessage(cs,"Command.RunAsConsole");
            return null;
        }
    }

    default Player findPlayer(CommandSender from,String name){
        Player p = Bukkit.getPlayer(name);
        if (p == null){
            sendMessage(from, "Command.PlayerNotFound");
        }
        return p;
    }

    default Player findPlayerNoMessage(String name){
        return Bukkit.getPlayer(name);
    }

    default int toInteger(CommandSender cs,String s,int position){
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

    default double toDouble(CommandSender cs, String s, int position){
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

    default List<String> copyPartialMatches(String token, Iterable<String> original){
        return StringUtil.copyPartialMatches(token,original,new ArrayList<>());
    }
}
