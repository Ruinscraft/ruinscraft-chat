package com.ruinscraft.chat.storage;

import com.ruinscraft.chat.ChatPlayer;

import java.util.concurrent.CompletableFuture;

public abstract class Storage {

    public abstract CompletableFuture<Void> loadChatPlayer(ChatPlayer chatPlayer);

    public abstract CompletableFuture<Void> saveChatPlayer(ChatPlayer chatPlayer);

}
