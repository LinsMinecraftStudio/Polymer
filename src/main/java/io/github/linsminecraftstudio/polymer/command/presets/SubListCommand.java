package io.github.linsminecraftstudio.polymer.command.presets;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.command.IListCommand;
import io.github.linsminecraftstudio.polymer.command.SubCommand;
import io.github.linsminecraftstudio.polymer.objects.MapBuilder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class SubListCommand<T> extends SubCommand implements IListCommand<T> {
    public SubListCommand(@NotNull String name) {
        super(name);
    }

    private CommandSender sender;

    @Override
    public String name() {
        return getName();
    }

    @Override
    public final Map<Integer, List<String>> tabCompletion(@NotNull CommandSender commandSender){
        return new MapBuilder<Integer, List<String>>()
                .put(0, tabComplete(commandSender))
                .build();
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
                Polymer.INSTANCE.getMessageHandler().sendMessage(sender,"Command.ArgError");
            }
        }
    }

    private void sendMessages(int page){
        sendMessages(sender, page);
    }
}
