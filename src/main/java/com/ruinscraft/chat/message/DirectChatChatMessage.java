package com.ruinscraft.chat.message;

import com.ruinscraft.chat.player.ChatPlayer;

import java.util.UUID;

public class DirectChatChatMessage implements ChatMessage {

    private UUID id;
    private UUID originServerId;
    private ChatPlayer sender;
    private ChatPlayer recipient;
    private long time;
    private String content;

    public DirectChatChatMessage(UUID id, UUID originServerId, ChatPlayer sender, ChatPlayer recipient, long time, String content) {
        this.id = id;
        this.originServerId = originServerId;
        this.sender = sender;
        this.recipient = recipient;
        this.time = time;
        this.content = content;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public UUID getOriginServerId() {
        return originServerId;
    }

    @Override
    public String getChannelDbName() {
        return "";
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public ChatPlayer getSender() {
        return sender;
    }

    @Override
    public String getContent() {
        return content;
    }

    public ChatPlayer getRecipient() {
        return recipient;
    }

}
