package io.github.linsminecraftstudio.polymer.command.presets;

import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * It lets me write help messages again.
 * @author lijinhong11(mmmjjkx)
 */
public class HelpMessager extends ListCommand<SubCommand> {
    private final PolymerCommand mainCommand;

    public HelpMessager(@NotNull PolymerCommand command, @NotNull String name) {
        super(name, command.getPlugin());
        this.mainCommand = command;
    }

    @Override
    public List<SubCommand> list(CommandSender sender) {
        return mainCommand.getSubCommands().values().stream().toList();
    }

    @Override
    public final void sendLineMessage(CommandSender sender, int number, SubCommand sub) {
        if (sub.hasPermission(sender)) {
            String usage = sub.getUsage().replaceAll("<command>", sub.getName());
            String cmdUsage, option;

            if (sub.hasArgumentWithTypes()) {
                if (sub.hasArgumentOption()) {
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

            TempPolymer.getInstance().getMessageHandler().sendMessage(sender, "HelpCmdMsg", cmdUsage, option, sub.getHelpDescription());
        }
    }
}
