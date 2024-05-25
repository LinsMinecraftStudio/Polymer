package io.github.linsminecraftstudio.bungee.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class ObjectConverterBC {
    public static String componentAsString(BaseComponent component) {
        return component.toLegacyText();
    }

    public static String componentAsString(BaseComponent[] components) {
        return TextComponent.toLegacyText(components);
    }

    public static BaseComponent[] toComponent(String s) {
        return new ComponentBuilder(s).create();
    }
}
