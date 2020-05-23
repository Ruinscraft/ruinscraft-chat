package com.ruinscraft.chat.api;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IChatStorage {

    CompletableFuture<Void> savePlayer(IChatPlayer player);

    CompletableFuture<Void> loadPlayer(IChatPlayer player);

    CompletableFuture<Void> queryOnlinePlayers(IOnlinePlayers onlinePlayers);

    CompletableFuture<Void> purgeOfflinePlayers(List<String> usernames);

    CompletableFuture<Void> logMessage(IChatMessage message);

    void close();

}
