package com.ruinscraft.chat;

import com.google.gson.JsonObject;

import java.util.UUID;

public class ChatPlayer {

    private UUID mojangId;
    private String nickname;
    private ChatChannel focused;

    public ChatPlayer(UUID mojangId) {
        this.mojangId = mojangId;
    }

    public UUID getMojangId() {
        return mojangId;
    }

    public String getNickname() {
        return nickname;
    }

    public ChatChannel getFocused() {
        return new ChatChannel("default");
    }

    public static ChatPlayer deserialize(ChatPlugin chatPlugin, JsonObject jsonObject) {
        UUID mojangId = UUID.fromString(jsonObject.get("mojang_id").getAsString());
        return chatPlugin.getChatPlayer(mojangId);
    }

}
