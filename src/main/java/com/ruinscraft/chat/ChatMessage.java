package com.ruinscraft.chat;

import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.player.ChatPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ChatMessage {

    private UUID id;
    private UUID originServerId;
    private ChatChannel channel;
    private long time;
    private ChatPlayer sender;
    private String content;

    public ChatMessage(ChatPlugin chatPlugin, Player sender, String content) {
        id = UUID.randomUUID();
        originServerId = chatPlugin.getServerId();
        time = System.currentTimeMillis();
        this.sender = chatPlugin.getChatPlayerManager().get(sender);
        this.channel = this.sender.getFocused();
        this.content = content;
    }

    public ChatMessage(ChatPlugin chatPlugin, ChatChannel channel, Player sender, String content) {
        id = UUID.randomUUID();
        originServerId = chatPlugin.getServerId();
        this.channel = channel;
        time = System.currentTimeMillis();
        this.sender = chatPlugin.getChatPlayerManager().get(sender);
        this.content = content;
    }

    public ChatMessage(UUID id, UUID originServerId, ChatChannel channel, long time, ChatPlayer sender, String content) {
        this.id = id;
        this.originServerId = originServerId;
        this.channel = channel;
        this.time = time;
        this.sender = sender;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOriginServerId() {
        return originServerId;
    }

    public ChatChannel getChannel() {
        return channel;
    }

    public long getTime() {
        return time;
    }

    public ChatPlayer getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public void showToChat(ChatPlugin chatPlugin) {
        if (!chatPlugin.getChatChannelManager().hasChannel(channel.getName())) {
            return;
        }

        for (Player player : channel.getRecipients(this)) {
            ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player);

            if (chatPlayer.isMuted(channel)) {
                continue;
            }

            if (chatPlayer.isMuted(sender)) {
                continue;
            }

            String message = channel.format(this);

            player.sendMessage(message);
        }
    }

}
