package com.ruinscraft.chat.core.channel;

import com.ruinscraft.chat.api.*;
import com.ruinscraft.chat.core.message.ChatMessageLog;

import java.util.concurrent.CompletableFuture;

public abstract class ChatChannel implements IChatChannel {

    private String name;
    private ChatChannelType type;
    private boolean local;
    private boolean filtered;

    public ChatChannel(String name, ChatChannelType type, boolean local, boolean filtered) {
        this.name = name;
        this.type = type;
        this.local = local;
        this.filtered = filtered;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChatChannelType getType() {
        return type;
    }

    @Override
    public boolean isLocal() {
        return local;
    }

    @Override
    public boolean isFiltered() {
        return filtered;
    }

    @Override
    public CompletableFuture<IChatMessageLog> sendMessage(IChat chat, IChatMessage chatMessage) {
        return CompletableFuture.supplyAsync(() -> {
            String senderName = chatMessage.getSender().getUsername();
            String channelName = getName();
            ChatChannelType channelType = getType();
            String message = chatMessage.getContent();
            String formattedMessage = getFormatted(chat, chatMessage).join();
            long timeMillis = System.currentTimeMillis();
            boolean blocked = false;

            if (filtered) {
                // apply filters
            }

            ChatMessageLog log = new ChatMessageLog(senderName, channelName, channelType, message, formattedMessage, timeMillis, blocked);

            if (local) {
                chat.getMessageManager().consume(log);
            } else {
                chat.getMessageManager().publish(log);
            }

            return log;
        });
    }

}
