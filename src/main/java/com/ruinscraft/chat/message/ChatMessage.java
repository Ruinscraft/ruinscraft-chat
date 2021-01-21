package com.ruinscraft.chat.message;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.player.ChatPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ChatMessage extends Message {

    private UUID originServerId;
    private String channelDbName;

    public ChatMessage(UUID id, long time, ChatPlayer sender, String content, UUID originServerId, String channelDbName) {
        super(id, time, sender, content);
        this.originServerId = originServerId;
        this.channelDbName = channelDbName;
    }

    public ChatMessage(ChatPlayer sender, String content, UUID originServerId, String channelDbName) {
        this(UUID.randomUUID(), System.currentTimeMillis(), sender, content, originServerId, channelDbName);
    }

    public UUID getOriginServerId() {
        return originServerId;
    }

    public String getChannelDbName() {
        return channelDbName;
    }

    @Override
    protected void show0(ChatPlugin chatPlugin, Player to) {
        ChatChannel channel = chatPlugin.getChatChannelManager().getChannel(getChannelDbName());
        String message = channel.format(this);
        to.sendMessage(message);
    }

}
