package com.ruinscraft.chat.core.messagebroker;

import com.google.gson.JsonObject;
import com.ruinscraft.chat.api.messagebroker.MessageType;

import java.util.UUID;

public class JsonMessage extends Message {

    private JsonObject json;

    public JsonMessage(UUID id, long time, MessageType type, JsonObject json) {
        super(id, time, type, json.getAsString());
    }

    public JsonObject getJson() {
        return json;
    }

}
