package com.ruinscraft.chat;

import java.util.UUID;

public class MailMessage {

    private UUID id;
    private UUID senderId;
    private UUID recipientId;
    private long time;
    private boolean read;
    private String content;

    public MailMessage(UUID id, UUID senderId, UUID recipientId, long time, boolean read, String content) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.time = time;
        this.read = read;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public long getTime() {
        return time;
    }

    public boolean isRead() {
        return read;
    }

    public String getContent() {
        return content;
    }

}
