package io.github.linsminecraftstudio.polymer.command.interfaces;

import io.github.linsminecraftstudio.polymer.objects.CooldownMap;

import java.time.Duration;

public interface INeedsCooldownCommand<K> extends ICommand{
    CooldownMap<K> getCooldownMap();

    default Duration getRemaining(K key) {
        return getCooldownMap().remaining(key);
    }
}
