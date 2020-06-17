package com.ruinscraft.chat.core.messagebroker;

import com.ruinscraft.chat.api.messagebroker.IMessage;
import com.ruinscraft.chat.api.messagebroker.MessageType;

public class Message implements IMessage {

    private final MessageType type;
    private final String payload;

    public Message(MessageType type, String payload) {
        this.type = type;
        this.payload = payload;
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
