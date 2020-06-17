package com.ruinscraft.chat.core.messagebroker;

import com.google.gson.JsonObject;
import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.messagebroker.MessageType;

public class JsonMessage extends Message {

    private JsonObject json;

    public JsonMessage(MessageType type, JsonObject json) {
        super(type, json.getAsString());
    }

    public JsonObject getJson() {
        return json;
    }

    public static JsonMessage createFromChatMessage(IChatMessage chatMessage) {
        return null;
    }

}
