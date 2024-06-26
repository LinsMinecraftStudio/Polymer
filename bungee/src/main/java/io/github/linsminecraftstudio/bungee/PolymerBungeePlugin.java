package io.github.linsminecraftstudio.bungee;

import com.google.common.base.Preconditions;
import io.github.linsminecraftstudio.bungee.command.PolymerBungeeCommand;
import io.github.linsminecraftstudio.bungee.objects.PolymerBungeeMessageHandler;
import io.github.linsminecraftstudio.bungee.utils.MetricsBC;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.logging.Level;

@Getter
public abstract class PolymerBungeePlugin extends Plugin {
    private PolymerBungeeMessageHandler messageHandler;
    private Configuration config;
    private MetricsBC metrics;

    @Override
    public final void onLoad() {
        ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
        try {
            config = provider.load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            getLogger().severe("Failed to load configuration file.");
            throw new RuntimeException(e);
        }
        messageHandler = new PolymerBungeeMessageHandler(this);
    }

    @Override
    public final void onDisable() {
        if (metrics != null) {
            metrics.shutdown();
        }
        onPlDisable();
    }

    public abstract void onPlDisable();

    public void saveResource(@Nonnull String resourcePath, boolean replace) {
        File dataFolder = getDataFolder();

        if (resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile().getName());
        }

        File outFile = new File(dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = Files.newOutputStream(outFile.toPath());
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    protected final void startMetrics(int pluginId) {
        metrics = new MetricsBC(this, pluginId);
    }

    protected void registerCommand(PolymerBungeeCommand command) {
        Preconditions.checkNotNull(command, "Command cannot be null");

        getProxy().getPluginManager().registerCommand(this, command);
    }
}
