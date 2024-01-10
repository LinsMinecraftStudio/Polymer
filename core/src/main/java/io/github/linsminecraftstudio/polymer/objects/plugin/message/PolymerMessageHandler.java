package io.github.linsminecraftstudio.polymer.objects.plugin.message;

import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.objectutils.array.ObjectArray;
import io.github.linsminecraftstudio.polymer.objectutils.translation.TranslationFunction;
import io.github.linsminecraftstudio.polymer.utils.FileUtil;
import io.github.linsminecraftstudio.polymer.utils.ObjectConverter;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.TitlePart;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A message handler that handles messages from language files.
 * Ensure that this is a Paper server or the MiniMessage library is loaded before use, otherwise it will throw a {@link ClassNotFoundException}.
 */
public final class PolymerMessageHandler {
    private final PolymerPlugin plugin;
    private final Map<String, YamlConfiguration> configurations = new HashMap<>();
    private final Map<TranslationFunction<CommandSender, String>, TranslationFunction.Priority> translationFunctionMap = new HashMap<>();

    private @Getter @Setter boolean autoDetectClientLanguage = true;


    /**
     * Creates a new message handler
     * @param plugin need the plugin to read language files from plugin data folder
     */
    public PolymerMessageHandler(PolymerPlugin plugin) {
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

        //Replace the M:{KEY} to message
        addFunction((cs, s) -> {
            Pattern pattern = Pattern.compile("M:\\{\\w.*}");
            for (Matcher matcher = pattern.matcher(s); matcher.find(); matcher = pattern.matcher(s)) {
                String result = s.substring(matcher.start(), matcher.end());
                String key = result.substring(3, result.length() - 1);
                s = s.replaceAll(result, get(cs, key));
            }
            return s;
        }, TranslationFunction.Priority.HIGHEST);

        //Replace the T:{KEY} to translation(minecraft i18n)
        addFunction((cs, s) -> {
            Pattern pattern = Pattern.compile("T:\\{\\w.*}");
            for (Matcher matcher = pattern.matcher(s); matcher.find(); matcher = pattern.matcher(s)) {
                String result = s.substring(matcher.start(), matcher.end());
                String key = result.substring(3, result.length() - 1);
                TranslatableComponent component = Component.translatable(key);
                s = s.replaceAll(result, ObjectConverter.componentAsString(component));
            }
            return s;
        }, TranslationFunction.Priority.HIGHEST);
    }

    public void addFunction(TranslationFunction<CommandSender, String> function, TranslationFunction.Priority priority) {
        translationFunctionMap.put(function, priority);
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
        try {
            return parse(cs, ObjectConverter.toComponent(String.format(get(cs, node), args)));
        } catch (Exception e) {return ObjectConverter.toComponent(get(cs, node));}
    }

    private Component parse(CommandSender source, Component component) {
        String original = ObjectConverter.componentAsString(component);
        AtomicReference<String> context = new AtomicReference<>(original);


        Arrays.stream(TranslationFunction.Priority.values()).forEach((p) -> {
            for (TranslationFunction<CommandSender, String> fun : getAll(p)) {
                context.set(fun.apply(source, context.get()));
            }
        });

        return MiniMessage.miniMessage().deserialize(context.get());
    }

    public Component getColored(@Nullable CommandSender cs, String node, Map<String, Object> argMap, char replacementChar) {
        String original = get(cs, node);
        for (Map.Entry<String, Object> entry : argMap.entrySet()) {
            original = original.replaceAll(replacementChar+entry.getKey()+replacementChar, entry.getValue().toString());
        }
        return parse(cs, ObjectConverter.toComponent(original));
    }

    /**
     * Get string list and parse variables(per line)
     * @param node key
     * @param replacements the args you want to replace
     * @return components
     */
    public List<Component> getColoredMessages(@Nullable CommandSender cs, String node, ObjectArray... replacements){
        List<String> s = getConfig(cs).getStringList(node);
        List<Component> new_s = new ArrayList<>();
        for (int j = 0; j < replacements.length; j++) {
            String st = s.get(j);
            ObjectArray arg = replacements[j];
            if (!arg.isEmpty()) st = String.format(st, arg.args());
            Component st2 = ObjectConverter.toComponent(st);
            new_s.add(parse(cs, st2));
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
            main = main.append(c);
            if (components.indexOf(c) != components.size() - 1) {
                main = main.append(Component.newline());
            }
        }
        return parse(cs, main);
    }

    /**
     * send a message
     * @param cs the sender
     * @param node the key
     * @param args the replacements
     */
    public void sendMessage(CommandSender cs,String node,Object... args) {
        if (!get(cs, node).isBlank()) {
            cs.sendMessage(getColored(cs, node, args));
        }
    }

    /**
     * send messages
     * @param cs the sender
     * @param node the key
     * @param arguments replacements (each for each line)
     */
    public void sendMessages(CommandSender cs, String node, ObjectArray... arguments) {
        for (Component c : getColoredMessages(cs, node, arguments)) {
            cs.sendMessage(c);
        }
    }

    public void sendTitle(Player p, String node, Object... args) {
        p.sendTitlePart(TitlePart.TITLE,  getColored(p, node, args));
    }

    public void sendSubTitle(Player p, String node, Object... args) {
        p.sendTitlePart(TitlePart.SUBTITLE, getColored(p, node, args));
    }

    public void sendActionBar(Player p, String node, Object... args) {
        p.sendActionBar(getColored(p, node, args));
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

    private YamlConfiguration getConfig(@Nullable CommandSender cs){
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

    private List<TranslationFunction<CommandSender, String>> getAll(TranslationFunction.Priority priority) {
        List<TranslationFunction<CommandSender, String>> list = new ArrayList<>();
        translationFunctionMap.forEach((fun, p) -> {
            if (p.getAsInt() == priority.getAsInt()) {
                list.add(fun);
            }
        });
        return list;
    }
}