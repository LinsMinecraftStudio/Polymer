package io.github.linsminecraftstudio.polymer.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Ensure that this is a Paper server or the MinieMessage library is loaded before use, otherwise it will throw a {@link ClassNotFoundException}.
 */
public class ComponentConverter {
    public static String replaceLegacyColorsToMiniMessageFormat(String text){
        MiniMessage miniMessage = MiniMessage.miniMessage();
        LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacyAmpersand();
        return miniMessage.serialize(legacyComponentSerializer.deserialize(text));
    }

    public static Component toComponent(String text){
        return MiniMessage.miniMessage().deserialize(replaceLegacyColorsToMiniMessageFormat(text));
    }
}
