package io.github.linsminecraftstudio.polymer.command;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.objects.PolymerConstants;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class PolymerCommand extends Command {
    protected JavaPlugin pluginInstance;
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
            pluginInstance = plugin;
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
    protected void sendMessage(CommandSender sender, String message, Object... args) {
        if (pluginInstance instanceof PolymerPlugin pp) {
            pp.messageHandler.sendMessage(sender, message, args);
        }
    }
    protected boolean hasPermission(CommandSender cs){
        return hasCustomPermission(cs,"command." + this.getName());
    }
    protected boolean hasSubPermission(CommandSender cs,String... subs){
        List<String> subList = new ArrayList<>(List.of(subs));
        subList.add(0, "command");
        subList.add(1, this.getName());
        return hasCustomPermission(cs, String.join(".", subList));
    }
    protected boolean hasCustomPermission(CommandSender cs,String perm){
        if (cs == null) return true;
        if (!cs.hasPermission(pluginInstance.getPluginMeta().getName().toLowerCase()+"."+perm)){
            Polymer.messageHandler.sendMessage(cs,"Command.NoPermission");
            return false;
        }
        return true;
    }
    protected Player toPlayer(CommandSender cs){
        if (cs instanceof Player p){
            return p;
        }else {
            Polymer.messageHandler.sendMessage(cs,"Command.RunAsConsole");
            return null;
        }
    }

    protected Player findPlayer(CommandSender from,String name){
        Player p = Bukkit.getPlayer(name);
        if (p == null){
            Polymer.messageHandler.sendMessage(from, "Command.PlayerNotFound");
        }
        return p;
    }

    protected int toInteger(CommandSender cs,String s,int position){
        try {
            int i = Integer.parseInt(s);
            if (i < 1){
                Polymer.messageHandler.sendMessage(cs, "Value.TooLow");
                return PolymerConstants.ERROR_CODE;
            }
            return i;
        }catch (NumberFormatException e){
            Polymer.messageHandler.sendMessage(cs,"Value.NotInt",position);
            return PolymerConstants.ERROR_CODE;
        }
    }

    protected double toDouble(CommandSender cs, String s, int position){
        try {
            double d = Double.parseDouble(s);
            if (d < 0.01){
                Polymer.messageHandler.sendMessage(cs,"Value.TooLow",position);
                return PolymerConstants.ERROR_CODE;
            }
            return d;
        }catch (NumberFormatException e){
            Polymer.messageHandler.sendMessage(cs,"Value.NotDouble",position);
            return PolymerConstants.ERROR_CODE;
        }
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
}
