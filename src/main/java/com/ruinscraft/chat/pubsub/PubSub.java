package com.ruinscraft.chat.pubsub;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruinscraft.chat.ChatMessage;
import com.ruinscraft.chat.ChatPlayer;
import com.ruinscraft.chat.event.ChatMessageEvent;
import com.ruinscraft.chat.friend.FriendRequest;
import com.ruinscraft.chat.friend.FriendRequestResponse;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;

public abstract class PubSub {

    private static final Gson GSON = new Gson();

    public void publishChatPlayerLogin(ChatPlayer chatPlayer) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("message_type", MessageType.MESSAGE_CHAT_PLAYER_LOGIN.name());
        jsonObject.addProperty("mojang_id", chatPlayer.getMojangId().toString());

        publishChatPlayerLogin(GSON.toJson(jsonObject));
    }

    public void publishChatPlayerLogout(ChatPlayer chatPlayer) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("message_type", MessageType.MESSAGE_CHAT_PLAYER_LOGOUT.name());
        jsonObject.addProperty("mojang_id", chatPlayer.getMojangId().toString());

        publishChatPlayerLogin(GSON.toJson(jsonObject));
    }

    public void publishChatMessage(ChatMessage chatMessage) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("message_type", MessageType.MESSAGE_CHAT_MESSAGE.name());
        jsonObject.addProperty("mojang_id", chatMessage.getSender().getMojangId().toString());
        jsonObject.addProperty("channel", chatMessage.getChannel().getName());
        jsonObject.addProperty("content", chatMessage.getContent());

        publishChatMessage(GSON.toJson(jsonObject));
    }

    public void publishFriendRequest(FriendRequest friendRequest) {
        JsonObject jsonObject = new JsonObject();

        publishFriendRequest(jsonObject.getAsString());
    }

    public void publishFriendRequestResponse(FriendRequestResponse friendRequestResponse) {
        JsonObject jsonObject = new JsonObject();

        publishFriendRequestResponse(jsonObject.getAsString());
    }

    public void handleChatPlayerLogin(ChatPlayer chatPlayer) {

    }

    public void handleChatPlayerLogout(ChatPlayer chatPlayer) {

    }

    public void handleChatMessage(ChatMessage chatMessage) {
        ChatMessageEvent event = new ChatMessageEvent(chatMessage);

        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void handleFriendRequest(FriendRequest friendRequest) {

    }

    public void handleFriendRequestResponse(FriendRequestResponse friendRequestResponse) {

    }

    protected abstract CompletableFuture<Void> publishChatPlayerLogin(String json);

    protected abstract CompletableFuture<Void> publishChatPlayerLogout(String json);

    protected abstract CompletableFuture<Void> publishChatMessage(String json);

    protected abstract CompletableFuture<Void> publishFriendRequest(String json);

    protected abstract CompletableFuture<Void> publishFriendRequestResponse(String json);

    public abstract void close();

    protected enum MessageType {
        MESSAGE_CHAT_PLAYER_LOGIN,
        MESSAGE_CHAT_PLAYER_LOGOUT,
        MESSAGE_CHAT_MESSAGE,
        MESSAGE_FRIEND_REQUEST,
        MESSAGE_FRIEND_REQUEST_RESPONSE,
    }

}
