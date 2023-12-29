package io.github.linsminecraftstudio.polymer.command;

import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.github.linsminecraftstudio.polymer.command.presets.ListCommand;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * It lets me write help messages again.
 * @author lijinhong11(mmmjjkx)
 */
public class HelpMessager extends ListCommand<PolymerCommand> {
    private final PolymerPlugin plugin;

    public HelpMessager(@NotNull PolymerPlugin plugin, @NotNull String name) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public List<PolymerCommand> list(CommandSender sender) {
        return plugin.registerCommands();
    }

    @Override
    public void sendLineMessage(CommandSender sender, int number, PolymerCommand object) {
        String usage = object.getHelpUsage().replaceAll("<command>", object.getName());
        String description = object.getHelpDescription().replaceAll("<command>", object.getName());
        TempPolymer.getInstance().getMessageHandler().sendMessage(sender, "HelpCmdMsg", number, usage, description);
    }
}
