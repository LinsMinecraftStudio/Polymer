package io.github.linsminecraftstudio.polymer;

import io.github.linsminecraftstudio.polymer.objects.plugin.message.PolymerMessageHandler;
import io.github.linsminecraftstudio.polymer.utils.FileUtil;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Polymer extends JavaPlugin {
    public static Polymer INSTANCE;
    public static PolymerMessageHandler messageHandler;
    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        FileUtil.completeFile(this, "config.yml");
        messageHandler = new PolymerMessageHandler(this);
        getLogger().info("Polymer enabled!");
        if (getConfig().getBoolean("checkUpdate")) {
            new OtherUtils.Updater(110542, (ver, success) -> {
                if (success) {
                    getLogger().log(Level.WARNING, "A new version of Polymer is available: " + ver + ".");
                } else {
                    getLogger().log(Level.SEVERE, "Failed to check update.");
                }
            });
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static boolean isDebug() {
        return INSTANCE.getConfig().getBoolean("debug", false);
    }
}
