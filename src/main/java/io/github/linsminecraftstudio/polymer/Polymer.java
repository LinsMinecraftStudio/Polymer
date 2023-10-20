package io.github.linsminecraftstudio.polymer;

import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.plugin.MainCmd;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.objects.plugin.message.PolymerMessageHandler;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;

import java.util.List;

public final class Polymer extends PolymerPlugin {
    public static Polymer INSTANCE;
    public static PolymerMessageHandler messageHandler;

    @Override
    public void onPlEnable() {
        // Plugin startup logic
        INSTANCE = this;
        completeLangFile("en-us", "zh-cn");
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
                    getLogger().warning("Failed to check update.");
                }
            });
        }
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

    public static boolean isDebug() {
        return INSTANCE.getConfig().getBoolean("debug", false);
    }

    public static void doReload() {
        INSTANCE.reloadConfig();
        messageHandler = new PolymerMessageHandler(INSTANCE);
    }
}
