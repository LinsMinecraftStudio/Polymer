package io.github.linsminecraftstudio.polymer.objects.other;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EnumStateMap<K, V extends Enum<V>> extends HashMap<K, V> {

    public void set(K key, V state) {
        super.put(key, state);
    }

    public List<K> getAllKeysAtTheState(V state) {
        return keySet().stream().filter(key -> get(key) == state).collect(Collectors.toList());
    }
}