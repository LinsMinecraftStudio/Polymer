package io.github.linsminecraftstudio.polymer.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;


public class ObjectConverter {
    /**
     * Ensure that this is a Paper server or the MiniMessage library is loaded before use, otherwise it will throw a {@link ClassNotFoundException}.
     */
    public static String replaceLegacyColorsToMiniMessageFormat(String text){
        MiniMessage miniMessage = MiniMessage.miniMessage();
        LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacyAmpersand();
        return miniMessage.serialize(legacyComponentSerializer.deserialize(text));
    }
    /**
     * Ensure that this is a Paper server or the MiniMessage library is loaded before use, otherwise it will throw a {@link ClassNotFoundException}.
     */
    public static Component toComponent(String text){
        return MiniMessage.miniMessage().deserialize(replaceLegacyColorsToMiniMessageFormat(text));
    }

    public static Location toLocation(String singleString) {
        String[] strings = singleString.split(",");
        if (strings.length == 4) {
            return new Location(Bukkit.getWorld(strings[0]),
                    Double.parseDouble(strings[1]),
                    Double.parseDouble(strings[2]),
                    Double.parseDouble(strings[3]));
        } else if (strings.length == 6) {
            return new Location(Bukkit.getWorld(strings[0]),
                    Double.parseDouble(strings[1]),
                    Double.parseDouble(strings[2]),
                    Double.parseDouble(strings[3]),
                    Float.parseFloat(strings[4]),
                    Float.parseFloat(strings[5]));
        }
        return null;
    }
    public static String toLocationString(Location location){
        return String.join(",", location.getWorld().getName(),
                String.valueOf(location.getX()), String.valueOf(location.getY()),
                String.valueOf(location.getZ()), String.valueOf(location.getYaw()),
                String.valueOf(location.getPitch()));
    }
}
