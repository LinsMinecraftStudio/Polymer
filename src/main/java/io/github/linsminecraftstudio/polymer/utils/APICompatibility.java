package io.github.linsminecraftstudio.polymer.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;

import java.lang.reflect.Method;

public class APICompatibility {
    private APICompatibility() {}

    private static Method miniMessage;

    static {
        try {
            miniMessage = MiniMessage.class.getMethod("miniMessage");
        } catch (NoSuchMethodException e) {
            miniMessage = null;
        }
    }

    public static MiniMessage getMiniMessage() {
        try {
            return (MiniMessage) miniMessage.invoke(null);
        } catch (Exception e) {
            return MiniMessage.get();
        }
    }
}
