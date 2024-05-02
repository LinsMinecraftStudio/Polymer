package io.github.linsminecraftstudio.polymer.command.presets.sub;

import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.github.linsminecraftstudio.polymer.command.PolymerCommand;
import io.github.linsminecraftstudio.polymer.command.SubCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * It lets me write help messages again.
 *
 * @author lijinhong11(mmmjjkx)
 */
public class SubHelpMessager extends SubListCommand<SubCommand> {
    private final PolymerCommand polymerCommand;

    public SubHelpMessager(@NotNull PolymerCommand command) {
        super("help");
        this.polymerCommand = command;
    }

    @Override
    public List<SubCommand> list(CommandSender sender) {
        return polymerCommand.getSubCommands().values().stream().distinct().toList();
    }

    @Override
    public final void sendLineMessage(CommandSender sender, int number, SubCommand sub) {
        if (sub.hasPermission(sender)) {
            String usage = sub.getUsage().replaceAll("%_cmd_%", sub.getName());
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

    @Override
    public Component buildClickEvent(CommandSender sender, List<List<SubCommand>> partition, int page) {
        Component prev = TempPolymer.getInstance().getMessageHandler().getColored(sender, "Info.List.Prev");
        Component next = TempPolymer.getInstance().getMessageHandler().getColored(sender, "Info.List.Next");

        ClickEvent prevClick = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + polymerCommand.getName() + " " + getName() + " " + (page - 1));
        ClickEvent nextClick = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + polymerCommand.getName() + " " + getName() + " " + (page + 1));

        if (page == 1) {
            prev = TempPolymer.getInstance().getMessageHandler().getColored(sender, "Info.List.PrevUnavailable");
            prevClick = null;
        }
        if (page >= partition.size()) {
            next = TempPolymer.getInstance().getMessageHandler().getColored(sender, "Info.List.NextUnavailable");
            nextClick = null;
        }

        Component component = Component.empty();
        if (prevClick != null) {
            prev = prev.clickEvent(prevClick);
        }
        if (nextClick != null) {
            next = next.clickEvent(nextClick);
        }

        Component space = Component.space();
        return component.append(prev).append(space).append(space).append(next);
    }
}
