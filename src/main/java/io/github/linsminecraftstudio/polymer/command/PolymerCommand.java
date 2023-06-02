package io.github.linsminecraftstudio.polymer.command;

import com.google.common.base.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PolymerCommand extends Command {
    public PolymerCommand(@Nonnull String name){
        this(name, new ArrayList<>());
    }
    public PolymerCommand(@Nonnull String name, List<String> aliases) {
        super(name);
        this.setAliases(aliases);
        if (!Strings.isNullOrEmpty(requirePlugin().trim())){
            if (Bukkit.getPluginManager().isPluginEnabled(requirePlugin())){
                register(Bukkit.getCommandMap());
            }
        }else {
            register(Bukkit.getCommandMap());
        }
    }
    public abstract String requirePlugin();
    public abstract void sendMessage(CommandSender sender, String message, Object... args);
    public boolean hasPermission(CommandSender cs){
        return hasCustomPermission(cs,"command." + this.getName());
    }
    public boolean hasSubPermission(CommandSender cs,String... subs){
        String perm = "mixtools.command." + this.getName();
        List<String> subList = Arrays.stream(subs).toList();
        for (String sub : subList){
            if (!(subList.size() - 1 == subList.indexOf(sub))){
                perm = perm.concat(sub);
            }
        }
        return hasCustomPermission(cs, perm);
    }
    public boolean hasCustomPermission(CommandSender cs,String perm){
        boolean b = cs.hasPermission("mixtools."+perm);
        if (!b){
            sendMessage(cs,"Command.NoPermission");
        }
        return b;
    }
    public Player toPlayer(CommandSender cs){
        if (cs instanceof Player){
            return (Player)cs;
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

    public Player findPlayerNoMessage(String name){
        return Bukkit.getPlayer(name);
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
