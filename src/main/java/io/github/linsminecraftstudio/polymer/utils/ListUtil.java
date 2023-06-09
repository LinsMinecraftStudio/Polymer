package io.github.linsminecraftstudio.polymer.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
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
    @ParametersAreNonnullByDefault
    public static <T> Optional<T> getIf(Iterable<T> iterable, Predicate<T> filter){
        for (T item : iterable){
            if (filter.test(item)) return Optional.of(item);
        }
        return Optional.empty();
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public static <T> T getIfOrElse(Iterable<T> iterable, Predicate<T> filter, T def){
        return getIf(iterable, filter).orElse(def);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public static <T> List<T> getAllMatches(Iterable<T> iterable, Predicate<T> filter){
        List<T> tList = new ArrayList<>();
        for (T item : iterable){
            if (filter.test(item)) tList.add(item);
        }
        return tList;
    }

    /**
     * To components
     * @param stringList the strings you want to convert
     * @return the components
     */
    @Nonnull
    public static List<Component> stringListToComponentList(List<String> stringList){
        if (stringList == null || stringList.isEmpty()) return new ArrayList<>();
        return stringList.stream().map(ComponentConverter::toComponent).toList();
    }

    /**
     * To strings
     * @param componentList the components you want to convert
     * @return the strings
     */
    @Nonnull
    public static List<String> componentListToStringList(List<Component> componentList){
        if (componentList == null || componentList.isEmpty()) return new ArrayList<>();
        return componentList.stream().map(MiniMessage.miniMessage()::serialize).toList();
    }
}
