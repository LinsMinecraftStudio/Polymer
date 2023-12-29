package io.github.linsminecraftstudio.polymer.objects.other;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

/**
 * The cooldown map.<br>
 * The value {@link Instant} is the time when the cooldown ends,
 * and it's not recommend to put the end time directly.
 *
 * @param <K> the key
 */
public final class CooldownMap<K> extends HashMap<K, Instant> {

    public void set(K key, Duration duration) {
        this.put(key, duration);
    }

    public void put(K key, Duration duration) {
        super.put(key, Instant.now().plus(duration));
    }

    @Override
    public boolean containsKey(Object key) {
        Instant cooldown = get(key);
        Instant now = Instant.now();
        boolean before = cooldown != null && now.isBefore(cooldown);

        if (!before) {
            clean(key);
        }

        return before;
    }

    private void clean(Object key) {
        super.remove(key);
    }

    public Duration remaining(K key) {
        Instant cooldown = get(key);
        Instant now = Instant.now();
        if (cooldown != null && now.isBefore(cooldown)) {
            return Duration.between(now, cooldown);
        } else {
            super.remove(key);
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
        try {
            Class<?> c = Class.forName("org.apache.commons.lang.time.DurationFormatUtils");
            return (String) c.getMethod("formatDuration", long.class, String.class).invoke(null, remaining(key).toMillis(), pattern);
        } catch (Exception e) {
            return DurationFormatUtils.formatDuration(remaining(key).toMillis(), pattern);
        }
    }
}
