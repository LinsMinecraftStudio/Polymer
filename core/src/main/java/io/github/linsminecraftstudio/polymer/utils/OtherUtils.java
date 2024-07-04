package io.github.linsminecraftstudio.polymer.utils;

import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class OtherUtils {
    public static boolean isPolymerVersionAtLeast(String version) {
        return isPluginVersionAtLeast(TempPolymer.getInstance(), version);
    }

    public static boolean isPluginVersionAtLeast(Plugin plugin, String version) {
        String[] split = version
                .replaceAll("-SNAPSHOT", "")
                .split("\\.");

        if (split.length == 2) {
            return isPluginVersionAtLeast(plugin, Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0, 0);
        } else if (split.length == 3) {
            return isPluginVersionAtLeast(plugin, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), 0);
        } else {
            return isPluginVersionAtLeast(plugin, Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3].split("-")[1]));
        }
    }

    public static boolean isPluginVersionAtLeast(Plugin plugin, int major, int minor, int p, int sp) {
        String[] version = plugin.getDescription().getVersion()
                .replaceAll("-SNAPSHOT", "")
                .split("\\.");
        int pluginVerMajor = Integer.parseInt(version[0]);
        int pluginVerMinor = Integer.parseInt(version[1]);
        int pluginVerPatch, pluginVerSmallPatch;

        if (version.length == 3) {
            String[] split = version[2].split("-");
            pluginVerPatch = Integer.parseInt(split[0]);
            pluginVerSmallPatch = (split.length > 1) ? Integer.parseInt(split[1]) : 0;
        } else {
            pluginVerPatch = 0;
            pluginVerSmallPatch = 0;
        }

        return pluginVerMajor > major ||
                (pluginVerMajor == major && pluginVerMinor > minor) ||
                (pluginVerMajor == major && pluginVerMinor == minor && pluginVerPatch > p) ||
                (pluginVerMajor == major && pluginVerMinor == minor && pluginVerPatch == p && pluginVerSmallPatch >= sp);
    }

    private static final Map<Class<?>, PolymerPlugin> pluginCache = new HashMap<>();

    public static PolymerPlugin findCallingPlugin() {
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTraceElements){
                String className = stackTraceElement.getClassName();
                Class<?> clazz = Class.forName(className);

                if (pluginCache.containsKey(clazz)) {
                    return pluginCache.get(clazz);
                }

                if (clazz.getSuperclass() != null && clazz.getSuperclass() == PolymerPlugin.class) {
                    PolymerPlugin plugin = PolymerPlugin.getPolymerPlugin((Class<? extends PolymerPlugin>) clazz);
                    pluginCache.put(clazz, plugin);
                    return plugin;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String convertToRightLangCode(String lang) {
        if (lang == null || lang.isBlank()) return "en-US";
        String[] split = lang.split("-");
        if (split.length == 1) {
            String[] split2 = lang.split("_");
            if (split2.length == 1) return lang;
            return lang.replace(split2[1], split2[1].toUpperCase()).replace("_", "-");
        }
        return lang.replace(split[1], split[1].toUpperCase());
    }
}