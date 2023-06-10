package io.github.linsminecraftstudio.polymer.utils;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.objects.PolymerPlugin;
import org.bukkit.Bukkit;

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
