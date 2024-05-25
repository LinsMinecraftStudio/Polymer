package io.github.linsminecraftstudio.bungee.utils;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.linsminecraftstudio.bungee.PolymerBungeePlugin;
import net.md_5.bungee.config.Configuration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static io.github.linsminecraftstudio.bungee.Constants.CONFIGURATION_PROVIDER;

public final class FileUtilsBC {
    /**
     * Complete configuration(key and value, comments, etc)
     *
     * @param resourceFile the resource file you want to complete
     */
    public static void completeFile(@Nonnull PolymerBungeePlugin plugin, @Nonnull String resourceFile, String... notNeedSyncKeys) {
        InputStream stream = plugin.getResourceAsStream(resourceFile);
        File file = new File(plugin.getDataFolder(), resourceFile);
        if (!file.exists()) {
            if (stream != null) {
                plugin.saveResource(resourceFile, false);
                return;
            }
            return;
        }
        if (stream == null) {
            plugin.getLogger().warning("File completion of '" + resourceFile + "' is failed.");
            return;
        }
        try {
            Configuration configuration = CONFIGURATION_PROVIDER.load(stream);
            Configuration configuration2 = CONFIGURATION_PROVIDER.load(file);

            for (String key : configuration.getKeys()) {
                Object value = configuration.get(key);
                if (value instanceof List<?>) {
                    List<?> list2 = configuration2.getList(key);
                    if (list2 == null) {
                        configuration2.set(key, value);
                        continue;
                    }
                }

                if (!configuration2.contains(key)) {
                    configuration2.set(key, value);
                }
            }

            List<String> notSync = Arrays.asList(notNeedSyncKeys);

            for (String key2 : configuration2.getKeys()) {
                boolean b = notSync.contains(key2);

                if (!configuration.contains(key2)) {
                    if (!b) {
                        configuration2.set(key2, null);
                    }
                }
            }

            CONFIGURATION_PROVIDER.save(configuration2, file);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().warning("File completion of '" + resourceFile + "' is failed.");
        }
    }

    /**
     * Complete language file (keys and values, comments, etc.)
     * FORCE SYNC
     *
     * @param plugin       plugin instance
     * @param resourceFile the language file you want to complete
     */
    public static void completeLangFile(PolymerBungeePlugin plugin, String resourceFile) {
        InputStream stream = plugin.getResourceAsStream(resourceFile);
        File file = new File(plugin.getDataFolder(), resourceFile);

        if (!file.exists()) {
            if (stream != null) {
                plugin.saveResource(resourceFile, false);
                return;
            }
            return;
        }

        if (stream == null) {
            plugin.getLogger().warning("File completion of '" + resourceFile + "' is failed.");
            return;
        }

        try {
            Configuration configuration = CONFIGURATION_PROVIDER.load(stream);
            Configuration configuration2 = CONFIGURATION_PROVIDER.load(file);

            Collection<String> keys = configuration.getKeys();
            for (String key : keys) {
                Object value = configuration.get(key);
                if (value instanceof List<?>) {
                    List<?> list = (List<?>) value;
                    List<?> list2 = configuration2.getList(key);
                    if (list2 == null || !(list.size() == list2.size())) {
                        configuration2.set(key, value);
                        continue;
                    }
                }
                if (!configuration2.contains(key)) {
                    configuration2.set(key, value);
                }
            }
            for (String key : configuration2.getKeys()) {
                if (configuration2.contains(key) & !configuration.contains(key)) {
                    configuration2.set(key, null);
                }
            }

            CONFIGURATION_PROVIDER.save(configuration2, file);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().warning("File completion of '" + resourceFile + "' is failed.");
        }
    }

    /**
     * Delete a directory
     *
     * @param dirFile the directory
     * @return result
     */
    @CanIgnoreReturnValue
    public static boolean deleteDir(File dirFile) {
        Callable<Boolean> callable = () -> {
            if (!dirFile.exists() || !dirFile.isDirectory() || dirFile.listFiles() == null) {
                return false;
            }
            boolean flag = true;

            File[] files = dirFile.listFiles();

            for (File file : Objects.requireNonNull(files)) {
                if (file.isFile()) {
                    flag = deleteFile(file);
                } else {
                    flag = deleteDir(file);
                }
                if (!flag) {
                    break;
                }
            }

            return flag && dirFile.delete();
        };

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executorService.submit(callable);
        executorService.shutdown();

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete a file
     *
     * @param file the file
     * @return result
     */
    @CanIgnoreReturnValue
    public static boolean deleteFile(File file) {
        boolean flag = false;

        if (file.isFile() && file.exists()) {
            flag = file.delete();
        }

        return flag;
    }
}
