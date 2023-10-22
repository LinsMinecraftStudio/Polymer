package io.github.linsminecraftstudio.polymer.command.presets;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.command.SubCommand;
import io.github.linsminecraftstudio.polymer.objects.MapBuilder;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class SubReloadCommand extends SubCommand {
    private PolymerPlugin plugin;

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
            Polymer.INSTANCE.getMessageHandler().sendMessage(sender, "Info.ReloadSuccess");
        }
    }
}
