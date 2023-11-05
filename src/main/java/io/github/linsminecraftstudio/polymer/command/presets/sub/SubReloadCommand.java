package io.github.linsminecraftstudio.polymer.command.presets.sub;

import io.github.linsminecraftstudio.polymer.command.SubCommand;
import io.github.linsminecraftstudio.polymer.objects.other.MapBuilder;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * A preset command for reload the plugin.
 */
public class SubReloadCommand extends SubCommand {
    private final PolymerPlugin plugin;

    public SubReloadCommand(@NotNull PolymerPlugin plugin) {
        super("reload");
        this.plugin = plugin;
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public Map<Integer, List<String>> tabCompletion(CommandSender sender) {
        return MapBuilder.empty();
    }

    @Override
    public void execute(CommandSender sender, String alias) {
        if (hasPermission()) {
            plugin.reload();
            sendPolymerMessage(sender, "Info.ReloadSuccess");
        }
    }
}