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
        super(name);
        try {
            if (requirePlugin() != null && !requirePlugin().isBlank()) {
                if (Bukkit.getPluginManager().isPluginEnabled(requirePlugin())) putCommand(name, aliases);
            } else {
                putCommand(name, aliases);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void putCommand(String name, List<String> aliases) {
        JavaPlugin plugin = OtherUtils.findPlugin();
        List<String> editableAliases = new ArrayList<>(aliases);
        if (plugin != null) {
            if (Polymer.isDebug()) {
                Polymer.INSTANCE.getLogger().warning("Registering command: "+name+
                        " , plugin: " + plugin.getPluginMeta().getName());
            }
            Map<String,Object> descriptionObject = plugin.getDescription().getCommands().get(name);
            Object descriptionObject2 = descriptionObject != null ? descriptionObject.get("description") : null;
            this.description = descriptionObject2 != null ? descriptionObject.toString() : "No descriptions available";
            String pluginName = plugin.getPluginMeta().getName().toLowerCase(Locale.ENGLISH);
            for (String string : aliases){
                editableAliases.add(pluginName+":"+string);
            }
        }
        this.setAliases(editableAliases);
    }
    public abstract String requirePlugin();
    public abstract void sendMessage(CommandSender sender, String message, Object... args);
    public boolean hasPermission(CommandSender cs){
        return hasCustomPermission(cs,"command." + this.getName());
    }
    public boolean hasSubPermission(CommandSender cs,String... subs){
        List<String> subList = Arrays.asList(subs);
        subList.add(0, "command");
        subList.add(1, this.getName());
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
