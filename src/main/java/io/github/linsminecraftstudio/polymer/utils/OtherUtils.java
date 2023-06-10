package io.github.linsminecraftstudio.polymer.utils;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.objects.PolymerPlugin;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

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

    public static PolymerPlugin findPolymerPlugin(){
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTraceElements){
                String className = stackTraceElement.getClassName();
                Class<?> clazz = Class.forName(className);
                if (clazz.isAssignableFrom(PolymerPlugin.class)) {
                    return clazz.asSubclass(PolymerPlugin.class).getDeclaredConstructor().newInstance();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
