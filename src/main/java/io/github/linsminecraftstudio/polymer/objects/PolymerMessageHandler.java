package io.github.linsminecraftstudio.polymer.objects;

import io.github.linsminecraftstudio.polymer.utils.ComponentConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A message handler that handles messages from language files
 */
public class PolymerMessageHandler {
    private final YamlConfiguration message;

    /**
     * Creates a new message handler
     * @param plugin need the plugin to read language files from plugin data folder
     */
    public PolymerMessageHandler(Plugin plugin){
        String language = plugin.getConfig().getString("language","en-us");
        String fileName = "lang/"+language.toLowerCase()+".yml";
        File file = new File(plugin.getDataFolder(),fileName);
        if (!file.exists()) {
            InputStream is = plugin.getResource(fileName);
            if (is != null) {
                plugin.saveResource(fileName,false);
            }else {
                file = new File(plugin.getDataFolder(),"lang/en-us.yml");
            }
        }
        message = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String node){
        return message.getString(node,"&4Get message '"+node+"' failed, maybe it's not exists.");
    }

    public Component getColored(String node, Object... args){
        try {return colorize(String.format(get(node),args));
        } catch (IllegalFormatException e) {return colorize(get(node));}
    }

    public Component getColoredReplaceToOtherMessages(String node, String... keys){
        try {return colorize(String.format(get(node), getStrMessagesObj(keys)));
        } catch (IllegalFormatException e) {return colorize(get(node));}
    }

    public Object[] getStrMessagesObj(String... keys){
        Object[] s = new Object[keys.length];
        for (int i = 0; i < keys.length; i++) {
            s[i] = getColored(keys[i]);
        }
        return s;
    }

    public List<Component> getColoredMessagesParseVarPerLine(String node, ArgumentReplacement... replacement){
        List<String> s = message.getStringList(node);
        List<Component> new_s = new ArrayList<>();
        for (int j = 0; j < replacement.length; j++) {
            String st = s.get(j);
            ArgumentReplacement arg = replacement[j];
            if (!arg.isEmpty()) st = String.format(st, arg.args());
            Component st2 = colorize(st);
            new_s.add(st2);
        }
        return new_s;
    }

    public void sendMessage(CommandSender cs,String node,Object... args){
        if (!get(node).isBlank()) {
            cs.sendMessage(getColored(node, args));
        }
    }

    public void sendMessages(CommandSender cs, List<Component> list){
        if (!list.isEmpty()){
            for (Component msg : list){
                cs.sendMessage(msg);
            }
        }
    }

    public void broadcastMessage(String node,Object... args){
        Bukkit.broadcast(getColored(node, args));
    }

    public void broadcastCustomMessage(String message){
        Bukkit.broadcast(colorize(message));
    }

    public Component colorize(String string) {
        return ComponentConverter.toComponent(string);
    }

    public Component colorize(Component component){
        return colorize(MiniMessage.miniMessage().serialize(component));
    }

    /**
     * For color legacy colors to color
     * @param string The string to color
     * @return colored string
     */
    @Deprecated
    public String legacyColorize(String string) {
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        for (Matcher matcher = pattern.matcher(string); matcher.find(); matcher = pattern.matcher(string)) {
            String str = string.substring(matcher.start(), matcher.end());
            String color = str.replace("&","");
            string = string.replace(str, net.md_5.bungee.api.ChatColor.of(color)+"");
        }
        string = org.bukkit.ChatColor.translateAlternateColorCodes('&', string);
        return string;
    }
}