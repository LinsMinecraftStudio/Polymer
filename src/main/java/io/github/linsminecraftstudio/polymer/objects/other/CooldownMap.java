package io.github.linsminecraftstudio.polymer.objects.other;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

public class CooldownMap<K> extends HashMap<K, Instant> {

    public void set(K key, Duration duration) {
        this.put(key, duration);
    }

    public void put(K key, Duration duration) {
        super.put(key, Instant.now().plus(duration));
    }

    @Override
    public boolean containsKey(Object key) {
        Instant cooldown = get(key);
        return cooldown != null && Instant.now().isBefore(cooldown);
    }

    public Duration remaining(K key) {
        Instant cooldown = get(key);
        Instant now = Instant.now();
        if (cooldown != null && now.isBefore(cooldown)) {
            return Duration.between(now, cooldown);
        } else {
            return Duration.ZERO;
        }
    }

    /**
     * Get formatted remaining duration.
     * @param key the key
     * @param pattern The pattern you want to format.
     *                Can use formats like 'HH:mm:ss' or ISO formats
     * @return formatted remaining duration.
     */
    public String getFormattedRemaining(K key, String pattern) {
        return DurationFormatUtils.formatDuration(remaining(key).toMillis(), pattern);
    }
}
