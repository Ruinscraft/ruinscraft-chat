package com.ruinscraft.chat.core.messagebroker;

import com.google.gson.Gson;
import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.messagebroker.IMessage;

public class Message implements IMessage {

    private static final Gson gson = new Gson();

    public static Message deserialize(String serialized) {
        return gson.fromJson(serialized, Message.class);
    }

    private IChatMessage content;

    public Message(IChatMessage content) {
        this.content = content;
    }

    @Override
    public IChatMessage getContent() {
        return content;
    }

    @Override
    public String serialize() {
        return gson.toJson(this);
    }

}
