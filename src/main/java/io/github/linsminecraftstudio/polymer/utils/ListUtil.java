package io.github.linsminecraftstudio.polymer.utils;

import io.github.linsminecraftstudio.polymer.Polymer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class ListUtil {
    public static <T> Optional<T> listGetIf(Iterable<T> iterable, Predicate<T> filter){
        for (T item : iterable){
            if (filter.test(item)) return Optional.of(item);
        }
        return Optional.empty();
    }

    public static List<Component> stringListToComponentList(List<String> stringList){
        if (stringList == null) return new ArrayList<>();
        if (stringList.isEmpty()) return new ArrayList<>();
        LegacyComponentSerializer serializer = Polymer.serializer;
        List<Component> components = new ArrayList<>();
        for (String string : stringList) {
            if (string == null || string.isBlank()) continue;
            components.add(serializer.deserialize(string));
        }
        return components;
    }

    public static List<String> componentListToStringList(List<Component> componentList){
        if (componentList == null) return new ArrayList<>();
        if (componentList.isEmpty()) return new ArrayList<>();
        LegacyComponentSerializer serializer = Polymer.serializer;
        List<String> components = new ArrayList<>();
        for (Component component : componentList) {
            if (component == null) continue;
            components.add(serializer.serialize(component));
        }
        return components;
    }
}
