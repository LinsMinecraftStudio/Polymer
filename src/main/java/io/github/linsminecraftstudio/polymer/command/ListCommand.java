package io.github.linsminecraftstudio.polymer.command;

import com.google.common.collect.Lists;
import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.objects.PolymerConstants;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ListCommand<T> extends PolymerCommand{
    private CommandSender sender;

    public ListCommand(@NotNull String name) {
        this(name, new ArrayList<>());
    }

    public ListCommand(@NotNull String name, List<String> aliases) {
        super(name, aliases);
    }

    @Override
    public String requirePlugin() {
        return null;
    }

    public abstract List<T> list(CommandSender sender);
    public abstract void sendLineMessage(CommandSender sender, int number, T object);

    @Override
    public final @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args){
        if (args.length==1) {
            List<String> argList = IntStream.rangeClosed(1, list(commandSender).size() / 10)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.toList());
            return copyPartialMatches(args[0], argList);
        }
        return new ArrayList<>();
    }

    @Override
    public final void execute(@NotNull CommandSender sender, @NotNull String alias){
        this.sender = sender;
        if (hasPermission()){
            if (isArgEmpty()){
                sendMessages(1);
            } else if (argSize() == 1){
                sendMessages((int) getArgAsDoubleOrInt(0, true, false));
            } else {
                Polymer.messageHandler.sendMessage(sender,"Command.ArgError");
            }
        }
    }

    private void sendMessages(int page){
        List<List<T>> partition = Lists.partition(list(sender), 10);

        if (page == PolymerConstants.ERROR_CODE) {
            return;
        }

        if (list(sender).isEmpty()) {
            Polymer.messageHandler.sendMessage(sender, "Info.List.Empty");
            return;
        }

        if (page > partition.size()) {
            Polymer.messageHandler.sendMessage(sender, "Value.TooHigh", 1);
            return;
        }

        int realPage = page - 1;
        List<T> partedList = partition.get(realPage);
        Polymer.messageHandler.sendMessage(sender, "Info.List.Head", page);

        int head = (page == 1) ? 1 : (10 * realPage) + 1;

        for (T obj : partedList) {
            sendLineMessage(sender, head, obj);
            head++;
        }

        Polymer.messageHandler.sendMessage(sender, "Info.List.Tail");
        sender.sendMessage(buildClickEvent(partition, page));
    }

    private Component buildClickEvent(List<List<T>> partition, int page) {
        Component prev = Polymer.messageHandler.getColored("Info.List.Prev");
        Component next = Polymer.messageHandler.getColored("Info.List.Next");

        ClickEvent prevClick = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + getName() + " " + (page-1));
        ClickEvent nextClick = ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/" + getName() + " " + (page+1));

        if (page == 1) {
            prev = Polymer.messageHandler.getColored("Info.List.PrevUnavailable");
            prevClick = null;
        }
        if (page >= partition.size()) {
            next = Polymer.messageHandler.getColored("Info.List.NextUnavailable");
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
