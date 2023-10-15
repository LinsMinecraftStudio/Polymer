package io.github.linsminecraftstudio.polymer;

import io.github.linsminecraftstudio.polymer.objects.plugin.message.PolymerMessageHandler;
import io.github.linsminecraftstudio.polymer.utils.FileUtil;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.plugin.java.JavaPlugin;

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
                    boolean b = OtherUtils.isPolymerVersionAtLeast(ver.replaceAll("b.*", ""));
                    if (!b) {
                        getLogger().warning("A new version of Polymer is available: " + ver + ".");
                    } else {
                        getLogger().info("Polymer is up to date.");
                    }
                } else {
                    getLogger().severe("Failed to check update.");
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
