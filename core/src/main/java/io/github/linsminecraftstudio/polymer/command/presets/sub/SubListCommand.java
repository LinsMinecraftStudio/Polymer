package io.github.linsminecraftstudio.polymer.command.presets.sub;

import io.github.linsminecraftstudio.polymer.TempPolymer;
import io.github.linsminecraftstudio.polymer.command.SubCommand;
import io.github.linsminecraftstudio.polymer.command.interfaces.IListCommand;
import io.github.linsminecraftstudio.polymer.objectutils.MapBuilder;
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
    public final Map<Integer, List<String>> tabCompletion(@NotNull CommandSender commandSender){
        return new MapBuilder<Integer, List<String>>()
                .put(0, tabCompletes(commandSender))
                .build();
    }

    @Override
    public final void execute(@NotNull CommandSender sender, @NotNull String alias){
        this.sender = sender;
        if (hasPermission()){
            if (getArgs().isEmpty()) {
                sendMessages(1);
            } else if (argSize() == 1){
                sendMessages(getArgAsDoubleOrInt(0, true, false).getB().intValue());
            } else {
                TempPolymer.getInstance().getMessageHandler().sendMessage(sender, "Command.ArgError");
            }
        }
    }

    private void sendMessages(int page){
        sendMessages(sender, page);
    }
}
