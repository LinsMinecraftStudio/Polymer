package io.github.linsminecraftstudio.polymer.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.plugin.Plugin;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.*;

public final class FileUtils {
    /**
     * Complete configuration(key and value, comments, etc)
     * @param plugin plugin instance
     * @param resourceFile the resource file you want to complete
     */
    @ParametersAreNonnullByDefault
    public static void completeFile(Plugin plugin,String resourceFile){
        completeFile(plugin,resourceFile,new ArrayList<>());
    }

    /**
     * Complete configuration(key and value, comments, etc)
     * @param plugin plugin instance
     * @param resourceFile the resource file you want to complete
     * @param notNeedToComplete the keys that are not needed to complete
     */
    @ParametersAreNonnullByDefault
    public static void completeFile(Plugin plugin,String resourceFile,List<String> notNeedToComplete){
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
            KeyCheck:
            for (String key : keys) {
                Object value = configuration.get(key);
                if (value instanceof List<?>) {
                    List<?> list2 = configuration2.getList(key);
                    if (list2 == null) {
                        configuration2.set(key, value);
                        continue;
                    }
                }
                NotNeedToComplete:
                for (String str : notNeedToComplete) {
                    List<String> strings = Arrays.stream(key.split("\\.")).toList();
                    for (String s : strings) {
                        if (s.equals(str)) {
                            // check subs
                            int index = strings.indexOf(str);
                            if (index != -1) {
                                ConfigurationSection section = configuration2.createSection(key);
                                if (section.getKeys(false).size()==0){
                                    ConfigurationSection section1 = configuration.getConfigurationSection(key);
                                    if (section1 == null) continue KeyCheck;
                                    String str1 = section1.getKeys(false).stream().toList().get(0);
                                    if (str1 == null) continue NotNeedToComplete;
                                    section.set(key, str1);
                                }
                            }
                        }
                    }
                }
                if (!configuration2.contains(key)) {
                    configuration2.set(key, value);
                }
                if (!configuration.getComments(key).equals(configuration2.getComments(key))) {
                    configuration2.setComments(key, configuration.getComments(key));
                }
                YamlConfigurationOptions options1 = configuration.options();
                YamlConfigurationOptions options2 = configuration2.options();
                if (!options2.getHeader().equals(options1.getHeader())) {
                    options2.setHeader(options1.getHeader());
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
    @ParametersAreNonnullByDefault
    public static void completeLangFile(Plugin plugin, String resourceFile){
        InputStream stream = plugin.getResource(resourceFile);
        File file = new File(plugin.getDataFolder(), resourceFile);

        if (!file.exists()){
            if (stream != null) {
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
     * @param file the file
     * @return result
     */
    public static boolean deleteFile(File file) {
        boolean flag = false;

        if (file.isFile() && file.exists()) {
            flag = file.delete();
        }

        return flag;
    }
}
