package io.github.linsminecraftstudio.polymer.utils;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class OtherUtils {
    public static boolean isPolymerVersionAtLeast(String version) {
        String[] split = version.replaceAll("-SNAPSHOT", "").split("\\.");
        if (split.length == 2) {
            return isPolymerVersionAtLeast(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0);
        } else {
            return isPolymerVersionAtLeast(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        }
    }

    public static boolean isPolymerVersionAtLeast(int major, int minor, int p) {
        String[] version = Polymer.INSTANCE.getPluginMeta().getVersion().replaceAll("-SNAPSHOT", "").split("\\.");
        int polymerMajor = Integer.parseInt(version[0]);
        int polymerMinor = Integer.parseInt(version[1]);
        int polymerPatch = (version.length > 2) ? Integer.parseInt(version[2]) : 0;

        return polymerMajor > major ||
                (polymerMajor == major && polymerMinor > minor) ||
                (polymerMajor == major && polymerMinor == minor && polymerPatch >= p);
    }

    public static boolean isMinecraftVersionAtLeast(int minor, int patch){
        String[] version = Bukkit.getMinecraftVersion().split("\\.");
        int p = version.length == 2 ? 0 : Integer.parseInt(version[2]);
        return Integer.parseInt(version[1]) >= minor & p >= patch;
    }

    public static Optional<String> getPluginLatestVersion(int resourceID) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try (InputStream stream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceID).openStream();
                 Scanner scanner = new Scanner(stream)) {
                StringBuilder builder = new StringBuilder();
                while (scanner.hasNextLine()) {
                    builder.append(scanner.nextLine());
                }
                return builder.toString();
            } catch (IOException e) {
                if (Polymer.isDebug()) e.printStackTrace();
                return null;
            }
        });
        try {
            return Optional.ofNullable(future.join());
        } catch (Exception e) {
            if (Polymer.isDebug()) e.printStackTrace();
            return Optional.empty();
        }
    }

    public static PolymerPlugin findCallingPlugin() {
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTraceElements){
                String className = stackTraceElement.getClassName();
                Class<?> clazz = Class.forName(className);
                if (clazz.getSuperclass() != null && clazz.getSuperclass() == PolymerPlugin.class) {
                    return PolymerPlugin.getPolymerPlugin((Class<? extends PolymerPlugin>) clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Updater {
        /**
         * Make a updater
         * @param resourceId the resource id on spigotmc
         * @param consumer handle
         */
        public Updater(int resourceId, BiConsumer<String, Boolean> consumer) {
            Optional<String> ver = getPluginLatestVersion(resourceId);
            consumer.accept(ver.orElse(""), ver.isPresent());
        }
    }
}
