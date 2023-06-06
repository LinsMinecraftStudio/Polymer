package io.github.linsminecraftstudio.polymer.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class ListUtil {
    /**
     * Get the object that matches the given filter from the given list
     * @param iterable an iterable list
     * @param filter an filter
     * @return the object that matches the given filter or null
     * @param <T> type
     */
    public static <T> Optional<T> listGetIf(Iterable<T> iterable, Predicate<T> filter){
        for (T item : iterable){
            if (filter.test(item)) return Optional.of(item);
        }
        return Optional.empty();
    }

    /**
     * To components
     * @param stringList the strings you want to convert
     * @return the components
     */
    public static List<Component> stringListToComponentList(List<String> stringList){
        if (stringList == null) return new ArrayList<>();
        if (stringList.isEmpty()) return new ArrayList<>();
        List<Component> components = new ArrayList<>();
        for (String string : stringList) {
            if (string == null || string.isBlank()) continue;
            components.add(MiniMessage.miniMessage().deserialize(string));
        }
        return components;
    }

    /**
     * To strings
     * @param componentList the components you want to convert
     * @return the strings
     */
    public static List<String> componentListToStringList(List<Component> componentList){
        if (componentList == null) return new ArrayList<>();
        if (componentList.isEmpty()) return new ArrayList<>();
        List<String> components = new ArrayList<>();
        for (Component component : componentList) {
            if (component == null) continue;
            components.add(MiniMessage.miniMessage().serialize(component));
        }
        return components;
    }
}
