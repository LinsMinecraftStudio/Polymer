package io.github.linsminecraftstudio.polymer;

import io.github.linsminecraftstudio.polymer.command.FOR_POLYMER_ONLY.MainCmd;
import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.logging.Level;

public final class Polymer extends PolymerPlugin implements Listener {
    public static Polymer INSTANCE;

    @Override
    public void onPlEnable() {
        // Plugin startup logic
        INSTANCE = this;
        getLogger().info("Polymer enabled!");
        getMessageHandler().setAutoDetectClientLanguage(getConfig().getBoolean("auto-detect-client-language", true));
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
    }

    public static void debug(String message) {
        if (Polymer.isDebug()) {
            INSTANCE.getLogger().warning("[DEBUGGER-Polymer] " + message);
        }
    }

    public static void debug(String message, Throwable throwable) {
        if (Polymer.isDebug()) {
            INSTANCE.getLogger().log(Level.WARNING, "[DEBUGGER-Polymer] " + message, throwable);
        }
    }

    @Override
    public void onPlDisable() {
        getLogger().info("Polymer disabled!");
    }

    @Override
    public List<PolymerCommand> registerCommands() {
        return List.of(new MainCmd());
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
    }
}
