package io.github.linsminecraftstudio.polymer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.java.JavaPlugin;

public final class Polymer extends JavaPlugin {
    public static LegacyComponentSerializer serializer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        serializer = LegacyComponentSerializer.builder().character('&')
                .hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static String replaceLegacyColorsToMiniMessageFormat(String text){
        MiniMessage miniMessage = MiniMessage.miniMessage();
        LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacyAmpersand();
        return miniMessage.serialize(legacyComponentSerializer.deserialize(text));
    }

    public static Component toComponent(String text){
        return MiniMessage.miniMessage().deserialize(replaceLegacyColorsToMiniMessageFormat(text));
    }
}
