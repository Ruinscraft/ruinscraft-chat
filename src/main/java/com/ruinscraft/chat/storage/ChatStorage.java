package com.ruinscraft.chat.storage;

import com.ruinscraft.chat.ChatMessage;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class ChatStorage {

    public abstract CompletableFuture<Void> saveChatPlayer(ChatPlayer chatPlayer);

    public abstract CompletableFuture<ChatPlayerQuery> queryChatPlayer(UUID mojangId);

    public abstract CompletableFuture<ChatPlayerQuery> queryChatPlayer(String username);

    public abstract CompletableFuture<Void> saveChatMessage(ChatMessage chatMessage);

    public abstract CompletableFuture<ChatMessageQuery> queryChatMessage(UUID chatMessageId);

    public abstract CompletableFuture<Void> saveOnlineChatPlayer(OnlineChatPlayer chatPlayer);

    public abstract CompletableFuture<OnlineChatPlayerQuery> queryOnlineChatPlayers();

    public abstract CompletableFuture<Void> deleteOfflineChatPlayers();

}
