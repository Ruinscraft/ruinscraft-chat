package com.ruinscraft.chat;

import com.google.gson.JsonObject;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatMessage {

    private ChatPlayer sender;
    private ChatChannel channel;
    private String content;

    public ChatPlayer getSender() {
        return sender;
    }

    public ChatChannel getChannel() {
        return channel;
    }

    public String getContent() {
        return content;
    }

    public static ChatMessage create(ChatPlugin chatPlugin, AsyncPlayerChatEvent event) {
        ChatMessage chatMessage = new ChatMessage();
        ChatPlayer sender = chatPlugin.getChatPlayer(event.getPlayer().getUniqueId());
        ChatChannel channel = sender.getFocused();
        String content = event.getMessage();

        chatMessage.sender = sender;
        chatMessage.channel = channel;
        chatMessage.content = content;

        return chatMessage;
    }

    public static ChatMessage deserialize(ChatPlugin chatPlugin, JsonObject jsonObject) {
        ChatMessage chatMessage = new ChatMessage();
        UUID mojangId = UUID.fromString(jsonObject.get("mojang_id").getAsString());
        String chatChannelName = jsonObject.get("channel").getAsString();
        String content = jsonObject.get("content").getAsString();
        ChatPlayer sender = chatPlugin.getChatPlayer(mojangId);
        ChatChannel channel = chatPlugin.getChatChannel(chatChannelName);

        chatMessage.sender = sender;
        chatMessage.channel = channel;
        chatMessage.content = content;

        return chatMessage;
    }

}
