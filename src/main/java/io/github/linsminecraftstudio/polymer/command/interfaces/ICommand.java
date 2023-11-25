package io.github.linsminecraftstudio.polymer.command.interfaces;

import io.github.linsminecraftstudio.polymer.Polymer;
import io.github.linsminecraftstudio.polymer.objects.other.CooldownMap;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public interface ICommand {
    /**
     * DON'T TRY TO INVOKE IT DIRECTLY
     */
    void execute(CommandSender sender, String alias);
    String getArg(int index);
    int argSize();
    void sendMessage(CommandSender sender, String key, Object... args);
    String getName();

    default void sendPolymerMessage(CommandSender sender, String key, Object... args){
        Polymer.INSTANCE.getMessageHandler().sendMessage(sender, key, args);
    }

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

    default Pair<Boolean, Double> getArgAsDoubleOrInt(CommandSender sender, int index, boolean isInt, boolean allowNegative) {
        String s = getArg(index);
        try {
            double d = isInt ? Integer.parseInt(s) : Double.parseDouble(s);
            if (!allowNegative) {
                if ((isInt && d < 0) || (!isInt && d < 0.01)) {
                    sendPolymerMessage(sender, "Value.TooLow", index + 1);
                    return ImmutablePair.of(false, null);
                }
            }
            return ImmutablePair.of(true, d);
        }catch (NumberFormatException e){
            sendPolymerMessage(sender, isInt ? "Value.NotInt" : "Value.NotDouble", index+1);
            return ImmutablePair.of(false, null);
        }
    }

    interface IOptionCommand extends ICommand {
        String tokenHead = "-O:";

        boolean containsOption(String token);

        @Nullable
        String getOptionValue(String token);
    }

    interface INeedsCooldownCommand<K> extends ICommand{
        CooldownMap<K> getCooldownMap();

        default Duration getRemaining(K key) {
            return getCooldownMap().remaining(key);
        }

        default boolean hasCooldown(K key) {
            return getCooldownMap().containsKey(key);
        }
    }
}