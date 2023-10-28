package io.github.linsminecraftstudio.polymer.objects.plugin.message;

import io.github.linsminecraftstudio.polymer.objects.array.ObjectArray;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.utils.FileUtil;
import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A message handler that handles messages from language files.
 * Ensure that this is a Paper server or the MiniMessage library is loaded before use, otherwise it will throw a {@link ClassNotFoundException}.
 */
public final class PolymerMessageHandler {
    private final PolymerPlugin plugin;
    private final Map<String, YamlConfiguration> configurations = new HashMap<>();

    private @Getter @Setter boolean autoDetectClientLanguage = true;

    /**
     * Creates a new message handler
     * @param plugin need the plugin to read language files from plugin data folder
     */
    public PolymerMessageHandler(PolymerPlugin plugin){
        this.plugin = plugin;

        File pluginFolder = plugin.getDataFolder();

        URL fileURL = Objects.requireNonNull(plugin.getClass().getClassLoader().getResource("lang/"));
        String jarPath = fileURL.toString().substring(0, fileURL.toString().indexOf("!/") + 2);
        try {
            URL jar = new URL(jarPath);
            JarURLConnection jarCon = (JarURLConnection) jar.openConnection();
            JarFile jarFile = jarCon.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();

            while (jarEntries.hasMoreElements()) {
                JarEntry entry = jarEntries.nextElement();
                String name = entry.getName();
                if (name.startsWith("lang/") && !entry.isDirectory()) {
                    String realName = name.replaceAll("lang/","");
                    InputStream stream = plugin.getClass().getClassLoader().getResourceAsStream(name);
                    File destinationFile = new File(pluginFolder, "lang/" + realName);

                    if (!destinationFile.exists() && stream != null) {
                        plugin.saveResource("lang/" + realName, false);
                    }

                    FileUtil.completeLangFile(plugin, "lang/" + realName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        File[] languageFiles = new File(pluginFolder, "lang").listFiles();
        if (languageFiles != null) {
            for (File languageFile : languageFiles) {
                String language = OtherUtils.convertToRightLangCode(languageFile.getName().replace(".yml", ""));
                configurations.put(language, YamlConfiguration.loadConfiguration(languageFile));
            }
        }
    }

    /**
     * Get message
     * @param node the node
     * @return the message
     */
    public String get(@Nullable CommandSender cs, String node){
        return getConfig(cs).getString(node,"get message '"+node+"' failed, maybe it's not exists.");
    }

    public Component getColored(@Nullable CommandSender cs, String node, Object... args){
        try {return ObjectConverter.toComponent(String.format(get(cs, node),args));
        } catch (Exception e) {return ObjectConverter.toComponent(get(cs, node));}
    }

    public Component getColored(@Nullable CommandSender cs, String node, Map<String, Object> argMap, char replacementChar) {
        String original = get(cs, node);
        for (Map.Entry<String, Object> entry : argMap.entrySet()) {
            original = original.replaceAll(replacementChar+entry.getKey()+replacementChar, (String) entry.getValue());
        }
        return ObjectConverter.toComponent(original);
    }

    /**
     * Obtain color messages and format them with messages.
     * You can see {@link #getMessageObjects(CommandSender, String...)} for how to get message objects.
     * @param node the node
     * @param keys message nodes
     * @return the message
     */
    public Component getColoredFormatToOtherMessages(@Nullable CommandSender cs, String node, String... keys){
        try {return ObjectConverter.toComponent(String.format(get(cs, node), getMessageObjects(cs, keys)));
        } catch (Exception e) {return ObjectConverter.toComponent(get(cs, node));}
    }

    /**
     * Get messages and convert them to {@link Object} array.
     * @param keys message nodes
     * @return the message objects
     */
    public Object[] getMessageObjects(@Nullable CommandSender cs, String... keys){
        Object[] s = new Object[keys.length];
        for (int i = 0; i < keys.length; i++) {
            s[i] = get(cs, keys[i]);
        }
        return s;
    }

    /**
     * Get string list and parse variables(per line)
     * @param node key
     * @param replacements the args you want to replace
     * @return components
     */
    private List<Component> getColoredMessages(@Nullable CommandSender cs, String node, ObjectArray... replacements){
        List<String> s = getConfig(cs).getStringList(node);
        List<Component> new_s = new ArrayList<>();
        for (int j = 0; j < replacements.length; j++) {
            String st = s.get(j);
            ObjectArray arg = replacements[j];
            if (!arg.isEmpty()) st = String.format(st, arg.args());
            Component st2 = ObjectConverter.toComponent(st);
            new_s.add(st2);
        }
        return new_s;
    }

    /**
     * The function is the same as the {@link #getColoredMessages(CommandSender, String, ObjectArray...)} method,
     * but this method concatenates all components into one component.
     * @param node key
     * @param replacements the args you want to replace
     * @return a single component
     */
    public Component getColoredMessagesAsSingle(CommandSender cs, String node, ObjectArray... replacements){
        List<Component> components = getColoredMessages(cs, node, replacements);
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
        if (!get(cs, node).isBlank()) {
            cs.sendMessage(getColored(cs, node, args));
        }
    }

    /**
     * @param cs the sender
     * @param node the key
     * @param arguments replacements
     */
    public void sendMessages(CommandSender cs, String node, ObjectArray... arguments) {
        for (Component c : getColoredMessages(cs, node, arguments)) {
            cs.sendMessage(c);
        }
    }

    public void broadcastMessage(String node,Object... args){
        Bukkit.broadcast(getColored(null, node, args));
    }

    public void reload() {
        configurations.clear();
        File pluginFolder = plugin.getDataFolder();

        URL fileURL = Objects.requireNonNull(plugin.getClass().getClassLoader().getResource("lang/"));
        String jarPath = fileURL.toString().substring(0, fileURL.toString().indexOf("!/") + 2);
        try {
            URL jar = new URL(jarPath);
            JarURLConnection jarCon = (JarURLConnection) jar.openConnection();
            JarFile jarFile = jarCon.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();

            while (jarEntries.hasMoreElements()) {
                JarEntry entry = jarEntries.nextElement();
                String name = entry.getName();
                if (name.startsWith("lang/") && !entry.isDirectory()) {
                    String realName = name.replaceAll("lang/","");
                    InputStream stream = plugin.getClass().getClassLoader().getResourceAsStream(name);
                    File destinationFile = new File(pluginFolder, "lang/" + realName);

                    if (!destinationFile.exists() && stream != null) {
                        plugin.saveResource("lang/" + realName, false);
                    }

                    FileUtil.completeLangFile(plugin, "lang/" + realName);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        File[] languageFiles = new File(pluginFolder, "lang").listFiles();
        if (languageFiles != null) {
            for (File languageFile : languageFiles) {
                String language = OtherUtils.convertToRightLangCode(languageFile.getName().replace(".yml", ""));
                configurations.put(language, YamlConfiguration.loadConfiguration(languageFile));
            }
        }
    }

    private YamlConfiguration getConfig(CommandSender cs){
        String tag = "en-US";
        if (autoDetectClientLanguage && cs != null) {
            if (cs instanceof Player p) {
                tag = p.locale().toLanguageTag();
            }
        } else {
            tag = OtherUtils.convertToRightLangCode(plugin.getConfig().getString("language", ""));
        }
        return configurations.computeIfAbsent(tag, y -> configurations.get("en-US"));
    }
}