package io.github.linsminecraftstudio.polymer;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.plugin.MainCmd;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.event.Listener;

import java.util.List;

public final class Polymer extends PolymerPlugin implements Listener {
    public static Polymer INSTANCE;
    public static boolean autoDetectClientLang;

    @Override
    public void onPlEnable() {
        // Plugin startup logic
        INSTANCE = this;
        getLogger().info("Polymer enabled!");
        getServer().getPluginManager().registerEvents(this, this);
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
                    getLogger().warning("Failed to check update.");
                }
            });
        }
        autoDetectClientLang = getConfig().getBoolean("auto-detect-client-language", true);
    }

    @Override
    public void onPlDisable() {
    }

    @Override
    public List<PolymerCommand> registerCommands() {
        return List.of(new MainCmd("polymer"));
    }

    @Override
    public String requireVersion() {
        return null;
    }

    @Override
    public int requireApiVersion() {
        return 0;
    }

    public static boolean isDebug() {
        return INSTANCE.getConfig().getBoolean("debug", false);
    }

    @Override
    public void reload() {
        super.reload();
        autoDetectClientLang = getConfig().getBoolean("auto-detect-client-language", true);
    }
}
