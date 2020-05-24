package com.ruinscraft.chat.core.message;

import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IMessageFormatter;

import java.util.UUID;

public class ChatMessage implements IChatMessage {

    private long time;
    private String sender;
    private UUID senderId;
    private String channelName;
    private String message;

    private ChatMessage(long time, String sender, UUID senderId, String channelName, String message) {
        this.time = time;
        this.sender = sender;
        this.senderId = senderId;
        this.channelName = channelName;
        this.message = message;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public UUID getSenderId() {
        return senderId;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void applyFormatter(IMessageFormatter formatter) {
        formatter.getReplacements().forEach(f -> message = f.apply(message));
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "time=" + time +
                ", sender='" + sender + '\'' +
                ", senderId=" + senderId +
                ", channelName='" + channelName + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    public static ChatMessage of(IChatPlayer sender, IChatChannel channel, String message) {
        return new ChatMessage(System.currentTimeMillis(), sender.getNickname(), sender.getMojangId(), channel.getName(), message);
    }

}
