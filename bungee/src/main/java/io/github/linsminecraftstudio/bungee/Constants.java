package io.github.linsminecraftstudio.bungee;

import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Constants {
    public static final ConfigurationProvider CONFIGURATION_PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);
    public static final int ERROR_CODE = Integer.MIN_VALUE;
}
