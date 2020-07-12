package com.ruinscraft.chat.storage;

import com.ruinscraft.chat.ChatPlayer;

import java.util.concurrent.CompletableFuture;

public class SQLStorage extends Storage {

    @Override
    public CompletableFuture<Void> loadChatPlayer(ChatPlayer chatPlayer) {
        return null;
    }

    @Override
    public CompletableFuture<Void> saveChatPlayer(ChatPlayer chatPlayer) {
        return null;
    }

}
