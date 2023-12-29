package io.github.linsminecraftstudio.polymer.objectutils;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K, V> {
    private final Map<K, V> map;
    public MapBuilder() {
        this.map = new HashMap<>();
    }

    public MapBuilder(Map<K, V> map) {
        this.map = map;
    }

    public MapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public MapBuilder<K, V> putAll(Map<K, V> aMap) {
        map.putAll(aMap);
        return this;
    }

    public MapBuilder<K, V> set(K key, V value) {
        map.remove(key);
        map.put(key, value);
        return this;
    }

    public MapBuilder<K, V> remove(K key) {
        map.remove(key);
        return this;
    }

    public Map<K, V> build() {
        return map;
    }

    public static <K, V> Map<K, V> empty() {
        return new HashMap<>();
    }
}
