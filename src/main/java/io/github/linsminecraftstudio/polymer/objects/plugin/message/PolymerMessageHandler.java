package io.github.linsminecraftstudio.polymer.objects.plugin.message;

import io.github.linsminecraftstudio.polymer.objects.array.ObjectArray;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A message handler that handles messages from language files.
 * Ensure that this is a Paper server or the MiniMessage library is loaded before use, otherwise it will throw a {@link ClassNotFoundException}.
 */
public class PolymerMessageHandler {
    private final YamlConfiguration message;

    public <T extends PolymerPlugin> PolymerMessageHandler(PolymerPlugin plugin){
        this(plugin, "en-us");
    }
    /**
     * Creates a new message handler
     * @param plugin need the plugin to read language files from plugin data folder
     * @param defLangName the name of the default language (Usually English is used as the default language)
     */
    public <T extends PolymerPlugin> PolymerMessageHandler(PolymerPlugin plugin, String defLangName){
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
        return message.getString(node,"get message '"+node+"' failed, maybe it's not exists.");
    }

    public Component getColored(String node, Object... args){
        try {return colorize(String.format(get(node),args));
        } catch (Exception e) {return colorize(get(node));}
    }

    public Component getColored(String node, Map<String, Object> argMap, char replacementChar) {
        String original = get(node);
        for (Map.Entry<String, Object> entry : argMap.entrySet()) {
            original = original.replaceAll(replacementChar+entry.getKey()+replacementChar, (String) entry.getValue());
        }
        return colorize(original);
    }

    /**
     * Obtain color messages and format them with messages.
     * You can see {@link #getMessageObjects(String...)} for how to get message objects.
     * @param node the node
     * @param keys message nodes
     * @return the message
     */
    public Component getColoredFormatToOtherMessages(String node, String... keys){
        try {return colorize(String.format(get(node), getMessageObjects(keys)));
        } catch (Exception e) {return colorize(get(node));}
    }

    /**
     * Get messages and convert them to {@link Object} array.
     * @param keys message nodes
     * @return the message objects
     */
    public Object[] getMessageObjects(String... keys){
        Object[] s = new Object[keys.length];
        for (int i = 0; i < keys.length; i++) {
            s[i] = get(keys[i]);
        }
        return s;
    }

    /**
     * Get string list and parse variables(per line)
     * @param node key
     * @param replacements the args you want to replace
     * @return components
     */
    public List<Component> getColoredMessages(String node, ObjectArray... replacements){
        List<String> s = message.getStringList(node);
        List<Component> new_s = new ArrayList<>();
        for (int j = 0; j < replacements.length; j++) {
            String st = s.get(j);
            ObjectArray arg = replacements[j];
            if (!arg.isEmpty()) st = String.format(st, arg.args());
            Component st2 = colorize(st);
            new_s.add(st2);
        }
        return new_s;
    }

    /**
     * The function is the same as the {@link #getColoredMessages(String, ObjectArray...)} method,
     * but this method concatenates all components into one component.
     * @param node key
     * @param replacements the args you want to replace
     * @return a single component
     */
    public Component getColoredMessagesAsSingle(String node, ObjectArray... replacements){
        List<Component> components = getColoredMessages(node, replacements);
        Component main = Component.empty();
        for (Component c : components){
            if (components.indexOf(c) != components.size() - 1) {
                main = main.appendNewline();
            }
            main = main.append(c);
        }
        return main;
    }

    public void sendMessage(CommandSender cs,String node,Object... args) {
        if (!get(node).isBlank()) {
            cs.sendMessage(getColored(node, args));
        }
    }

    /**
     * @param cs the sender
     * @param node the key
     * @param arguments replacements
     */
    public void sendMessages(CommandSender cs, String node, ObjectArray... arguments) {
        for (Component c : getColoredMessages(node, arguments)) {
            cs.sendMessage(c);
        }
    }

    public void broadcastMessage(String node,Object... args){
        Bukkit.broadcast(getColored(node, args));
    }

    public void broadcastCustomMessage(String message){
        Bukkit.broadcast(colorize(message));
    }

    public Component colorize(String string) {
        return ObjectConverter.toComponent(string);
    }
}