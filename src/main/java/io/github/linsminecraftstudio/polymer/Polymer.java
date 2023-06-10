package io.github.linsminecraftstudio.polymer;

import io.github.linsminecraftstudio.polymer.utils.FileUtils;
import org.bukkit.plugin.java.JavaPlugin;

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
}
