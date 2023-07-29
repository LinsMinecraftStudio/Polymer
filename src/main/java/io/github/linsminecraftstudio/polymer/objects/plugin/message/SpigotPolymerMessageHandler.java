package io.github.linsminecraftstudio.polymer.objects.plugin.message;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SpigotPolymerMessageHandler {
    private final YamlConfiguration message;

    public SpigotPolymerMessageHandler(Plugin plugin){
        this(plugin, "en-us");
    }
    /**
     * Creates a new message handler
     * @param plugin need the plugin to read language files from plugin data folder
     * @param defLangName the name of the default language(Usually English is used as the default language)
     */
    public SpigotPolymerMessageHandler(Plugin plugin, String defLangName){
        String language = plugin.getConfig().getString("language",defLangName);
        String fileName = "lang/"+language.toLowerCase()+".yml";
        File file = new File(plugin.getDataFolder(),fileName);
        if (!file.exists()) {
            InputStream is = plugin.getResource(fileName);
            if (is != null) {
                plugin.saveResource(fileName,false);
            }else {
                file = new File(plugin.getDataFolder(),"lang/"+defLangName+".yml");
            }
        }
        message = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Get message
     * @param node the node
     * @return the message
     */
    public String get(String node){
        return message.getString(node,"§4Get message '"+node+"' failed, maybe it's not exists.");
    }

    public String getColored(String node, Object... args){
        try {return colorize(String.format(get(node),args));
        } catch (Exception e) {return colorize(get(node));}
    }

    public String getColored(String node, Map<String, Object> argMap, char replacementChar){
        String original = get(node);
        for (Map.Entry<String, Object> entry : argMap.entrySet()) {
            original = original.replaceAll(replacementChar+entry.getKey()+replacementChar, (String) entry.getValue());
        }
        return colorize(original);
    }

    public static String colorize(String string) {
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        for (Matcher matcher = pattern.matcher(string); matcher.find(); matcher = pattern.matcher(string)) {
            String str = string.substring(matcher.start(), matcher.end());
            String color = str.replace("&","");
            string = string.replace(str, net.md_5.bungee.api.ChatColor.of(color)+"");
        }
        string = org.bukkit.ChatColor.translateAlternateColorCodes('&', string);
        return string;
    }

    public void sendMessage(CommandSender cs, String node, Object... args){
        if (!get(node).isBlank()) {
            cs.sendMessage(getColored(node, args));
        }
    }

    public void sendMessages(CommandSender cs, List<String> list){
        if (!list.isEmpty()){
            for (String msg : list){
                cs.sendMessage(msg);
            }
        }
    }
}
