package io.github.linsminecraftstudio.polymer;

import io.github.linsminecraftstudio.polymer.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.logging.Level;

public final class Polymer extends JavaPlugin {
    public static Polymer INSTANCE;
    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        FileUtils.completeFile(this, "config.yml");
        getLogger().info("Polymer enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static boolean isDebug() {
        return INSTANCE.getConfig().getBoolean("debug", false);
    }

    public static void syncCommands() {
        try {
            Class<?> craftServer = Bukkit.getServer().getClass();
            Method syncCommandsMethod = craftServer.getDeclaredMethod("syncCommands");
            syncCommandsMethod.setAccessible(true);
            new Runnable() {
                @Override
                public void run() {
                    try {
                        syncCommandsMethod.invoke(Bukkit.getServer());
                    } catch (ReflectiveOperationException e) {
                        Bukkit.getLogger().log(Level.WARNING, "Error when syncing commands", e);
                    }
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
