package io.github.linsminecraftstudio.polymer.utils;

import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.logging.Level;

public class OtherUtils {
    public static boolean isPolymerVersionAtLeast(String version) {
        String[] split = version
                .replaceAll("-SNAPSHOT", "")
                .replaceAll("/-\\d*", "")
                .split("\\.");
        if (split.length == 2) {
            return isPolymerVersionAtLeast(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0);
        } else {
            return isPolymerVersionAtLeast(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }
    }

    public static boolean isPolymerVersionAtLeast(int major, int minor, int p) {
        String[] version = TempPolymer.getInstance().getPluginVersion()
                .replaceAll("-SNAPSHOT", "")
                .replaceAll("/-\\d*", "")
                .split("\\.");
        int polymerMajor = Integer.parseInt(version[0]);
        int polymerMinor = Integer.parseInt(version[1]);
        int polymerPatch = (version.length > 2) ? Integer.parseInt(version[2]) : 0;

        return polymerMajor > major ||
                (polymerMajor == major && polymerMinor > minor) ||
                (polymerMajor == major && polymerMinor == minor && polymerPatch >= p);
    }

    private static final Map<Class<?>, PolymerPlugin> pluginCache = new HashMap<>();

    public static PolymerPlugin findCallingPlugin() {
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTraceElements){
                String className = stackTraceElement.getClassName();
                Class<?> clazz = Class.forName(className);
                if (clazz.getSuperclass() != null && clazz.getSuperclass() == PolymerPlugin.class) {
                    if (pluginCache.containsKey(clazz)) {
                        return pluginCache.get(clazz);
                    }
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

    public static class Updater {
        /**
         * Make an updater
         * @param resourceId the resource id on spigotmc
         * @param consumer handle
         */
        public Updater(int resourceId, BiConsumer<String, Boolean> consumer) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                try (InputStream stream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream();
                     Scanner scanner = new Scanner(stream)) {
                    StringBuilder builder = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        builder.append(scanner.nextLine());
                    }
                    return builder.toString();
                } catch (IOException e) {
                    if (TempPolymer.getInstance().isDebug()) e.printStackTrace();
                    return null;
                }
            }).completeOnTimeout(null, 5, TimeUnit.SECONDS);
            PolymerPlugin plugin = findCallingPlugin();
            String ver;
            try {
                ver = future.join();
            } catch (Exception e) {
                if (plugin != null) {
                    plugin.getLogger().log(Level.WARNING, "Failed to check a plugin update, resource id: " + resourceId, e);
                }
                ver = null;
            }
            consumer.accept(ver, ver != null);
        }
    }
}
