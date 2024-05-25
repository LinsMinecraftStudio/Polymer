package io.github.linsminecraftstudio.bungee.objects;

import io.github.linsminecraftstudio.bungee.Constants;
import io.github.linsminecraftstudio.bungee.PolymerBungeePlugin;
import io.github.linsminecraftstudio.bungee.utils.FileUtilsBC;
import io.github.linsminecraftstudio.bungee.utils.ObjectConverterBC;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PolymerBungeeMessageHandler {
    private final PolymerBungeePlugin plugin;
    private final Map<String, Configuration> configurations = new HashMap<>();

    @Setter
    private boolean autoDetectClientLanguage = true;

    public PolymerBungeeMessageHandler(PolymerBungeePlugin plugin) {
        this.plugin = plugin;
        setup();
    }

    private void setup() {
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
                    String realName = name.replaceAll("lang/", "");
                    InputStream stream = plugin.getClass().getClassLoader().getResourceAsStream(name);
                    File destinationFile = new File(pluginFolder, "lang/" + realName);

                    if (!destinationFile.exists() && stream != null) {
                        plugin.saveResource("lang/" + realName, false);
                    }

                    FileUtilsBC.completeLangFile(plugin, "lang/" + realName);
                }
            }

            File[] languageFiles = new File(pluginFolder, "lang").listFiles();
            if (languageFiles != null) {
                for (File languageFile : languageFiles) {
                    String language = convertToRightLangCode(languageFile.getName().replace(".yml", ""));
                    configurations.put(language, Constants.CONFIGURATION_PROVIDER.load(languageFile));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String convertToRightLangCode(String lang) {
        if (lang == null || lang.isEmpty()) return "en-US";
        String[] split = lang.split("-");
        if (split.length == 1) {
            String[] split2 = lang.split("_");
            if (split2.length == 1) return lang;
            return lang.replace(split2[1], split2[1].toUpperCase()).replace("_", "-");
        }
        return lang.replace(split[1], split[1].toUpperCase());
    }

    public String get(@Nullable CommandSender cs, String node) {
        return getConfig(cs).getString(node, "get message '" + node + "' failed, maybe it's not exists.");
    }

    public BaseComponent[] getColored(@Nullable CommandSender cs, String node, Object... args) {
        try {
            return ObjectConverterBC.toComponent(String.format(get(cs, node), args));
        } catch (Exception e) {
            return ObjectConverterBC.toComponent(get(cs, node));
        }
    }

    private Configuration getConfig(@Nullable CommandSender cs) {
        String tag = "en-US";
        if (autoDetectClientLanguage && cs != null) {
            if (cs instanceof ProxiedPlayer) {
                ProxiedPlayer p = (ProxiedPlayer) cs;
                tag = p.getLocale().toLanguageTag();
            }
        } else {
            tag = convertToRightLangCode(plugin.getConfig().getString("language", ""));
        }
        return configurations.computeIfAbsent(tag, y -> configurations.get("en-US"));
    }
}
