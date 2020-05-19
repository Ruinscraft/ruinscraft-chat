package com.ruinscraft.chat.api;

import java.util.concurrent.CompletableFuture;

public interface IChatStorage {

    CompletableFuture<Void> savePlayer(IChatPlayer player);

    CompletableFuture<Void> loadPlayer(IChatPlayer player);

    CompletableFuture<Void> logMessage(IChatMessage message);

    void close();

}
