package io.github.linsminecraftstudio.bungee.objects;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public abstract class MessageChannel implements Listener {
    private final String channelName;

    public MessageChannel(String channelName) {
        this.channelName = channelName;
    }

    @EventHandler
    public final void onMessageReceived(PluginMessageEvent e) {
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String channel = in.readUTF();
        if (channel.equals(channelName)) {
            onMessageReceived(e, in);
        }
    }

    public abstract void onMessageReceived(PluginMessageEvent e, ByteArrayDataInput in);

    public void sendMessage(ProxiedPlayer connection, byte[] data) {
        connection.sendData(channelName, data);
    }
}