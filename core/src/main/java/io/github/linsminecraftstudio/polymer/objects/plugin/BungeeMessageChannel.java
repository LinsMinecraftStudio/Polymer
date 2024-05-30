package io.github.linsminecraftstudio.polymer.objects.plugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class BungeeMessageChannel implements PluginMessageListener {
    private final String channelName;
    private final Plugin plugin;

    public BungeeMessageChannel(Plugin plugin, String channelName) {
        this.plugin = plugin;
        this.channelName = channelName;
    }

    @Override
    public final void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        if (channel.equals(channelName)) {
            onMessageReceived(player, in);
        }
    }

    public abstract void onMessageReceived(Player p, ByteArrayDataInput in);

    public void sendMessage(Player connection, byte[] data) {
        connection.sendPluginMessage(plugin, channelName, data);
    }
}