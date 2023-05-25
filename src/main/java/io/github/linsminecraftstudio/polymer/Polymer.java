package io.github.linsminecraftstudio.polymer;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

public final class Polymer extends JavaPlugin {
    public static LegacyComponentSerializer serializer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        serializer = LegacyComponentSerializer.builder()
                .hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
