package com.ruinscraft.chat.core.messagebroker;

import com.ruinscraft.chat.api.messagebroker.IMessage;
import com.ruinscraft.chat.api.messagebroker.MessageType;

import java.util.UUID;

public abstract class Message implements IMessage {

    private final UUID id;
    private final long time;
    private final MessageType type;
    private final String payload;

    public Message(UUID id, long time, MessageType type, String payload) {
        this.id = id;
        this.time = time;
        this.type = type;
        this.payload = payload;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public String getPayload() {
        return payload;
    }

}
