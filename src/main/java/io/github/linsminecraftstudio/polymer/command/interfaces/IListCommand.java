package io.github.linsminecraftstudio.polymer.command.interfaces;

import com.google.common.collect.Lists;
import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.objects.PolymerConstants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

public interface IListCommand<T> extends ICommand {
    List<T> list(CommandSender sender);
    void sendLineMessage(CommandSender sender, int number, T object);

    default @NotNull List<String> tabCompletes(@NotNull CommandSender commandSender){
        List<List<T>> part = Lists.partition(list(commandSender), 10);
        List<String> intList = IntStream.rangeClosed(0, part.size() - 1).mapToObj(String::valueOf).toList();
        return copyPartialMatches(getArg(0), intList);
    }

     default void sendMessages(CommandSender sender, int page){
        List<List<T>> partition = Lists.partition(list(sender), 10);

        if (page == PolymerConstants.ERROR_CODE) {
            return;
        }

        if (list(sender).isEmpty()) {
            Polymer.INSTANCE.getMessageHandler().sendMessage(sender, "Info.List.Empty");
            return;
        }

        if (page > partition.size()) {
            Polymer.INSTANCE.getMessageHandler().sendMessage(sender, "Value.TooHigh", 1);
            return;
        }

        int realPage = page - 1;
        List<T> partedList = partition.get(realPage);
        Polymer.INSTANCE.getMessageHandler().sendMessage(sender, "Info.List.Head", page);

        int head = (page == 1) ? 1 : (10 * realPage) + 1;

        for (T obj : partedList) {
            sendLineMessage(sender, head, obj);
            head++;
        }

        Polymer.INSTANCE.getMessageHandler().sendMessage(sender, "Info.List.Tail");
        sender.sendMessage(buildClickEvent(sender, partition, page));
    }

    default Component buildClickEvent(CommandSender sender, List<List<T>> partition, int page) {
        Component prev = Polymer.INSTANCE.getMessageHandler().getColored(sender, "Info.List.Prev");
        Component next = Polymer.INSTANCE.getMessageHandler().getColored(sender, "Info.List.Next");

        ClickEvent prevClick = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + getName() + " " + (page-1));
        ClickEvent nextClick = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + getName() + " " + (page+1));

        if (page == 1) {
            prev = Polymer.INSTANCE.getMessageHandler().getColored(sender, "Info.List.PrevUnavailable");
            prevClick = null;
        }
        if (page >= partition.size()) {
            next = Polymer.INSTANCE.getMessageHandler().getColored(sender, "Info.List.NextUnavailable");
            nextClick = null;
        }

        Component component = Component.empty();
        if (prevClick != null) {
            prev = prev.clickEvent(prevClick);
        }
        if (nextClick != null) {
            next = next.clickEvent(nextClick);
        }

        return component.append(prev).appendSpace().appendSpace().append(next);
    }
}
