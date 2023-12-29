package io.github.linsminecraftstudio.polymer.objects.plugin.file;

import com.google.common.io.Files;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.linsminecraftstudio.polyer.objectutils.TuplePair;
import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class MultiFileStorage<K> {
    private final File folder;

    private final Map<K, TuplePair<File, YamlConfiguration>> cacheMap = new HashMap<>();

    @SneakyThrows
    public MultiFileStorage(File folder) {
        Validate.notNull(folder, "Folder cannot be null");
        Files.createParentDirs(folder);
        this.folder = folder;
    }

    protected abstract String keyToName(@NotNull K key);

    protected abstract @NotNull K nameToKey(@NotNull String name);

    @SneakyThrows
    @CanIgnoreReturnValue
    public YamlConfiguration getOrMakeNew(@NotNull K key) {
        if (cacheMap.containsKey(key)) {
            return cacheMap.get(key).getSecond();
        }

        File file = new File(folder, keyToName(key) + ".yml");
        if (!file.exists()) {
            file.createNewFile();
        }
        var config = YamlConfiguration.loadConfiguration(file);
        cacheMap.put(key, TuplePair.of(file, config));
        return config;
    }

    @SneakyThrows
    public void saveConfiguration(@NotNull K key) {
        if (!cacheMap.containsKey(key)) {
            getOrMakeNew(key);
            return;
        }

        var pair = cacheMap.get(key);
        pair.getSecond().save(pair.getFirst());
    }


    public void saveAll() {
        checkNew();

        cacheMap.values().forEach(pair -> {
            var yaml = pair.getSecond();
            try {
                yaml.save(pair.getFirst());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SneakyThrows
    private void checkNew() {
        File[] files = folder.listFiles();
        if (files != null) {
            CompletableFuture.runAsync(() -> {
                for (File file : files) {
                    boolean exists = IterableUtil.getIf(cacheMap.values(), p -> p.getA().equals(file)).isPresent();
                    if (!file.isDirectory() && !exists) {
                        K key = nameToKey(file.getName());
                        getOrMakeNew(key);
                    }
                }
            }).get();
        }
    }
}
