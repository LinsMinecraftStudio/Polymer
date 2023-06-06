package io.github.linsminecraftstudio.polymer;

import org.bukkit.plugin.java.JavaPlugin;

public final class Polymer extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Polymer enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
