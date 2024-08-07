package org.lins.mmmjjkx.polymer;

import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.objects.other.SimpleUpdateChecker;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;

import java.util.List;

public final class Polymer extends TempPolymer {
    public static Polymer INSTANCE;

    @Override
    public void onLoad() {
        TempPolymer.setInstance(this);
    }

    @Override
    public void onPlEnable() {
        // Plugin startup logic
        INSTANCE = this;
        getLogger().info("Polymer enabled!");
        getMessageHandler().setAutoDetectClientLanguage(getConfig().getBoolean("auto-detect-client-language", true));
        if (getConfig().getBoolean("checkUpdate")) {
            SimpleUpdateChecker updateChecker = new SimpleUpdateChecker(110542, (ver, success) -> {
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

            updateChecker.check();
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

    public boolean isDebug() {
        return getConfig().getBoolean("debug", false);
    }

    @Override
    public void reload() {
        super.reload();
    }
}
