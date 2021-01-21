package com.ruinscraft.chat.message;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BasicChatChatMessage implements ChatMessage {

    private UUID id;
    private UUID originServerId;
    private ChatChannel channel;
    private long time;
    private ChatPlayer sender;
    private String content;

    public BasicChatChatMessage(UUID id, UUID originServerId, ChatChannel channel, long time, ChatPlayer sender, String content) {
        this.id = id;
        this.originServerId = originServerId;
        this.channel = channel;
        this.time = time;
        this.sender = sender;
        this.content = content;
    }

    public BasicChatChatMessage(ChatPlugin chatPlugin, ChatPlayer sender, ChatChannel channel, String content) {
        id = UUID.randomUUID();
        originServerId = chatPlugin.getServerId();
        this.channel = channel;
        time = System.currentTimeMillis();
        this.sender = sender;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOriginServerId() {
        return originServerId;
    }

    @Override
    public String getChannelDbName() {
        return channel.getDatabaseName();
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
        for (Player player : channel.getRecipients(this)) {
            OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

            if (onlineChatPlayer.isBlocked(sender)) {
                continue;
            }

            String message = channel.format(this);

            player.sendMessage(message);
        }
    }

}
