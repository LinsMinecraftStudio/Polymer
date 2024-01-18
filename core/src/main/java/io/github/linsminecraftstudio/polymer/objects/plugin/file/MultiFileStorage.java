package io.github.linsminecraftstudio.polymer.objects.plugin.file;

import com.google.common.io.Files;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.linsminecraftstudio.polymer.objectutils.TuplePair;
import io.github.linsminecraftstudio.polymer.utils.IterableUtil;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MultiFileStorage {
    private final File folder;

    private final Map<String, TuplePair<File, YamlConfiguration>> cacheMap = new HashMap<>();

    @SneakyThrows
    public MultiFileStorage(File folder) {
        if (folder == null) {
            throw new NullPointerException("folder cannot be null");
        }

        if (folder.isFile()) {
            throw new IOException("folder cannot be a file");
        }

        Files.createParentDirs(folder);
        this.folder = folder;
    }

    @SneakyThrows
    @CanIgnoreReturnValue
    public YamlConfiguration getOrMakeNew(@NotNull String key) {
        if (get(key) != null) {
            return get(key);
        }

        File file = new File(folder, key + ".yml");
        if (!file.exists()) {
            file.createNewFile();
        }

        var config = YamlConfiguration.loadConfiguration(file);
        cacheMap.put(key, TuplePair.of(file, config));
        return config;
    }

    @Nullable
    public YamlConfiguration get(@NotNull String key) {
        return cacheMap.get(key) != null ? cacheMap.get(key).getSecond() : null;
    }

    @SneakyThrows
    public void setDirectly(@NotNull String key, @NotNull String yamlKey, Object value) {
        var yaml = getOrMakeNew(key);
        yaml.set(yamlKey, value);
        yaml.save(new File(folder, key + ".yml"));
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

    /**
     * Check the file is in cache map(NOT the file in the file system)
     *
     * @param key file name
     * @return the result
     */
    public boolean contains(@NotNull String key) {
        return cacheMap.containsKey(key);
    }

    /**
     * Check the file is in cache map and the file is in the folder(the file in the file system)
     *
     * @param key file name
     * @return the result
     */
    public boolean ca_contains(@NotNull String key) {
        return cacheMap.containsKey(key) && new File(folder, key + ".yml").exists();
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
