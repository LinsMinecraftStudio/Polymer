package io.github.linsminecraftstudio.polymer.objects.plugin.file;

import com.google.common.io.Files;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.linsminecraftstudio.polymer.objectutils.TuplePair;
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

public abstract class MultiFileStorage {
    private final File folder;

    private final Map<String, TuplePair<File, YamlConfiguration>> cacheMap = new HashMap<>();

    @SneakyThrows
    public MultiFileStorage(File folder) {
        Validate.notNull(folder, "Folder cannot be null");
        Files.createParentDirs(folder);
        this.folder = folder;
    }

    @SneakyThrows
    @CanIgnoreReturnValue
    public YamlConfiguration getOrMakeNew(@NotNull String key) {
        if (cacheMap.containsKey(key)) {
            return cacheMap.get(key).getSecond();
        }

        File file = new File(folder, key + ".yml");
        if (!file.exists()) {
            file.createNewFile();
        }
        var config = YamlConfiguration.loadConfiguration(file);
        cacheMap.put(key, TuplePair.of(file, config));
        return config;
    }

    @SneakyThrows
    public void saveConfiguration(@NotNull String key) {
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

    public void addConfigForce(@NotNull File file) {
        Validate.notNull(file, "File cannot be null");
        cacheMap.put(file.getName(), TuplePair.of(file, YamlConfiguration.loadConfiguration(file)));
    }

    @SneakyThrows
    private void checkNew() {
        File[] files = folder.listFiles();
        if (files != null) {
            CompletableFuture.runAsync(() -> {
                for (File file : files) {
                    boolean exists = IterableUtil.getIf(cacheMap.values(), p -> p.getA().equals(file)).isPresent();
                    if (!file.isDirectory() && !exists) {
                        addConfigForce(file);
                    }
                }
            }).get();
        }
    }
}
