package com.ruinscraft.chat.core.message;

import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IMessageFormatter;

import java.util.UUID;

public class ChatMessage implements IChatMessage {

    private long time;
    private String sender;
    private UUID senderId;
    private String content;

    private ChatMessage(long time, String sender, UUID senderId, String content) {
        this.time = time;
        this.sender = sender;
        this.senderId = senderId;
        this.content = content;
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
    public String getContent() {
        return content;
    }

    @Override
    public void applyFormatter(IMessageFormatter formatter) {
        formatter.getReplacements().forEach(f -> content = f.apply(content));
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "time=" + time +
                ", sender='" + sender + '\'' +
                ", senderId=" + senderId +
                ", content='" + content + '\'' +
                '}';
    }

    public static ChatMessage of(IChatPlayer sender, String message) {
        return new ChatMessage(System.currentTimeMillis(), sender.getNickname(), sender.getMojangId(), message);
    }

}
