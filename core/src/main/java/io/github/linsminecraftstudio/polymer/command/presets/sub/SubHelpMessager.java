package io.github.linsminecraftstudio.polymer.command.presets.sub;

import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.objects.plugin.PolymerPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * It lets me write help messages again.
 *
 * @author lijinhong11(mmmjjkx)
 */
public class SubHelpMessager extends SubListCommand<PolymerCommand> {
    private final PolymerPlugin plugin;

    public SubHelpMessager(@NotNull PolymerPlugin plugin) {
        super("help");
        this.plugin = plugin;
    }

    @Override
    public List<PolymerCommand> list(CommandSender sender) {
        return plugin.registerCommands();
    }

    @Override
    public void sendLineMessage(CommandSender sender, int number, PolymerCommand object) {
        if (object.hasPermission(sender)) {
            String usage = object.getUsage().replaceAll("<command>", object.getName());
            String cmdUsage, option;

            if (object.hasArgumentWithTypes()) {
                if (object.hasArgumentOption()) {
                    cmdUsage = usage.substring(0, usage.indexOf(" {"));
                    option = usage.replaceAll(cmdUsage, "");
                } else {
                    cmdUsage = usage;
                    option = "";
                }
            } else {
                cmdUsage = usage;
                option = "";
            }

            String description = object.getHelpDescription().replaceAll("<command>", object.getName());
            TempPolymer.getInstance().getMessageHandler().sendMessage(sender, "HelpCmdMsg", number, cmdUsage, option, description);
        }
    }

    @Override
    public boolean enabled() {
        return true;
    }
}
