package io.github.linsminecraftstudio.polymer.objects.other;

import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import io.github.linsminecraftstudio.polymer.utils.OtherUtils;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Level;

public class SimpleUpdateChecker {
    private static final Function<Integer, CompletableFuture<String>> checkFunction = resourceId -> CompletableFuture.supplyAsync(() -> {
        try (InputStream stream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream();
             Scanner scanner = new Scanner(stream)) {
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }
            return builder.toString();
        } catch (IOException e) {
            if (TempPolymer.getInstance().isDebug()) e.printStackTrace();
            return null;
        }
    }).completeOnTimeout(null, 5, TimeUnit.SECONDS);

    private final int resourceId;
    private final BiConsumer<String, Boolean> consumer;
    private final PolymerPlugin plugin;

    @Setter
    private Function<String, Boolean> checkVersionHigherFunction;

    /**
     * Make an updater
     *
     * @param resourceId the resource id on spigotmc
     * @param consumer   handle
     */
    public SimpleUpdateChecker(int resourceId, BiConsumer<String, Boolean> consumer) {
        this.resourceId = resourceId;
        this.consumer = consumer;
        this.plugin = OtherUtils.findCallingPlugin();
        this.checkVersionHigherFunction = (version) -> {
            if (version == null) return false;

            return OtherUtils.isPluginVersionAtLeast(plugin, version);
        };
    }

    public void check() {
        CompletableFuture<String> future = checkFunction.apply(resourceId);
        PolymerPlugin plugin = OtherUtils.findCallingPlugin();
        String ver;
        try {
            ver = future.join();
        } catch (Exception e) {
            if (plugin != null) {
                plugin.getLogger().log(Level.WARNING, "Failed to check a plugin update, resource id: " + resourceId, e);
            }
            ver = null;
        }
        consumer.accept(ver, checkVersionHigherFunction.apply(ver));
    }
}
