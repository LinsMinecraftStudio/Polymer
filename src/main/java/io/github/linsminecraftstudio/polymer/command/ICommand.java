package io.github.linsminecraftstudio.polymer.command;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.objects.PolymerConstants;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public interface ICommand{
    /**
     * DON'T TRY TO INVOKE IT DIRECTLY
     */
    void execute(CommandSender sender, String alias);

    String getArg(int index);

    default List<String> copyPartialMatches(@Nonnull String token, Iterable<String> original){
        if (original == null) {
            return new ArrayList<>();
        }
        return StringUtil.copyPartialMatches(token,original,new ArrayList<>());
    }

    default List<String> getPlayerNames(){
        List<String> list = new ArrayList<>();
        for (Player p: Bukkit.getOnlinePlayers()){
            list.add(p.getName());
        }
        return list;
    }

    default double getArgAsDoubleOrInt(CommandSender sender, int index, boolean isInt, boolean allowNegative) {
        String s = getArg(index);
        try {
            double d = isInt ? Integer.parseInt(s) : Double.parseDouble(s);
            if (!allowNegative) {
                if ((isInt && d < 0) || (!isInt && d < 0.01)) {
                    Polymer.messageHandler.sendMessage(sender, "Value.TooLow", index + 1);
                    return PolymerConstants.ERROR_CODE;
                }
            }
            return d;
        }catch (NumberFormatException e){
            Polymer.messageHandler.sendMessage(sender, isInt ? "Value.NotInt" : "Value.NotDouble", index+1);
            return PolymerConstants.ERROR_CODE;
        }
    }
}