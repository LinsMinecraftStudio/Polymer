package io.github.linsminecraftstudio.polymer.objects;

import io.github.linsminecraftstudio.polymer.Polymer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStreamReader;
import java.util.Objects;

public class PolymerConstants {
    public static int ERROR_CODE = -100;
    public static int MC_VERSION_CODE = Integer.parseInt(Bukkit.getMinecraftVersion().replaceAll("\\.",""));
    public static int BUILD_NUM = Integer.parseInt(Objects.requireNonNull(YamlConfiguration.loadConfiguration(new InputStreamReader(
            Objects.requireNonNull(Polymer.class.getResourceAsStream("/plugin.yml")))).getString("build_number")));
}
