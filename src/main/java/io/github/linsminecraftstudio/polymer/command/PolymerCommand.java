package io.github.linsminecraftstudio.polymer.command;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.*;

public abstract class PolymerCommand extends Command {
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

    /**
     * Gets description for this command
     */
    private void autoSetCMDInfo(List<String> defAliases) {
        JavaPlugin plugin = OtherUtils.findPlugin();
        if (plugin != null) {
            Map<String,Object> commandObject = plugin.getDescription().getCommands().get(this.getName());
            if (commandObject != null) {
                Object descriptionObject = commandObject.get("description");
                Object aliasesObject = commandObject.get("aliases");
                this.description = descriptionObject != null ? descriptionObject.toString() : "No descriptions available";
                if (aliasesObject.getClass().isArray()) {
                    List<String> list = Arrays.stream((Object[])aliasesObject).map(String::valueOf).toList();
                    this.setAliases(list);
                } else {
                    this.setAliases(defAliases);
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
        List<String> subList = new ArrayList<>(List.of(subs));
        subList.add(0, "command");
        subList.add(1, this.getName());
        return hasCustomPermission(cs, String.join(".", subList));
    }
    public boolean hasCustomPermission(CommandSender cs,String perm){
        if (cs == null) return true;
        if (!cs.hasPermission("mixtools."+perm)){
            Polymer.messageHandler.sendMessage(cs,"Command.NoPermission");
            return false;
        }
        return true;
    }
    public Player toPlayer(CommandSender cs){
        if (cs instanceof Player p){
            return p;
        }else {
            Polymer.messageHandler.sendMessage(cs,"Command.RunAsConsole");
            return null;
        }
    }

    public Player findPlayer(CommandSender from,String name){
        Player p = Bukkit.getPlayer(name);
        if (p == null){
            Polymer.messageHandler.sendMessage(from, "Command.PlayerNotFound");
        }
        return p;
    }

    public int toInteger(CommandSender cs,String s,int position){
        try {
            int i = Integer.parseInt(s);
            if (i < 1){
                Polymer.messageHandler.sendMessage(cs, "");
                return -100;
            }
            return i;
        }catch (NumberFormatException e){
            Polymer.messageHandler.sendMessage(cs,"Value.NotInt",position);
            return -100;
        }
    }

    public double toDouble(CommandSender cs, String s, int position){
        try {
            double d = Double.parseDouble(s);
            if (d < 0.01){
                Polymer.messageHandler.sendMessage(cs,"Value.TooLow",position);
                return -100;
            }
            return d;
        }catch (NumberFormatException e){
            Polymer.messageHandler.sendMessage(cs,"Value.NotDouble",position);
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
