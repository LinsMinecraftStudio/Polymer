package io.github.linsminecraftstudio.polymer.utils;

import io.github.linsminecraftstudio.polymer.Polymer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class OtherUtils {
    public static int getPolymerVersionWorth(){
        String version = Polymer.INSTANCE.getPluginMeta().getVersion().replaceAll("\\.", "")
                .replaceAll("-SNAPSHOT", "");
        return Integer.parseInt(version);
    }
    public static boolean isVersionAtLeast(int minor){
        String version = Bukkit.getMinecraftVersion().split("\\.")[1];
        return Integer.parseInt(version) >= minor;
    }

    public static String getPluginLatestVersion(String resourceID) {
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
                return "";
            }
        });
        try {
            return future.join();
        } catch (Exception e) {
            if (Polymer.isDebug()) e.printStackTrace();
        }
        return "";
    }

    public static JavaPlugin findPlugin(){
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTraceElements){
                String className = stackTraceElement.getClassName();
                Class<?> clazz = Class.forName(className);
                if (JavaPlugin.class.isAssignableFrom(clazz)) {
                    Class<? extends JavaPlugin> clazz2 = (Class<? extends JavaPlugin>) clazz;
                    return JavaPlugin.getPlugin(clazz2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
