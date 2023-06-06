package io.github.linsminecraftstudio.polymer.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public final class FileUtils {
    /**
     * Complete configuration(key and value, comments, etc)
     * @param plugin plugin instance
     * @param resourceFile the resource file you want to complete
     */
    public static void completeFile(Plugin plugin,String resourceFile){
        InputStream stream = plugin.getResource(resourceFile);
        File file = new File(plugin.getDataFolder(), resourceFile);
        if (!file.exists()){
            if (stream!=null) {
                plugin.saveResource(resourceFile,false);
                return;
            }
            return;
        }
        if (stream==null) {
            plugin.getLogger().warning("File completion of '"+resourceFile+"' is failed.");
            return;
        }
        try {File temp = File.createTempFile(resourceFile+"_temp","yml");
            Files.copy(stream, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.load(temp);
            YamlConfiguration configuration2 = new YamlConfiguration();
            configuration2.load(file);
            Set<String> keys = configuration.getKeys(true);
            for (String key : keys) {
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
                if (!configuration.getComments(key).equals(configuration2.getComments(key))) {
                    configuration2.setComments(key, configuration.getComments(key));
                }
            }
            configuration2.save(file);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().warning("File completion of '"+resourceFile+"' is failed.");
        }
    }

    /**
     * Complete language file(keys and values, comments, etc)
     * @param plugin plugin instance
     * @param resourceFile the language file you want to complete
     */
    public static void completeLangFile(Plugin plugin, String resourceFile){
        InputStream stream = plugin.getResource(resourceFile);
        File file = new File(plugin.getDataFolder(), resourceFile);
        if (!file.exists()){
            if (stream!=null) {
                plugin.saveResource(resourceFile,false);
                return;
            }
            return;
        }
        if (stream==null) {
            plugin.getLogger().warning("File completion of '"+resourceFile+"' is failed.");
            return;
        }
        try {File temp = File.createTempFile(resourceFile+"_temp","yml");
            Files.copy(stream, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.load(temp);
            YamlConfiguration configuration2 = new YamlConfiguration();
            configuration2.load(file);
            Set<String> keys = configuration.getKeys(true);
            for (String key : keys) {
                Object value = configuration.get(key);
                if (value instanceof List<?> list) {
                    List<?> list2 = configuration2.getList(key);
                    if (list2 == null || !(list.size() == list2.size())) {
                        configuration2.set(key, value);
                        continue;
                    }
                }
                if (!configuration2.contains(key)) {
                    configuration2.set(key, value);
                }
                if (!configuration.getComments(key).equals(configuration2.getComments(key))) {
                    configuration2.setComments(key, configuration.getComments(key));
                }
            }
            for (String key : configuration2.getKeys(true)) {
                if (configuration2.contains(key) & !configuration.contains(key)) {
                    configuration2.set(key,null);
                }
            }
            configuration2.save(file);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().warning("File completion of '"+resourceFile+"' is failed.");
        }
    }

    /**
     * Delete a directory
     * @param dirFile the directory
     * @return result
     */
    public static boolean deleteDir(File dirFile) {
        Callable<Boolean> callable = () -> {
            if (!dirFile.exists() || !dirFile.isDirectory() || dirFile.listFiles() == null) {
                return false;
            }
            boolean flag = true;

            File[] files = dirFile.listFiles();


            for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
                if (files[i].isFile()) {
                    flag = deleteFile(files[i]);
                } else {
                    flag = deleteDir(files[i]);
                }
                if (!flag) break;
            }
            if (!flag) return false;

            return dirFile.delete();
        };
        FutureTask<Boolean> future = new FutureTask<>(callable);
        new Thread(future).start();
        try {return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Delete a file
     * @param file the file
     * @return result
     */
    public static boolean deleteFile(File file) {
        boolean flag = false;

        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }
}
