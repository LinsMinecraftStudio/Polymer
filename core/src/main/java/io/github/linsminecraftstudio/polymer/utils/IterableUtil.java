package io.github.linsminecraftstudio.polymer.utils;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class IterableUtil {
    private IterableUtil() {
    }

    /**
     * Get the object that matches the given filter from the given list
     * @param iterable an iterable list
     * @param filter an filter
     * @return the object that matches the given filter or null
     * @param <T> type
     */
    public static <T> Optional<T> getIf(Iterable<T> iterable, Predicate<T> filter){
        for (T item : iterable){
            if (filter.test(item)) return Optional.of(item);
        }
        return Optional.empty();
    }

    @Nullable
    public static <T> T getIfOrElse(Iterable<T> iterable, Predicate<T> filter, @Nullable T def){
        return getIf(iterable, filter).orElse(def);
    }

    @NotNull
    public static <T> List<T> getAllMatches(Iterable<T> iterable, Predicate<T> filter){
        List<T> tList = new ArrayList<>();
        for (T item : iterable){
            if (filter.test(item)) tList.add(item);
        }
        return tList;
    }

    @NotNull
    public static <T> List<T> getAllMatches(Iterable<T> iterable, Predicate<T> filter, int limit){
        List<T> tList = new ArrayList<>();
        int i = 0;
        for (T item : iterable) {
            if (filter.test(item)) {
                tList.add(item);
                i++;
                if (i == limit) break;
            }
        }
        return tList;
    }

    /**
     * To components
     * @param stringList the strings you want to convert
     * @return the components
     */
    @NotNull
    public static List<Component> stringListToComponentList(@NotNull List<String> stringList){
        if (stringList.isEmpty()) return new ArrayList<>();
        return stringList.stream().map(ObjectConverter::toComponent).toList();
    }

    /**
     * To strings
     * @param componentList the components you want to convert
     * @return the strings
     */
    @NotNull
    public static List<String> componentListToStringList(@NotNull List<Component> componentList){
        if (componentList.isEmpty()) return new ArrayList<>();
        return componentList.stream().map(ObjectConverter.miniMessage::serialize).toList();
    }

    public static String asString(List<?> list) {
        return list.toString().substring(1, list.toString().length() - 1);
    }

    public static int indexOf(int[] array, int objectToFind) {
        return indexOf(array, objectToFind, 0);
    }

    public static int indexOf(int[] array, int valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; i++) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
}
